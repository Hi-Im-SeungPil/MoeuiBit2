package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import app.dvkyun.flexhybridand.FlexWebView
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.ui.CoinInfoContent
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.ui.CoinInfoEmptyScreen
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.ui.initFlex
import org.jeonfeel.moeuibit2.ui.coindetail.NewCoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun CoinInfoScreen(
    viewModel: NewCoinDetailViewModel,
    market: String
) {
    if (viewModel.coinInfo.coinInfoLoading.value) {
        CommonLoadingDialog(
            dialogState = viewModel.coinInfo._coinInfoLoading,
            text = stringResource(id = R.string.coinInfoLoading),
        )
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coinInfoMap = remember {
        mutableStateMapOf<String,String>()
    }
    val selected = remember {
        mutableStateOf("")
    }
    val flex = remember {
        FlexWebView(context)
    }
    val selectedButton = remember {
        mutableIntStateOf(-1)
    }

    AddLifecycleEvent(
        onStartAction = {
            flex.initFlex()
            viewModel.getCoinInfo(market = market)
        },
        onStopAction = {
            viewModel.coinInfo._coinInfoLoading.value = false
        }
    )

    LaunchedEffect(true) {
        viewModel.coinInfo.coinInfoLiveData.observe(lifecycleOwner) {
            coinInfoMap.putAll(it)
            viewModel.coinInfo._coinInfoLoading.value = false
        }
    }

    if (coinInfoMap.isNotEmpty() && !viewModel.coinInfo.coinInfoLoading.value) {
        CoinInfoContent(
            selected = selected,
            selectedButton = selectedButton,
            coinInfoHashMap = coinInfoMap.toMap(),
            flex = flex
        )
    } else if (coinInfoMap.isEmpty() && !viewModel.coinInfo.coinInfoLoading.value) {
        CoinInfoEmptyScreen()
    }
}