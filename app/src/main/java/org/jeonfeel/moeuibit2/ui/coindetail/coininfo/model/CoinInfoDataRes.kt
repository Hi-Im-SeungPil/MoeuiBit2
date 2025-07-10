package org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfoCalculator.calculateSupplyToString
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfoCalculator.formatMarketCap

@Keep
@Serializable
data class CoinInfoDataRes(
    @SerialName("circulatingSupply")
    val circulatingSupply: Double = 0.0,

    @SerialName("fullyDilutedValuation")
    val fullyDilutedValuation: Double = 0.0,

    @SerialName("image")
    val image: String = "",

    @SerialName("marketCap")
    val marketCap: Double= 0.0,

    @SerialName("marketCapRank")
    val marketCapRank: Int = 0,

    @SerialName("maxSupply")
    val maxSupply: Double = 0.0,

    @SerialName("totalSupply")
    val totalSupply: Double = 0.0,

    @SerialName("symbol")
    val symbol: String = "",

    @SerialName("community")
    val community: Community = Community(),
) {

    fun mapToCoinInfoModel(): CoinInfoModel {
        return CoinInfoModel(
            circulatingSupply = circulatingSupply.calculateSupplyToString(),
            fullyDilutedValuation = fullyDilutedValuation.formatMarketCap(),
            marketCap = marketCap.formatMarketCap(),
            marketCapRank = marketCapRank.toString(),
            totalSupply = totalSupply.calculateSupplyToString(),
            maxSupply = maxSupply.calculateSupplyToString(),
            image = image,
            blockchainSite = community.blockchainSite,
            description = community.description,
            homePage = community.homePage,
            twitterScreenName = community.twitterScreenName,
            whitePaper = community.whitePaper,
            symbol = symbol.uppercase()
        )
    }

    @Keep
    @Serializable
    data class Community(
        @SerialName("blockchainSite")
        val blockchainSite: String = "",

        @SerialName("description")
        val description: String = "",

        @SerialName("homePage")
        val homePage: String = "",

        @SerialName("twitterScreenName")
        val twitterScreenName: String = "",

        @SerialName("whitePaper")
        val whitePaper: String = "",
    )
}