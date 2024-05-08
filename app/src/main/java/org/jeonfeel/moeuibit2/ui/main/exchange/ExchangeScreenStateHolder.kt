package org.jeonfeel.moeuibit2.ui.main.exchange

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.CAUTION
import org.jeonfeel.moeuibit2.constants.INTENT_COIN_SYMBOL
import org.jeonfeel.moeuibit2.constants.INTENT_ENG_NAME
import org.jeonfeel.moeuibit2.constants.INTENT_IS_FAVORITE
import org.jeonfeel.moeuibit2.constants.INTENT_KOREAN_NAME
import org.jeonfeel.moeuibit2.constants.INTENT_MARKET_STATE
import org.jeonfeel.moeuibit2.constants.INTENT_OPENING_PRICE
import org.jeonfeel.moeuibit2.constants.INTENT_WARNING
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_FAVORITE
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.constants.SORT_AMOUNT_ASC
import org.jeonfeel.moeuibit2.constants.SORT_AMOUNT_DEC
import org.jeonfeel.moeuibit2.constants.SORT_DEFAULT
import org.jeonfeel.moeuibit2.constants.SORT_PRICE_ASC
import org.jeonfeel.moeuibit2.constants.SORT_PRICE_DEC
import org.jeonfeel.moeuibit2.constants.SORT_RATE_ASC
import org.jeonfeel.moeuibit2.constants.SORT_RATE_DEC
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.activities.CoinDetailActivity
import org.jeonfeel.moeuibit2.ui.activities.MainActivity
import org.jeonfeel.moeuibit2.utils.Utils

enum class SortButtons {
    SortPriceButton, SortRateButton, SortAmountButton
}

class ExchangeScreenStateHolder @OptIn(ExperimentalPagerApi::class) constructor(
    val context: Context,
    val pagerState: PagerState,
    val lazyScrollState: LazyListState,
    val startForActivityResult: ManagedActivityResultLauncher<Intent, ActivityResult>,
    val selectedMarketState: State<Int>,
    private val isUpdateExchange: State<Boolean>,
    private val marketChangeAction: (marketState: Int, sortButtonState: MutableIntState) -> Unit,
    private val sortList: (marketState: Int, sortButtonState: MutableIntState) -> Unit
) {
    val sortButtonState = mutableIntStateOf(SORT_DEFAULT)
    val searchTextFieldValue = mutableStateOf("")
    val tabTitleList = listOf(
        context.getString(R.string.krw),
        context.getString(R.string.btc),
        context.getString(R.string.favorite)
    )

    /**
     * krw btc 관심 버튼 동작
     */
    suspend fun selectMarketButtonClickAction() {
        when (selectedMarketState.value) {
            SELECTED_KRW_MARKET -> {
                marketChangeAction(SELECTED_KRW_MARKET, sortButtonState)
            }

            SELECTED_BTC_MARKET -> {
                marketChangeAction(SELECTED_BTC_MARKET, sortButtonState)
            }

            else -> {
                marketChangeAction(SELECTED_FAVORITE, sortButtonState)
            }
        }
        lazyScrollState.scrollToItem(index = 0)
    }

    /**
     * 정렬 버튼 동작
     */
    fun sortButtonClickAction(
        buttonId: SortButtons
    ) {
        when (buttonId) {
            SortButtons.SortPriceButton -> {
                if (isUpdateExchange.value) {
                    when {
                        sortButtonState.intValue != SORT_PRICE_DEC && sortButtonState.intValue != SORT_PRICE_ASC -> {
                            sortButtonState.intValue = SORT_PRICE_DEC
                        }

                        sortButtonState.intValue == SORT_PRICE_DEC -> {
                            sortButtonState.intValue = SORT_PRICE_ASC
                        }

                        else -> {
                            sortButtonState.intValue = SORT_DEFAULT
                        }
                    }
                    sortList(selectedMarketState.value, sortButtonState)
                }
            }

            SortButtons.SortRateButton -> {
                if (isUpdateExchange.value) {
                    when {
                        sortButtonState.intValue != SORT_RATE_DEC && sortButtonState.intValue != SORT_RATE_ASC -> {
                            sortButtonState.intValue = SORT_RATE_DEC
                        }

                        sortButtonState.intValue == SORT_RATE_DEC -> {
                            sortButtonState.intValue = SORT_RATE_ASC
                        }

                        else -> {
                            sortButtonState.intValue = SORT_DEFAULT
                        }
                    }
                    sortList(selectedMarketState.value, sortButtonState)
                }
            }

            SortButtons.SortAmountButton -> {
                if (isUpdateExchange.value) {
                    when {
                        sortButtonState.intValue != SORT_AMOUNT_DEC && sortButtonState.intValue != SORT_AMOUNT_ASC -> {
                            sortButtonState.intValue = SORT_AMOUNT_DEC
                        }

                        sortButtonState.intValue == SORT_AMOUNT_DEC -> {
                            sortButtonState.intValue = SORT_AMOUNT_ASC
                        }

                        else -> {
                            sortButtonState.intValue = SORT_DEFAULT
                        }
                    }
                    sortList(selectedMarketState.value, sortButtonState)
                }
            }
        }
    }

    /**
     * 코인 정렬
     */
    fun sortButtonLaunchEffectAction(
        buttonId: SortButtons,
        buttonText: MutableState<String>,
        textColor: MutableState<Color>,
        textBackground: MutableState<Color>,
        sortButtonSelectedBackgroundColor: Color,
        onBackground: Color,
        background: Color
    ) {
        when (buttonId) {
            SortButtons.SortPriceButton -> {
                when (sortButtonState.intValue) {
                    SORT_PRICE_DEC -> {
                        buttonText.value = context.getString(R.string.currentPriceSortDec)
                        textColor.value = Color.White
                        textBackground.value = sortButtonSelectedBackgroundColor
                    }

                    SORT_PRICE_ASC -> {
                        buttonText.value = context.getString(R.string.currentPriceSort)
                        textColor.value = Color.White
                        textBackground.value = sortButtonSelectedBackgroundColor
                    }

                    else -> {
                        buttonText.value = context.getString(R.string.currentPriceButton)
                        textColor.value = onBackground
                        textBackground.value = background
                    }
                }
            }

            SortButtons.SortRateButton -> {
                when (sortButtonState.value) {
                    SORT_RATE_DEC -> {
                        buttonText.value = context.getString(R.string.changeSortDec)
                        textColor.value = Color.White
                        textBackground.value = sortButtonSelectedBackgroundColor
                    }

                    SORT_RATE_ASC -> {
                        buttonText.value = context.getString(R.string.changeSort)
                        textColor.value = Color.White
                        textBackground.value = sortButtonSelectedBackgroundColor
                    }

                    else -> {
                        buttonText.value = context.getString(R.string.change)
                        textColor.value = onBackground
                        textBackground.value = background
                    }
                }
            }

            SortButtons.SortAmountButton -> {
                when (sortButtonState.value) {
                    SORT_AMOUNT_DEC -> {
                        buttonText.value = context.getString(R.string.volumeSortDec)
                        textColor.value = Color.White
                        textBackground.value = sortButtonSelectedBackgroundColor
                    }

                    SORT_AMOUNT_ASC -> {
                        buttonText.value = context.getString(R.string.volumeSort)
                        textColor.value = Color.White
                        textBackground.value = sortButtonSelectedBackgroundColor
                    }

                    else -> {
                        buttonText.value = context.getString(R.string.volume)
                        textColor.value = onBackground
                        textBackground.value = background
                    }
                }
            }
        }
    }

    /**
     * 코인 클릭했을 때 상세 화면으로 이동
     */
    fun lazyColumnItemClickAction(commonExchangeModel: CommonExchangeModel) {
        val marketState = Utils.getSelectedMarket(commonExchangeModel.market)

        val intent = Intent(context, CoinDetailActivity::class.java).apply {
            putExtra(INTENT_KOREAN_NAME, commonExchangeModel.koreanName)
            putExtra(INTENT_ENG_NAME, commonExchangeModel.englishName)
            putExtra(INTENT_COIN_SYMBOL, commonExchangeModel.symbol)
            putExtra(INTENT_OPENING_PRICE, commonExchangeModel.opening_price)
            putExtra(
                INTENT_IS_FAVORITE,
                MoeuiBitDataStore.upBitFavoriteHashMap[commonExchangeModel.market] != null
            )
            putExtra(INTENT_MARKET_STATE, marketState)
            putExtra(INTENT_WARNING, commonExchangeModel.warning)
        }
        startForActivityResult.launch(intent)
        (context as MainActivity).overridePendingTransition(
            R.anim.lazy_column_item_slide_left, R.anim.none
        )
    }

    /**
     * 코인 이름 생성
     */
    fun createCoinName(isWarning: String, koreanName: String): AnnotatedString {
        return buildAnnotatedString {
            if (isWarning == CAUTION) {
                withStyle(
                    style = SpanStyle(
                        color = Color.Magenta,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(context.getString(R.string.exchangeCaution))
                }
            }
            append(koreanName)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun rememberExchangeScreenStateHolder(
    startForActivityResult: ManagedActivityResultLauncher<Intent, ActivityResult>,
    context: Context,
    pagerState: PagerState,
    lazyScrollState: LazyListState,
    isUpdateExchange: State<Boolean>,
    marketChangeAction: (marketState: Int, sortButtonState: MutableIntState) -> Unit,
    sortList: (marketState: Int, sortButtonState: MutableIntState) -> Unit,
    selectedMarketState: State<Int>
) = remember() {
    ExchangeScreenStateHolder(
        context = context,
        pagerState = pagerState,
        lazyScrollState = lazyScrollState,
        startForActivityResult = startForActivityResult,
        isUpdateExchange = isUpdateExchange,
        marketChangeAction = marketChangeAction,
        sortList = sortList,
        selectedMarketState = selectedMarketState
    )
}
