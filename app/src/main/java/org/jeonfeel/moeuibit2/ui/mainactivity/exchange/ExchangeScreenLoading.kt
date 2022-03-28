package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer
import org.jeonfeel.moeuibit2.R

@Composable
fun ExchangeScreenLoading() {
    CompositionLocalProvider(
        LocalShimmerTheme provides creditCardTheme
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .shimmer()) {

            Box(modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(25.dp)
                .background(color = colorResource(id = R.color.CECECEC)))
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)) {
                Text(text = "", modifier = Modifier.weight(1f))
                Text(text = "",
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .weight(1f)
                        .background(color = colorResource(id = R.color.CECECEC)))
                Text(text = "",
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .weight(1f)
                        .background(color = colorResource(id = R.color.CECECEC)))
                Text(text = "",
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .weight(1f)
                        .background(color = colorResource(id = R.color.CECECEC)))
            }
            for (i in 0..14) {
                LoadingLazyColumnItem()
            }
        }
    }
}

@Composable
fun LoadingLazyColumnItem() {
    Row(Modifier
        .fillMaxWidth()
        .height(50.dp)) {

        Column(Modifier
            .weight(1f)
            .align(Alignment.Bottom)) {
            Text(text = "",
                modifier = Modifier
                    .padding(10.dp, 10.dp, 10.dp, 2.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom)
                    .background(color = colorResource(id = R.color.CECECEC)),
                style = TextStyle(textAlign = TextAlign.Center)
            )
            Text(text = "",
                modifier = Modifier
                    .padding(10.dp, 2.dp, 10.dp, 10.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = colorResource(id = R.color.CECECEC)),
                style = TextStyle(textAlign = TextAlign.Center))
        }

        Text(text = "",
            modifier = Modifier
                .padding(5.dp, 0.dp)
                .weight(1f)
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentHeight()
                .background(color = colorResource(id = R.color.CECECEC))
        )

        Text(text = "",
            modifier = Modifier
                .padding(5.dp, 0.dp)
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
                .background(color = colorResource(id = R.color.CECECEC))
        )

        Text(text = "",
            modifier = Modifier
                .padding(5.dp, 0.dp, 10.dp, 0.dp)
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
                .background(color = colorResource(id = R.color.CECECEC)),
            style = TextStyle(textAlign = TextAlign.Center))
    }
}

private val creditCardTheme = defaultShimmerTheme.copy(
    animationSpec = infiniteRepeatable(
        animation = tween(
            durationMillis = 800,
            delayMillis = 0,
            easing = LinearEasing,
        ),
    ),
    blendMode = BlendMode.Hardlight,
    rotation = 25f,
    shaderColors = listOf(
        Color.White.copy(alpha = 0.0f),
        Color.White.copy(alpha = 0.6f),
        Color.White.copy(alpha = 0.0f),
    ),
    shaderColorStops = null,
    shimmerWidth = 400.dp,
)

@Composable
@Preview
fun LoadingLazyColumnItemp() {
    LoadingLazyColumnItem()
}