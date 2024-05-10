package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.upbit.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.showToast

@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val startForActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                if (resultData != null) {
                    val isFavorite = resultData.getBooleanExtra(INTENT_IS_FAVORITE, false)
                    val market = resultData.getStringExtra(INTENT_MARKET) ?: ""
                    viewModel.updateFavorite(market = market, isFavorite = isFavorite)
                }
            }
        }
    val holder = rememberPortfolioScreenStateHolder(
        context = context,
        resultLauncher = startForActivityResult,
        adMobManager = viewModel.adMobManager,
        errorReward = viewModel::errorReward,
        earnReward = viewModel::earnReward,
        btcTradePrice = viewModel.state.btcTradePrice
    )
    EditUserHoldCoinDialog(
        dialogState = holder.editHoldCoinDialogState,
        editUserHoldCoin = viewModel::editUserHoldCoin
    )
    TwoButtonCommonDialog(
        dialogState = holder.adConfirmDialogState,
        title = stringResource(id = R.string.chargeMoney),
        content = stringResource(id = R.string.adDialogContent),
        leftButtonText = stringResource(id = R.string.commonCancel),
        rightButtonText = stringResource(id = R.string.commonAccept),
        leftButtonAction = { holder.adConfirmDialogState.value = false },
        rightButtonAction = {
            holder.showAd()
            holder.adConfirmDialogState.value = false
            holder.adLoadingDialogState.value = true
        })
    CommonLoadingDialog(
        holder.adLoadingDialogState,
        stringResource(id = R.string.loadAd)
    )

    AddLifecycleEvent(
        onPauseAction = {
            UpBitTickerWebSocket.currentPage = IS_ANOTHER_SCREEN
            viewModel.state.isPortfolioSocketRunning.value = false
            UpBitTickerWebSocket.onPause()
        },
        onResumeAction = {
            if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION) {
                UpBitTickerWebSocket.currentPage = IS_PORTFOLIO_SCREEN
                viewModel.init()
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
                    holder.editHoldCoinDialogState.value = true
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
                columnItemDialogState = holder.columnItemDialogState,
                portfolioOrderState = viewModel.state.portfolioOrderState,
                totalValuedAssets = viewModel.state.totalValuedAssets,
                totalPurchase = viewModel.state.totalPurchase,
                userSeedMoney = viewModel.state.userSeedMoney,
                adDialogState = holder.adConfirmDialogState,
                pieChartState = holder.pieChartState,
                userHoldCoinList = viewModel.userHoldCoinList,
                userHoldCoinDTOList = viewModel.state.userHoldCoinDtoList.value,
                selectedCoinKoreanName = holder.selectedCoinKoreanName,
                isPortfolioSocketRunning = viewModel.state.isPortfolioSocketRunning,
                sortUserHoldCoin = viewModel::sortUserHoldCoin,
                getUserCoinInfo = holder::getUserCoinResultMap,
                getPortFolioMainInfoMap = holder::getPortfolioMainInfoMap
            )
        }
    }
}

