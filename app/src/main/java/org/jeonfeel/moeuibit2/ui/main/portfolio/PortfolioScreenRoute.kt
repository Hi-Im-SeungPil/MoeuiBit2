package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.main.portfolio.dialogs.RemoveCoinBottomSheet
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun PortfolioScreenRoute(
    viewModel: PortfolioViewModel = hiltViewModel(),
    appNavController: NavHostController,
    bottomNavController: NavHostController,
) {
    val context = LocalContext.current

    AddLifecycleEvent(
        onStartAction = {
            viewModel.onStart()
        },
        onStopAction = {
            if (viewModel.showRemoveCoinDialogState.value) {
                viewModel.hideBottomSheet()
            }
            viewModel.onStop()
        }
    )

    BackHandler {
        if (viewModel.showRemoveCoinDialogState.value) {
            viewModel.hideBottomSheet()
        } else {
            bottomNavController.popBackStack()
        }
    }

    val holder = rememberPortfolioScreenStateHolder(
        context = context,
        adMobManager = viewModel.adMobManager,
        errorReward = viewModel::errorReward,
        earnReward = viewModel::earnReward,
        btcTradePrice = viewModel.btcTradePrice,
        userHoldCoinDtoList = viewModel.userHoldCoinDtoList,
        portfolioSearchTextState = viewModel.portfolioSearchTextState,
    )

    TwoButtonCommonDialog(
        dialogState = holder.adConfirmDialogState,
        icon = R.drawable.img_advertisement_3x,
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

    Box(modifier = Modifier.fillMaxSize()) {
        PortfolioScreen(
            portfolioOrderState = viewModel.portfolioOrderState,
            totalValuedAssets = viewModel.totalValuedAssets,
            totalPurchase = viewModel.totalPurchase,
            userSeedMoney = viewModel.userSeedMoney,
            adDialogState = holder.adConfirmDialogState,
            sortUserHoldCoin = viewModel::sortUserHoldCoin,
            getUserCoinInfo = holder::getUserCoinResultMap,
            getPortFolioMainInfoMap = holder::getPortfolioMainInfoMap,
            loadingState = viewModel.loadingState,
            currentBTCPrice = viewModel.btcTradePrice,
            appNavController = appNavController,
            earnReward = viewModel::earnReward,
            portfolioSearchTextState = viewModel.portfolioSearchTextState,
            getList = holder::getList,
            findWrongCoin = viewModel::findWrongCoin,
            loading = viewModel.loading,
        )

        RemoveCoinBottomSheet(
            removeCoinList = viewModel.removeCoinInfo,
            hideSheet = viewModel::hideBottomSheet,
            dialogState = viewModel.showRemoveCoinDialogState,
            checkList = viewModel.removeCoinCheckedState,
            updateCheckedList = viewModel::updateRemoveCoinCheckState
        )
    }
}