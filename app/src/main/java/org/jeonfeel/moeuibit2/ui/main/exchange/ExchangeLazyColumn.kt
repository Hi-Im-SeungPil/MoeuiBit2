package org.jeonfeel.moeuibit2.ui.main.exchange

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CommonExchangeModel
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator

@Composable
fun ExchangeScreenLazyColumn(
    filteredExchangeCoinList: SnapshotStateList<CommonExchangeModel>,
    preCoinListAndPosition: Pair<ArrayList<CommonExchangeModel>, HashMap<String, Int>>,
    textFieldValueState: MutableState<String>,
    loadingFavorite: MutableState<Boolean>? = null,
    btcPrice: MutableState<Double>,
    startForActivityResult: ActivityResultLauncher<Intent>,
    scrollState: LazyListState
) {
    when {
        loadingFavorite != null && !loadingFavorite.value && filteredExchangeCoinList.isEmpty() -> {
            Text(
                text = stringResource(id = R.string.noFavorite),
                modifier = Modifier
                    .padding(0.dp, 20.dp, 0.dp, 0.dp)
                    .fillMaxSize(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        filteredExchangeCoinList.isEmpty() && textFieldValueState.value.isNotEmpty() -> {
            Text(
                text = stringResource(id = R.string.noSearchingCoin),
                modifier = Modifier
                    .padding(0.dp, 20.dp, 0.dp, 0.dp)
                    .fillMaxSize(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        filteredExchangeCoinList.isNotEmpty() -> {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState) {
                itemsIndexed(
                    items = filteredExchangeCoinList
                ) { _, coinListElement ->
                    val preCoinListPosition =
                        preCoinListAndPosition.second[coinListElement.market] ?: 0
                    val preCoinElement = preCoinListAndPosition.first[preCoinListPosition]
                    val marketState = Utils.getSelectedMarket(coinListElement.market)
                    ExchangeScreenLazyColumnItem(
                        commonExchangeModel = coinListElement,
                        isFavorite = MoeuiBitDataStore.favoriteHashMap[coinListElement.market] != null,
                        startForActivityResult = startForActivityResult,
                        marketState = marketState,
                        signedChangeRate = CurrentCalculator.signedChangeRateCalculator(
                            coinListElement.signedChangeRate
                        ),
                        curTradePrice = CurrentCalculator.tradePriceCalculator(
                            coinListElement.tradePrice,
                            marketState
                        ),
                        accTradePrice24h = CurrentCalculator.accTradePrice24hCalculator(
                            coinListElement.accTradePrice24h,
                            marketState
                        ),
                        formattedPreTradePrice = CurrentCalculator.tradePriceCalculator(
                            preCoinElement.tradePrice,
                            marketState
                        ),
                        btcToKrw = CurrentCalculator.btcToKrw(
                            (coinListElement.tradePrice * btcPrice.value),
                            marketState
                        ),
                        unit = Utils.getUnit(marketState),
                    )
                    preCoinListAndPosition.first[preCoinListPosition] = coinListElement
                }
            }
        }
    }
}