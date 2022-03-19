package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchTextField(searchTextFieldValue: MutableState<String>) {
    TextField(value = searchTextFieldValue.value,
        onValueChange = { value ->
            searchTextFieldValue.value = value
        },
        maxLines = 1,
        placeholder = { Text(text = "코인명/심볼 검색") },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp))
}