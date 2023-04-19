package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.viewmodels.PortfolioViewModel
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.showToast

@Composable
fun PortfolioScreen(
    portfolioViewModel: PortfolioViewModel = viewModel()
) {
    val context = LocalContext.current
    val startForActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                if (resultData != null) {
                    val isFavorite = resultData.getBooleanExtra(INTENT_IS_FAVORITE, false)
                    val market = resultData.getStringExtra(INTENT_MARKET) ?: ""
                    portfolioViewModel.updateFavorite(market = market, isFavorite = isFavorite)
                }
            }
        }
    EditUserHoldCoinDialog(
        dialogState = portfolioViewModel.state.editHoldCoinDialogState,
        editUserHoldCoin = portfolioViewModel::editUserHoldCoin
    )
    TwoButtonCommonDialog(
        dialogState = portfolioViewModel.state.adConfirmDialogState,
        title = stringResource(id = R.string.chargeMoney),
        content = stringResource(id = R.string.adDialogContent),
        leftButtonText = stringResource(id = R.string.commonCancel),
        rightButtonText = stringResource(id = R.string.commonAccept),
        leftButtonAction = { portfolioViewModel.state.adConfirmDialogState.value = false },
        rightButtonAction = {
            portfolioViewModel.showAd(context)
            portfolioViewModel.state.adConfirmDialogState.value = false
            portfolioViewModel.state.adLoadingDialogState.value = true
        })
    CommonLoadingDialog(
        portfolioViewModel.state.adLoadingDialogState,
        stringResource(id = R.string.loadAd)
    )

    AddLifecycleEvent(
        onPauseAction = {
            UpBitTickerWebSocket.currentPage = IS_ANOTHER_SCREEN
            portfolioViewModel.state.isPortfolioSocketRunning.value = false
            UpBitPortfolioWebSocket.getListener().setPortfolioMessageListener(null)
            UpBitTickerWebSocket.onPause()
        },
        onResumeAction = {
            if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION) {
                UpBitTickerWebSocket.currentPage = IS_PORTFOLIO_SCREEN
                portfolioViewModel.init()
            } else {
                context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
            }
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        10.dp,
                        ambientColor = MaterialTheme.colorScheme.onBackground,
                        spotColor = MaterialTheme.colorScheme.onBackground
                    ),
                backgroundColor = MaterialTheme.colorScheme.background
            ) {
                Text(
                    text = stringResource(id = R.string.investmentDetail),
                    modifier = Modifier
                        .padding(5.dp, 0.dp, 0.dp, 0.dp)
                        .weight(1f, true)
                        .fillMaxHeight()
                        .wrapContentHeight(),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = DpToSp(25.dp),
                        fontWeight = FontWeight.Bold
                    )
                )
                IconButton(onClick = {
                    portfolioViewModel.state.editHoldCoinDialogState.value = true
                }) {
                    Icon(
                        painterResource(id = R.drawable.img_eraser),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            UserHoldCoinLazyColumn(
                startForActivityResult = startForActivityResult,
                columnItemDialogState = portfolioViewModel.state.columnItemDialogState,
                portfolioOrderState = portfolioViewModel.state.portfolioOrderState,
                totalValuedAssets = portfolioViewModel.state.totalValuedAssets,
                totalPurchase = portfolioViewModel.state.totalPurchase,
                userSeedMoney = portfolioViewModel.state.userSeedMoney,
                adDialogState = portfolioViewModel.state.adConfirmDialogState,
                pieChartState = portfolioViewModel.state.pieChartState,
                userHoldCoinList = portfolioViewModel.userHoldCoinList,
                userHoldCoinDTOList = portfolioViewModel.state.userHoldCoinDtoList.value,
                selectedCoinKoreanName = portfolioViewModel.state.selectedCoinKoreanName,
                btcTradePrice = portfolioViewModel.state.btcTradePrice,
                isPortfolioSocketRunning = portfolioViewModel.state.isPortfolioSocketRunning,
                sortUserHoldCoin = portfolioViewModel::sortUserHoldCoin
            )
        }
    }
}

