package org.jeonfeel.moeuibit2.ui.custom

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
import androidx.compose.ui.platform.LocalFocusManager
import org.jeonfeel.moeuibit2.ui.custom.rememberIsKeyboardOpen

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