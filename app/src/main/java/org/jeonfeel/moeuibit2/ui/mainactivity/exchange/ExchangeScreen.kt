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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.activity.main.MainActivity
import org.jeonfeel.moeuibit2.activity.main.MainViewModel

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

        OnLifecycleEvent { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    mainViewModel.isSocketRunning = false
                    UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
                    UpBitTickerWebSocket.onPause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    mainViewModel.initViewModel()
                }
                else -> {}
            }
        }

        BackHandler(true) {
            val curTime = System.currentTimeMillis()
            val gapTime = curTime - backBtnTime
            if (gapTime in 0..2000) {
                (context as MainActivity).finish()
            } else {
                backBtnTime = curTime
                Toast.makeText(context, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

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
    }
}

