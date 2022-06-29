package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.activity.main.MainActivity
import org.jeonfeel.moeuibit2.activity.main.MainViewModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.KrwExchangeModel
import org.jeonfeel.moeuibit2.util.calculator.Calculator
import org.jeonfeel.moeuibit2.util.calculator.ExchangeCalculator
import org.jeonfeel.moeuibit2.util.commaFormat

@Composable
fun ExchangeScreenLazyColumn(
    mainViewModel: MainViewModel = viewModel(),
    startForActivityResult: ActivityResultLauncher<Intent>
) {
    val filteredKrwExchangeList = mainViewModel.getFilteredKrwCoinList()
    when {
        filteredKrwExchangeList.isEmpty() && mainViewModel.showFavorite.value -> {
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
        filteredKrwExchangeList.isEmpty() && !mainViewModel.showFavorite.value && mainViewModel.searchTextFieldValue.value.isNotEmpty() -> {
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