package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import app.dvkyun.flexhybridand.FlexWebView
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.util.AddLifecycleEvent


@SuppressLint("MutableCollectionMutableState")
@Composable
fun CoinInfoScreen(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    CommonLoadingDialog(dialogState = coinDetailViewModel.coinInfoDialog, text = stringResource(id = R.string.coinInfoLoading))
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coinInfoHashMap = remember {
        mutableStateOf(HashMap<String, String>())
    }
    val selected = remember {
        mutableStateOf("")
    }
    val flex = remember {
        FlexWebView(context)
    }
    val selectedButton = remember {
        mutableStateOf(-1)
    }
    AddLifecycleEvent(
        onStopAction = {
            coinDetailViewModel.coinInfoLoading.value = false
        },
        onStartAction = {
            flex.initFlex()
            coinDetailViewModel.getCoinInfo()
            coinDetailViewModel.coinInfoLiveData.observe(lifecycleOwner) {
                coinInfoHashMap.value = it
                coinDetailViewModel.coinInfoDialog.value = false
            }
        }
    )

    if (coinInfoHashMap.value.isNotEmpty() && coinDetailViewModel.coinInfoLoading.value) {
        CoinInfoContent(selected, selectedButton, coinInfoHashMap, flex)
    } else if (coinInfoHashMap.value.isEmpty() && coinDetailViewModel.coinInfoLoading.value) {
        CoinInfoEmptyScreen()
    }
}