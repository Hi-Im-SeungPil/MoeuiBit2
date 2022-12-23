package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.ui.util.clearFocusOnKeyboardDismiss

@Composable
fun SearchBasicTextFieldResult(mainViewModel: MainViewModel = viewModel()) {
    SearchBasic(
        mainViewModel = mainViewModel,
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
        placeholderText = stringResource(id = R.string.textFieldText),
        fontSize = 17.sp
    )
}

@Composable
fun SearchBasic(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(),
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable (onClick: () -> Unit) -> Unit)? = null,
    placeholderText: String = "",
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize,
) {
    val focusManager = LocalFocusManager.current
    val onClick = {
        mainViewModel.searchTextFieldValueState.value = ""
        focusManager.clearFocus(true)
    }

    BasicTextField(value = mainViewModel.searchTextFieldValueState.value, onValueChange = {
        mainViewModel.searchTextFieldValueState.value = it
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
                    if (mainViewModel.searchTextFieldValueState.value.isEmpty()) {
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
                if (trailingIcon != null && mainViewModel.searchTextFieldValueState.value.isNotEmpty()) {
                    trailingIcon(onClick = onClick)
                }
            }
        })
}