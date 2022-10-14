package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.MainActivity
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.constant.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.util.AddLifecycleEvent
import org.jeonfeel.moeuibit2.util.showToast

@Composable
fun ExchangeScreen(
    mainViewModel: MainViewModel = viewModel(),
    startForActivityResult: ActivityResultLauncher<Intent>,
) {
    Column(Modifier.fillMaxSize()) {
        val context = LocalContext.current

        AddLifecycleEvent(
            onPauseAction = {
                mainViewModel.updateExchange = false
                UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
                UpBitTickerWebSocket.onPause()
            },
            onResumeAction = {
                mainViewModel.requestExchangeData()
            }
        )

        when (mainViewModel.loadingState.value) {
            true -> {
                ExchangeScreenLoading()
            }
            false -> {
                mainLazyColumn(mainViewModel, startForActivityResult)
            }
        }
        mainBackHandler(context)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun mainLazyColumn(
    mainViewModel: MainViewModel,
    startForActivityResult: ActivityResultLauncher<Intent>,
) {

    if (mainViewModel.errorState.value == INTERNET_CONNECTION) {
        SearchBasicTextFieldResult(mainViewModel)
        marketButtons2(mainViewModel,startForActivityResult)
    } else {
        ExchangeErrorScreen(mainViewModel)
    }
}

@Composable
fun mainBackHandler(context: Context) {
    var backBtnTime = remember { 0L }
    BackHandler(true) {
        val curTime = System.currentTimeMillis()
        val gapTime = curTime - backBtnTime
        if (gapTime in 0..2000) {
            (context as MainActivity).finish()
        } else {
            backBtnTime = curTime
            context.showToast(context.getString(R.string.backPressText))
        }
    }
}