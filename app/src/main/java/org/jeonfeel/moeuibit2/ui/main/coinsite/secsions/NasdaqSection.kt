package org.jeonfeel.moeuibit2.ui.main.coinsite.secsions

import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.dvkyun.flexhybridand.FlexWebView
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun NasdaqSection() {
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
            .padding(vertical = 20.dp, horizontal = 10.dp),
    ) {

        Text(
            text = "나스닥",
            modifier = Modifier.padding(horizontal = 10.dp),
            style = TextStyle(
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W500,
                color = commonTextColor()
            )
        )

        NasdaqIndexChart()
    }
}

@Composable
private fun NasdaqIndexChart() {
    val context = LocalContext.current
    val width = (getScreenWidthPx(context) - context.dpToPx(49f)).toInt()
    val height = context.dpToPx(250f).toInt()
    val isDark = isSystemInDarkTheme()

    val frameLayout = remember {
        FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    val webView = remember {
        FlexWebView(context).apply {
            settings.apply {
                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false
                loadWithOverviewMode = true // 콘텐츠가 WebView 크기에 맞게 조정됨
                useWideViewPort = false
            }
            setInitialScale(400)
            setPadding(0, 0, 0, 0)
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY;
        }
    }

    Box(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        AndroidView(
            factory = {
                frameLayout.addView(webView)
                frameLayout
            },
            modifier = Modifier
                .background(color = Color.Transparent, shape = RoundedCornerShape(10.dp))
                .fillMaxSize(), // WebView 크기 꽉 채우기
            update = {
                (it.getChildAt(0) as WebView).loadDataWithBaseURL(
                    "https://www.tradingview.com",
                    getTradingViewHtml(width, height, isDark),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        )
    }
}

private fun getTradingViewHtml(width: Int, height: Int, isDark: Boolean): String {
    val isDarkMode = if (isDark) {
        "dark"
    } else {
        "light"
    }

    return """
        <div class="tradingview-widget-container">
          <div class="tradingview-widget-container__widget"></div>
          <script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-single-quote.js" async>
          {
          "symbol": "NASDAQ:IXIC",
          "width": 500,
          "isTransparent": false,
          "colorTheme": "$isDarkMode",
          "locale": "kr"
        }
          </script>
        </div>
    """.trimIndent()
}