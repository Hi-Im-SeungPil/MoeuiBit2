package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel

@Composable
fun FavoriteExchangeScreenLazyColumn(
    mainViewModel: MainViewModel = viewModel(),
    startForActivityResult: ActivityResultLauncher<Intent>,
) {
    val filteredExchangeList = mainViewModel.getFilteredCoinList()
//    val filteredExchangeList = emptyList<CommonExchangeModel>()
    when {
        filteredExchangeList.isEmpty() && mainViewModel.searchTextFieldValueState.value.isNotEmpty() -> {
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
        filteredExchangeList.isEmpty() && !mainViewModel.loadingFavorite.value -> {
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
        else -> {
            if(filteredExchangeList.size != 0) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(
                        items = filteredExchangeList
                    ) { _, coinListElement ->
                        ExchangeScreenLazyColumnItem(
                            coinListElement,
                            mainViewModel.favoritePreItemArray[mainViewModel.favoriteExchangeModelListPosition[coinListElement.market]
                                ?: 0].tradePrice,
                            mainViewModel.favoriteHashMap[coinListElement.market] != null,
                            startForActivityResult,
                            mainViewModel.btcTradePrice.value
                        )
                        mainViewModel.favoritePreItemArray[mainViewModel.favoriteExchangeModelListPosition[coinListElement.market]
                            ?: 0] = coinListElement
                    }
                }
            }
        }
    }
}