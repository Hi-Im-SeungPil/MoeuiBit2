package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.LinkType

class CoinInfoStateHolderActionHandler() {
    fun moveToUrl(url: String, linkType: LinkType) {
        if (linkType == LinkType.IN_APP) {
            moveInAppToWebViewURL(url)
        } else {
            moveToBrowser(url)
        }
    }

    private fun moveInAppToWebViewURL(url: String) {

    }

    private fun moveToBrowser(url: String) {

    }
}