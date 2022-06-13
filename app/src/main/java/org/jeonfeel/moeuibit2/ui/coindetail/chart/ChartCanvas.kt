package org.jeonfeel.moeuibit2.ui.coindetail.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import com.github.mikephil.charting.utils.MPPointF

@SuppressLint("ViewConstructor")
class ChartCanvas constructor(context: Context?) :
    View(context) {

    private var xPosition = 0f
    private var yPosition = 0f
    private var textSize = 0f
    private var textMarginLeft = 0f
    private var width = 0f
    private var lastCandlePriceYPosition = 0f
    private var lastBarYPosition = 0f
    private var purchaseAverageYPosition = 0f
    private var selectPrice = ""
    private var lastCandlePrice = ""
    private var lastBarPrice = ""
    private var purchaseAveragePrice = ""
    private val selectPriceRectPaint = Paint()
    private val lastCandlePriceRectPaint = Paint()
    private val purchaseAverageRectPaint = Paint()
    private val lastBarRecPaint = Paint()
    private val textPaint = Paint()

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        if(purchaseAveragePrice.isNotEmpty()) {
            val left = xPosition
            val top = purchaseAverageYPosition - textSize / 2 - 10f
            val right = xPosition + width + textMarginLeft * 2
            val bottom = purchaseAverageYPosition + textSize / 2 + 10f

            val textY = (top - (top - bottom) * 0.75).toFloat()
            val textX = left + textMarginLeft

            canvas?.drawRect(left, top, right * 2, bottom, purchaseAverageRectPaint)
            canvas?.drawText(purchaseAveragePrice,textX,textY,textPaint)

        }
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

    fun canvasInit(textSize: Float, textMarginLeft: Float,
                   width: Float, x: Float) {
        this.textSize = textSize
        this.width = width
        this.xPosition = x
        this.textMarginLeft = textMarginLeft
        selectPriceRectPaint.color = Color.parseColor("#004B00")
        lastCandlePriceRectPaint.color = Color.RED
        purchaseAverageRectPaint.color = Color.parseColor("#2F9D27")
        lastBarRecPaint.color = Color.RED
        textPaint.color = Color.WHITE
        textPaint.textSize = textSize
    }

    fun purchaseAverageSetPosition(position: Float) {
        purchaseAverageYPosition = position
        Log.d("draw",purchaseAverageYPosition.toString())
        this.invalidate()
    }

    fun purchaseAverageSetPrice(price: String) {
        purchaseAveragePrice = price
        Log.d("price",purchaseAveragePrice)
        this.invalidate()
    }

    fun actionDownInvalidate(
        y: Float,
        text: String,
    ) {
        this.yPosition = y
        this.selectPrice = text
        this.invalidate()
        Log.d("draw2",yPosition.toString())
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
}