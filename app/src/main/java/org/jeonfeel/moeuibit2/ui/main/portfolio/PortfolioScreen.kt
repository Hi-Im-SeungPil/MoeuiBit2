package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.MutableStateFlow
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.SYMBOL_KRW
import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.COIN_IMAGE_BASE_URL
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.Caution
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText2
import org.jeonfeel.moeuibit2.ui.common.CommonText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.ui.main.portfolio.component.PortfolioMain
import org.jeonfeel.moeuibit2.ui.main.portfolio.component.PortfolioMainSortButtons
import org.jeonfeel.moeuibit2.ui.main.portfolio.component.PortfolioSortButton
import org.jeonfeel.moeuibit2.ui.main.portfolio.component.getTextColors
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.ui.nav.AppScreen
import org.jeonfeel.moeuibit2.ui.theme.decreaseColor
import org.jeonfeel.moeuibit2.ui.theme.increaseColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.eightDecimalCommaFormat
import org.jeonfeel.moeuibit2.utils.ext.showToast
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
    loading: MutableStateFlow<Boolean>,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = commonBackground())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(id = R.string.investmentDetail),
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    .weight(1f, true)
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    color = commonTextColor(),
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
                            color = commonDividerColor(),
                            shape = RoundedCornerShape(5.dp)
                        ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.chargeMoney),
                        modifier = Modifier
                            .background(color = commonBackground())
                            .padding(9.dp)
                            .wrapContentWidth()
                            .align(Alignment.CenterVertically)
                            .noRippleClickable {
                                //TODO 필수도 수정해야함.
//                                earnReward()
                                adDialogState.value = true
                            },
                        style = TextStyle(
                            color = commonTextColor(),
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
                            color = commonDividerColor(),
                            shape = RoundedCornerShape(5.dp)
                        ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = "코인 정리",
                        modifier = Modifier
                            .background(color = commonBackground())
                            .padding(9.dp)
                            .wrapContentWidth()
                            .align(Alignment.CenterVertically)
                            .noRippleClickable {
                                if (NetworkConnectivityObserver.isNetworkAvailable.value) {
                                    findWrongCoin()
                                } else {
                                    context.showToast("인터넷 상태를 확인해주세요.")
                                }
                            },
                        style = TextStyle(
                            color = commonTextColor(),
                            fontSize = DpToSp(dp = 13.dp)
                        )
                    )
                }
            }
        }

        Divider(
            Modifier
                .fillMaxWidth()
                .height(1.dp), color = commonDividerColor()
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                PortfolioMain(
                    totalValuedAssets = totalValuedAssets,
                    totalPurchase = totalPurchase,
                    userSeedMoney = userSeedMoney,
                    getPortFolioMainInfoMap = getPortFolioMainInfoMap
                )
            }

            stickyHeader {
                SearchSection(portfolioSearchTextState, focusManager = focusManager)

                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = commonDividerColor()
                )

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
                        .height(1.dp),
                    color = commonDividerColor()
                )

                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = Color.Transparent
                )
            }

            itemsIndexed(
                items = getList()
            ) { index, item ->
                val topPadding = if (index == 0) 0.dp else 10.dp
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
                    appNavController = appNavController,
                    topPadding = topPadding
                )
            }
        }
    }
}

@Composable
private fun SearchSection(
    textFieldValueState: MutableState<String>,
    focusManager: FocusManager,
    modifier: Modifier = Modifier,
) {
    val hintFocusState: MutableState<Boolean> = remember { mutableStateOf(false) }

    BasicTextField(value = textFieldValueState.value, onValueChange = {
        textFieldValueState.value = it
    }, singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(commonBackground())
            .clearFocusOnKeyboardDismiss()
            .onFocusChanged { focusState ->
                hintFocusState.value = focusState.isFocused
            },
        textStyle = TextStyle(
            color = commonTextColor(),
            fontSize = DpToSp(17.dp)
        ),
        cursorBrush = SolidColor(commonTextColor()),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(25.dp),
                    tint = commonTextColor()
                )
                Box(Modifier.weight(1f)) {
                    if (textFieldValueState.value.isEmpty() && !hintFocusState.value) {
                        CommonText(
                            stringResource(id = R.string.textFieldText),
                            textStyle = LocalTextStyle.current.copy(
                                color = commonHintTextColor(),
                            ),
                            fontSize = 17.dp,
                        )
                    }
                    innerTextField()
                }
                if (textFieldValueState.value.isNotEmpty()) {
                    IconButton(onClick = {
                        textFieldValueState.value = ""
                        focusManager.clearFocus(true)
                    }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(25.dp),
                            tint = commonTextColor()
                        )
                    }
                }
            }
        })
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
    appNavController: NavHostController,
    topPadding: Dp,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(color = commonBackground())
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
                .padding(0.dp, topPadding, 0.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
//            key(market) {
//                GlideImage(
//                    imageModel = COIN_IMAGE_BASE_URL.plus("$symbol.png"),
//                    modifier = Modifier
//                        .padding(start = 8.dp, bottom = 12.dp)
//                        .size(30.dp)
//                        .clip(CircleShape)
//                        .border(width = 1.dp, color = Color(0xFFE8E8E8), shape = CircleShape)
//                        .background(Color.White)
//                        .align(Alignment.CenterVertically),
//                    error = ImageBitmap.imageResource(R.drawable.img_moeuibit_icon3),
//                    placeHolder = ImageBitmap.imageResource(R.drawable.img_moeuibit_icon3),
//                )
//            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Row {
                    if (market.startsWith(UPBIT_BTC_SYMBOL_PREFIX)) {
                        Text(
                            text = "BTC",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 4.dp)
                                .background(
                                    color = Color(0xffFDE500),
                                    shape = RoundedCornerShape(size = 4.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 3.dp),
                            style = TextStyle(
                                color = Color(0xff191919),
                                fontSize = DpToSp(9.dp),
                                fontWeight = FontWeight.Bold,
                            )
                        )
                    }
                    AutoSizeText2(
                        text = coinKoreanName,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(0.dp, 0.dp, 0.dp, 1.dp)
                            .fillMaxWidth(),
                        textStyle = TextStyle(
                            color = commonTextColor(),
                            fontSize = DpToSp(17.dp),
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
                Text(
                    text = symbol, fontWeight = FontWeight.Bold, style = TextStyle(
                        color = commonTextColor(), fontSize = DpToSp(17.dp)
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
                            color = commonTextColor(),
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
                            color = commonTextColor(),
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
                .height(1.dp), color = commonDividerColor()
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
                    color = commonTextColor()
                ),
                color = commonTextColor()
            )
            Text(
                text = "  ".plus(text2),
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    color = commonTextColor(),
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
                commonTextColor()
            }
        }
    } else {
        commonTextColor()
    }
}