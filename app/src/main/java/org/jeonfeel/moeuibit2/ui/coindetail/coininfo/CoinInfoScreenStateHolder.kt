package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class CoinInfoScreenStateHolder(
    private val context: Context
) {
    val actionHandler = CoinInfoStateHolderActionHandler()

}

@Composable
fun rememberCoinInfoScreenStateHolder(
    context: Context = LocalContext.current,
): CoinInfoScreenStateHolder = remember {
    CoinInfoScreenStateHolder(
        context = context
    )
}