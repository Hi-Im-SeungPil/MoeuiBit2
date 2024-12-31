package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.orhanobut.logger.Logger
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.MutableStateFlow
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.SYMBOL_KRW
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.coinImageUrl
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.Caution
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText2
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeScreenSearchTextField
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.ui.main.portfolio.component.PortfolioMain
import org.jeonfeel.moeuibit2.ui.main.portfolio.component.PortfolioMainSortButtons
import org.jeonfeel.moeuibit2.ui.main.portfolio.component.PortfolioSortButton
import org.jeonfeel.moeuibit2.ui.main.portfolio.component.getTextColors
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.ui.nav.AppScreen
import org.jeonfeel.moeuibit2.ui.theme.chargingKrwBackgroundColor
import org.jeonfeel.moeuibit2.ui.theme.decreaseColor
import org.jeonfeel.moeuibit2.ui.theme.increaseColor
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.eightDecimalCommaFormat
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import java.math.BigDecimal
import kotlin.reflect.KFunction0

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PortfolioScreen(
    portfolioOrderState: State<Int>,
    totalValuedAssets: State<BigDecimal>,
    totalPurchase: State<BigDecimal>,
    userSeedMoney: State<Long>,
    adDialogState: MutableState<Boolean>,
    userHoldCoinDTOList: List<UserHoldCoinDTO>,
    sortUserHoldCoin: (orderState: Int) -> Unit,
    getUserCoinInfo: (UserHoldCoinDTO) -> Map<String, String>,
    loadingState: State<Boolean>,
    currentBTCPrice: State<Double>,
    getPortFolioMainInfoMap: (totalValuedAssets: State<BigDecimal>, totalPurchase: State<BigDecimal>, userSeedMoney: State<Long>) -> Map<String, String>,
    appNavController: NavHostController,
    earnReward: () -> Unit,
    portfolioSearchTextState: MutableState<String>,
    getList: () -> List<UserHoldCoinDTO>,
    findWrongCoin: KFunction0<Unit>,
    id: Int,
    loading: MutableStateFlow<Boolean>
) {
    val loading2 = loading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = stringResource(id = R.string.investmentDetail),
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    .weight(1f, true)
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = DpToSp(20.dp),
                    fontWeight = FontWeight.W600
                )
            )
            Row {
                Card(
                    modifier = Modifier
                        .background(color = Color.Transparent)
                        .padding(0.dp, 12.dp, 8.dp, 12.dp)
                        .wrapContentWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDFDFDF),
                            shape = RoundedCornerShape(5.dp)
                        ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.chargeMoney),
                        modifier = Modifier
                            .background(color = chargingKrwBackgroundColor())
                            .padding(9.dp)
                            .wrapContentWidth()
                            .align(Alignment.CenterVertically)
                            .noRippleClickable {
                                //TODO 필수도 수정해야함.
                                earnReward()
//                                adDialogState.value = true
                            },
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = DpToSp(dp = 13.dp)
                        )
                    )
                }
                Card(
                    modifier = Modifier
                        .background(color = Color.Transparent)
                        .padding(0.dp, 12.dp, 8.dp, 12.dp)
                        .wrapContentWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDFDFDF),
                            shape = RoundedCornerShape(5.dp)
                        ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = "코인 정리",
                        modifier = Modifier
                            .background(color = chargingKrwBackgroundColor())
                            .padding(9.dp)
                            .wrapContentWidth()
                            .align(Alignment.CenterVertically)
                            .noRippleClickable {
//                            adDialogState.value = true
                                findWrongCoin()
                            },
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = DpToSp(dp = 13.dp)
                        )
                    )
                }
            }
        }

        Divider(
            Modifier
                .fillMaxWidth()
                .height(1.dp), color = Color(0xFFDFDFDF)
        )

        if (loading2.value) {
            Logger.e("호출호출 $id")
            Logger.e(loadingState.toString())
            PortfolioLoadingScreen()
        } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                PortfolioMain(
                    totalValuedAssets = totalValuedAssets,
                    totalPurchase = totalPurchase,
                    userSeedMoney = userSeedMoney,
                    getPortFolioMainInfoMap = getPortFolioMainInfoMap
                )
            }

            stickyHeader {
                SearchTextBox(portfolioSearchTextState)
                PortfolioMainSortButtons(
                    orderByNameTextInfo = getTextColors(
                        button = PortfolioSortButton.BUTTON_NAME,
                        textState = portfolioOrderState.value
                    ),
                    orderByRateTextInfo = getTextColors(
                        button = PortfolioSortButton.BUTTON_RATE,
                        textState = portfolioOrderState.value
                    ),
                    portfolioOrderState = portfolioOrderState,
                    sortUserHoldCoin = sortUserHoldCoin,
                )
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color.Transparent
                )
            }

            item {
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = Color.LightGray
                )
            }

            itemsIndexed(items = getList()) { _, item ->
                val userCoinInfo = getUserCoinInfo(item)
                val increaseColorOrDecreaseColor = Utils.getIncreaseOrDecreaseColor(
                    value = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_VALUATION_GAIN_OR_LOSE]?.toFloat()
                        ?: 0f
                )
                UserHoldCoinLazyColumnItem(
                    coinKoreanName = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_COIN_KOREAN_NAME]
                        ?: "",
                    coinEngName = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_COIN_ENG_NAME]
                        ?: "",
                    symbol = item.myCoinsSymbol,
                    valuationGainOrLoss = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_VALUATION_GAIN_OR_LOSE_RESULT]
                        ?: "",
                    aReturn = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_A_RETURN]
                        ?: "",
                    coinQuantity = item.myCoinsQuantity.eightDecimalCommaFormat(),
                    purchaseAverage = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_PURCHASE_PRICE]
                        ?: "",
                    purchaseAmount = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_PURCHASE_AMOUNT_RESULT]
                        ?: "",
                    evaluationAmount = userCoinInfo[PortfolioScreenStateHolder.USER_COIN_RESULT_KEY_EVALUATION_AMOUNT_FORMAT]
                        ?: "",
                    color = increaseColorOrDecreaseColor,
                    warning = item.warning,
                    caution = item.caution,
                    currentPrice = if (item.market.isTradeCurrencyKrw()) item.currentPrice else item.currentPrice.toBigDecimal()
                        .multiply(currentBTCPrice.value.newBigDecimal()).toDouble(),
                    market = item.market,
                    appNavController = appNavController
                )
            }
            }
        }
    }
}

@Composable
private fun SearchTextBox(
    searchTextFieldValue: MutableState<String>
) {
    ExchangeScreenSearchTextField(
        textFieldValueState = searchTextFieldValue,
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(Color.White)
            .clearFocusOnKeyboardDismiss(),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .size(25.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        trailingIcon = {
            IconButton(onClick = { it.invoke() }) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(25.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        placeholderText = stringResource(id = R.string.textFieldText),
        fontSize = DpToSp(dp = 17.dp),
        placeholderTextColor = Color.Gray
    )
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
    warning: Boolean,
    caution: Caution?,
    currentPrice: Double,
    market: String,
    appNavController: NavHostController
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(color = MaterialTheme.colorScheme.background)
        .clickable {
            val cautionJson = caution?.let {
                Utils.gson.toJson(caution)
            } ?: " "
            appNavController.navigate("${AppScreen.CoinDetail.name}/$market/$warning/$cautionJson") {
                launchSingleTop = true
                popUpTo(appNavController.graph.findStartDestination().id) {
                    saveState = true
                }
                restoreState = true
            }
        }) {
        Row(
            modifier = Modifier
                .padding(0.dp, 10.dp, 0.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            GlideImage(
                imageModel = coinImageUrl.plus("$symbol.png"), modifier = Modifier
                    .padding(start = 8.dp, bottom = 12.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = Color(0xFFE8E8E8), shape = CircleShape)
                    .background(Color.White)
                    .align(Alignment.CenterVertically)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                AutoSizeText2(
                    text = coinKoreanName,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 1.dp)
                        .fillMaxWidth(),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = DpToSp(17.dp),
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = symbol, fontWeight = FontWeight.Bold, style = TextStyle(
                        color = MaterialTheme.colorScheme.primary, fontSize = DpToSp(17.dp)
                    ), overflow = TextOverflow.Ellipsis
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.currentPrice),
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterVertically),
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = DpToSp(dp = 12.dp)
                        )
                    )
                    AutoSizeText(
                        text = currentPrice.newBigDecimal(
                            ROOT_EXCHANGE_UPBIT,
                            UPBIT_KRW_SYMBOL_PREFIX
                        )
                            .formattedString(),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .fillMaxWidth()
                            .weight(1f, true)
                            .align(Alignment.CenterVertically),
                        textStyle = TextStyle(fontSize = DpToSp(12.dp)),
                        color = color
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.valuationGainOrLoss),
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterVertically),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = DpToSp(dp = 15.dp)
                        )
                    )
                    AutoSizeText(
                        text = valuationGainOrLoss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, true)
                            .align(Alignment.CenterVertically),
                        textStyle = TextStyle(textAlign = TextAlign.End, fontSize = DpToSp(15.dp)),
                        color = color
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.aReturn),
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterVertically),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = DpToSp(dp = 15.dp)
                        )
                    )
                    AutoSizeText(
                        text = aReturn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, true)
                            .align(Alignment.CenterVertically),
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
        Divider(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth()
                .height(1.dp), color = Color.LightGray
        )
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
                modifier = Modifier
                    .weight(1f, true)
                    .align(Alignment.CenterVertically),
                text = text1,
                textStyle = TextStyle(
                    textAlign = TextAlign.End,
                    fontSize = DpToSp(15.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "  ".plus(text2),
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = DpToSp(dp = 13.dp)
                )
            )
        }
        Text(
            text = text3,
            modifier = Modifier
                .fillMaxWidth(),
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