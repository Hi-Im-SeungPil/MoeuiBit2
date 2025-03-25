package org.jeonfeel.moeuibit2.ui.main.coinsite.secsions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.main.coinsite.component.AnimatedHalfGauge
import org.jeonfeel.moeuibit2.ui.main.coinsite.ui_model.FearAndGreedyUIModel
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun FearAndGreedySection(fearAndGreedyUIModel: FearAndGreedyUIModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
            .padding(vertical = 20.dp, horizontal = 20.dp),
    ) {
        Column {
            Box {
                AnimatedHalfGauge(
                    fearGreedIndex = fearAndGreedyUIModel.index,
                    fearGreedyIndexDescription = fearAndGreedyUIModel.indexDescription
                )
                Text(
                    text = "Powered by Alternative",
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .align(Alignment.BottomEnd),
                    fontSize = DpToSp(10.dp),
                    color = commonHintTextColor()
                )
            }
        }
    }
}