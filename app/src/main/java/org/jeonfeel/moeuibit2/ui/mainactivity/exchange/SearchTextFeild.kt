package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.graphics.Rect
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@Composable
fun SearchBasic(
    modifier: Modifier = Modifier,
    searchTextFieldValue: MutableState<String> = mutableStateOf(""),
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable (onClick: () -> Unit) -> Unit)? = null,
    placeholderText: String = "Placeholder",
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize,
) {
    val focusManager = LocalFocusManager.current
    val onClick = {
        searchTextFieldValue.value = ""
        focusManager.clearFocus(true)
    }

    BasicTextField(value = searchTextFieldValue.value, onValueChange = {
        searchTextFieldValue.value = it
    }, singleLine = true,
        modifier = modifier.clearFocusOnKeyboardDismiss(),
        textStyle = TextStyle(color = colorResource(id = R.color.C0F0F5C),
            fontSize = 17.sp),
        decorationBox = { innerTextField ->
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    leadingIcon()
                }
                Box(Modifier.weight(1f)) {
                    if (searchTextFieldValue.value.isEmpty()) {
                        Text(
                            placeholderText,
                            style = LocalTextStyle.current.copy(
                                color = colorResource(id = R.color.C0F0F5C),
                                fontSize = fontSize
                            )
                        )
                    }
                    innerTextField()
                }
                if (trailingIcon != null && searchTextFieldValue.value.isNotEmpty()) {
                    trailingIcon(onClick = onClick)
                }
            }
        })
}

@Composable
fun SearchBasicTextFieldResult(exchangeViewModel: ExchangeViewModel = viewModel()) {
    SearchBasic(
        searchTextFieldValue = exchangeViewModel.searchTextFieldValue,
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clearFocusOnKeyboardDismiss(),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .size(25.dp),
                tint = colorResource(id = R.color.C0F0F5C)
            )
        },
        trailingIcon = {
            IconButton(onClick = { it.invoke() }) {
                Icon(Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(25.dp),
                    tint = colorResource(id = R.color.C0F0F5C))
            }
        },
        placeholderText = "코인명/심볼 검색",
        fontSize = 17.sp
    )
}

@Composable
fun rememberIsKeyboardOpen(): State<Boolean> {
    val view = LocalView.current

    return produceState(initialValue = view.isKeyboardOpen()) {
        val viewTreeObserver = view.viewTreeObserver
        val listener = android.view.ViewTreeObserver.OnGlobalLayoutListener { value = view.isKeyboardOpen() }
        viewTreeObserver.addOnGlobalLayoutListener(listener)

        awaitDispose { viewTreeObserver.removeOnGlobalLayoutListener(listener)  }
    }
}

fun View.isKeyboardOpen(): Boolean {
    val rect = Rect()
    getWindowVisibleDisplayFrame(rect);
    val screenHeight = rootView.height
    val keypadHeight = screenHeight - rect.bottom;
    return keypadHeight > screenHeight * 0.15
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