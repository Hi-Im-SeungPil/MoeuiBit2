package org.jeonfeel.moeuibit2.ui.main.exchange

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.tradingview.lightweightcharts.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.MainActivity
import org.jeonfeel.moeuibit2.ui.main.exchange.component.ExchangeLoadingScreen
import org.jeonfeel.moeuibit2.ui.main.exchange.component.ExchangeNetworkDisconnectScreen
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import org.jeonfeel.moeuibit2.utils.ext.showToast
import kotlin.system.exitProcess

@Composable
fun ExchangeScreenRoute(
    viewModel: ExchangeViewModel = hiltViewModel(),
    appNavController: NavHostController,
) {
    val context = LocalContext.current
    val backBtnTime = remember { mutableLongStateOf(0) }

    AddLifecycleEvent(
        onStartAction = {
            if (NetworkConnectivityObserver.isNetworkAvailable.value) {
                if (!viewModel.isStarted) {
                    viewModel.onStart()
                }
            }
        },
        onStopAction = {
            viewModel.onStop()
        },
    )

    LaunchedEffect(NetworkConnectivityObserver.isNetworkAvailable.value) {
        if (NetworkConnectivityObserver.isNetworkAvailable.value) {
            if (!viewModel.isStarted) {
                viewModel.onStart()
            }
        } else {
            viewModel.onStop()
        }
    }

    BackHandler(true) {
        val curTime = System.currentTimeMillis()
        val gapTime = curTime - backBtnTime.longValue
        if (gapTime in 0..2000) {
            (context as MainActivity).moveTaskToBack(true)
            context.finishAndRemoveTask()
            exitProcess(0)
        } else {
            backBtnTime.longValue = curTime
            context.showToast(context.getString(R.string.backPressText))
        }
    }

    when {
        viewModel.loadingState.value -> {
            ExchangeLoadingScreen()
        }

        !NetworkConnectivityObserver.isNetworkAvailable.value -> {
            ExchangeNetworkDisconnectScreen()
        }

        else -> {
            ExchangeScreen(
                tickerList = viewModel.getTickerList(),
                isUpdateExchange = viewModel.isUpdateExchange,
                sortTickerList = viewModel::sortTickerList,
                tradeCurrencyState = viewModel.tradeCurrencyState,
                changeTradeCurrency = viewModel::changeTradeCurrency,
                btcKrwPrice = viewModel.getBtcPrice(),
                appNavController = appNavController,
                selectedSortType = viewModel.selectedSortType,
                sortOrder = viewModel.sortOrder,
                updateSortType = viewModel::updateSortType,
                updateSortOrder = viewModel::updateSortOrder,
                textFieldValueState = viewModel.textFieldValue,
                updateTextFieldValue = viewModel::updateTextFieldValue,
                changeExchange = {
                    viewModel.saveRootExchange()
                    viewModel.onStop()
                    viewModel.onStart()
                }
            )
        }
    }
}
