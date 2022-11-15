package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import android.graphics.Bitmap
import android.os.Message
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.dvkyun.flexhybridand.FlexWebChromeClient
import app.dvkyun.flexhybridand.FlexWebView
import app.dvkyun.flexhybridand.FlexWebViewClient
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewState
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.util.moveUrl

@Composable
fun CoinInfoContent(
    selected: MutableState<String>,
    selectedButton: MutableState<Int>,
    coinInfoHashMap: MutableState<HashMap<String, String>>,
//    webViewState: WebViewState,
    flex: FlexWebView,
    webViewLoading: MutableState<Boolean>
) {
    val context = LocalContext.current
    val state = rememberWebViewState(url = "")
//    val moeuiBitWebView = MoeuiBitWebView(webViewState = webViewState)
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            MoveUrlText(text = stringResource(id = R.string.block), clickAction = {
                selected.value = coinInfoHashMap.value[INFO_BLOCK_KEY]!!
                context.moveUrl(selected.value)
            })
            MoveUrlText(text = stringResource(id = R.string.homePage), clickAction = {
                selected.value = coinInfoHashMap.value[INFO_HOMEPAGE_KEY]!!
                context.moveUrl(selected.value)
            })
            MoveUrlText(text = stringResource(id = R.string.info), clickAction = {
                selected.value = coinInfoHashMap.value[INFO_INFO_KEY]!!
                context.moveUrl(selected.value)
            })
            LoadWebViewText(text = stringResource(id = R.string.twitter), clickAction = {
                selectedButton.value = 4
                selected.value = coinInfoHashMap.value[INFO_TWITTER_KEY]!!
//                state.content = WebContent.Data(data = twitterUrl(selected.value))
//                webViewState.content = WebContent.Data(data = twitterUrl(selected.value))
                flex.loadData(
                    twitterUrl(selected.value),
                    "text/html; charset=utf-8",
                    "UTF-8"
                )
            }, selectedButton = selectedButton, buttonId = 4)
            LoadWebViewText(text = stringResource(id = R.string.amount), clickAction = {
                selectedButton.value = 5
                selected.value = coinInfoHashMap.value[INFO_AMOUNT_KEY]!!
//                state.content = WebContent.Url(
//                    url = selected.value,
//                    emptyMap()
//                )
//                webViewState.content = WebContent.Url(
//                    url = selected.value,
//                    emptyMap()
//                )
                flex.loadUrl(selected.value)
            }, selectedButton = selectedButton, buttonId = 5)
        }

        if (selectedButton.value == -1) {
            GlideImage(
                imageModel = R.drawable.img_default_wv, modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .padding(10.dp, 0.dp, 10.dp, 10.dp)
            )
        }
//        else {
//            AndroidView(
//                factory = {
//                    Button(it)
//                }, modifier = Modifier
//                    .fillMaxHeight()
//                    .wrapContentWidth()
//                    .padding(10.dp, 0.dp)
//                    .background(Color.Black)
//            )
//        }
        else if (selectedButton.value != -1 && flex.url != null
            || flex.url == null && selectedButton.value == 4
            || flex.url == null && selectedButton.value == 5) {
            if(!webViewLoading.value) {
                AndroidView(
                    factory = {
                        flex
                    }, modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .padding(10.dp, 0.dp)
                        .background(Color.Transparent)
                )
            }
        }
//            WebView(
//                state = state ,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight()
//                    .background(Color.Black),
//                client = AccompanistWebViewClient(),
//                chromeClient = object: AccompanistWebChromeClient() {
//                    override fun onCreateWindow(
//                        view: WebView?,
//                        dialog: Boolean,
//                        userGesture: Boolean,
//                        resultMsg: Message,
//                    ): Boolean {
//                        val newWebView = WebView(context)
//                        val trans = resultMsg.obj as WebView.WebViewTransport
//                        trans.webView = newWebView
//                        resultMsg.sendToTarget()
//                        return true
//                    }
//                },
//                onCreated = {
//                    with(it) {
//                        settings.run {
//                            javaScriptEnabled = true
//                            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
//                            useWideViewPort = false
//                            loadWithOverviewMode = true
//                            domStorageEnabled = true
//                            setSupportMultipleWindows(true)
//                           javaScriptCanOpenWindowsAutomatically = true
//                            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//                        }
//                    }
//                }
//            )
    }
}

@Composable
fun RowScope.MoveUrlText(text: String, clickAction: () -> Unit) {
    Text(
        text = text,
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
                clickAction()
            }
            .padding(0.dp, 5.dp)
    )
}

@Composable
fun RowScope.LoadWebViewText(
    text: String,
    clickAction: () -> Unit,
    selectedButton: MutableState<Int>,
    buttonId: Int,
) {
    Text(
        text = text,
        fontSize = 14.sp,
        style = TextStyle(
            color = getTextColor(selectedButton.value, buttonId),
            textAlign = TextAlign.Center
        ),
        modifier = getButtonModifier(selectedButton.value, buttonId)
            .weight(1f)
            .wrapContentHeight()
            .clickable {
                clickAction()
            }
            .padding(0.dp, 5.dp)
    )
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
fun MoeuiBitWebView(webViewState: WebViewState) {

}

fun FlexWebView.initFlex(webViewLoading: MutableState<Boolean>) {
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
    flex.webViewClient = object : FlexWebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            webViewLoading.value = true;
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            webViewLoading.value = false;
        }
    }
}