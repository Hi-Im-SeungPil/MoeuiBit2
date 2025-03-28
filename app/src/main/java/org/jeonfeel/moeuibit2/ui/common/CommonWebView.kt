package org.jeonfeel.moeuibit2.ui.common

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.dvkyun.flexhybridand.FlexWebChromeClient
import app.dvkyun.flexhybridand.FlexWebView
import app.dvkyun.flexhybridand.FlexWebViewClient
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor

@Composable
fun CommonWebView(
    title: String,
    symbol: String,
) {
    val context = LocalContext.current
    val iframeHtml = """
        <!-- TradingView Widget BEGIN -->
<div class="tradingview-widget-container" style="height:100%;width:100%">
  <div class="tradingview-widget-container__widget" style="height:calc(100% - 32px);width:100%"></div>
  <div class="tradingview-widget-copyright"><a href="https://kr.tradingview.com/" rel="noopener nofollow" target="_blank"><span class="blue-text">트레이딩뷰에서 모든 시장 추적</span></a></div>
  <script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-advanced-chart.js" async>
  {
  "autosize": true,
  "symbol": "CRYPTOCAP:BTC.D",
  "interval": "D",
  "timezone": "Asia/Seoul",
  "theme": "light",
  "style": "2",
  "locale": "kr",
  "allow_symbol_change": true,
  "support_host": "https://www.tradingview.com"
}
  </script>
</div>
<!-- TradingView Widget END -->
    """

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            webChromeClient = object : WebChromeClient() {}
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    // origin을 명확하게 설정하는 JavaScript 실행
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = {
                webView.apply {
                    loadDataWithBaseURL("https://www.tradingview.com", iframeHtml, "text/html", "UTF-8", null)
                }
            }
        )
    }
}