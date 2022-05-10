package org.jeonfeel.moeuibit2.ui.coindetail

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
    private var text = ""
    private var textSize = 0f
    private var textMarginLeft = 0f
    private var width = 0f
    private val rectPaint = Paint()
    private val textPaint = Paint()
    private val rectPaint2 = Paint()
    private var yPosition2 = 0f

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if(xPosition != 0f) {

            val left = xPosition
            val top = yPosition - textSize / 2 - 10f
            val right = xPosition + width + textMarginLeft * 2
            val bottom = yPosition + textSize / 2 + 10f

            val top2 = yPosition2 - textSize / 2 - 10f
            val bottom2 = yPosition2 + textSize / 2 + 10f

            canvas?.drawRect(left,top2,right,bottom2, rectPaint2)
        }
        if (text.isNotEmpty()) {
            val left = xPosition
            val top = yPosition - textSize / 2 - 10f
            val right = xPosition + width + textMarginLeft * 2
            val bottom = yPosition + textSize / 2 + 10f

            val textY = (top - (top - bottom) * 0.75).toFloat()
            val textX = left + textMarginLeft

            val top2 = yPosition2 - textSize / 2 - 10f
            val bottom2 = yPosition2 + textSize / 2 + 10f

            canvas?.drawRect(left, top, right, bottom, rectPaint)
            canvas?.drawText(text, textX,textY, textPaint)
        }
    }

    fun cInit(textSize: Float,textMarginLeft: Float,
              width: Float,x: Float) {
        this.textSize = textSize
        this.width = width
        this.xPosition = x
        this.textMarginLeft = textMarginLeft
        rectPaint.color = Color.parseColor("#004B00")
        rectPaint2.color = Color.BLUE
        textPaint.color = Color.WHITE
        textPaint.textSize = textSize
    }

    fun actionDownInvalidate(
        y: Float,
        text: String,
    ) {
        this.yPosition = y
        this.text = text
        this.invalidate()
    }

    fun actionMoveInvalidate(y: Float, text: String) {
        this.yPosition = y
        this.text = text
        this.invalidate()
    }

    fun actionDownDrawLastCandleClose(yPosition2: Float) {
        this.yPosition2 = yPosition2
        this.invalidate()
    }

    fun actionMoveDrawLastCandleClose(yPosition2: Float) {
        this.yPosition2 = yPosition2
        this.invalidate()
    }
}