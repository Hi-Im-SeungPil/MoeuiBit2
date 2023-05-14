package org.jeonfeel.moeuibit2.ui.main.exchange

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.theme.loadingColor

@Composable
fun ExchangeScreenLoading() {
    val color = loadingColor()
    CompositionLocalProvider(
        LocalShimmerTheme provides creditCardTheme
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .shimmer()
        ) {

            Box(
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .fillMaxWidth()
                    .height(45.dp)
                    .padding(0.dp, 5.dp)
                    .background(color = color)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(10.dp, 0.dp, 10.dp, 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(0.dp, 5.dp, 5.dp, 5.dp)
                        .weight(1f)
                        .background(color = color)
                        .fillMaxHeight()
                )
                Box(
                    modifier = Modifier
                        .padding(5.dp, 5.dp, 0.dp, 5.dp)
                        .weight(1f)
                        .background(color = color)
                        .fillMaxHeight()
                )
                Box(
                    modifier = Modifier
                        .padding(5.dp, 5.dp, 0.dp, 5.dp)
                        .weight(1f)
                        .background(color = color)
                        .fillMaxHeight()
                )
                Box(modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
                Box(modifier = Modifier.weight(1f))
                Text(
                    text = "",
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .weight(1f)
                        .background(color = color)
                        .wrapContentHeight()
                )
                Text(
                    text = "",
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .weight(1f)
                        .background(color = color)
                        .wrapContentHeight()
                )
                Text(
                    text = "",
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .weight(1f)
                        .background(color = color)
                        .wrapContentHeight()
                )
            }
            for (i in 0..14) {
                LoadingLazyColumnItem(color = color)
            }
        }
    }
}

@Composable
fun LoadingLazyColumnItem(
    color: Color
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {

        Column(
            Modifier
                .weight(1f)
                .align(Alignment.Bottom)
        ) {
            Text(
                text = "",
                modifier = Modifier
                    .padding(10.dp, 10.dp, 10.dp, 2.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom)
                    .background(color = color),
            )
            Text(
                text = "",
                modifier = Modifier
                    .padding(10.dp, 2.dp, 10.dp, 10.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = color),
            )
        }

        Text(
            text = "",
            modifier = Modifier
                .padding(5.dp, 0.dp)
                .weight(1f)
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentHeight()
                .background(color = color)
        )

        Text(
            text = "",
            modifier = Modifier
                .padding(5.dp, 0.dp)
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
                .background(color = color)
        )

        Text(
            text = "",
            modifier = Modifier
                .padding(5.dp, 0.dp, 10.dp, 0.dp)
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
                .background(color = color),
        )
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