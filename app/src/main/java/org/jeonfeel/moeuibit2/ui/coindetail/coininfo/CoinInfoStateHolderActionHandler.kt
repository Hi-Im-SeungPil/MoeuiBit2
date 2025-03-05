package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import android.content.Context
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.LinkType
import org.jeonfeel.moeuibit2.utils.ext.moveUrl

class CoinInfoStateHolderActionHandler(
    private val context: Context
) {
    fun moveToUrl(url: String, linkType: LinkType) {
        if (linkType == LinkType.IN_APP) {
            context.moveUrl(url)
        } else {
            context.moveUrl(url)
        }
    }

    private fun moveInAppToWebViewURL(url: String) {

    }
}