package org.jeonfeel.moeuibit2.ui.custom

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel

@Composable
fun AskBidDialog(visible: Boolean, coinDetailViewModel: CoinDetailViewModel) {
    if (visible) {
        AlertDialog(
            onDismissRequest = { coinDetailViewModel.askBidDialogState = false },
            text = {
                Card(Modifier.wrapContentSize()) {
                    AskBidDialogContent()
                }
            },
            dismissButton = {},
            confirmButton = {},
            backgroundColor = Color.Transparent
        )
    }
}

@Composable
fun AskBidDialogContent() {
    Column(
        modifier = Modifier
            .padding(30.dp, 0.dp)
            .wrapContentHeight()
    ) {
        Text(text = "금액을 입력해 주세요!")
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "취소")
            Text(text = "확인")
        }
    }
}