package org.jeonfeel.moeuibit2.ui.main.additional_features.secsions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.coindetail.order.ui.NumberCommaTransformation
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun AveragePurchaseCalculatorSection() {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    var existingAveragePrice by remember { mutableStateOf("") }
    var existingTotalAmount by remember { mutableStateOf("") }
    var newBuyPrice by remember { mutableStateOf("") }
    var newBuyAmount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
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
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = commonTextColor(),      // 포커스됐을 때 아웃라인 색
                    unfocusedBorderColor = commonDividerColor(),    // 포커스 안됐을 때 아웃라인 색
                    cursorColor = commonTextColor()               // 커서 색
                ),
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
                label = { Text(label, fontSize = DpToSp(15.dp), color = commonTextColor()) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = DpToSp(15.dp), textAlign = TextAlign.Start, color = commonTextColor()),
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

        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                "초기화",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .weight(2f)
                    .background(color = Color.Gray, RoundedCornerShape(10.dp))
                    .padding(vertical = 15.dp)
                    .noRippleClickable {
                        existingAveragePrice = ""
                        existingTotalAmount = ""
                        newBuyPrice = ""
                        newBuyAmount = ""
                        result = ""
                    },
                style = TextStyle(
                    fontSize = DpToSp(15.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
            )

            Text(
                "계산하기",
                modifier = Modifier
                    .padding(start = 5.dp)
                    .weight(4f)
                    .background(APP_PRIMARY_COLOR, RoundedCornerShape(10.dp))
                    .padding(vertical = 15.dp)
                    .noRippleClickable {
                        try {
                            // 숫자 변환 시 ',' 제거
                            val existingAvg = existingAveragePrice
                                .replace(",", "")
                                .toBigDecimal()
                            val existingTotal = existingTotalAmount
                                .replace(",", "")
                                .toBigDecimal()
                            val newBuy = newBuyPrice
                                .replace(",", "")
                                .toBigDecimal()
                            val newAmount = newBuyAmount
                                .replace(",", "")
                                .toBigDecimal()

                            // 두 값의 소수점 자리수 추출 함수
                            fun fractionalLength(input: String): Int {
                                val clean = input.replace(",", "")
                                return clean
                                    .split(".")
                                    .getOrNull(1)?.length ?: 0
                            }

                            val decLength1 = fractionalLength(existingAveragePrice)
                            val decLength3 = fractionalLength(newBuyPrice)
                            val maxDecLength = maxOf(decLength1, decLength3)
                            val scale = maxDecLength + 2

                            // 계산 로직
                            result =
                                if (existingAvg == BigDecimal.ZERO || newBuy == BigDecimal.ZERO) {
                                    "단가는 0이 될 수 없습니다."
                                } else {
                                    val existingQuantity =
                                        existingTotal.divide(existingAvg, 10, RoundingMode.HALF_UP)
                                    val newQuantity =
                                        newAmount.divide(newBuy, 10, RoundingMode.HALF_UP)
                                    val totalQuantity = existingQuantity + newQuantity
                                    val totalAmount = existingTotal + newAmount

                                    // 동적 소수점 자리수를 위한 DecimalFormat 패턴 생성
                                    val pattern = if (scale > 0) {
                                        "#,##0." + "0".repeat(scale)
                                    } else {
                                        "#,##0"
                                    }
                                    val formatter =
                                        DecimalFormat(
                                            pattern,
                                            DecimalFormatSymbols(Locale.getDefault())
                                        )
                                    formatter.format(
                                        totalAmount.divide(
                                            totalQuantity,
                                            scale,
                                            RoundingMode.HALF_UP
                                        )
                                    )
                                }
                        } catch (e: Exception) {
                            result = "잘못된 입력입니다."
                        }

                        keyboardController?.hide()
                    },
                style = TextStyle(
                    fontSize = DpToSp(15.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )
        }

        if (result.isNullOrEmpty() || result == "잘못된 입력입니다." || result == "단가는 0이 될 수 없습니다.") {
            Text(
                text = "$result",
                style = TextStyle(fontSize = DpToSp(18.dp), color = commonTextColor()),
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "결과 :",
                    style = TextStyle(fontSize = DpToSp(18.dp), color = commonTextColor()),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Text(
                    text = "$result",
                    style = TextStyle(
                        fontSize = DpToSp(18.dp),
                        color = commonTextColor(),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}