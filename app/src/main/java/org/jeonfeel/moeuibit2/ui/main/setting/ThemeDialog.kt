package org.jeonfeel.moeuibit2.ui.main.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager

@Composable
fun ThemeDialog(dismissRequest: MutableState<Boolean>, preferenceManager: PreferencesManager) {
    val radioText = remember {
        listOf("라이트 모드", "다크 모드", "시스템 설정값")
    }
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioText[2]) }

    if (dismissRequest.value) {
        Dialog(onDismissRequest = {
            dismissRequest.value
        }) {
            LaunchedEffect(key1 = dismissRequest.value) {
                //TODO : 테마 변경
//                when (preferenceManager.getString(PREF_KEY_THEME_MODE)) {
//                    ThemeHelper.ThemeMode.LIGHT.name -> onOptionSelected(radioText[0])
//                    ThemeHelper.ThemeMode.DARK.name -> onOptionSelected(radioText[1])
//                    else -> onOptionSelected(radioText[2])
//                }
            }

            Card {
                Column {
                    radioText.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(selected = (text == selectedOption), onClick = {
                                    onOptionSelected(text)
                                })
                                .padding(horizontal = 16.dp)
                        ) {
                            RadioButton(
                                selected = text == selectedOption,
                                onClick = { onOptionSelected(text) })
                            Text(
                                text = text,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .align(Alignment.CenterVertically),
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = DpToSp(
                                        dp = 15.dp
                                    )
                                )
                            )
                        }
                    }
                    Row {
                        TextButton(
                            onClick = {
                                //TODO : 테마 변경
//                                val selectedText =
//                                    when (preferenceManager.getString(PREF_KEY_THEME_MODE)) {
//                                        ThemeHelper.ThemeMode.LIGHT.name -> radioText[0]
//                                        ThemeHelper.ThemeMode.DARK.name -> radioText[1]
//                                        else -> radioText[2]
//                                    }
//                                onOptionSelected(selectedText)
//                                dismissRequest.value = false
                            }, modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(id = R.string.cancel), style = TextStyle(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = DpToSp(
                                        dp = 15.dp
                                    )
                                )
                            )
                        }
                        TextButton(onClick = {
                            val theme = when (selectedOption) {
                                "라이트 모드" -> {
                                    ThemeHelper.ThemeMode.LIGHT
                                }
                                "다크 모드" -> {
                                    ThemeHelper.ThemeMode.DARK
                                }
                                else -> {
                                    ThemeHelper.ThemeMode.DEFAULT
                                }
                            }
//                            preferenceManager.setValue(PREF_KEY_THEME_MODE, theme.name)
                            Logger.e(theme.name)
                            ThemeHelper.applyTheme(theme)
                            dismissRequest.value = false
                        }, modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = R.string.confirm),
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = DpToSp(
                                        dp = 15.dp
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}