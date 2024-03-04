package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.usdPrice
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.SYMBOL_KRW
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine
import org.jeonfeel.moeuibit2.utils.Utils.removeComma
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator

@Composable
fun UserHoldCoinLazyColumnItem(
    coinKoreanName: String,
    coinEngName: String,
    symbol: String,
    valuationGainOrLoss: String,
    aReturn: String,
    coinQuantity: String,
    purchaseAverage: String,
    purchaseAmount: String,
    evaluationAmount: String,
    color: Color,
    openingPrice: Double,
    warning: String,
    isFavorite: Int?,
    currentPrice: Double,
    marketState: Int,
    startForActivityResult: ActivityResultLauncher<Intent>,
    selectedCoinKoreanName: MutableState<String>,
    dialogState: MutableState<Boolean>,
) {
    val name = if (isKor) coinKoreanName else coinEngName
    if (dialogState.value && coinKoreanName == selectedCoinKoreanName.value) {
        UserHoldCoinLazyColumnItemDialog(
            dialogState,
            koreanName = coinKoreanName,
            engName = coinEngName,
            currentPrice,
            symbol,
            openingPrice,
            isFavorite,
            warning,
            marketState,
            startForActivityResult
        )
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(color = MaterialTheme.colorScheme.background)
        .drawUnderLine(lineColor = Color.Gray, strokeWidth = 2f)
        .clickable {
            selectedCoinKoreanName.value = coinKoreanName
            dialogState.value = true
        }) {
        Row(
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = name, modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 1.dp)
                        .fillMaxWidth(), style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = DpToSp(17.dp),
                        fontWeight = FontWeight.Bold,
                    ), maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = symbol, fontWeight = FontWeight.Bold, style = TextStyle(
                        color = MaterialTheme.colorScheme.primary, fontSize = DpToSp(17.dp)
                    ), overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                if (isKor) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.valuationGainOrLoss),
                            modifier = Modifier.wrapContentWidth(),
                            style = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = DpToSp(dp = 15.dp))
                        )
                        AutoSizeText(
                            text = valuationGainOrLoss, modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 4.dp)
                                .weight(1f, true), textStyle = TextStyle(
                                textAlign = TextAlign.End, fontSize = DpToSp(15.dp)
                            ), color = color
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.valuationGainOrLoss), style = TextStyle(
                                textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground
                            ), modifier = Modifier.fillMaxWidth()
                        )
                        AutoSizeText(
                            text = valuationGainOrLoss, modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 4.dp)
                                .fillMaxWidth(), textStyle = TextStyle(
                                textAlign = TextAlign.End, fontSize = DpToSp(15.dp)
                            ), color = color
                        )
                        AutoSizeText(
                            text = "= \$ ${
                                CurrentCalculator.krwToUsd(
                                    removeComma(valuationGainOrLoss).toDouble(), usdPrice
                                )
                            }", modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 4.dp)
                                .fillMaxWidth(), textStyle = TextStyle(
                                textAlign = TextAlign.End, fontSize = DpToSp(15.dp)
                            ), color = color
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.aReturn),
                        modifier = Modifier.wrapContentWidth(),
                        style = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = DpToSp(dp = 15.dp))
                    )
                    AutoSizeText(
                        text = aReturn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, true),
                        textStyle = TextStyle(textAlign = TextAlign.End, fontSize = DpToSp(15.dp)),
                        color = color
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            UserHoldCoinLazyColumnItemContent(
                coinQuantity, symbol, stringResource(id = R.string.holdingQuantity)
            )
            UserHoldCoinLazyColumnItemContent(
                purchaseAverage, SYMBOL_KRW, stringResource(id = R.string.purchaseAverage)
            )
        }
        Row(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            UserHoldCoinLazyColumnItemContent(
                evaluationAmount, SYMBOL_KRW, stringResource(id = R.string.evaluationAmount)
            )
            UserHoldCoinLazyColumnItemContent(
                purchaseAmount, SYMBOL_KRW, stringResource(id = R.string.purchaseAmount)
            )
        }
    }
}

@Composable
fun RowScope.UserHoldCoinLazyColumnItemContent(
    text1: String,
    text2: String,
    text3: String,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 2.dp)
        ) {
            AutoSizeText(
                modifier = Modifier.weight(1f, true),
                text = text1,
                textStyle = TextStyle(textAlign = TextAlign.End, fontSize = DpToSp(15.dp), color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "  ".plus(text2),
                modifier = Modifier.wrapContentWidth(),
                fontWeight = FontWeight.Bold,
                style = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = DpToSp(dp = 13.dp))
            )
        }
        if (!isKor && text3 != stringResource(id = R.string.holdingQuantity)) {
            Text(
                text = "\$ ${
                    CurrentCalculator.krwToUsd(
                        removeComma(text1).toDouble(), usdPrice
                    )
                }",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(color = Color.Gray, fontSize = DpToSp(dp = 11.dp)),
                textAlign = TextAlign.End
            )
        }
        Text(
            text = text3,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(color = Color.Gray, fontSize = DpToSp(dp = 12.dp)),
            textAlign = TextAlign.End
        )
    }
}