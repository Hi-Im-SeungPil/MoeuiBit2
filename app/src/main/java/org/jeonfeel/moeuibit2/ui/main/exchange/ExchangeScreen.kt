package org.jeonfeel.moeuibit2.ui.main.exchange

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.INTENT_IS_FAVORITE
import org.jeonfeel.moeuibit2.constants.INTENT_MARKET
import org.jeonfeel.moeuibit2.constants.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constants.IS_ANOTHER_SCREEN
import org.jeonfeel.moeuibit2.constants.IS_EXCHANGE_SCREEN
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.ui.activities.MainActivity
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine
import org.jeonfeel.moeuibit2.ui.theme.exchangeMarketButtonTextColor
import org.jeonfeel.moeuibit2.ui.theme.sortButtonSelectedBackgroundColor
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.showToast
import kotlin.system.exitProcess


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExchangeRoute(
    viewModel: ExchangeViewModel = hiltViewModel(),
    networkErrorState: MutableIntState,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    val lazyScrollState = rememberLazyListState()
    val startForActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                if (resultData != null) {
                    val isFavorite = resultData.getBooleanExtra(INTENT_IS_FAVORITE, false)
                    val market = resultData.getStringExtra(INTENT_MARKET) ?: ""
                    viewModel.updateFavorite(market = market, isFavorite = isFavorite)
                }
            }
        }
    // ui state, ui 로직
    val stateHolder = rememberExchangeScreenStateHolder(
        context = context,
        pagerState = pagerState,
        lazyScrollState = lazyScrollState,
        startForActivityResult = startForActivityResult,
        isUpdateExchange = viewModel.isUpdateExchange,
        marketChangeAction = viewModel::marketChangeAction,
        sortList = viewModel::sortList,
        selectedMarketState = viewModel.selectedMarketState
    )
    // init
    AddLifecycleEvent(
        onCreateAction = {

        },
        onPauseAction = {
            UpBitTickerWebSocket.currentPage = IS_ANOTHER_SCREEN
            viewModel.updateIsExchangeUpdateState(false)
            viewModel.stopExchangeUpdateCoroutine()
            UpBitTickerWebSocket.onPause()
        },
        onResumeAction = {
            UpBitTickerWebSocket.currentPage = IS_EXCHANGE_SCREEN
            viewModel.initExchangeData()
        }
    )
    // 백핸들러
    ExchangeBackHandler(context)
    // main network watcher value sync
    LaunchedEffect(key1 = networkErrorState.value) {
        viewModel.changeNetworkErrorState(networkState = networkErrorState.value)
    }

    when (viewModel.loadingExchange.value) {
        true -> {
            ExchangeScreenLoading()
        }

        false -> {
            Exchange(
                stateHolder = stateHolder,
                errorState = viewModel.networkErrorState,
                filteredExchangeCoinList = viewModel.getFilteredCoinList(stateHolder.searchTextFieldValue),
                preCoinListAndPosition = viewModel.getPreCoinListAndPosition(),
                loadingFavorite = viewModel.getFavoriteLoadingState(),
                btcPrice = viewModel.btcPrice,
                checkErrorScreen = viewModel::checkErrorScreen,
                changeSelectedMarketState = viewModel::changeSelectedMarketState,
                updateIsExchangeUpdate = viewModel::updateIsExchangeUpdateState
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Exchange(
    stateHolder: ExchangeScreenStateHolder,
    errorState: State<Int>,
    filteredExchangeCoinList: List<CommonExchangeModel>,
    preCoinListAndPosition: Pair<ArrayList<CommonExchangeModel>, HashMap<String, Int>>,
    loadingFavorite: MutableState<Boolean>? = null,
    btcPrice: State<Double>,
    checkErrorScreen: () -> Unit,
    changeSelectedMarketState: (Int) -> Unit,
    updateIsExchangeUpdate: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        if (errorState.value == INTERNET_CONNECTION) {
            LaunchedEffect(key1 = stateHolder.selectedMarketState.value) {
                stateHolder.selectMarketButtonClickAction()
            }
            SearchTextBox(searchTextFieldValue = stateHolder.searchTextFieldValue)
            MarketButtons(
                selectedMarketState = stateHolder.selectedMarketState,
                pagerState = stateHolder.pagerState,
                tabTitleList = stateHolder.tabTitleList,
                changeSelectedMarketState = changeSelectedMarketState
            )
            ExchangeSortButtons(
                sortButtonClickAction = stateHolder::sortButtonClickAction,
                sortButtonLaunchEffectAction = stateHolder::sortButtonLaunchEffectAction,
                sortButtonState = stateHolder.sortButtonState
            )
            ExchangeScreenLazyColumns(
                filteredExchangeCoinList = filteredExchangeCoinList,
                preCoinListAndPosition = preCoinListAndPosition,
                textFieldValueState = stateHolder.searchTextFieldValue,
                loadingFavorite = loadingFavorite,
                btcPrice = btcPrice,
                scrollState = stateHolder.lazyScrollState,
                lazyColumnItemClickAction = stateHolder::lazyColumnItemClickAction,
                createCoinName = stateHolder::createCoinName,
                changeSelectedMarketState = changeSelectedMarketState,
                selectedMarketState = stateHolder.selectedMarketState,
                pagerState = stateHolder.pagerState,
            )
        } else {
            updateIsExchangeUpdate(false)
            ExchangeErrorScreen(
                checkErrorScreen = checkErrorScreen,
                context = stateHolder.context
            )
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
        fontSize = DpToSp(dp = 17.dp)
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun MarketButtons(
    selectedMarketState: State<Int>,
    pagerState: PagerState,
    tabTitleList: List<String>,
    changeSelectedMarketState: (Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .drawUnderLine(lineColor = MaterialTheme.colorScheme.outline)
    ) {
        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                        color = Color.Transparent
                    )
                },
                modifier = Modifier
                    .weight(3f),
                backgroundColor = MaterialTheme.colorScheme.background,
                divider = {}
            ) {
                tabTitleList.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                fontSize = DpToSp(17.dp),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                        },
                        selectedContentColor = exchangeMarketButtonTextColor(selected = true),
                        unselectedContentColor = exchangeMarketButtonTextColor(selected = false),
                        selected = selectedMarketState.value == index,
                        onClick = {
                            if (selectedMarketState.value != index) {
                                changeSelectedMarketState(index)
                            }
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ExchangeScreenSearchTextField(
    textFieldValueState: MutableState<String>,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable (onClick: () -> Unit) -> Unit)? = null,
    placeholderText: String = "",
    fontSize: TextUnit = androidx.compose.material.MaterialTheme.typography.body2.fontSize,
) {
    val focusManager = LocalFocusManager.current
    val trailingIconOnClick = {
        textFieldValueState.value = ""
        focusManager.clearFocus(true)
    }

    BasicTextField(value = textFieldValueState.value, onValueChange = {
        textFieldValueState.value = it
    }, singleLine = true,
        modifier = modifier.clearFocusOnKeyboardDismiss(),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = DpToSp(17.dp)
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    leadingIcon()
                }
                Box(Modifier.weight(1f)) {
                    if (textFieldValueState.value.isEmpty()) {
                        Text(
                            placeholderText,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = fontSize
                            )
                        )
                    }
                    innerTextField()
                }
                if (trailingIcon != null && textFieldValueState.value.isNotEmpty()) {
                    trailingIcon(onClick = trailingIconOnClick)
                }
            }
        })
}

@Composable
fun ExchangeSortButtons(
    sortButtonClickAction: (buttonId: SortButtons) -> Unit,
    sortButtonLaunchEffectAction: (
        buttonId: SortButtons,
        buttonText: MutableState<String>,
        textColor: MutableState<Color>,
        textBackground: MutableState<Color>,
        sortButtonSelectedBackgroundColor: Color,
        onBackground: Color,
        background: Color
    ) -> Unit,
    sortButtonState: MutableIntState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawUnderLine(lineColor = MaterialTheme.colorScheme.outline),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f), text = ""
        )

        val values = SortButtons.values()
        for (i in values.indices) {
            SortButton(
                buttonId = values[i],
                materialTextColor = MaterialTheme.colorScheme.onBackground,
                materialBackground = MaterialTheme.colorScheme.background,
                sortButtonClickAction = sortButtonClickAction,
                sortButtonLaunchEffectAction = sortButtonLaunchEffectAction,
                sortButtonState = sortButtonState
            )
        }
    }
}

@Composable
private fun RowScope.SortButton(
    buttonId: SortButtons,
    materialTextColor: Color,
    materialBackground: Color,
    sortButtonClickAction: (buttonId: SortButtons) -> Unit,
    sortButtonLaunchEffectAction: (
        buttonId: SortButtons,
        buttonText: MutableState<String>,
        textColor: MutableState<Color>,
        textBackground: MutableState<Color>,
        sortButtonSelectedBackgroundColor: Color,
        onBackground: Color,
        background: Color
    ) -> Unit,
    sortButtonState: MutableIntState
) {
    val buttonText = remember {
        mutableStateOf("")
    }
    val textColor = remember {
        mutableStateOf(materialTextColor)
    }
    val textBackground = remember {
        mutableStateOf(materialBackground)
    }
    val sortButtonSelectedBackgroundColor = sortButtonSelectedBackgroundColor()

    LaunchedEffect(key1 = sortButtonState.intValue) {
        sortButtonLaunchEffectAction(
            buttonId,
            buttonText,
            textColor,
            textBackground,
            sortButtonSelectedBackgroundColor,
            materialTextColor,
            materialBackground
        )
    }

    Text(text = buttonText.value,
        modifier = Modifier
            .weight(1f)
            .clickable {
                Logger.e("sortbutton ${buttonId.name}")
                sortButtonClickAction(
                    buttonId
                )
            }
            .align(Alignment.CenterVertically)
            .background(textBackground.value)
            .padding(0.dp, 7.dp),
        style = TextStyle(
            color = textColor.value,
            fontSize = DpToSp(dp = 13.dp),
            textAlign = TextAlign.Center
        ))
}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}

@Composable
private fun ExchangeBackHandler(context: Context) {
    var backBtnTime = remember { 0L }
    BackHandler(true) {
        val curTime = System.currentTimeMillis()
        val gapTime = curTime - backBtnTime
        if (gapTime in 0..2000) {
            (context as MainActivity).moveTaskToBack(true)
            context.finishAndRemoveTask()
            exitProcess(0)
        } else {
            backBtnTime = curTime
            context.showToast(context.getString(R.string.backPressText))
        }
    }
}