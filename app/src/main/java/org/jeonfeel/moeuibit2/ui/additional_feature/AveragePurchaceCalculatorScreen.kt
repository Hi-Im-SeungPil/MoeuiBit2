package org.jeonfeel.moeuibit2.ui.additional_feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AverageCostCalculator() {
    // 입력값과 결과를 위한 상태 변수
    var existingAveragePrice by remember { mutableStateOf("") }
    var existingTotalAmount by remember { mutableStateOf("") }
    var newBuyPrice by remember { mutableStateOf("") }
    var newBuyAmount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    // UI 레이아웃 (세로로 배치)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 기존 매수 평균 단가 입력 필드
        OutlinedTextField(
            value = existingAveragePrice,
            onValueChange = { existingAveragePrice = it },
            label = { Text("기존 매수 평균 단가") },
            modifier = Modifier.fillMaxWidth()
        )

        // 기존 총액 입력 필드
        OutlinedTextField(
            value = existingTotalAmount,
            onValueChange = { existingTotalAmount = it },
            label = { Text("기존 총액") },
            modifier = Modifier.fillMaxWidth()
        )

        // 새로 살 매수 단가 입력 필드
        OutlinedTextField(
            value = newBuyPrice,
            onValueChange = { newBuyPrice = it },
            label = { Text("새로 살 매수 단가") },
            modifier = Modifier.fillMaxWidth()
        )

        // 새로 살 총액 입력 필드
        OutlinedTextField(
            value = newBuyAmount,
            onValueChange = { newBuyAmount = it },
            label = { Text("새로 살 총액") },
            modifier = Modifier.fillMaxWidth()
        )

        // 계산 버튼
        Button(
            onClick = {
                try {
                    // 입력값을 Double로 변환
                    val existingAvg = existingAveragePrice.toDouble()
                    val existingTotal = existingTotalAmount.toDouble()
                    val newBuy = newBuyPrice.toDouble()
                    val newAmount = newBuyAmount.toDouble()

                    // 단가가 0인지 확인
                    if (existingAvg == 0.0 || newBuy == 0.0) {
                        result = "단가는 0이 될 수 없습니다."
                    } else {
                        // 수량과 새로운 평균 단가 계산
                        val existingQuantity = existingTotal / existingAvg
                        val newQuantity = newAmount / newBuy
                        val totalQuantity = existingQuantity + newQuantity
                        val totalAmount = existingTotal + newAmount
                        val newAveragePrice = totalAmount / totalQuantity

                        // 결과에 소수점 2자리까지 표시
                        result = String.format("%.2f", newAveragePrice)
                    }
                } catch (e: NumberFormatException) {
                    // 숫자가 아닌 입력값 처리
                    result = "잘못된 입력입니다."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("계산하기")
        }

        // 계산 결과 표시
        Text(
            text = "새로운 매수 평균 단가: $result"
        )
    }
}