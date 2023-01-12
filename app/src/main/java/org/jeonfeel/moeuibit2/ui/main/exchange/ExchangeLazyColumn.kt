package org.jeonfeel.moeuibit2.ui.main.exchange

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.custom.DpToSp

@Composable
fun ExchangeScreenLazyColumn(
    filteredExchangeCoinList: SnapshotStateList<CommonExchangeModel>,
    preCoinListAndPosition: Pair<ArrayList<CommonExchangeModel>, HashMap<String, Int>>,
    textFieldValueState: MutableState<String>,
    favoriteHashMap: HashMap<String, Int>,
    loadingFavorite: MutableState<Boolean>? = null,
    btcPrice: MutableState<Double>,
    startForActivityResult: ActivityResultLauncher<Intent>
) {
    when {
        loadingFavorite != null && !loadingFavorite.value && filteredExchangeCoinList.isEmpty() -> {
            Text(
                text = stringResource(id = R.string.noFavorite),
                modifier = Modifier
                    .padding(0.dp, 20.dp, 0.dp, 0.dp)
                    .fillMaxSize(),
                fontSize = DpToSp(20),
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
                fontSize = DpToSp(20),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(
                    items = filteredExchangeCoinList
                ) { _, coinListElement ->
                    val preCoinListPosition =
                        preCoinListAndPosition.second[coinListElement.market] ?: 0
                    val preCoinElement = preCoinListAndPosition.first[preCoinListPosition]
                    ExchangeScreenLazyColumnItem(
                        commonExchangeModel = coinListElement,
                        preTradePrice = preCoinElement.tradePrice,
                        isFavorite = favoriteHashMap[coinListElement.market] != null,
                        startForActivityResult = startForActivityResult,
                        btcPrice = btcPrice
                    )
                    preCoinListAndPosition.first[preCoinListPosition] = coinListElement
                }
            }
        }
    }
}