package org.jeonfeel.moeuibit2.ui.main.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager

@Composable
fun ThemeDialog(dismissRequest: MutableState<Boolean>, preferenceManager: PreferenceManager) {
    val radioText = remember {
        listOf("라이트 모드", "다크 모드", "시스템 설정값")
    }
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioText[2]) }

    if (dismissRequest.value) {
        Dialog(onDismissRequest = { dismissRequest.value }) {
            Card {
                Column {
                    radioText.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = {
                                        onOptionSelected(text)
                                    }
                                )
                                .padding(horizontal = 16.dp)
                        ) {
                            RadioButton(
                                selected = text == selectedOption,
                                onClick = { onOptionSelected(text) }
                            )
                            Text(
                                text = text,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                    Button(onClick = {
                        dismissRequest.value = false
                        preferenceManager.setValue("themeMode", selectedOption)
                        val theme = when (selectedOption) {
                            "라이트 모드" -> ThemeHelper.ThemeMode.LIGHT
                            "다크모드" -> ThemeHelper.ThemeMode.DARK
                            else -> ThemeHelper.ThemeMode.DEFAULT
                        }
                        ThemeHelper.applyTheme(theme)
                    }) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}