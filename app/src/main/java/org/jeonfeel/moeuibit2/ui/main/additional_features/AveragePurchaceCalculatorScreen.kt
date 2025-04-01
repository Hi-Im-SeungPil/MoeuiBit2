package org.jeonfeel.moeuibit2.ui.main.additional_features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.ui.coindetail.order.ui.NumberCommaTransformation
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AverageCostCalculator() {
    var existingAveragePrice by remember { mutableStateOf("") }
    var existingTotalAmount by remember { mutableStateOf("") }
    var newBuyPrice by remember { mutableStateOf("") }
    var newBuyAmount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    val buttonColors = ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary
    )

    // 숫자 포맷팅 함수 (소수점 포함)
    fun formatNumber(input: String): AnnotatedString {
        return try {
            val inputWithZeroDecimal = if (input.endsWith(".")) {
                "${input}0" // 예: "3." -> "3.0"으로 처리
            } else {
                input
            }
            // 입력값에서 ','를 제거하고 BigDecimal로 변환
            val parsed = inputWithZeroDecimal.replace(",", "").toBigDecimal()
            AnnotatedString(NumberFormat.getNumberInstance().format(parsed))
        } catch (e: Exception) {
            AnnotatedString(input)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        listOf(
            "기존 매수 평균 단가" to existingAveragePrice,
            "기존 총액" to existingTotalAmount,
            "새로 살 매수 단가" to newBuyPrice,
            "새로 살 총액" to newBuyAmount
        ).forEachIndexed { index, (label, value) ->
            OutlinedTextField(
                value = value,
                onValueChange = {
                    val rawValue = it.replace(",", "")
                    if (rawValue.matches(Regex("^[0-9]*\\.?[0-9]{0,100}$"))) {
                        when (index) {
                            0 -> existingAveragePrice = it
                            1 -> existingTotalAmount = it
                            2 -> newBuyPrice = it
                            3 -> newBuyAmount = it
                        }
                    }
                },
                label = { Text(label, fontSize = 18.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Start),
                singleLine = true,
                trailingIcon = {
                    if (value.isNotEmpty()) {
                        IconButton(onClick = {
                            when (index) {
                                0 -> existingAveragePrice = ""
                                1 -> existingTotalAmount = ""
                                2 -> newBuyPrice = ""
                                3 -> newBuyAmount = ""
                            }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                visualTransformation = NumberCommaTransformation()
            )
        }

        Button(
            onClick = {
                try {
                    // 숫자 변환 시 ',' 제거
                    val existingAvg = existingAveragePrice.replace(",", "").toBigDecimal()
                    val existingTotal = existingTotalAmount.replace(",", "").toBigDecimal()
                    val newBuy = newBuyPrice.replace(",", "").toBigDecimal()
                    val newAmount = newBuyAmount.replace(",", "").toBigDecimal()

                    // 두 값의 소수점 자리수 추출 함수
                    fun fractionalLength(input: String): Int {
                        val clean = input.replace(",", "")
                        return clean.split(".").getOrNull(1)?.length ?: 0
                    }
                    val decLength1 = fractionalLength(existingAveragePrice)
                    val decLength3 = fractionalLength(newBuyPrice)
                    val maxDecLength = maxOf(decLength1, decLength3)
                    val scale = maxDecLength + 2

                    // 계산 로직
                    result = if (existingAvg == BigDecimal.ZERO || newBuy == BigDecimal.ZERO) {
                        "단가는 0이 될 수 없습니다."
                    } else {
                        val existingQuantity =
                            existingTotal.divide(existingAvg, 10, RoundingMode.HALF_UP)
                        val newQuantity = newAmount.divide(newBuy, 10, RoundingMode.HALF_UP)
                        val totalQuantity = existingQuantity + newQuantity
                        val totalAmount = existingTotal + newAmount

                        // 동적 소수점 자리수를 위한 DecimalFormat 패턴 생성
                        val pattern = if (scale > 0) {
                            "#,##0." + "0".repeat(scale)
                        } else {
                            "#,##0"
                        }
                        val formatter = DecimalFormat(pattern, DecimalFormatSymbols(Locale.getDefault()))
                        formatter.format(totalAmount.divide(totalQuantity, scale, RoundingMode.HALF_UP))
                    }
                } catch (e: Exception) {
                    result = "잘못된 입력입니다."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = buttonColors
        ) {
            Text("계산하기", fontSize = 18.sp)
        }

        Button(
            onClick = {
                existingAveragePrice = ""
                existingTotalAmount = ""
                newBuyPrice = ""
                newBuyAmount = ""
                result = ""
            },
            modifier = Modifier.fillMaxWidth(),
            colors = buttonColors
        ) {
            Text("초기화", fontSize = 18.sp)
        }

        Text(
            text = "새로운 매수 평균 단가: $result",
            style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}