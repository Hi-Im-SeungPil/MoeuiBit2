package org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model

enum class LinkType {
    IN_APP, BROWSER
}

data class CoinLinkModel(
    val title: String,
    val url: String,
    val linkType: LinkType,
)