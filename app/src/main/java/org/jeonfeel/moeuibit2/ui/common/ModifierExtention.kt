package org.jeonfeel.moeuibit2.ui.common

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.utils.showToast
import kotlin.math.abs

fun Modifier.drawUnderLine(
    lineColor: Color = Color.LightGray,
    strokeWidth: Float = Stroke.DefaultMiter
): Modifier = this
    .drawWithContent {
        drawContent()
        clipRect {
            val y = size.height
            drawLine(
                brush = SolidColor(lineColor),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Square,
                start = Offset.Zero.copy(y = y),
                end = Offset(x = size.width, y = y)
            )
        }
    }

fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }

    if (isFocused) {
        val isKeyboardOpen by rememberIsKeyboardOpen()

        val focusManager = LocalFocusManager.current
        LaunchedEffect(isKeyboardOpen) {
            if (isKeyboardOpen) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }
    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}

@Composable
fun Modifier.SwipeDetector(
    onSwipeLeftAction: () -> Unit,
    onSwipeRightAction: () -> Unit
): Modifier = composed {
    val offsetX = remember { mutableFloatStateOf(0f) }
    val direction = remember { mutableIntStateOf(-1) }

    this.pointerInput(Unit) {
        detectDragGestures(
            onDrag = { change, dragAmount ->
                change.consume()
                val (x, y) = dragAmount
                if (abs(x) > abs(y)) {
                    when {
                        x > 0 -> {    //right
                            offsetX.floatValue += x
                            direction.intValue = 0
                        }

                        x < 0 -> {  // left
                            offsetX.floatValue += x
                            direction.intValue = 1
                        }
                    }
                }
            },
            onDragEnd = {
                when (direction.intValue) {
                    0 -> {
                        if (offsetX.floatValue > 180) {
                            onSwipeLeftAction()
                        }
                        offsetX.floatValue = 0f
                    }

                    1 -> {
                        if (offsetX.floatValue < -180) {
                            onSwipeRightAction()
                        }
                        offsetX.floatValue = 0f
                    }
                }
            }
        )
    }
}