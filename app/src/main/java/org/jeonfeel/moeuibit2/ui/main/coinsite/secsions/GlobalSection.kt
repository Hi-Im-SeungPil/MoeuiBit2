package org.jeonfeel.moeuibit2.ui.main.coinsite.secsions

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.dvkyun.flexhybridand.FlexWebView
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun GlobalSection(navigateDominanceChart: (String, String) -> Unit) {
    val context = LocalContext.current
    val state = remember {
        mutableStateOf("BTC.D")
    }
    val horizontalScroll = rememberScrollState()

    val list = listOf(
        "BTC.D",
        "ETH.D",
        "XRP.D",
        "SOL.D",
        "USDT.D",
        "USDC.D",
        "OTHERS.D"
    )

    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
            .padding(vertical = 20.dp, horizontal = 10.dp),
    ) {

        Text(
            text = "코인 도미넌스",
            modifier = Modifier.padding(horizontal = 10.dp),
            style = TextStyle(
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W500,
                color = commonTextColor()
            )
        )

        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScroll)
        ) {
            list.forEach {
                Text(
                    text = it, modifier = Modifier
                        .padding(top = 10.dp, end = 10.dp)
                        .clickable { state.value = it },
                    color = if (state.value == it) APP_PRIMARY_COLOR else commonHintTextColor(),
                    fontSize = DpToSp(15.dp)
                )
            }
        }

        BitcoinDominanceChart(state.value)
    }
}

@Composable
fun BitcoinDominanceChart(value: String) {
    val context = LocalContext.current
    val width = (getScreenWidthPx(context) - context.dpToPx(49f)).toInt()
    val height = context.dpToPx(250f).toInt()
    val isDark = isSystemInDarkTheme()

    val webView = remember {
        FlexWebView(context).apply {
            settings.apply {
                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false
                loadWithOverviewMode = false // 콘텐츠가 WebView 크기에 맞게 조정됨
                useWideViewPort = false
            }
            setInitialScale(100)
            setPadding(0, 0, 0, 0)
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY;
        }
    }

    Box(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .height(250.dp)// 부모 컨테이너 크기 꽉 채우기
    ) {
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize(), // WebView 크기 꽉 채우기
            update = {
                it.loadDataWithBaseURL(
                    "https://www.tradingview.com",
                    getTradingViewHtml(width, height, isDark, value),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        )
    }
}

fun getTradingViewHtml(width: Int, height: Int, isDark: Boolean, symbol: String): String {
    val isDarkMode = if (isDark) {
        "dark"
    } else {
        "light"
    }

    return """
    <!-- TradingView Widget BEGIN -->
    <html>
    <head>
    <meta name="viewport",content="width=device-width, initial-scale=1.0, user-scalable=no" />
    <style>
      * { margin: 0; padding: 0; box-sizing: border-box; }
      body, html { width: ${width}; height: ${height}; overflow: hidden; }
      .tradingview-widget-container { width: ${width}; height: ${height}; }
      .tradingview-widget-container__widget { width: ${width}; height: ${height}; }
    </style>
    </head>
    <body style="margin: 0; padding: 0">
      <div class="tradingview-widget-container">
        <div class="tradingview-widget-container__widget"></div>
        <script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-symbol-overview.js" async>
        {
          "symbols": [
            [
              "CRYPTOCAP:${symbol}|1D" 
            ]
          ],
          "chartOnly": false,
          "width": "${width}",
          "height": "${height}",
          "locale": "kr",
          "colorTheme": "$isDarkMode",
          "autosize": false,
          "showVolume": false,
          "showMA": false,
          "hideDateRanges": false,
          "hideMarketStatus": false,
          "hideSymbolLogo": false,
          "scalePosition": "right",
          "scaleMode": "Normal",
          "fontFamily": "-apple-system, BlinkMacSystemFont, Trebuchet MS, Roboto, Ubuntu, sans-serif",
          "fontSize": "28",
          "headerFontSize": "large",
          "lineWidth": 2,
          "lineType": 0,
          "dateRanges": [
            "1d|1",
            "1m|30",
            "3m|60",
            "12m|1D",
            "60m|1W",
            "all|1M"
          ],
          "dateFormat": "yyyy-MM-dd"
        }
        </script>
      </div>
    </body>
    </html>
    """.trimIndent()
}

@Composable
fun getUsableScreenHeight(): Int {
    val context = LocalContext.current
    val resources = context.resources
    val displayMetrics = resources.displayMetrics

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics
        val insets =
            windowMetrics.windowInsets.getInsetsIgnoringVisibility(android.view.WindowInsets.Type.systemBars())
        windowMetrics.bounds.height() - insets.top - insets.bottom
    } else {
        val rect = Rect()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = windowManager.defaultDisplay
        defaultDisplay.getRectSize(rect)
        rect.height()
    }
}

fun pxToDp(context: Context, px: Float): Float {
    val density = context.resources.displayMetrics.density
    return px / density
}

fun getScreenWidthPx(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics
        windowMetrics.bounds.width()
    } else {
        val displayMetrics = context.resources.displayMetrics
        displayMetrics.widthPixels
    }
}

fun Context.dpToPx(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

//@Composable
//private fun DominanceItem(imgId: Int, coinName: String, dominance: String, onClick: () -> Unit) {
//    Row(modifier = Modifier
//        .padding(top = 10.dp)
//        .fillMaxWidth()
//        .noRippleClickable { onClick.invoke() }) {
//        Image(
//            painter = painterResource(imgId),
//            modifier = Modifier
//                .size(30.dp)
//                .clip(CircleShape)
//                .border(width = 1.dp, color = Color(0xFFE8E8E8), shape = CircleShape)
//                .background(Color.White)
//                .align(Alignment.CenterVertically),
//            contentDescription = ""
//        )
//
//        Text(
//            text = "$coinName 도미넌스",
//            modifier = Modifier
//                .padding(start = 10.dp)
//                .align(Alignment.CenterVertically),
//            style = TextStyle(fontSize = DpToSp(13.dp))
//        )
//
//        Text(
//            text = dominance, modifier = Modifier
//                .padding(start = 10.dp)
//                .weight(1f)
//                .align(Alignment.CenterVertically),
//            style = TextStyle(fontSize = DpToSp(13.dp))
//        )
//
//        Text(
//            text = "차트",
//            modifier = Modifier.align(Alignment.CenterVertically),
//            style = TextStyle(fontSize = DpToSp(11.dp))
//        )
//        Icon(
//            Icons.AutoMirrored.Filled.KeyboardArrowRight,
//            "",
//            modifier = Modifier.align(Alignment.CenterVertically),
//            tint = commonTextColor()
//        )
//    }
//
//    Divider(
//        modifier = Modifier
//            .padding(top = 10.dp, start = 10.dp)
//            .height(1.dp)
//            .fillMaxWidth()
//            .background(color = commonDividerColor())
//    )
//}
//
//@Composable
//fun GlobalInfoItem(
//    title: String,
//    value: String,
//    change: String,
//) {
//    Column(modifier = Modifier.padding(top = 10.dp)) {
//        Text(title)
//        Text(value)
//        Row {
//            Text("24시간 등락")
//            Text(change)
//        }
//    }
//}