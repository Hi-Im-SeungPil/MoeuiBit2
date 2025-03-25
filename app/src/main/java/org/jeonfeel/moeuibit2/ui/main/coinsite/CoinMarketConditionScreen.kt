package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.main.coinsite.secsions.ADSection
import org.jeonfeel.moeuibit2.ui.main.coinsite.secsions.CoinSiteSection
import org.jeonfeel.moeuibit2.ui.main.coinsite.secsions.FearAndGreedySection
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun CoinMarketConditionScreen(uiState: CoinMarketConditionUIState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        FearAndGreedySection(fearAndGreedyUIModel = uiState.fearAndGreedyUIModel)
        CoinSiteSection()
        ADSection()
        DominanceSection()
        MarketCapSection()
        ExchangeRateSection()
    }
}

@Composable
fun ColumnScope.DominanceSection() {
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .weight(1f)
            .background(color = portfolioMainBackground())
    ) {
        Text(text = "도미넌스")
        Text(text = "btc dominance >")
        Text(text = "eth dominance >")
    }
}

@Composable
fun MarketCapSection() {
    Column(modifier = Modifier.background(color = portfolioMainBackground())) {
        Text(text = "시가총액 순위")
        Text(text = "btc market cap")
        Text(text = "eth market cap")
        Text(text = "eth market cap")
        Text(text = "eth market cap")
        Text(text = "eth market cap")
        Text(text = "eth market cap")
        Text(text = "eth market cap")
        Text(text = "eth market cap")
        Text(text = "eth market cap")
    }
}

@Composable
fun ExchangeRateSection() {
    Column(modifier = Modifier.background(color = portfolioMainBackground())) {
        Text(text = "시가총액 순위")
        Text(text = "USD")
        Text(text = "EUR")
        Text(text = "JPY")
        Text(text = "CNY")
    }
}