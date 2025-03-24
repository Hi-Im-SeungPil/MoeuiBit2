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
private fun getStagesColors(): List<Triple<Float, Float, Color>> {
    val isDarkTheme = isSystemInDarkTheme()
    return if (isDarkTheme) {
        // 다크 모드용 색상
        listOf(
            Triple(0f, 25f, Color(0xFF40C4FF)), // 극단적 공포: 밝은 스카이 블루
            Triple(25f, 48f, Color(0xFF66BB6A)), // 공포: 부드러운 초록
            Triple(48f, 52f, Color(0xFFCFD8DC)), // 중립: 밝은 회색
            Triple(52f, 75f, Color(0xFFFFCA28)), // 탐욕: 밝은 황금빛
            Triple(75f, 100f, Color(0xFFEF5350)) // 극단적 탐욕: 밝은 레드
        )
    } else {
        // 라이트 모드용 색상
        listOf(
            Triple(0f, 25f, Color(0xFF0288D1)), // 극단적 공포: 깊은 파랑
            Triple(25f, 45f, Color(0xFF4CAF50)), // 공포: 밝은 초록
            Triple(45f, 55f, Color(0xFFB0BEC5)), // 중립: 연한 회색
            Triple(55f, 75f, Color(0xFFFFB300)), // 탐욕: 황금빛 주황
            Triple(75f, 100f, Color(0xFFD32F2F)) // 극단적 탐욕: 깊은 빨강
        )
    }
}

@Composable
fun RowScope.AnimatedHalfGauge(fearGreedIndex: Int) {
    val animatedProgress = remember { Animatable(0f) }
    val stages = getStagesColors()
    val textColor = commonTextColor()

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

                    // 테마에 따른 색상 가져오기

                    stages.forEach { (start, end, stageColor) ->
                        val startAngle = 180f + (start / 100f) * 180f
                        val sweepAngle = ((end - start) / 100f) * 180f
                        drawArc(
                            color = stageColor,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(strokeWidth, size.height / 2 - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                    }

                    // 바늘 그리기
                    val needleAngle = 180f + (animatedProgress.value / 100f) * 180f
                    val needleLength = radius
                    val needleEnd = Offset(
                        center.x + needleLength * cos(Math.toRadians(needleAngle.toDouble())).toFloat(),
                        center.y + needleLength * sin(Math.toRadians(needleAngle.toDouble())).toFloat()
                    )
                    drawLine(
                        color = textColor,
                        start = center,
                        end = needleEnd,
                        strokeWidth = 10f
                    )
                }

                Text(
                    text = animatedProgress.value.toInt().toString(),
                    modifier = Modifier.padding(top = 30.dp),
                    color = commonTextColor(),
                    fontSize = DpToSp(18.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun getFearGreedyStatus(value: Int): String {
    return when {
        value <= 25 -> "극단적 공포" // 극단적 공포: 밝은 스카이 블루
        value < 45 -> "공포" // 공포: 부드러운 초록
        value <= 55 -> "중립" // 중립: 밝은 회색
        value <= 75 -> "탐욕" // 탐욕: 밝은 황금빛
        else -> "극단적 탐욕" // 극단적 탐욕: 밝은 레드
    }
}