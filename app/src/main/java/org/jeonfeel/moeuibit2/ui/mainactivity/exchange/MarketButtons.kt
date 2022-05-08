package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.SELECTED_FAVORITE
import org.jeonfeel.moeuibit2.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@Composable
fun MarketButtons(exchangeViewModel: ExchangeViewModel = viewModel()) {
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
        TextButton(
            onClick = {
                exchangeViewModel.showFavorite.value = false
                exchangeViewModel.selectedMarket.value = SELECTED_KRW_MARKET
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
        ) {
            Text(text = "원화", style = TextStyle(color = getTextColor(exchangeViewModel,
                SELECTED_KRW_MARKET), fontSize = 17.sp, fontWeight = FontWeight.Bold
            ))
        }
        TextButton(
            onClick = {
                exchangeViewModel.showFavorite.value = true
                exchangeViewModel.selectedMarket.value = SELECTED_FAVORITE
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
        ) {
            Text(text = "관심", style = TextStyle(color = getTextColor(exchangeViewModel,
                SELECTED_FAVORITE), fontSize = 17.sp, fontWeight = FontWeight.Bold))
        }
        TextButton(
            onClick = {
//                exchangeViewModel.showFavorite.value = false
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
        ) {
            Text(text = "")
        }
        TextButton(
            onClick = {
//                exchangeViewModel.showFavorite.value = false
            },
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
fun getTextColor(exchangeViewModel: ExchangeViewModel = viewModel(),buttonId: Int): Color {
    return if(exchangeViewModel.selectedMarket.value == buttonId) {
        colorResource(R.color.C0F0F5C)
    } else {
        Color.LightGray
    }
}