package org.jeonfeel.moeuibit2.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.dvkyun.flexhybridand.FlexWebView
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor

@Composable
fun CommonWebView(
    title: String,
    url: String
) {
    val context = LocalContext.current
    val flex = remember {
        FlexWebView(context)
    }

    LaunchedEffect(true) {
        flex.loadUrl(url)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.background(commonBackground())) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "",
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    .align(Alignment.CenterVertically),
            )

            Text(
                text = title,
                modifier = Modifier
                    .padding(30.dp, 0.dp, 0.dp, 0.dp)
                    .weight(1f, true)
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    color = commonTextColor(),
                    fontSize = DpToSp(20.dp),
                    fontWeight = FontWeight.W600
                )
            )

            Text(
                text = "",
                modifier = Modifier
                    .padding(21.dp)
                    .wrapContentWidth(),
                style = TextStyle(
                    color = commonTextColor(),
                    fontSize = DpToSp(dp = 13.dp)
                )
            )
        }

        AndroidView(
            modifier = Modifier.weight(1f),
            factory = {
                flex
            }
        )
    }
}