package org.jeonfeel.moeuibit2.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import org.jeonfeel.moeuibit2.ui.common.MarketChangeState

fun Context.showToast(text: String) {
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}

fun Context.moveUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    this.startActivity(intent)
}

fun Context.intentActivity(cls: Class<*>) {
    val intent = Intent(this, cls)
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