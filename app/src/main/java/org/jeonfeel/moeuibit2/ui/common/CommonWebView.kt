package org.jeonfeel.moeuibit2.ui.common

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.dvkyun.flexhybridand.FlexWebView
import com.orhanobut.logger.Logger


@Composable
fun CommonWebView(
    title: String,
    symbol: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* 뒤로가기 로직 */ }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.W600)
            )
        }
        BitcoinDominanceChart()
    }
}

@Composable
fun BitcoinDominanceChart() {
    val context = LocalContext.current
    val screenSize = getUsableScreenHeight()
    val pxToDp = pxToDp(context, screenSize.toFloat())
    val isDark = isSystemInDarkTheme()

    val webView = remember {
        FlexWebView(context).apply {
            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()  // 부모 컨테이너 크기 꽉 채우기
            .padding(0.dp)  // 여백 제거
    ) {
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize(), // WebView 크기 꽉 채우기
            update = {
                it.loadDataWithBaseURL(
                    "https://www.tradingview.com",
                    getTradingViewHtml(screenSize.toInt(), isDrak = isDark),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        )
    }
}

fun getTradingViewHtml(screenSize: Int, isDrak: Boolean): String {
    val isDarkMode = if (isDrak) {
        "dark"
    } else {
        "light"
    }

    return """
<!-- TradingView Widget BEGIN -->
<div class="tradingview-widget-container">
  <div class="tradingview-widget-container__widget"></div>
  <div class="tradingview-widget-copyright"><a href="https://kr.tradingview.com/" rel="noopener nofollow" target="_blank"><span class="blue-text">트레이딩뷰에서 모든 시장 추적</span></a></div>
  <script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-symbol-overview.js" async>
  {
  "symbols": [
    [
      "Apple",
      "AAPL|1D|KRW"
    ],
    [
      "Google",
      "GOOGL|1D|KRW"
    ],
    [
      "Microsoft",
      "MSFT|1D|KRW"
    ]
  ],
  "chartOnly": false,
  "width": "1000",
  "height": "1800",
  "locale": "kr",
  "colorTheme": "dark",
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
  "noTimeScale": false,
  "valuesTracking": "1",
  "changeMode": "price-and-percent",
  "chartType": "area",
  "maLineColor": "#2962FF",
  "maLineWidth": 1,
  "maLength": 9,
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
  ]
}
  </script>
</div>
<!-- TradingView Widget END -->
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