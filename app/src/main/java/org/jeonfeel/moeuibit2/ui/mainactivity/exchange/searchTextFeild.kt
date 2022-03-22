package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.R

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
        modifier = modifier,
        textStyle = TextStyle(color = colorResource(id = R.color.C0F0F5C), fontSize = 17.sp),
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
fun SearchBasicTextFieldResult(searchTextFieldValue: MutableState<String> = mutableStateOf("")) {
    SearchBasic(
        searchTextFieldValue = searchTextFieldValue,
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .drawWithContent {
                drawContent()
                clipRect {
                    val strokeWidth = Stroke.DefaultMiter
                    val y = size.height // - strokeWidth
                    drawLine(
                        brush = SolidColor(Color.LightGray),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Square,
                        start = Offset.Zero.copy(y = y),
                        end = Offset(x = size.width, y = y)
                    )
                }
            },
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

@Preview(showBackground = true)
@Composable
fun PreviewSearch2() {
    SearchBasic(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .drawWithContent {
                drawContent()
                clipRect {
                    val strokeWidth = Stroke.DefaultMiter
                    val y = size.height // - strokeWidth
                    drawLine(
                        brush = SolidColor(Color.LightGray),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Square,
                        start = Offset.Zero.copy(y = y),
                        end = Offset(x = size.width, y = y)
                    )
                }
            },
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