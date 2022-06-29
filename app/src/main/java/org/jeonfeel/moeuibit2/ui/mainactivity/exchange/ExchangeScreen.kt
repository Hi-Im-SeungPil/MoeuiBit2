package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.MainActivity
import org.jeonfeel.moeuibit2.activity.main.MainViewModel
import org.jeonfeel.moeuibit2.constant.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.util.AddLifecycleEvent
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.util.showToast

@Composable
fun ExchangeScreen(
    mainViewModel: MainViewModel = viewModel(),
    startForActivityResult: ActivityResultLauncher<Intent>
) {
    Column(Modifier
        .fillMaxSize()) {
        val context = LocalContext.current
        var backBtnTime = remember{
            0L
        }

        AddLifecycleEvent(
            onPauseAction = {
                mainViewModel.isSocketRunning = false
                UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
                UpBitTickerWebSocket.onPause()
            },
            onResumeAction = {
                mainViewModel.initViewModel()
            }
        )

        if (!mainViewModel.loading.value) {
            if (mainViewModel.errorState.value == INTERNET_CONNECTION) {
                SearchBasicTextFieldResult(mainViewModel)
                MarketButtons(mainViewModel)
                SortButtons(mainViewModel)
                ExchangeScreenLazyColumn(mainViewModel,startForActivityResult)
            } else {
                ExchangeErrorScreen(mainViewModel)
            }
        } else {
            ExchangeScreenLoading()
        }

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
}