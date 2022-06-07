package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.SELECTED_FAVORITE
import org.jeonfeel.moeuibit2.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.activity.main.MainViewModel

@Composable
fun MarketButtons(mainViewModel: MainViewModel = viewModel()) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .drawWithContent {
                drawContent()
                clipRect {
                    val strokeWidth = Stroke.DefaultMiter
                    val y = size.height
                    drawLine(
                        brush = SolidColor(Color.DarkGray),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Square,
                        start = Offset.Zero.copy(y = y),
                        end = Offset(x = size.width, y = y)
                    )
                }
            }
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    mainViewModel.showFavorite.value = false
                    mainViewModel.selectedMarket.value = SELECTED_KRW_MARKET
                }
        ) {
            Text(
                text = "원화",
                modifier = Modifier.fillMaxWidth()
                ,style = TextStyle(
                    color = getTextColor(
                        mainViewModel,
                        SELECTED_KRW_MARKET
                    ), fontSize = 17.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                )
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    mainViewModel.showFavorite.value = true
                    mainViewModel.selectedMarket.value = SELECTED_FAVORITE
                }
        ) {
            Text(
                text = "관심",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    color = getTextColor(
                        mainViewModel,
                        SELECTED_FAVORITE
                    ), fontSize = 17.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                )
            )
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
        ) {
            Text(text = "")
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
        ) {
            Text(text = "")
        }
    }
}

@Composable
fun getTextColor(mainViewModel: MainViewModel = viewModel(), buttonId: Int): Color {
    return if (mainViewModel.selectedMarket.value == buttonId) {
        colorResource(R.color.C0F0F5C)
    } else {
        Color.LightGray
    }
}