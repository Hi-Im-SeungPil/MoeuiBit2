package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.dvkyun.flexhybridand.FlexWebChromeClient
import app.dvkyun.flexhybridand.FlexWebView
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent


@SuppressLint("MutableCollectionMutableState")
@Composable
fun CoinInfoScreen(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    CoinInfoProgressDialog(coinDetailViewModel)
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
                coinDetailViewModel.coinInfoLoading.value = false
            }
            Lifecycle.Event.ON_START -> {
                flex.initFlex()
                coinDetailViewModel.getCoinInfo()
                coinDetailViewModel.coinInfoLiveData.observe(lifeCycleOwner) {
                    coinInfoHashMap.value = it
                    coinDetailViewModel.coinInfoDialog.value = false
                }
            }
            else -> {}
        }
    }
    if (coinInfoHashMap.value.isNotEmpty() && coinDetailViewModel.coinInfoLoading.value) {
        CoinInfoContent(selected, selectedButton, context, coinInfoHashMap, flex)
    } else if (coinInfoHashMap.value.isEmpty() && coinDetailViewModel.coinInfoLoading.value) {
        CoinInfoEmptyScreen()
    }
}

@Composable
fun CoinInfoContent(
    selected: MutableState<String>,
    selectedButton: MutableState<Int>,
    context: Context,
    coinInfoHashMap: MutableState<HashMap<String, String>>,
    flex: FlexWebView
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {

            Text(
                text = "블럭조회",
                fontSize = 14.sp,
                style = TextStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(0.dp, 4.dp)
                    .weight(1f)
                    .wrapContentHeight()
                    .clickable {
                        selected.value = coinInfoHashMap.value["block"]!!
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selected.value))
                        context.startActivity(intent)
                    }
                    .padding(0.dp, 5.dp)
            )

            Text(
                text = "홈페이지",
                fontSize = 14.sp,
                style = TextStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(0.dp, 4.dp)
                    .weight(1f)
                    .wrapContentHeight()
                    .clickable {
                        selected.value = coinInfoHashMap.value["homepage"]!!
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selected.value))
                        context.startActivity(intent)
                    }
                    .padding(0.dp, 5.dp)
            )

            Text(
                text = "정보",
                fontSize = 14.sp,
                style = TextStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(0.dp, 4.dp)
                    .weight(1f)
                    .wrapContentHeight()
                    .clickable {
                        selected.value = coinInfoHashMap.value["info"]!!
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selected.value))
                        context.startActivity(intent)
                    }
                    .padding(0.dp, 5.dp)
            )

            Text(
                text = "트위터",
                fontSize = 14.sp,
                modifier = getButtonModifier(selectedButton.value, 4)
                    .weight(1f)
                    .wrapContentHeight()
                    .clickable {
                        selectedButton.value = 4
                        selected.value = coinInfoHashMap.value["twitter"]!!
                        flex.loadData(
                            "<a class=\"twitter-timeline\" href=\"${selected.value}?ref_src=twsrc%5Etfw\" target=\"_blank\">Tweets</a> <script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>",
                            "text/html; charset=utf-8",
                            "UTF-8"
                        )
                    }
                    .padding(0.dp, 5.dp),
                style = TextStyle(
                    color = getTextColor(selectedButton.value, 4),
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = "시가총액",
                fontSize = 14.sp,
                style = TextStyle(
                    color = getTextColor(selectedButton.value, 5),
                    textAlign = TextAlign.Center
                ),
                modifier = getButtonModifier(selectedButton.value, 5)
                    .weight(1f)
                    .wrapContentHeight()
                    .clickable {
                        selectedButton.value = 5
                        selected.value = coinInfoHashMap.value["amount"]!!
                        flex.loadUrl(selected.value)
                    }
                    .padding(0.dp, 5.dp)
            )
        }

        AndroidView(
            factory = {
                flex
            }, modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .padding(10.dp, 0.dp)
        )
    }
}

@Composable
fun getButtonModifier(selectedButton: Int, buttonId: Int): Modifier {
    return if (selectedButton == buttonId) {
        Modifier
            .padding(0.dp, 4.dp)
            .border(1.dp, colorResource(id = R.color.C0F0F5C))
    } else {
        Modifier.padding(0.dp, 4.dp)
    }
}

@Composable
fun getTextColor(selectedButton: Int, buttonId: Int): Color {
    return if (selectedButton == buttonId) {
        colorResource(id = R.color.C0F0F5C)
    } else {
        Color.LightGray
    }
}

@Composable
fun CoinInfoProgressDialog(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    if (coinDetailViewModel.coinInfoDialog.value) {
        Dialog(
            onDismissRequest = { coinDetailViewModel.dialogState = false },
            DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(colorResource(id = R.color.design_default_color_background))
            ) {
                Column {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(0.dp, 20.dp, 0.dp, 0.dp),
                        color = colorResource(id = R.color.C0F0F5C)
                    )
                    Text(text = "코인정보 불러오는 중...", Modifier.padding(20.dp, 8.dp, 20.dp, 15.dp))
                }
            }
        }
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