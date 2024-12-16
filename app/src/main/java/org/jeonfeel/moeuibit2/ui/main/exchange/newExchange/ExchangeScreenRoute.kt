package org.jeonfeel.moeuibit2.ui.main.exchange.newExchange

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.activities.MainActivity
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.showToast
import kotlin.system.exitProcess

@Composable
fun ExchangeScreenRoute(
    viewModel: ExchangeViewModel = hiltViewModel(),
    appNavController: NavHostController
) {
    val context = LocalContext.current
    val backBtnTime = remember { mutableLongStateOf(0) }

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

    AddLifecycleEvent(
        onCreateAction = {
            Logger.e("onCreate")
        },
        onPauseAction = {
            viewModel.onPause()
        },
        onResumeAction = {
            viewModel.onResume()
        }
    )

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
        updateSortOrder = viewModel::updateSortOrder
    )
}
