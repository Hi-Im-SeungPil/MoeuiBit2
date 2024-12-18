package org.jeonfeel.moeuibit2.ui.main.coinsite

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.jeonfeel.moeuibit2.ui.main.coinsite.item.*
import kotlin.reflect.KFunction1


@Composable
fun CoinSiteLazyColumn(
    exchangeIsOpen: Boolean,
    infoIsOpen: Boolean,
    kimpIsOpen: Boolean,
    newsIsOpen: Boolean,
    communityIsOpen: Boolean,
    updateIsOpen: KFunction1<String, Unit>
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        item {
            ExchangeItem(updateIsOpen, exchangeIsOpen, context)
            CommunityItem(updateIsOpen, communityIsOpen, context)
            CoinInfoItem(updateIsOpen ,infoIsOpen, context)
            KimpItem(updateIsOpen ,kimpIsOpen, context)
            CoinNews(updateIsOpen, newsIsOpen, context)
        }
    }
}

fun moveUrlOrApp(context: Context, url: String, packageName: String?) {
    if (packageName == null) {
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