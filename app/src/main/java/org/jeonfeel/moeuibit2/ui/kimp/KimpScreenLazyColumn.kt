package org.jeonfeel.moeuibit2.ui.kimp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jeonfeel.moeuibit2.ui.viewmodels.KimpViewModel

@Composable
fun KimpScreenLazyColumn(kimpViewModel: KimpViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val coinList = kimpViewModel.upBitKRWCoinList
        itemsIndexed(
            items = coinList
        ) { _, coinListElement ->
            KimpScreenLazyColumnItem(
                coinListElement.market,
                coinListElement.korean_name
            )
        }
    }
}