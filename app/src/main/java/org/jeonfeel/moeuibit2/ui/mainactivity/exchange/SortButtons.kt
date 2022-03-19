package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SortButtons() {
    val selectedButtonState = remember { mutableStateOf(-1) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    ) {
        Text(modifier = Modifier.weight(1f), text = "")

        Button(onClick = {
            when {
                selectedButtonState.value != 0 && selectedButtonState.value != 1 -> {
                    selectedButtonState.value = 0
                }
                selectedButtonState.value == 0 -> {
                    selectedButtonState.value = 1
                }
                else -> {
                    selectedButtonState.value = -1
                }
            }
        }, modifier = Modifier.weight(1f)) {
            when (selectedButtonState.value) {
                0 -> {
                    Text(text = "현재가↓", fontSize = 10.sp)
                }
                1 -> {
                    Text(text = "현재가↑", fontSize = 10.sp)
                }
                else -> {
                    Text(text = "현재가↓↑", fontSize = 10.sp)
                }
            }
        }

        Button(onClick = {
            when {
                selectedButtonState.value != 2 && selectedButtonState.value != 3 -> {
                    selectedButtonState.value = 2
                }
                selectedButtonState.value == 2 -> {
                    selectedButtonState.value = 3
                }
                else -> {
                    selectedButtonState.value = -1
                }
            }
        }, modifier = Modifier.weight(1f)) {
            when (selectedButtonState.value) {
                2 -> {
                    Text(text = "전일대비↓", fontSize = 10.sp, maxLines = 1)
                }
                3 -> {
                    Text(text = "전일대비↑", fontSize = 10.sp, maxLines = 1)
                }
                else -> {
                    Text(text = "전일대비↓↑", fontSize = 10.sp, maxLines = 1)
                }
            }
        }

        Button(onClick = {
            when {
                selectedButtonState.value != 4 && selectedButtonState.value != 5 -> {
                    selectedButtonState.value = 4
                }
                selectedButtonState.value == 4 -> {
                    selectedButtonState.value = 5
                }
                else -> {
                    selectedButtonState.value = -1
                }
            }
        }, modifier = Modifier.weight(1f)) {
            when (selectedButtonState.value) {
                4 -> {
                    Text(text = "거래량↓", fontSize = 10.sp)
                }
                5 -> {
                    Text(text = "거래량↑", fontSize = 10.sp)
                }
                else -> {
                    Text(text = "거래량↓↑", fontSize = 10.sp)
                }
            }
        }
    }
}