package org.jeonfeel.moeuibit2.ui.mainactivity.coinsite

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.jeonfeel.moeuibit2.ui.mainactivity.coinsite.item.*


@Composable
fun CoinSiteLazyColumn() {
    val context = LocalContext.current
    val exchangeState = remember {
        mutableStateOf(false)
    }
    val communityState = remember {
        mutableStateOf(false)
    }
    val coinInfoState = remember {
        mutableStateOf(false)
    }
    val kimpState = remember {
        mutableStateOf(false)
    }
    val coinNewsState = remember {
        mutableStateOf(false)
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            ExchangeItem(exchangeState,context)
            CommunityItem(communityState,context)
            CoinInfoItem(coinInfoState,context)
            KimpItem(kimpState,context)
            CoinNews(coinNewsState,context)
        }
    }
}

fun moveUrlOrApp(context: Context,url: String,packageName: String?) {
    if(packageName == null) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } else {
        var intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent == null) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } else {
            context.startActivity(intent)
        }
    }
}