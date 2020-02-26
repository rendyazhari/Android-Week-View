package com.alamkanak.weekview

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.alamkanak.weekview.WeekViewEvent.ColorResource

internal class EventChipDrawer<T>(
    private val context: Context,
    private val config: WeekViewConfigWrapper
) {

    private val backgroundPaint = Paint()
    private val borderPaint = Paint()

    internal fun draw(
        eventChip: EventChip<T>,
        canvas: Canvas
    ) {
        val event = eventChip.event

        val cornerRadius = config.eventCornerRadius.toFloat()
        updateBackgroundPaint(event, backgroundPaint)

        val rect = checkNotNull(eventChip.bounds)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)

        if (event.style.hasBorder) {
            updateBorderPaint(event, borderPaint)

            val borderWidth = event.style.getBorderWidth(context)
            val adjustedRect = RectF(
                rect.left + borderWidth / 2f,
                rect.top + borderWidth / 2f,
                rect.right - borderWidth / 2f,
                rect.bottom - borderWidth / 2f)
            canvas.drawRoundRect(adjustedRect, cornerRadius, cornerRadius, borderPaint)
        }

        if (event.isNotAllDay) {
            drawCornersForMultiDayEvents(eventChip, cornerRadius, canvas)
        }

        drawEventImage(eventChip, canvas)
    }

    private fun drawCornersForMultiDayEvents(
        eventChip: EventChip<T>,
        cornerRadius: Float,
        canvas: Canvas
    ) {
        val event = eventChip.event
        val originalEvent = eventChip.originalEvent
        val rect = checkNotNull(eventChip.bounds)

        updateBackgroundPaint(event, backgroundPaint)

        if (event.startsOnEarlierDay(originalEvent)) {
            val topRect = RectF(rect.left, rect.top, rect.right, rect.top + cornerRadius)
            canvas.drawRect(topRect, backgroundPaint)
        }

        if (event.endsOnLaterDay(originalEvent)) {
            val bottomRect = RectF(rect.left, rect.bottom - cornerRadius, rect.right, rect.bottom)
            canvas.drawRect(bottomRect, backgroundPaint)
        }

        if (event.style.hasBorder) {
            drawStroke(eventChip, canvas)
        }
    }

    private fun drawStroke(
        eventChip: EventChip<T>,
        canvas: Canvas
    ) {
        val event = eventChip.event
        val originalEvent = eventChip.originalEvent
        val rect = checkNotNull(eventChip.bounds)

        val borderWidth = event.style.getBorderWidth(context)
        val innerWidth = rect.width() - borderWidth * 2

        val borderStartX = rect.left + borderWidth
        val borderEndX = borderStartX + innerWidth

        updateBorderPaint(event, backgroundPaint)

        if (event.startsOnEarlierDay(originalEvent)) {
            // Remove top rounded corners by drawing a rectangle
            val borderStartY = rect.top
            val borderEndY = borderStartY + borderWidth
            val newRect = RectF(borderStartX, borderStartY, borderEndX, borderEndY)
            canvas.drawRect(newRect, backgroundPaint)
        }

        if (event.endsOnLaterDay(originalEvent)) {
            // Remove bottom rounded corners by drawing a rectangle
            val borderEndY = rect.bottom
            val borderStartY = borderEndY - borderWidth
            val newRect = RectF(borderStartX, borderStartY, borderEndX, borderEndY)
            canvas.drawRect(newRect, backgroundPaint)
        }
    }

    private fun drawEventImage(
        eventChip: EventChip<T>,
        canvas: Canvas
    ) {
        val rect = checkNotNull(eventChip.bounds)
        canvas.apply {
            val paddingHorizontal = config.eventPaddingHorizontal.toFloat()
            save()
            translate(
                paddingHorizontal,
                0f
            )

            var offsetX = 0f
            val lenght = if (rect.width() > rect.height()) {
                offsetX = (rect.width() - rect.height()) / 2
                rect.height()
            } else {
                rect.width()
            } - (paddingHorizontal * 2)

            rect.offset(offsetX, (rect.height() / 2) - (lenght / 2))
            rect.bottom = rect.top + lenght
            rect.right = rect.left + lenght
            eventChip.event.eventImage?.let { bitmap ->
                drawBitmap(bitmap, null, rect, null)
            }

            restore()
        }
    }

    private fun updateBackgroundPaint(
        event: WeekViewEvent<T>,
        paint: Paint
    ) {
        val resource = event.style.getBackgroundColorOrDefault(config)
        paint.color = when (resource) {
            is ColorResource.Id -> ContextCompat.getColor(context, resource.resId)
            is ColorResource.Value -> resource.color
        }
        paint.isAntiAlias = true
        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL
    }

    private fun updateBorderPaint(
        event: WeekViewEvent<T>,
        paint: Paint
    ) {
        paint.color = when (val resource = event.style.borderColorResource) {
            is ColorResource.Id -> ContextCompat.getColor(context, resource.resId)
            is ColorResource.Value -> resource.color
            null -> 0
        }
        paint.isAntiAlias = true
        paint.strokeWidth = event.style.getBorderWidth(context).toFloat()
        paint.style = Paint.Style.STROKE
    }
}
