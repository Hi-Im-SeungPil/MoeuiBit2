package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.common.internal.service.Common
import org.jeonfeel.moeuibit2.ui.common.CommonLoading
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.common.UIState
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import org.jeonfeel.moeuibit2.utils.ext.showToast

@Composable
fun CoinInfoRoute(
    viewModel: CoinInfoViewModel = hiltViewModel(),
    engName: String,
    symbol: String,
) {
    val coinInfoStateHolder = rememberCoinInfoScreenStateHolder()
    val loadingDialog = remember { mutableStateOf(true) }
    val context = LocalContext.current

    AddLifecycleEvent(
        onStartAction = {
            if (NetworkConnectivityObserver.isNetworkAvailable.value) {
                viewModel.init(engName = engName, symbol = symbol)
            }
        },
        onStopAction = {

        }
    )

    LaunchedEffect(NetworkConnectivityObserver.isNetworkAvailable.value) {
        if (!NetworkConnectivityObserver.isNetworkAvailable.value) {
            viewModel.dismissLoading()
            context.showToast("인터넷 연결을 확인해주세요.")
        }
    }

    when (viewModel.uiState.value.state) {
        UIState.SUCCESS -> {
            CoinInfoScreen(
                coinInfoModel = viewModel.uiState.value.coinInfoModel,
                coinLinkList = viewModel.uiState.value.coinLinkList,
                moveToWeb = coinInfoStateHolder.actionHandler::moveToUrl
            )
        }

        UIState.ERROR -> {

        }

        UIState.LOADING -> {
            CommonLoading()
        }

        UIState.DISMISS_LOADING -> {

        }
    }
}