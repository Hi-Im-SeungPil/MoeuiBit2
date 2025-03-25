package org.jeonfeel.moeuibit2.ui.main.coinsite.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun CoinSiteScreen(viewModel: CoinSiteViewModel = hiltViewModel()) {
    AddLifecycleEvent(
        onStartAction = {
            viewModel.getIsOpen()
        },
        onStopAction = {
            viewModel.saveIsOpen()
        }
    )

    Column {
        Row(modifier = Modifier.background(commonBackground())) {
            Text(
                text = stringResource(id = R.string.coinSite),
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    .weight(1f, true)
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    color = commonTextColor(),
                    fontSize = DpToSp(20.dp),
                    fontWeight = FontWeight.W600
                )
            )
            Text(
                text = "",
                modifier = Modifier
                    .padding(21.dp)
                    .wrapContentWidth(),
                style = TextStyle(
                    color = commonTextColor(),
                    fontSize = DpToSp(dp = 13.dp)
                )
            )
        }
        Divider(
            Modifier
                .fillMaxWidth()
                .height(1.dp), color = commonDividerColor()
        )
        Box(modifier = Modifier.fillMaxSize()) {
            CoinSiteLazyColumn(
                koreaExchangeIsOpen = viewModel.coinSiteKoreaExchangeIsOpen.value,
                globalExchangeIsOpen = viewModel.coinSiteGlobalExchangeIsOpen.value,
                infoIsOpen = viewModel.coinSiteInfoIsOpen.value,
                kimpIsOpen = viewModel.coinSiteKimpIsOpen.value,
                newsIsOpen = viewModel.coinSiteNewsIsOpen.value,
                communityIsOpen = viewModel.coinSiteCommunityIsOpen.value,
                updateIsOpen = viewModel::updateIsOpen
            )
        }
    }
}

@Composable
fun CoinSiteTopAppBar() {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = commonBackground(),

        ) {

    }
}