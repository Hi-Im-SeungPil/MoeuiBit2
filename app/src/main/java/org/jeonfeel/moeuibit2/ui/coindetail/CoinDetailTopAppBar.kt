package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.view.activity.coindetail.CoinDetailActivity

@Composable
fun CoinDetailTopAppBar(coinKoreanName: String, coinSymbol: String) {
    val context = LocalContext.current
    TopAppBar(
        backgroundColor = colorResource(id = R.color.C0F0F5C),
        title = {
            Text(text = "${coinKoreanName}(${coinSymbol}/KRW)",
                style = TextStyle(color = Color.White,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1)
        },
        navigationIcon = {
            IconButton(onClick = {
                (context as CoinDetailActivity).finish()
                context.overridePendingTransition(R.anim.none, R.anim.lazy_column_item_slide_right)
            }) {
                Icon(Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Outlined.Call, contentDescription = null, tint = Color.White)
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Outlined.Star, contentDescription = null, tint = Color.White)
            }
        }
    )
}