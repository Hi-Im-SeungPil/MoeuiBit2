package org.jeonfeel.moeuibit2.ui.main.setting

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.apache2
import org.jeonfeel.moeuibit2.constants.bsd_2
import org.jeonfeel.moeuibit2.constants.bsd_3
import org.jeonfeel.moeuibit2.constants.mit
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.decreaseColor

@Composable
fun OpenSourceLicenseLazyColumn(openSourceState: MutableState<Boolean>) {
    val openSourceNames = stringArrayResource(id = R.array.openSourceNames)
    val openSourceUrls = stringArrayResource(id = R.array.openSourceUrl)
    val openSourceContents = stringArrayResource(id = R.array.openSourceLicense)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        item {
            ActionBar(openSourceState)
            Intro()
        }
        itemsIndexed(openSourceNames) { index, _ ->
            OpenSourceLicenseLazyColumnItem(
                openSourceNames[index],
                openSourceUrls[index],
                openSourceContents[index]
            )
        }
        item {
            OpenSourceLicenseLazyColumnItem("Apache License 2.0", "", apache2)
            OpenSourceLicenseLazyColumnItem("MIT License", "", mit)
            OpenSourceLicenseLazyColumnItem("2-clause BSD license (BSD-2-Clause)", "", bsd_2)
            OpenSourceLicenseLazyColumnItem("3-Clause BSD License (BSD-3-Clause)", "", bsd_3)
        }
    }
}

@Composable
private fun OpenSourceLicenseLazyColumnItem(
    openSourceName: String,
    opensourceUrl: String,
    openSourceLicense: String,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(10.dp, 10.dp, 10.dp, 0.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        if (opensourceUrl.isEmpty()) {
            Divider(
                color = Color.Magenta,
                thickness = 1.dp,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp)
            )
        }

        Text(
            text = openSourceName,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            fontSize = DpToSp(17.dp),
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )

        if (opensourceUrl.isNotEmpty()) {
            Text(
                text = opensourceUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(opensourceUrl)))
                    },
                fontSize = DpToSp(15.dp),
                style = TextStyle(color = decreaseColor()),
                textDecoration = TextDecoration.Underline
            )
        }

        Text(
            text = openSourceLicense,
            modifier = Modifier.fillMaxWidth(),
            fontSize = DpToSp(15.dp),
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}

@Composable
private fun Intro() {
    Text(text = stringResource(id = R.string.openSourceIntro),
        modifier = Modifier
            .drawWithContent {
                drawContent()
                clipRect {
                    val strokeWidth = 4f
                    val y = size.height
                    drawLine(
                        brush = SolidColor(Color.Magenta),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Square,
                        start = Offset.Zero.copy(y = y),
                        end = Offset(x = size.width, y = y)
                    )
                }
            }
            .padding(10.dp, 10.dp, 10.dp, 10.dp),
        style = TextStyle(fontSize = DpToSp(15.dp), color = MaterialTheme.colorScheme.onBackground))
}

@Composable
private fun ActionBar(openSourceState: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        IconButton(onClick = { openSourceState.value = false }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = "오픈소스 라이선스",
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterVertically),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = DpToSp(dp = 20.dp),
                textAlign = TextAlign.Center
            )
        )
    }
}

