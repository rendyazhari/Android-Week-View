package com.alamkanak.weekview

import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

internal class HeaderRowDrawer<T : Any>(
    private val view: WeekView<T>,
    private val config: WeekViewConfigWrapper
) : Drawer {

    override fun draw(
        drawingContext: DrawingContext,
        canvas: Canvas
    ) {
        val width = view.width.toFloat()

        // Disable configurable background with custom one
//        canvas.drawRect(0f, 0f, width, config.headerHeight, config.headerBackgroundPaint)

        val vectorDrawable = VectorDrawableCompat.create(view.context.resources, R.drawable.shape_rectangle_seagreen, null)
        vectorDrawable?.run {
            setBounds(0, 0, width.toInt(), config.headerHeight.toInt())
            draw(canvas)
        }

        if (config.showHeaderRowBottomLine) {
            val top = config.headerHeight - config.headerRowBottomLineWidth
            canvas.drawLine(0f, top, width, top, config.headerRowBottomLinePaint)
        }
    }
}
