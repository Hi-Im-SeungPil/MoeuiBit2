package org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model

import androidx.annotation.Keep

@Keep
data class CoinInfoModel(
    val circulatingSupply: String,
    val fullyDilutedValuation: String,
    val image: String,
    val marketCap: String,
    val marketCapRank: String,
    val maxSupply: String,
    val totalSupply: String,
    val blockchainSite: String,
    val description: String,
    val homePage: String,
    val twitterScreenName: String,
    val whitePaper: String,
)
