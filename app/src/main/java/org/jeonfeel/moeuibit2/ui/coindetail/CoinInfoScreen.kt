package org.jeonfeel.moeuibit2.ui.coindetail

import android.content.Intent
import android.net.Uri
import android.os.Message
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import app.dvkyun.flexhybridand.FlexWebChromeClient
import app.dvkyun.flexhybridand.FlexWebView
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.view.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel


@Composable
fun CoinInfoScreen(coinDetailViewModel: CoinDetailViewModel) {
    val context = LocalContext.current
    val coinInfoHashMap = remember {
        mutableStateOf(HashMap<String, String>())
    }
    val selected = remember {
        mutableStateOf("")
    }
    val flex = remember {
        FlexWebView(context)
    }
    val selectedButton = remember {
        mutableStateOf(-1)
    }

    OnLifecycleEvent { lifeCycleOwner, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {

            }
            Lifecycle.Event.ON_START -> {
                flex.initFlex()
                coinDetailViewModel.getCoinInfo()
                coinDetailViewModel.coinInfoLiveData.observe(lifeCycleOwner, Observer {
                    coinInfoHashMap.value = it
                })
            }
            else -> {}
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()) {

            TextButton(onClick = {
                selectedButton.value = 1
                selected.value = coinInfoHashMap.value["homepage"]!!
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selected.value))
                context.startActivity(intent)
            },
                modifier = getButtonModifier(selectedButton.value, 1)
                    .weight(1f)
                    .fillMaxHeight()) {
                Text(text = "홈페이지",
                    fontSize = 12.sp,
                    style = TextStyle(color = getTextColor(selectedButton.value, 1)))
            }

            TextButton(onClick = {
                selectedButton.value = 2
                selected.value = coinInfoHashMap.value["amount"]!!
                flex.loadUrl(selected.value)
            },
                modifier = getButtonModifier(selectedButton.value, 2)
                    .weight(1f)
                    .fillMaxHeight()) {
                Text(text = "시가총액",
                    fontSize = 12.sp,
                    style = TextStyle(color = getTextColor(selectedButton.value, 2)))
            }

            TextButton(onClick = {
                selectedButton.value = 3
                selected.value = coinInfoHashMap.value["twitter"]!!
                flex.loadData("<a class=\"twitter-timeline\" href=\"${selected.value}?ref_src=twsrc%5Etfw\" target=\"_blank\">Tweets</a> <script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>",
                    "text/html; charset=utf-8",
                    "UTF-8")
            },
                modifier = getButtonModifier(selectedButton.value, 3)
                    .weight(1f)
                    .fillMaxHeight()) {
                Text(text = "트위터",
                    fontSize = 12.sp,
                    style = TextStyle(color = getTextColor(selectedButton.value, 3)))
            }

            TextButton(onClick = {
                selectedButton.value = 4
                selected.value = coinInfoHashMap.value["block"]!!
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selected.value))
                context.startActivity(intent)
            },
                modifier = getButtonModifier(selectedButton.value, 4)
                    .weight(1f)
                    .fillMaxHeight()) {
                Text(text = "블럭조회",
                    fontSize = 12.sp,
                    style = TextStyle(color = getTextColor(selectedButton.value, 4)))
            }

            TextButton(onClick = {
                selectedButton.value = 5
                selected.value = coinInfoHashMap.value["info"]!!
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selected.value))
                context.startActivity(intent)
            },
                modifier = getButtonModifier(selectedButton.value, 5)
                    .weight(1f)
                    .fillMaxHeight()) {
                Text(text = "정보",
                    fontSize = 12.sp,
                    style = TextStyle(color = getTextColor(selectedButton.value, 5)))
            }
        }

        AndroidView(factory = { context ->
            flex
        }, modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .padding(10.dp, 0.dp))
    }
}

@Composable
fun getButtonModifier(selectedButton: Int, buttonId: Int): Modifier {
    return if (selectedButton == buttonId) {
        Modifier.border(1.dp, Color.Magenta)
    } else {
        Modifier
    }
}

@Composable
fun getTextColor(selectedButton: Int, buttonId: Int): Color {
    return if (selectedButton == buttonId) {
        Color.Magenta
    } else {
        Color.LightGray
    }
}

fun FlexWebView.initFlex() {
    val flex = this
    flex.settings.setSupportMultipleWindows(true)
    flex.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
    flex.settings.useWideViewPort = false
    flex.settings.loadWithOverviewMode = true
    flex.webChromeClient =
        object : FlexWebChromeClient(CoinDetailActivity::class.java.newInstance()) {
            override fun onCreateWindow(
                view: WebView?,
                dialog: Boolean,
                userGesture: Boolean,
                resultMsg: Message,
            ): Boolean {
                val newWebView = WebView(context)
                val trans = resultMsg.obj as WebView.WebViewTransport
                trans.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }
}