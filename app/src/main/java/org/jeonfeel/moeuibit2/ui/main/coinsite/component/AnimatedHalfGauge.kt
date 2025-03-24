package org.jeonfeel.moeuibit2.ui.main.coinsite.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
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
fun RowScope.AnimatedHalfGauge(fearGreedIndex: Int) {
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

    Row(
        modifier = Modifier
            .background(color = commonBackground(), RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "공포 탐욕 지수 : ${getFearGreedyStatus(fearGreedIndex)}")
            Box(contentAlignment = Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .size(150.dp)
                        .fillMaxWidth()
                ) {
                    val strokeWidth = 30f
                    val radius = size.width / 2 - strokeWidth
                    val center = Offset(size.width / 2, size.height / 2)

                    // 그라디언트 브러시 생성
                    val gradientBrush = Brush.sweepGradient(
                        colorStops = gradientColor.toTypedArray(),
                        center = center
                    )

                    // 그라디언트 적용된 아크 그리기
                    drawArc(
                        brush = gradientBrush,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth, size.height / 2 - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )

                    // 바늘 그리기
                    val needleAngle = 180f + (animatedProgress.value / 100f) * 180f
                    val needleEnd = Offset(
                        center.x + radius * cos(Math.toRadians(needleAngle.toDouble())).toFloat(),
                        center.y + radius * sin(Math.toRadians(needleAngle.toDouble())).toFloat()
                    )
                    drawLine(
                        color = textColor,
                        start = center,
                        end = needleEnd,
                        strokeWidth = 10f
                    )
                }

                // 현재 값 텍스트
                Text(
                    text = animatedProgress.value.toInt().toString(),
                    modifier = Modifier.padding(top = 30.dp),
                    color = textColor,
                    fontSize = DpToSp(18.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 상태 문자열 반환
@Composable
private fun getFearGreedyStatus(value: Int): String {
    return when {
        value <= 25 -> "극단적 공포"
        value < 45 -> "공포"
        value <= 55 -> "중립"
        value <= 75 -> "탐욕"
        else -> "극단적 탐욕"
    }
}