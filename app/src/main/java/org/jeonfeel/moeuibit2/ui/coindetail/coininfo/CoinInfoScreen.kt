package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import app.dvkyun.flexhybridand.FlexWebView
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CoinInfoScreen(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    CommonLoadingDialog(
        dialogState = coinDetailViewModel.coinInfo.state.coinInfoDialog,
        text = stringResource(id = R.string.coinInfoLoading)
    )
    CommonLoadingDialog(
        dialogState = coinDetailViewModel.coinInfo.state.webViewLoading,
        text = "페이지 로드중..."
    )
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
            coinDetailViewModel.coinInfo.state.coinInfoLoading.value = false
        },
        onStartAction = {
            flex.initFlex(coinDetailViewModel.coinInfo.state.webViewLoading)
            coinDetailViewModel.getCoinInfo()
        }
    )

    LaunchedEffect(true) {
        coinDetailViewModel.coinInfo.coinInfoLiveData.observe(lifecycleOwner) {
            coinInfoHashMap.value = it
            coinDetailViewModel.coinInfo.state.coinInfoDialog.value = false
        }
    }

    if (coinInfoHashMap.value.isNotEmpty() && coinDetailViewModel.coinInfo.state.coinInfoLoading.value) {
        CoinInfoContent(
            selected = selected,
            selectedButton = selectedButton,
            coinInfoHashMap = coinInfoHashMap,
            flex = flex,
            webViewLoading = coinDetailViewModel.coinInfo.state.webViewLoading
        )
    } else if (coinInfoHashMap.value.isEmpty() && coinDetailViewModel.coinInfo.state.coinInfoLoading.value) {
        CoinInfoEmptyScreen()
    }
}