package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.dvkyun.flexhybridand.FlexWebChromeClient
import app.dvkyun.flexhybridand.FlexWebView
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.util.AddLifecycleEvent
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.util.moveUrl


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