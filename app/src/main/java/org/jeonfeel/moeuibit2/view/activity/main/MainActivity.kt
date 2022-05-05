package org.jeonfeel.moeuibit2.view.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.mainactivity.MainBottomNavigation
import org.jeonfeel.moeuibit2.ui.mainactivity.MainNavigation
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil.Companion.currentNetworkState
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var backBtnTime: Long = 0
    private val networkMonitorUtil = NetworkMonitorUtil(this)
    private val exchangeViewModel: ExchangeViewModel by viewModels()
    private lateinit var startForActivityResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()

        setContent {
            MainScreen(exchangeViewModel)
        }
    }

    private fun initActivity() {
        startForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val resultData = it.data
                    if (resultData != null) {
                        val isFavorite = resultData.getBooleanExtra("isFavorite", false)
                        val market = resultData.getStringExtra("market") ?: ""
                        exchangeViewModel.updateFavorite(market, isFavorite)
                    }
                }
            }
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
                TopAppBar(backgroundColor = colorResource(id = R.color.C0F0F5C),
                    title = { Text(text = "모의비트",style = TextStyle(color = Color.White, fontSize = 23.sp)) },
                    actions = {
                        Text(text = "관심" , style = TextStyle(color = Color.White))
                        Switch(
                            checked = exchangeViewModel.showFavorite.value,
                            onCheckedChange = {
                                exchangeViewModel.showFavorite.value =
                                    !exchangeViewModel.showFavorite.value
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                uncheckedThumbColor = Color.White,
                                checkedTrackColor = Color.Yellow,
                                uncheckedTrackColor = Color.DarkGray,
                                checkedTrackAlpha = 1.0f,
                                uncheckedTrackAlpha = 1.0f
                            )
                        )
                    }
                )
            },
            bottomBar = { MainBottomNavigation(navController) },
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                MainNavigation(navController, viewModel, startForActivityResult)
            }
        }
        RESULT_OK
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