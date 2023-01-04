package org.jeonfeel.moeuibit2.ui.main.setting

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.apache2
import org.jeonfeel.moeuibit2.constants.bsd_2
import org.jeonfeel.moeuibit2.constants.bsd_3
import org.jeonfeel.moeuibit2.constants.mit

@Composable
fun OpenSourceLicenseLazyColumn() {
    val openSourceNames = stringArrayResource(id = R.array.openSourceNames)
    val openSourceUrls = stringArrayResource(id = R.array.openSourceUrl)
    val openSourceContents = stringArrayResource(id = R.array.openSourceLicense)
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Intro()
        }
        itemsIndexed(openSourceNames) { index, _ ->
            OpenSourceLicenseLazyColumnItem(openSourceNames[index],
                openSourceUrls[index],
                openSourceContents[index])
        }
        item{
            OpenSourceLicenseLazyColumnItem("Apache License 2.0","", apache2)
            OpenSourceLicenseLazyColumnItem("MIT License","", mit)
            OpenSourceLicenseLazyColumnItem("2-clause BSD license (BSD-2-Clause)","", bsd_2)
            OpenSourceLicenseLazyColumnItem("3-Clause BSD License (BSD-3-Clause)","", bsd_3)
        }
    }
}

@Composable
fun OpenSourceLicenseLazyColumnItem(
    openSourceName: String,
    opensourceUrl: String,
    openSourceLicense: String,
) {
    val context = LocalContext.current
    Column(modifier = Modifier
        .padding(10.dp, 10.dp, 10.dp, 0.dp)
        .fillMaxWidth()
        .wrapContentHeight()
    ) {

        if(opensourceUrl.isEmpty()) {
            Divider(color = Color.Magenta, thickness = 1.dp, modifier = Modifier.padding(0.dp,0.dp,0.dp,10.dp))
        }

        Text(text = openSourceName,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp)

        if(opensourceUrl.isNotEmpty()){
            Text(text = opensourceUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(opensourceUrl)))
                    },
                fontSize = 15.sp,
                style = TextStyle(color = Color.Blue),
                textDecoration = TextDecoration.Underline)
        }

        Text(text = openSourceLicense,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 15.sp)
    }
}

@Composable
fun Intro() {
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
        style = TextStyle(fontSize = 15.sp))
}

