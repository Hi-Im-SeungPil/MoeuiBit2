package org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model

import androidx.annotation.Keep

@Keep
data class CoinInfoModel(
    val marketCapKRW: String,
    val maxSupply: String,
    val supply: String,
    val timeString: String,
    val rank: String,
    val unit: String,
)
