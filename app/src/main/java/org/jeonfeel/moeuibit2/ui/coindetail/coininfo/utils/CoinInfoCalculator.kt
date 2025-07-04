package org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils

import java.text.DecimalFormat

object CoinInfoCalculator {
    fun Double.calculateSupplyToString(): String {
        return this.toLong().toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    }

    fun Double.formatMarketCap(): String {
        val intFormat = DecimalFormat("#,###")
        val floatFormat = DecimalFormat("#,###.#")

        return when {
            this >= 1_0000_0000_0000 -> {
                val value = this / 1_0000_0000_0000.0
                val formatted = floatFormat.format(value)
                "$formatted 조"
            }
            this >= 1_0000_0000 -> intFormat.format(this / 1_0000_0000) + " 억"
            this >= 1000_0000 -> intFormat.format(this / 10000) + " 만"
            else -> intFormat.format(this)
        }
    }
}