package org.jeonfeel.moeuibit2.ui.main.additional_features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AverageCostCalculator() {
    // 입력값과 결과를 위한 상태 변수
    var existingAveragePrice by remember { mutableStateOf("") }
    var existingTotalAmount by remember { mutableStateOf("") }
    var newBuyPrice by remember { mutableStateOf("") }
    var newBuyAmount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    // 내부 컨텐츠를 깔끔하게 보여주기 위한 Column (scrollable하지 않음)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "평균 단가 계산기",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = existingAveragePrice,
            onValueChange = { existingAveragePrice = it },
            label = { Text("기존 매수 평균 단가") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = existingTotalAmount,
            onValueChange = { existingTotalAmount = it },
            label = { Text("기존 총액") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = newBuyPrice,
            onValueChange = { newBuyPrice = it },
            label = { Text("새로 살 매수 단가") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = newBuyAmount,
            onValueChange = { newBuyAmount = it },
            label = { Text("새로 살 총액") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                try {
                    val existingAvg = existingAveragePrice.toDouble()
                    val existingTotal = existingTotalAmount.toDouble()
                    val newBuy = newBuyPrice.toDouble()
                    val newAmount = newBuyAmount.toDouble()

                    result = if (existingAvg == 0.0 || newBuy == 0.0) {
                        "단가는 0이 될 수 없습니다."
                    } else {
                        val existingQuantity = existingTotal / existingAvg
                        val newQuantity = newAmount / newBuy
                        val totalQuantity = existingQuantity + newQuantity
                        val totalAmount = existingTotal + newAmount
                        String.format("%.2f", totalAmount / totalQuantity)
                    }
                } catch (e: NumberFormatException) {
                    result = "잘못된 입력입니다."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("계산하기")
        }

        Text(
            text = "새로운 매수 평균 단가: $result",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}