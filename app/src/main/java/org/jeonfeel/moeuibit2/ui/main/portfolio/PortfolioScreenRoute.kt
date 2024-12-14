package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.portfolio.dialogs.EditUserHoldCoinDialog
import org.jeonfeel.moeuibit2.ui.theme.chargingKrwBackgroundColor
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.showToast

@Composable
fun PortfolioScreenRoute(
    viewModel: PortfolioViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val startForActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                if (resultData != null) {
                    val isFavorite = resultData.getBooleanExtra(KeyConst.INTENT_IS_FAVORITE, false)
                    val market = resultData.getStringExtra(KeyConst.INTENT_MARKET) ?: ""
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
        btcTradePrice = viewModel.btcTradePrice
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
        onCreateAction = {
            Logger.e("onCreate")
        },
        onResumeAction = {
            if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION) {
                viewModel.onResume()
            } else {
                context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
            }
        },
        onPauseAction = {
            viewModel.onPause()
        }
    )
    PortfolioScreen(
        startForActivityResult = startForActivityResult,
        columnItemDialogState = holder.columnItemDialogState,
        portfolioOrderState = viewModel.portfolioOrderState,
        totalValuedAssets = viewModel.totalValuedAssets,
        totalPurchase = viewModel.totalPurchase,
        userSeedMoney = viewModel.userSeedMoney,
        adDialogState = holder.adConfirmDialogState,
        pieChartState = holder.pieChartState,
        userHoldCoinDTOList = viewModel.userHoldCoinDtoList,
        selectedCoinKoreanName = holder.selectedCoinKoreanName,
        sortUserHoldCoin = viewModel::sortUserHoldCoin,
        getUserCoinInfo = holder::getUserCoinResultMap,
        getPortFolioMainInfoMap = holder::getPortfolioMainInfoMap,
        loadingState = viewModel.loadingState,
        currentBTCPrice = viewModel.btcTradePrice
    )
}
//                IconButton(onClick = {
//                    holder.editHoldCoinDialogState.value = true
//                }) {
//                    Icon(
//                        painterResource(id = R.drawable.img_eraser),
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.onBackground,
//                        modifier = Modifier.size(30.dp)
//                    )
//                }

