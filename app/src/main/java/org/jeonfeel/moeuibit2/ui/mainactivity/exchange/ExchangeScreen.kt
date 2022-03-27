package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.util.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@Composable
fun ExchangeScreen(exchangeViewModel: ExchangeViewModel = viewModel()) {
    Column(Modifier
        .fillMaxSize()) {
//        if(!exchangeViewModel.loading.value) {
        if (exchangeViewModel.errorState.value == INTERNET_CONNECTION) {
            SearchBasicTextFieldResult(exchangeViewModel)
            SortButtons(exchangeViewModel)
            ExchangeScreenLazyColumn(exchangeViewModel)
        } else {
            ErrorScreen(exchangeViewModel)
        }
    }
//        } else {
//            ExchangeScreenLoading()
//        }
}

