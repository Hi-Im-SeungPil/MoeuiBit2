package org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import org.jeonfeel.moeuibit2.ui.theme.increase_candle_color

@SuppressLint("ViewConstructor")
class ChartCanvas constructor(context: Context?) :
    View(context) {

    private var xPosition = 0f
    private var yPosition = 0f
    private var textSize = 0f
    private var textMarginLeft = 0f
    private var width = 0f
    private var lastCandlePriceYPosition = 0f
    private var selectPrice = ""
    private var lastCandlePrice = ""
    private val selectPriceRectPaint = Paint()
    private val lastCandlePriceRectPaint = Paint()
    private val textPaint = Paint()

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if(lastCandlePrice.isNotEmpty()) {
            val left = xPosition
            val right = xPosition + width + textMarginLeft * 2
            val top = lastCandlePriceYPosition - textSize / 2 - 10f
            val bottom = lastCandlePriceYPosition + textSize / 2 + 10f

            val textY = (top - (top - bottom) * 0.75).toFloat()
            val textX = left + textMarginLeft

            canvas?.drawRect(left,top,right * 2,bottom, lastCandlePriceRectPaint)
            canvas?.drawText(lastCandlePrice,textX,textY,textPaint)
        }
        if (selectPrice.isNotEmpty()) {
            val left = xPosition
            val top = yPosition - textSize / 2 - 10f
            val right = xPosition + width + textMarginLeft * 2
            val bottom = yPosition + textSize / 2 + 10f

            val textY = (top - (top - bottom) * 0.75).toFloat()
            val textX = left + textMarginLeft

            canvas?.drawRect(left, top, right * 2, bottom, selectPriceRectPaint)
            canvas?.drawText(selectPrice, textX,textY, textPaint)
        }
    }

    fun canvasInit(textSize: Float, textMarginLeft: Float,
                   width: Float, x: Float) {
        this.textSize = textSize
        this.width = width
        this.xPosition = x
        this.textMarginLeft = textMarginLeft
        selectPriceRectPaint.color = Color.parseColor("#004B00")
        lastCandlePriceRectPaint.color = increase_candle_color
        textPaint.color = Color.WHITE
        textPaint.textSize = textSize
    }

    fun actionDownInvalidate(
        y: Float,
        text: String
    ) {
        this.yPosition = y
        this.selectPrice = text
        this.invalidate()
    }

    fun actionMoveInvalidate(y: Float, text: String) {
        this.yPosition = y
        this.selectPrice = text
        this.invalidate()
    }

    fun realTimeLastCandleClose(lastCandlePriceYPosition: Float, lastCandlePrice: String, color: Int) {
        this.lastCandlePrice = lastCandlePrice
        this.lastCandlePriceYPosition = lastCandlePriceYPosition
        this.lastCandlePriceRectPaint.color = color
        this.invalidate()
    }

    fun getTextPaint(): Paint {
        return textPaint
    }
}