package org.jeonfeel.moeuibit2.utils.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.common.MarketChangeState
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonFallColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonRiseColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.exchange.exchangeEvenColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.exchange.exchangeFallColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.exchange.exchangeRiseColor

private var appToast: Toast? = null

/**
 * 토스트 보여줌
 */
fun Context.showToast(text: String) {
    appToast?.cancel()
    appToast = Toast.makeText(this, text, Toast.LENGTH_SHORT).also { it.show() }
}

/**
 * url로 이동
 */
fun Context.moveUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    this.startActivity(intent)
}

fun List<String>.mapToMarketCodesRequest(): String {
    val builder = StringBuilder()
    forEachIndexed { index, s ->
        if (index == 0) builder.append(s) else builder.append(",$s")
    }
    return builder.toString()
}

fun String.convertMarketChangeState(): MarketChangeState {
    return if (equals("RISE")) {
        MarketChangeState.Rise
    } else if (equals("FALL")) {
        MarketChangeState.Fall
    } else {
        MarketChangeState.Even
    }
}

@Composable
fun Int.toDp() {
    return with(LocalDensity.current) {
        this@toDp.dp.toSp()
    }
}

@Composable
fun Float.getFluctuateColor() =
    if (this > 0f) {
        commonRiseColor()
    } else if (this < 0f) {
        commonFallColor()
    } else {
        commonTextColor()
    }

val RiseColor = Color(0xFFE15241)
val FallColor = Color(0xFF4A80EA)
val EvenColor = Color(0xFF000000)