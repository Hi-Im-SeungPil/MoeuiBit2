package org.jeonfeel.moeuibit2.ui.coindetail

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.view.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.viewmodel.coindetail.CoinDetailViewModel

@Composable
fun CoinDetailTopAppBar(
    coinKoreanName: String,
    coinSymbol: String,
    warning: String,
    coinDetailViewModel: CoinDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    TopAppBar(
        backgroundColor = colorResource(id = R.color.C0F0F5C),
        title = {
            Text(
                if (warning == "CAUTION") {
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Yellow, fontWeight = FontWeight.Bold)) {
                            append("[유의]")
                        }
                        append("${coinKoreanName}(${coinSymbol}/KRW)")
                    }
                } else {
                    buildAnnotatedString {
                        append("${coinKoreanName}(${coinSymbol}/KRW)")
                    }
                },
                style = TextStyle(
                    color = Color.White,
                    fontSize = 17.sp,
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
                intent.putExtra("market", "KRW-".plus(coinSymbol))
                intent.putExtra("isFavorite", coinDetailViewModel.favoriteMutableState.value)
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
            IconButton(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    coinDetailViewModel.localRepository.getUserDao().insert()
                    coinDetailViewModel.userSeedMoney.value =
                        coinDetailViewModel.localRepository.getUserDao().all?.krw ?: 0
                }
            }) {
                Icon(Icons.Default.Clear, contentDescription = null, tint = Color.White)
            }
            if (!coinDetailViewModel.favoriteMutableState.value) {
                IconButton(onClick = {
                    coinDetailViewModel.favoriteMutableState.value = true
                    Toast.makeText(context, "관심 코인에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        painterResource(R.drawable.img_unfavorite),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            } else {
                IconButton(onClick = {
                    coinDetailViewModel.favoriteMutableState.value = false
                    Toast.makeText(context, "관심 코인에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        painterResource(R.drawable.img_favorite),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    )
}