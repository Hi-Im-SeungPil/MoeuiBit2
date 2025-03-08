package org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model

import androidx.annotation.Keep

@Keep
enum class LinkType {
    IN_APP, BROWSER
}

@Keep
data class CoinLinkModel(
    val title: String,
    val url: String,
    val linkType: LinkType,
)