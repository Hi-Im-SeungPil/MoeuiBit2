package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
fun ExchangeScreenLazyColumn(
    mainViewModel: MainViewModel = viewModel(),
    startForActivityResult: ActivityResultLauncher<Intent>
) {
    val filteredKrwExchangeList = mainViewModel.getFilteredKrwCoinList()
    when {
        filteredKrwExchangeList.isEmpty() && mainViewModel.showFavoriteState.value -> {
            Text(
                text = stringResource(id = R.string.noFavorite),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        filteredKrwExchangeList.isEmpty() && !mainViewModel.showFavoriteState.value && mainViewModel.searchTextFieldValueState.value.isNotEmpty() -> {
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
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(
                    items = filteredKrwExchangeList
                ) { _, krwCoinListElement ->
                    ExchangeScreenLazyColumnItem(
                        krwCoinListElement,
                        mainViewModel.preItemArray[mainViewModel.krwExchangeModelListPosition[krwCoinListElement.market]
                            ?: 0].tradePrice,
                        mainViewModel.favoriteHashMap[krwCoinListElement.market] != null,
                        startForActivityResult
                    )
                    mainViewModel.preItemArray[mainViewModel.krwExchangeModelListPosition[krwCoinListElement.market]
                        ?: 0] = krwCoinListElement
                }
            }
        }
    }
}