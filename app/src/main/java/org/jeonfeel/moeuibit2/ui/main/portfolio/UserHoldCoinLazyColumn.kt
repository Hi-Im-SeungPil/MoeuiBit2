package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.SYMBOL_KRW
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.ui.theme.decreaseColor
import org.jeonfeel.moeuibit2.ui.theme.increaseColor
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.commaFormat
import org.jeonfeel.moeuibit2.utils.eightDecimalCommaFormat
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.secondDecimal

@Composable
fun UserHoldCoinLazyColumn(
    startForActivityResult: ActivityResultLauncher<Intent>,
    columnItemDialogState: MutableState<Boolean>,
    portfolioOrderState: MutableState<Int>,
    totalValuedAssets: MutableState<Double>,
    totalPurchase: MutableState<Double>,
    userSeedMoney: MutableState<Long>,
    adDialogState: MutableState<Boolean>,
    pieChartState: MutableState<Boolean>,
    userHoldCoinList: List<MyCoin?>,
    userHoldCoinDTOList: SnapshotStateList<UserHoldCoinDTO>,
    selectedCoinKoreanName: MutableState<String>,
    isPortfolioSocketRunning: MutableState<Boolean>,
    sortUserHoldCoin: (orderState: Int) -> Unit,
    getUserCoinInfo: (UserHoldCoinDTO) -> Map<String, String>,
    getPortFolioMainInfoMap: (
        totalValuedAssets: MutableState<Double>,
        totalPurchase: MutableState<Double>,
        userSeedMoney: MutableState<Long>
    ) -> Map<String, String>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            PortfolioMain(
                portfolioOrderState = portfolioOrderState,
                totalValuedAssets = totalValuedAssets,
                totalPurchase = totalPurchase,
                userSeedMoney = userSeedMoney,
                orderByNameTextInfo = getTextColors(
                    button = PortfolioSortButton.BUTTON_NAME,
                    textState = portfolioOrderState.value
                ),
                orderByRateTextInfo = getTextColors(
                    button = PortfolioSortButton.BUTTON_RATE,
                    textState = portfolioOrderState.value
                ),
                adDialogState = adDialogState,
                pieChartState = pieChartState,
                userHoldCoinList = userHoldCoinList,
                isPortfolioSocketRunning = isPortfolioSocketRunning,
                sortUserHoldCoin = sortUserHoldCoin,
                getPortFolioMainInfoMap = getPortFolioMainInfoMap
            )
        }
        itemsIndexed(items = userHoldCoinDTOList) { _, item ->
            val userCoinInfo = getUserCoinInfo(item)
            val increaseColorOrDecreaseColor = Utils.getIncreaseOrDecreaseColor(
                value = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_VALUATION_GAIN_OR_LOSE]?.toFloat()
                    ?: 0f
            )
            UserHoldCoinLazyColumnItem(
                coinKoreanName = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_COIN_KOREAN_NAME] ?: "",
                coinEngName = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_COIN_ENG_NAME] ?: "",
                symbol = item.myCoinsSymbol,
                valuationGainOrLoss = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_VALUATION_GAIN_OR_LOSE_RESULT] ?: "",
                aReturn = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_A_RETURN] ?: "",
                coinQuantity = item.myCoinsQuantity.eightDecimalCommaFormat(),
                purchaseAverage = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_PURCHASE_PRICE] ?: "",
                purchaseAmount = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_PURCHASE_AMOUNT_RESULT] ?: "",
                evaluationAmount = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_EVALUATION_AMOUNT_FORMAT] ?: "",
                color = increaseColorOrDecreaseColor,
                openingPrice = item.openingPrice,
                warning = item.warning,
                isFavorite = item.isFavorite,
                currentPrice = item.currentPrice,
                marketState = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_MARKET_STATE]?.toInt() ?: 0,
                startForActivityResult = startForActivityResult,
                selectedCoinKoreanName = selectedCoinKoreanName,
                dialogState = columnItemDialogState
            )
        }
    }
}

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
                    text = coinKoreanName, modifier = Modifier
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
                if (MoeuiBitDataStore.isKor) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.valuationGainOrLoss),
                            modifier = Modifier.wrapContentWidth(),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = DpToSp(dp = 15.dp)
                            )
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
                            text = stringResource(id = R.string.valuationGainOrLoss),
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.fillMaxWidth()
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
                                    Utils.removeComma(valuationGainOrLoss).toDouble(),
                                    MoeuiBitDataStore.usdPrice
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
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = DpToSp(dp = 15.dp)
                        )
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
                textStyle = TextStyle(
                    textAlign = TextAlign.End,
                    fontSize = DpToSp(15.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "  ".plus(text2),
                modifier = Modifier.wrapContentWidth(),
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = DpToSp(dp = 13.dp)
                )
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

@Composable
fun getReturnTextColor(colorStandard: Long, text5: String): Color {
    return if (text5 == stringResource(id = R.string.aReturn)) {
        when {
            colorStandard < 0 -> {
                decreaseColor()
            }

            colorStandard > 0 -> {
                increaseColor()
            }

            else -> {
                MaterialTheme.colorScheme.onBackground
            }
        }
    } else {
        MaterialTheme.colorScheme.onBackground
    }
}