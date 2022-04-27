package org.jeonfeel.moeuibit2.view.activity.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.mainactivity.MainBottomNavigation
import org.jeonfeel.moeuibit2.ui.mainactivity.MainNavigation
import org.jeonfeel.moeuibit2.util.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.util.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil.Companion.currentNetworkState
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var backBtnTime: Long = 0
    val networkMonitorUtil = NetworkMonitorUtil(this)
    private val exchangeViewModel: ExchangeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()

        setContent {
            MainScreen(exchangeViewModel)
        }
    }

    private fun initActivity() {
        initNetworkStateMonitor()
    }

    private fun initNetworkStateMonitor() {
        networkMonitorUtil.result = { isAvailable, _ ->
            when (isAvailable) {
                true -> {
                    if (currentNetworkState != INTERNET_CONNECTION) {
                        currentNetworkState = INTERNET_CONNECTION
                    }
                }
                false -> {
                    if (currentNetworkState != NO_INTERNET_CONNECTION) {
                        currentNetworkState = NO_INTERNET_CONNECTION
                        exchangeViewModel.errorState.value = NO_INTERNET_CONNECTION
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        networkMonitorUtil.register()
    }

    override fun onStop() {
        super.onStop()
        networkMonitorUtil.unregister()
    }

    @Composable
    fun MainScreen(viewModel: ExchangeViewModel) {
        val navController = rememberNavController()
        Scaffold(
            topBar = {
                TopAppBar(backgroundColor = colorResource(id = R.color.C0F0F5C), title = {Text(text = "")} )
            },
            bottomBar = { MainBottomNavigation(navController) },
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                MainNavigation(navController, viewModel)
            }
        }
    }

//    override fun onBackPressed() {
//        val curTime = System.currentTimeMillis()
//        val gapTime = curTime - backBtnTime
//        if (gapTime in 0..2000) {
//            super.onBackPressed()
//        } else {
//            backBtnTime = curTime
//            Toast.makeText(this@MainActivity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }
}