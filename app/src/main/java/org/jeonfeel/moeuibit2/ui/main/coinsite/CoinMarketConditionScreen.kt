package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.main.coinsite.component.AnimatedHalfGauge
import org.jeonfeel.moeuibit2.ui.main.coinsite.component.GaugeGuide
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun CoinMarketConditionScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
        Row {
            AnimatedHalfGauge(66)
            GaugeGuide()
        }
        DominanceSection()
        ADSection()
        MarketCapSection()
        ExchangeRateSection()
    }
}

@Composable
fun ADSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = portfolioMainBackground(),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Text(text = "광고")
    }
}

@Composable
fun ColumnScope.DominanceSection() {
    Column(modifier = Modifier
        .weight(1f)
        .background(color = portfolioMainBackground())) {
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