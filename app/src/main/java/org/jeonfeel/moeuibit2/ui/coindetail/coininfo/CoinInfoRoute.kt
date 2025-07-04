package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.Operation.State.SUCCESS
import com.google.android.gms.common.internal.service.Common
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.MainActivity
import org.jeonfeel.moeuibit2.ui.common.CommonLoading
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.common.UIState
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import org.jeonfeel.moeuibit2.utils.ext.showToast
import kotlin.system.exitProcess

@Composable
fun CoinInfoRoute(
    viewModel: CoinInfoViewModel = hiltViewModel(),
    engName: String,
    symbol: String,
    navigateUP: () -> Unit,
) {
    val coinInfoStateHolder = rememberCoinInfoScreenStateHolder()
    val loadingDialog = remember { mutableStateOf(true) }
    val context = LocalContext.current

    AddLifecycleEvent(
        onStartAction = {
            if (NetworkConnectivityObserver.isNetworkAvailable.value) {
                viewModel.init(symbol = symbol)
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

    if (loadingDialog.value) {
        CommonLoading(dismissRequest = {
            viewModel.dismissLoading()
            loadingDialog.value = false
        })
    }

    when (viewModel.uiState.value.state) {
        UIState.SUCCESS -> {
            loadingDialog.value = false
            CoinInfoScreen(
                coinInfoModel = viewModel.uiState.value.coinInfoModel,
                moveToWeb = coinInfoStateHolder.actionHandler::moveToUrl
            )
        }

        UIState.ERROR -> {

        }

        UIState.LOADING -> {
            loadingDialog.value = true
        }

        UIState.DISMISS_LOADING -> {
            loadingDialog.value = false
        }
    }
}