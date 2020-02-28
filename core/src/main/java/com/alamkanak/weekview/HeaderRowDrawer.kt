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
//        canvas.drawRect(0f, 0f, width, config.headerHeight, config.headerBackgroundPaint)

        val vectorDrawable = VectorDrawableCompat.create(view.context.resources, R.drawable.shape_rectangle_seagreen, null).apply {
            this?.setBounds(0, 0, width.toInt(), config.headerHeight.toInt())
        }

        vectorDrawable?.draw(canvas)

//        val bitmap = BitmapFactory.decodeResource(view.context.resources, R.drawable.ic_launcher)
//        canvas.drawBitmap(bitmap, 0f, 0f, config.headerBackgroundPaint)

        if (config.showHeaderRowBottomLine) {
            val top = config.headerHeight - config.headerRowBottomLineWidth
            canvas.drawLine(0f, top, width, top, config.headerRowBottomLinePaint)
        }
    }
}
