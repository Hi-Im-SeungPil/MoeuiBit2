package org.jeonfeel.moeuibit2.util

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.view.View
import android.widget.Toast

fun Context.showToast(text: String) {
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}

fun Context.moveUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    this.startActivity(intent)
}