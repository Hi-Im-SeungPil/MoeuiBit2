package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine

@Composable
fun PortfolioPieChart(
    pieChartState: MutableState<Boolean>,
    userSeedMoney: MutableState<Long>,
    userHoldCoinList: List<MyCoin?>,
) {
    val imageVector = remember {
        mutableStateOf(Icons.Filled.KeyboardArrowDown)
    }
    if (pieChartState.value) {
        imageVector.value = Icons.Filled.KeyboardArrowUp
    } else {
        imageVector.value = Icons.Filled.KeyboardArrowDown
    }

    Column(modifier = Modifier.wrapContentSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = MaterialTheme.colorScheme.background)
                .drawUnderLine(lineColor = Color.Gray, strokeWidth = 2f)
                .clickable { pieChartState.value = !pieChartState.value }
        ) {
            Text(
                text = stringResource(id = R.string.holdingAssetsPortfolio), modifier = Modifier
                    .padding(8.dp, 8.dp, 0.dp, 8.dp)
                    .weight(1f, true)
                    .align(Alignment.CenterVertically), style = TextStyle(fontSize = DpToSp(16.dp), color = MaterialTheme.colorScheme.primary)
            )
            Icon(
                imageVector = imageVector.value,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp, 8.dp)
                    .fillMaxHeight()
            )
        }
        if (pieChartState.value) {
            HoldCoinPieChart(
                userSeedMoney = userSeedMoney.value,
                userHoldCoinList = userHoldCoinList
            )
        }
    }
}