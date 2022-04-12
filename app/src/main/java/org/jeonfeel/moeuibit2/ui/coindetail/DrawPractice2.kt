package org.jeonfeel.moeuibit2.ui.coindetail

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class DrawPractice2 constructor(context: Context?) :
    View(context) {

    private var xPosition = 0f
    private var yPosition = 0f
    private var text = ""
    private var textSize = 0f
    private var textMarginLeft = 0f
    private var width = 0f
    private val rectPaint = Paint()
    private val textPaint = Paint()

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (text.isNotEmpty()) {
//            val left = xPosition
//            val top = yPosition - textSize / 2 - 10f
//            val right = xPosition + width + textMarginLeft * 2
//            val bottom = yPosition + textSize / 2 + 10f

//            val textY = (top - (top - bottom) * 0.75).toFloat()
//            val textX = left + textMarginLeft

//            canvas?.drawRect(left, top, right, bottom, rectPaint)
            canvas?.drawText(text, xPosition, yPosition, textPaint)
        }
    }

    fun initCanvas(x: Float) {
        xPosition = x
        textPaint.color = Color.BLACK
        textPaint.textSize = 50f
    }

    fun invalidate1(text: String , y: Float) {
        this.text = text
        this.yPosition = y
        this.invalidate()
    }

    fun invalidate2(text: String, y: Float) {
        this.text = text
        this.yPosition = y
        this.invalidate()
    }
}