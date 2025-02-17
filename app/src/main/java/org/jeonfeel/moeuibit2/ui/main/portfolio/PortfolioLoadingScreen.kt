package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonSkeletonColor

@Composable
fun PortfolioLoadingScreen() {
    val backgroundColor = commonSkeletonColor()

    CompositionLocalProvider(
        LocalShimmerTheme provides creditCardTheme
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = commonBackground())
                .shimmer()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(color = backgroundColor, shape = RoundedCornerShape(5.dp))
            )

            repeat(2) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .height(35.dp)
                        .background(color = backgroundColor, shape = RoundedCornerShape(5.dp))
                )
            }

            repeat(4) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(color = backgroundColor, shape = RoundedCornerShape(5.dp))
                )
            }
        }
    }
}

val creditCardTheme = defaultShimmerTheme.copy(
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