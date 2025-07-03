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
        val df = DecimalFormat("#,###")
        return when {
            this >= 1_0000_0000_0000 -> String.format("%.1f조", this / 1_0000_0000_0000.0)
            this >= 1_0000_0000 -> df.format(this / 1_0000_0000) + "억"
            this >= 1000_0000 -> df.format(this / 10000) + "만"
            else -> df.format(this)
        }
    }
}