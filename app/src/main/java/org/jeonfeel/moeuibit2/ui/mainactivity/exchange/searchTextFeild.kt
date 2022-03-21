package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R

@Composable
fun SearchTextField(searchTextFieldValue: MutableState<String>) {
    val focusManager = LocalFocusManager.current
    TextField(value = searchTextFieldValue.value,
        onValueChange = { value ->
            searchTextFieldValue.value = value
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .size(25.dp)
            )
        },
        trailingIcon = {
            if (searchTextFieldValue.value.isNotEmpty()) {
                IconButton(onClick = {
                    searchTextFieldValue.value = ""
                    focusManager.clearFocus(true)
                }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(25.dp),
                    )
                }
            }
        },
        maxLines = 1,
        placeholder = { Text(text = "코인명/심볼 검색") },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
        colors = TextFieldDefaults.textFieldColors(
            textColor = colorResource(id = R.color.C0F0F5C),
            cursorColor = colorResource(id = R.color.C0F0F5C),
            leadingIconColor = colorResource(id = R.color.C0F0F5C),
            trailingIconColor = colorResource(id = R.color.C0F0F5C),
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            placeholderColor = colorResource(id = R.color.C0F0F5C)
        )
    )
}