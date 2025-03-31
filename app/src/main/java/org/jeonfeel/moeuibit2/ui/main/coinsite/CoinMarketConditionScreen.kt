package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.main.coinsite.secsions.ADSection
import org.jeonfeel.moeuibit2.ui.main.coinsite.secsions.CoinSiteSection
import org.jeonfeel.moeuibit2.ui.main.coinsite.secsions.ExchangeRateSection
import org.jeonfeel.moeuibit2.ui.main.coinsite.secsions.FearAndGreedySection
import org.jeonfeel.moeuibit2.ui.main.coinsite.secsions.DominanceSection

@Composable
fun CoinMarketConditionScreen(
    uiState: CoinMarketConditionUIState,
    navigateDominanceChart: (String, String) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(15.dp)
    ) {
        FearAndGreedySection(fearAndGreedyUIModel = uiState.fearAndGreedyUIModel)
        CoinSiteSection()
        ADSection()
        DominanceSection(navigateDominanceChart = navigateDominanceChart)
        ExchangeRateSection()
    }
}