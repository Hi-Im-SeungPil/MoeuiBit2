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

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (text.isNotEmpty()) {
            val left = xPosition
            val top = yPosition - textSize / 2 - 10f
            val right = xPosition + width + textMarginLeft * 2
            val bottom = yPosition + textSize / 2 + 10f

            val textY = (top - (top - bottom) * 0.75).toFloat()
            val textX = left + textMarginLeft

            canvas?.drawRect(left, top, right, bottom, rectPaint)
            canvas?.drawText(text, textX,textY, textPaint)
        }
    }

    fun cInit(textSize: Float) {
        this.textSize = textSize
        rectPaint.color = Color.parseColor("#004B00")
        textPaint.color = Color.WHITE
        textPaint.textSize = textSize
    }

    fun actionDownInvalidate(
        x: Float,
        y: Float,
        text: String,
        textMarginLeft: Float,
        width: Float
    ) {
        this.width = width
        this.xPosition = x
        this.yPosition = y
        this.text = text
        this.textMarginLeft = textMarginLeft
        this.invalidate()
    }

    fun actionMoveInvalidate(y: Float, text: String) {
        this.yPosition = y
        this.text = text
        this.invalidate()
    }
}