package org.jeonfeel.moeuibit2.ui.coindetail

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.view.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.viewmodel.coindetail.CoinDetailViewModel

@Composable
fun CoinDetailTopAppBar(coinKoreanName: String, coinSymbol: String, coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val context = LocalContext.current
    TopAppBar(
        backgroundColor = colorResource(id = R.color.C0F0F5C),
        title = {
            Text(text = "${coinKoreanName}(${coinSymbol}/KRW)",
                style = TextStyle(color = Color.White,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = {
            IconButton(onClick = {
                val intent = Intent()
                intent.putExtra("market","KRW-".plus(coinSymbol))
                intent.putExtra("isFavorite",coinDetailViewModel.favoriteMutableState.value)
                (context as CoinDetailActivity).setResult(-1,intent)
                (context).finish()
                context.overridePendingTransition(R.anim.none, R.anim.lazy_column_item_slide_right)
            }) {
                Icon(Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White)
            }
        },
        actions = {
            if(!coinDetailViewModel.favoriteMutableState.value) {
                IconButton(onClick = { coinDetailViewModel.favoriteMutableState.value = true }) {
                    Icon(painterResource(R.drawable.img_unfavorite), contentDescription = null, tint = Color.White)
                }
            } else {
                IconButton(onClick = { coinDetailViewModel.favoriteMutableState.value = false }) {
                    Icon(painterResource(R.drawable.img_favorite), contentDescription = null, tint = Color.White)
                }
            }
        }
    )
}