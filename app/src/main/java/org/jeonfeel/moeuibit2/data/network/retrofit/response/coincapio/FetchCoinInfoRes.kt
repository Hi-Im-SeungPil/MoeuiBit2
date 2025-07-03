package org.jeonfeel.moeuibit2.data.network.retrofit.response.coincapio

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinInfoModel
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForKRW
import org.jeonfeel.moeuibit2.utils.Utils
import java.math.BigDecimal
import java.text.DecimalFormat

@Keep
@Serializable
data class FetchCoinInfoRes(
    val data: List<Data>,
    val timestamp: Long,
) {
    @Keep
    @Serializable
    data class Data(
        val changePercent24Hr: String?,
        val explorer: String?,
        val id: String?,
        val marketCapUsd: String?,
        val maxSupply: String?,
        val name: String?,
        val priceUsd: String?,
        val rank: String?,
        val supply: String?,
        val symbol: String?,
        val volumeUsd24Hr: String?,
        val vwap24Hr: String?,
    ) {
//        fun toCoinInfoModel(usdPrice: BigDecimal, timeStamp: Long): CoinInfoModel {
//            return CoinInfoModel(
//                marketCapKRW = getMarketCapKRW(usdPrice),
//                maxSupply = getMaxSupplyString(),
//                supply = supply?.toBigDecimal()?.formattedStringForKRW() ?: "-",
//                timeString = Utils.convertTimestampToString(timestamp = timeStamp),
//                rank = rank ?: "-",
//                unit = getUnit(usdPrice),
//            )
//        }

        private fun getMarketCapKRW(usdPrice: BigDecimal): String {
            val marketCapUsd = marketCapUsd?.toBigDecimal() ?: BigDecimal.ZERO
            return if (usdPrice == BigDecimal.ZERO) {
                formatBigDecimal(marketCapUsd)
            } else if (marketCapUsd == BigDecimal.ZERO) {
                "-"
            } else {
                formatBigDecimal(marketCapUsd.multiply(usdPrice))
            }
        }

        private fun formatBigDecimal(value: BigDecimal): String {
            val formatter = DecimalFormat("#,###")

            return when {
                value >= BigDecimal("1000000000000") -> {
                    val trillion = value.divide(BigDecimal("1000000000000"))
                    val billion =
                        value.remainder(BigDecimal("1000000000000")).divide(BigDecimal("100000000"))
                    "${formatter.format(trillion)}조 ${formatter.format(billion)}억".trim()
                }

                value >= BigDecimal("100000000") -> {
                    val billion = value.divide(BigDecimal("100000000"))
                    val million =
                        value.remainder(BigDecimal("100000000")).divide(BigDecimal("10000"))
                    "${formatter.format(billion)}억 ${formatter.format(million)}만".trim()
                }

                value >= BigDecimal("10000") -> {
                    val million = value.divide(BigDecimal("10000"))
                    "${formatter.format(million)}만"
                }

                else -> formatter.format(value)
            }
        }

        private fun getUnit(usdPrice: BigDecimal): String {
            return if (usdPrice == BigDecimal.ZERO) {
                "USD"
            } else {
                "원"
            }
        }

        private fun getMaxSupplyString(): String {
            if (maxSupply == null) return "-" else maxSupply.toBigDecimal().formattedStringForKRW()
            return "-"
        }
    }
}