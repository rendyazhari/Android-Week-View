package com.alamkanak.weekview

import android.graphics.RectF
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.text.TextUtils.TruncateAt.END
import kotlin.math.roundToInt

internal class AllDayEventsUpdater<T : Any>(
    private val view: WeekView<T>,
    private val config: WeekViewConfigWrapper,
    private val cache: WeekViewCache<T>,
    private val chipCache: EventChipCache<T>
) : Updater {

    private val context = view.context
    private val rectCalculator = EventChipRectCalculator<T>(config)

    private var previousHorizontalOrigin: Float? = null
    private var dummyTextLayout: StaticLayout? = null

    override fun isRequired(drawingContext: DrawingContext): Boolean {
        val didScrollHorizontally = previousHorizontalOrigin != config.currentOrigin.x
        val dateRange = drawingContext.dateRange
        val containsNewChips = chipCache.allDayEventChipsInDateRange(dateRange).any { it.bounds == null }
        return didScrollHorizontally || containsNewChips
    }

    override fun update(drawingContext: DrawingContext) {
        cache.clearAllDayEventLayouts()

        val datesWithStartPixels = drawingContext.dateRangeWithStartPixels
        for ((date, startPixel) in datesWithStartPixels) {
            // If we use a horizontal margin in the day view, we need to offset the start pixel.
            val modifiedStartPixel = when {
                config.isSingleDay -> startPixel + config.eventMarginHorizontal.toFloat()
                else -> startPixel
            }

            val eventChips = chipCache.allDayEventChipsByDate(date)
            for (eventChip in eventChips) {
                calculateTextLayout(eventChip, modifiedStartPixel)
            }
        }

        val maximumChipHeight = cache.allDayEventLayouts
            .mapNotNull { it.bounds }
            .map { it.height().roundToInt() }
            .max() ?: 0

        config.updateAllDayEventHeight(maximumChipHeight)
    }

    private fun calculateTextLayout(
        eventChip: EventChip<T>,
        startPixel: Float
    ) {
        val chipRect = rectCalculator.calculateAllDayEvent(eventChip, startPixel)
        eventChip.bounds = if (chipRect.isValidEventBounds) chipRect else null
    }

    /**
     * Creates a dummy text layout that is only used to determine the height of all-day events.
     */
    private fun createDummyTextLayout(
        event: WeekViewEvent<T>
    ): StaticLayout {
        if (dummyTextLayout == null) {
            val textPaint = event.getTextPaint(context, config)
            dummyTextLayout = TextLayoutBuilder.build("", textPaint, width = 0)
        }
        return checkNotNull(dummyTextLayout)
    }

    private val RectF.isValidEventBounds: Boolean
        get() = (left < right &&
            left < view.width &&
            top < view.height &&
            right > config.timeColumnWidth &&
            bottom > 0)

    private operator fun RectF.component1() = left

    private operator fun RectF.component2() = top

    private operator fun RectF.component3() = right

    private operator fun RectF.component4() = bottom

    private fun CharSequence.ellipsized(
        textPaint: TextPaint,
        availableArea: Int,
        truncateAt: TextUtils.TruncateAt = END
    ): CharSequence = TextUtils.ellipsize(this, textPaint, availableArea.toFloat(), truncateAt)
}
