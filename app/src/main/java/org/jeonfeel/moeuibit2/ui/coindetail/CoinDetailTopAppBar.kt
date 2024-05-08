package org.jeonfeel.moeuibit2.ui.coindetail

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.CAUTION
import org.jeonfeel.moeuibit2.constants.INTENT_IS_FAVORITE
import org.jeonfeel.moeuibit2.constants.INTENT_MARKET
import org.jeonfeel.moeuibit2.ui.activities.CoinDetailActivity
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.showToast

@Composable
fun CoinDetailTopAppBar(
    coinKoreanName: String,
    coinSymbol: String,
    warning: String,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
) {
    val context = LocalContext.current
    val market = coinDetailViewModel.market
    val marketState = Utils.getSelectedMarket(market)
    val unit = Utils.getUnit(marketState)

    TopAppBar(
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        title = {
            Text(
                if (warning == CAUTION) {
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Yellow,
                            fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.CAUTION_KOREAN))
                        }
                        append("${coinKoreanName}(${coinSymbol}/$unit)")
                    }
                } else {
                    buildAnnotatedString {
                        append("${coinKoreanName}(${coinSymbol}/$unit)")
                    }
                },
                style = TextStyle(
                    color = Color.White,
                    fontSize = DpToSp(dp = 22.dp),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                val intent = Intent()
                intent.putExtra(INTENT_MARKET, market.substring(0,4).plus(coinSymbol))
                intent.putExtra(INTENT_IS_FAVORITE, coinDetailViewModel.favoriteMutableState.value)
                (context as CoinDetailActivity).setResult(-1, intent)
                (context).finish()
                context.overridePendingTransition(R.anim.none, R.anim.lazy_column_item_slide_right)
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        actions = {
            if (!coinDetailViewModel.favoriteMutableState.value) {
                IconButton(onClick = {
                    coinDetailViewModel.favoriteMutableState.value = true
                    context.showToast(context.getString(R.string.favorite_insert_message))
                }) {
                    Icon(
                        painterResource(R.drawable.img_unfavorite),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            } else {
                IconButton(onClick = {
                    coinDetailViewModel.favoriteMutableState.value = false
                    context.showToast(context.getString(R.string.favorite_remove_message))
                }) {
                    Icon(
                        painterResource(R.drawable.img_favorite),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    )
}