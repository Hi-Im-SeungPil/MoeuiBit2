package org.jeonfeel.moeuibit2.ui.main.coinsite.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
private fun getGradientColorStops(): List<Pair<Float, Color>> {
    val isDarkTheme = isSystemInDarkTheme()
    return if (isDarkTheme) {
        listOf(
            0.5f to Color(0xFF40C4FF),  // 180도 (0%): 극단적 공포 - 밝은 스카이 블루
            0.75f to Color(0xFF66BB6A), // 270도 (50%): 중립 - 부드러운 초록
            1.0f to Color(0xFFEF5350)   // 360도 (100%): 극단적 탐욕 - 밝은 레드
        )
    } else {
        listOf(
            0.5f to Color(0xFF0288D1),  // 180도 (0%): 극단적 공포 - 깊은 파랑
            0.75f to Color(0xFF4CAF50), // 270도 (50%): 중립 - 밝은 초록
            1.0f to Color(0xFFD32F2F)   // 360도 (100%): 극단적 탐욕 - 깊은 빨강
        )
    }
}

// 공포 탐욕 게이지 컴포저블
@Composable
fun AnimatedHalfGauge(fearGreedIndex: Int, fearGreedyIndexDescription: String) {
    val animatedProgress = remember { Animatable(0f) }
    val textColor = commonTextColor()
    val gradientColor = getGradientColorStops()

    // 애니메이션 적용
    LaunchedEffect(fearGreedIndex) {
        animatedProgress.animateTo(
            targetValue = fearGreedIndex.toFloat(),
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "공포 탐욕 지수",
                style = TextStyle(
                    fontSize = DpToSp(14.dp),
                    fontWeight = FontWeight.W500,
                    color = commonTextColor()
                )
            )

            Box(contentAlignment = Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(150.dp)
                ) {
                    val strokeWidth = 30f
                    val radius = size.width / 2 - strokeWidth
                    val center = Offset(size.width / 2, (size.height * 0.7).toFloat())

                    val gradientBrush = Brush.sweepGradient(
                        colorStops = gradientColor.toTypedArray(),
                        center = center
                    )

                    drawArc(
                        brush = gradientBrush,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth, (size.height * 0.7).toFloat() - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )

                    val needleAngle = 180f + (animatedProgress.value / 100f) * 180f
                    val needleEnd = Offset(
                        center.x + radius * cos(Math.toRadians(needleAngle.toDouble())).toFloat(),
                        center.y + radius * sin(Math.toRadians(needleAngle.toDouble())).toFloat()
                    )

                    drawLine(
                        color = APP_PRIMARY_COLOR,
                        start = center,
                        end = needleEnd,
                        strokeWidth = 10f
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .height(150.dp)
        ) {
            Text(
                text = animatedProgress.value.toInt().toString(),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(1f)
                    .padding(top = 10.dp),
                style = TextStyle(
                    fontSize = DpToSp(30.dp),
                    fontWeight = FontWeight.W500,
                    color = commonTextColor(),
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = fearGreedyIndexDescription,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(1f)
                    .padding(top = 10.dp),
                style = TextStyle(
                    fontSize = DpToSp(30.dp),
                    fontWeight = FontWeight.W500,
                    color = commonTextColor(),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}