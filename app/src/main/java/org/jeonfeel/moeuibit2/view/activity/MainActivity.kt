package org.jeonfeel.moeuibit2.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.listener.OnMessageReceiveListener
import org.jeonfeel.moeuibit2.ui.mainactivity.MainBottomNavigation
import org.jeonfeel.moeuibit2.ui.mainactivity.Navigation
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMessageReceiveListener {

    private var backBtnTime: Long = 0
    private var text = ""
    private val viewModel: ExchangeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(viewModel)
        }
    }

    @Composable
    fun MainScreen(viewModel: ExchangeViewModel) {
        val navController = rememberNavController()
//        val scaffoldState = rememberScaffoldState()
        Scaffold(
            bottomBar = { MainBottomNavigation(navController) },
//            topBar =
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                Navigation(navController, viewModel)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        MainScreen(viewModel)
    }


    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        val gapTime = curTime - backBtnTime
        if (gapTime in 0..2000) {
            super.onBackPressed()
        } else {
            backBtnTime = curTime
            Toast.makeText(this@MainActivity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onMessageReceiveListener(tickerJsonObject: String) {
        this.text = tickerJsonObject
    }

//    private val callback2 = object : Callback{
//        override fun onFailure(call: Call, e: IOException) {
//
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//            val gson = Gson()
//            Log.d("TAG","// ${response.body()!!.string()} //")
//        }
//    }
}
