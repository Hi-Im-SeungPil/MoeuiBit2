package org.jeonfeel.moeuibit2.ui.coindetail.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

@SuppressLint("ViewConstructor")
class DrawPractice constructor(context: Context?) :
    View(context) {

    private var xPosition = 0f
    private var yPosition = 0f
    private var textSize = 0f
    private var textMarginLeft = 0f
    private var width = 0f
    private var lastCandlePriceYPosition = 0f
    private var lastBarYPosition = 0f
    private var selectPrice = ""
    private var lastCandlePrice = ""
    private var lastBarPrice = ""
    private val selectPriceRectPaint = Paint()
    private val lastCandlePriceRectPaint = Paint()
    private val lastBarRecPaint = Paint()
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
        if(lastBarPrice.isNotEmpty()) {
            val left = xPosition
            val right = xPosition + width + textMarginLeft * 2
            val top = lastBarYPosition - textSize / 2 - 10f
            val bottom = lastBarYPosition + textSize / 2 + 10f

            val textY = (top - (top - bottom) * 0.75).toFloat()
            val textX = left + textMarginLeft

            canvas?.drawRect(left,top,right * 2,bottom, lastBarRecPaint)
            canvas?.drawText(lastBarPrice,textX,textY,textPaint)
        }
    }

    fun cInit(textSize: Float,textMarginLeft: Float,
              width: Float,x: Float) {
        this.textSize = textSize
        this.width = width
        this.xPosition = x
        this.textMarginLeft = textMarginLeft
        selectPriceRectPaint.color = Color.parseColor("#004B00")
        lastCandlePriceRectPaint.color = Color.RED
        lastBarRecPaint.color = Color.RED
        textPaint.color = Color.WHITE
        textPaint.textSize = textSize
    }

    fun actionDownInvalidate(
        y: Float,
        text: String,
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
        this.lastBarRecPaint.color = color
        this.lastBarPrice = ""
        this.lastBarYPosition = 0f
        this.invalidate()
    }

    fun actionDownDrawLastCandleClose(lastCandlePriceYPosition: Float,lastCandlePrice: String, color: Int,lastBarPrice: String,lastBarYPosition: Float) {
        this.lastCandlePrice = lastCandlePrice
        this.lastCandlePriceYPosition = lastCandlePriceYPosition
        this.lastCandlePriceRectPaint.color = color
        this.lastBarRecPaint.color = color
        this.lastBarPrice = lastBarPrice
        this.lastBarYPosition = lastBarYPosition
        this.invalidate()
    }

    fun actionMoveDrawLastCandleClose(lastCandlePriceYPosition: Float,lastCandlePrice: String, color: Int,lastBarPrice: String,lastBarYPosition: Float) {
        this.lastCandlePrice = lastCandlePrice
        this.lastCandlePriceYPosition = lastCandlePriceYPosition
        this.lastCandlePriceRectPaint.color = color
        this.lastBarRecPaint.color = color
        this.lastBarPrice = lastBarPrice
        this.lastBarYPosition = lastBarYPosition
        this.invalidate()
    }
}