package org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfoCalculator.calculateSupplyToString
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfoCalculator.formatMarketCap
import org.jeonfeel.moeuibit2.utils.formatWithComma

@Serializable
data class CoinInfoDataRes(
    @SerialName("circulatingSupply")
    val circulatingSupply: Double,

    @SerialName("fullyDilutedValuation")
    val fullyDilutedValuation: Double,

    @SerialName("image")
    val image: String,

    @SerialName("marketCap")
    val marketCap: Double,

    @SerialName("marketCapRank")
    val marketCapRank: Int,

    @SerialName("maxSupply")
    val maxSupply: Double,

    @SerialName("totalSupply")
    val totalSupply: Double,

    @SerialName("symbol")
    val symbol: String,

    @SerialName("community")
    val community: Community
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
            whitePaper = community.whitePaper
        )
    }

    @Serializable
    data class Community(
        @SerialName("blockchainSite")
        val blockchainSite: String,

        @SerialName("description")
        val description: String,

        @SerialName("homePage")
        val homePage: String,

        @SerialName("twitterScreenName")
        val twitterScreenName: String,

        @SerialName("whitePaper")
        val whitePaper: String,
    )
}