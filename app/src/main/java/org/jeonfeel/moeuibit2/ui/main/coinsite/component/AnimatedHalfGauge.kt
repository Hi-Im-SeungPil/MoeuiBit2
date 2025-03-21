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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun RowScope.AnimatedHalfGauge(fearGreedIndex: Int) {
    val animatedProgress = remember { Animatable(0f) }
    val color = getProgressColor(fearGreedIndex)

    LaunchedEffect(fearGreedIndex) {
        animatedProgress.animateTo(
            targetValue = fearGreedIndex.toFloat(),
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
    }

    Row(
        modifier = Modifier
            .background(color = portfolioMainBackground(), RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(text = "공포 탐욕 지수 : ${getFearGreedyStatus(fearGreedIndex)}")
            Box(contentAlignment = Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .size(150.dp)
                        .fillMaxWidth()
                ) {
                    val strokeWidth = 30f
                    val radius = size.width / 2 - strokeWidth
                    val totalSweepAngle = (animatedProgress.value / 100f) * 180f

                    val stages = listOf(
                        25f to Color(0xFF40C4FF), // 극단적 공포
                        50f to Color(0xFF66BB6A), // 공포
                        50f to Color(0xFFCFD8DC), // 중립 (범위 없음, 단일 값)
                        75f to Color(0xFFFFCA28), // 탐욕
                        100f to Color(0xFFEF5350) // 극단적 탐욕
                    )

                    var startAngle = 180f
                    var previousValue = 0f

                    drawArc(
                        color = Color.LightGray,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth, (size.height * 0.7).toFloat() - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth)
                    )

                    stages.forEach { (stageValue, stageColor) ->
                        if (animatedProgress.value > previousValue) {
                            val stageSweepAngle = ((stageValue - previousValue) / 100f) * 180f
                            val sweepAngle = minOf(
                                stageSweepAngle,
                                maxOf(0f, totalSweepAngle - (previousValue / 100f) * 180f)
                            )
                            if (sweepAngle > 0) {
                                drawArc(
                                    color = stageColor,
                                    startAngle = startAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    topLeft = Offset(strokeWidth, size.height / 2 - radius),
                                    size = Size(radius * 2, radius * 2),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                )
                                startAngle += sweepAngle
                            }
                        }
                        previousValue = stageValue
                    }
                }

                Text(
                    text = animatedProgress.value.toInt().toString(),
                    modifier = Modifier.padding(top = 30.dp),
                    color = color,
                    fontSize = DpToSp(18.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        GaugeGuide()
    }
}

@Composable
fun RowScope.GaugeGuide() {
    val colorNumList = listOf(1, 26, 50, 74, 99)

    Column(modifier = Modifier.weight(1f)) {
        colorNumList.forEach {
            Row(modifier = Modifier.padding(5.dp)) {
                Spacer(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color = getProgressColor(it), RoundedCornerShape(999.dp))
                )
                Text(text = getFearGreedyStatus(it))
            }
        }
    }
}

@Composable
private fun getProgressColor(value: Int, isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (isDarkTheme) {
        when {
            value <= 25 -> Color(0xFF40C4FF) // 극단적 공포: 밝은 스카이 블루
            value < 50 -> Color(0xFF66BB6A) // 공포: 부드러운 초록
            value == 50 -> Color(0xFFCFD8DC) // 중립: 밝은 회색
            value <= 75 -> Color(0xFFFFCA28) // 탐욕: 밝은 황금빛
            else -> Color(0xFFEF5350) // 극단적 탐욕: 밝은 레드
        }
    } else {
        when {
            value <= 25 -> Color(0xFF0288D1) // 극단적 공포: 깊은 파랑
            value < 50 -> Color(0xFF4CAF50) // 공포: 밝은 초록
            value == 50 -> Color(0xFFB0BEC5) // 중립: 연한 회색
            value <= 75 -> Color(0xFFFFB300) // 탐욕: 황금빛 주황
            else -> Color(0xFFD32F2F) // 극단적 탐욕: 깊은 빨강
        }
    }
}

@Composable
private fun getFearGreedyStatus(value: Int): String {
    return when {
        value <= 25 -> "극단적 공포" // 극단적 공포: 밝은 스카이 블루
        value < 50 -> "공포" // 공포: 부드러운 초록
        value == 50 -> "중립" // 중립: 밝은 회색
        value <= 75 -> "탐욕" // 탐욕: 밝은 황금빛
        else -> "극단적 탐욕" // 극단적 탐욕: 밝은 레드
    }
}