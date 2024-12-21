package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.main.portfolio.dialogs.EditUserHoldCoinDialog
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.showToast

@Composable
fun PortfolioScreenRoute(
    viewModel: PortfolioViewModel = hiltViewModel(),
    appNavController: NavHostController,
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
        btcTradePrice = viewModel.btcTradePrice,
        userHoldCoinDtoList = viewModel.userHoldCoinDtoList,
        portfolioSearchTextState = viewModel.portfolioSearchTextState,
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
        portfolioOrderState = viewModel.portfolioOrderState,
        totalValuedAssets = viewModel.totalValuedAssets,
        totalPurchase = viewModel.totalPurchase,
        userSeedMoney = viewModel.userSeedMoney,
        adDialogState = holder.adConfirmDialogState,
        userHoldCoinDTOList = viewModel.userHoldCoinDtoList,
        sortUserHoldCoin = viewModel::sortUserHoldCoin,
        getUserCoinInfo = holder::getUserCoinResultMap,
        getPortFolioMainInfoMap = holder::getPortfolioMainInfoMap,
        loadingState = viewModel.loadingState,
        currentBTCPrice = viewModel.btcTradePrice,
        appNavController = appNavController,
        earnReward = viewModel::earnReward,
        portfolioSearchTextState = viewModel.portfolioSearchTextState,
        getList = holder::getList
    )
}