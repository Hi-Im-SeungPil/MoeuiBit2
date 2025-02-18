package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CoinInfoRoute(viewModel: CoinInfoViewModel = hiltViewModel()) {
    CoinInfoScreen()
}