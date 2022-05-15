package org.jeonfeel.moeuibit2.ui.coindetail.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import org.jeonfeel.moeuibit2.databinding.CandleInfoMarkerBinding
import org.jeonfeel.moeuibit2.dtos.MarkerViewDataBinding
import org.jeonfeel.moeuibit2.util.Calculator

@SuppressLint("ViewConstructor")
class ChartMarkerView constructor(
    context: Context?,
    layoutResource: Int,
    private val dateHashMap: HashMap<Int, String>,
    private val chartData: HashMap<Int, Double>
) :
    MarkerView(context, layoutResource) {

    private val binding: CandleInfoMarkerBinding =
        CandleInfoMarkerBinding.inflate(LayoutInflater.from(context), this, false)
    var dateTime: String = ""
    var acc: String = ""
    init {
        addView(binding.root)
    }

    override fun refreshContent(e: Entry, highlight: Highlight) {
        try {
            if (e is CandleEntry) {
                val splitDateTime = dateHashMap[e.x.toInt()].toString().split('T')
                dateTime = splitDateTime[0].plus("  ${splitDateTime[1].slice(0 until 5)}")
                acc = Calculator.accTradePrice24hCalculatorForChart(chartData[e.x.toInt()]!!)
                val highPrice = Calculator.tradePriceCalculatorForChart(e.high)
                val openPrice = Calculator.tradePriceCalculatorForChart(e.open)
                val lowPrice = Calculator.tradePriceCalculatorForChart(e.low)
                val closePrice = Calculator.tradePriceCalculatorForChart(e.close)
                val highPriceRate = Calculator.markerViewRateCalculator(e.open, e.high)
                val lowPriceRate = Calculator.markerViewRateCalculator(e.open, e.low)
                val closePriceRate = Calculator.markerViewRateCalculator(e.open, e.close)

                val highPriceRateString =
                    String.format("%.2f", Calculator.markerViewRateCalculator(e.open, e.high))
                        .plus('%')
                val lowPriceRateString =
                    String.format("%.2f", Calculator.markerViewRateCalculator(e.open, e.low))
                        .plus('%')
                val closePriceRateString =
                    String.format("%.2f", Calculator.markerViewRateCalculator(e.open, e.close))
                        .plus('%')

                val tvHighColor = getTextColor(highPriceRate)
                val tvLowColor = getTextColor(lowPriceRate)
                val tvCloseColor = getTextColor(closePriceRate)

                binding.tvHighPrice.setTextColor(tvHighColor)
                binding.tvHighPriceRate.setTextColor(tvHighColor)
                binding.tvLowPrice.setTextColor(tvLowColor)
                binding.tvLowPriceRate.setTextColor(tvLowColor)
                binding.tvClosePrice.setTextColor(tvCloseColor)
                binding.tvClosePriceRate.setTextColor(tvCloseColor)

                val candleInfo = MarkerViewDataBinding(
                    dateTime,
                    highPrice,
                    openPrice,
                    lowPrice,
                    closePrice,
                    highPriceRateString,
                    "0.00%",
                    lowPriceRateString,
                    closePriceRateString,
                    acc.plus(" 백만")
                )
                with(binding) {
                    binding.candleInfo = candleInfo
                    executePendingBindings()
                }
                super.refreshContent(e, highlight)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 35f
        val textSize = textPaint.textSize
        val textLength = textPaint.measureText("2022-08-20 13:18") + 10f

        val rectPaint = Paint()
        rectPaint.color = Color.parseColor("#F361A6")

        val left = posX + textLength / 2 + 10f
        val top = canvas.height - textSize - 5f
        val right = posX - textLength / 2 - 10f
        val bottom = canvas.height.toFloat()

        canvas.drawRect(left, top, right, bottom, rectPaint)
        canvas.drawText(
            dateTime,
            right + 10f,
            canvas.height - 5f,
            textPaint
        )
        if (posX > canvas.width / 2.0) {
            getOffsetForDrawingAtPoint(posX, posY)
            super.draw(canvas)
        } else {
            super.draw(canvas, (canvas.width / 2).toFloat(), -posY)
        }
    }

    private fun getTextColor(rate: Float): Int {
        return when {
            rate > 0.00 -> {
                Color.RED
            }
            rate < 0.00 -> {
                Color.BLUE
            }
            else -> {
                Color.BLACK
            }
        }
    }
}