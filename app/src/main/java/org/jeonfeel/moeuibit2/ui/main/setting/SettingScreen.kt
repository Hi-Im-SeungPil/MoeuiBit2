package org.jeonfeel.moeuibit2.ui.main.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.UrlConst
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.coinsite.secsions.NativeAdTemplateView
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDialogBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDialogButtonsBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonRejectTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.ext.showToast

@Composable
fun SettingScreen(viewModel: SettingViewModel = hiltViewModel()) {
    BackHandler(viewModel.state.openSourceState.value) {
        viewModel.state.openSourceState.value = false
    }
    AddLifecycleEvent(
        onCreateAction = {
        },
        onPauseAction = {
            if (viewModel.state.openSourceState.value)
                viewModel.state.openSourceState.value = false
        }
    )

    Column(modifier = Modifier.background(color = commonBackground())) {
        Row(modifier = Modifier.background(color = commonBackground())) {
            Text(
                text = stringResource(id = R.string.setting),
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    .weight(1f, true)
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    color = commonTextColor(),
                    fontSize = DpToSp(20.dp),
                    fontWeight = FontWeight.W600
                )
            )
            Text(
                text = "",
                modifier = Modifier
                    .padding(21.dp)
                    .wrapContentWidth(),
                style = TextStyle(
                    color = commonTextColor(),
                    fontSize = DpToSp(dp = 13.dp)
                )
            )
        }
        Divider(
            Modifier
                .fillMaxWidth()
                .height(1.dp), color = commonDividerColor()
        )

        Box(modifier = Modifier.fillMaxSize()) {
            SettingScreenLazyColumn(viewModel)
        }
    }

    if (viewModel.state.openSourceState.value) {
        OpenSourceLicenseLazyColumn(viewModel.state.openSourceState)
    }
}

@Composable
fun SettingScreenLazyColumn(viewModel: SettingViewModel) {
    val context = LocalContext.current
    val resetDialogState = remember {
        mutableStateOf(false)
    }
    val transactionInfoDialogState = remember {
        mutableStateOf(false)
    }
    val themeDialogState = remember {
        mutableStateOf(false)
    }
//    ThemeDialog(themeDialogState)
    if (themeDialogState.value) {
        ThemeSettingDialog(
            dismissRequest = { themeDialogState.value = false },
            currentSettingTheme = viewModel.state.currentTheme.value,
            setTheme = {
                viewModel.updateTheme(it)
            }
        )
    }
    TwoButtonCommonDialog(dialogState = resetDialogState,
        icon = R.drawable.img_app_clear_3x,
        content = stringResource(id = R.string.resetDialogContent),
        leftButtonText = stringResource(id = R.string.cancel),
        rightButtonText = stringResource(id = R.string.confirm),
        leftButtonAction = { resetDialogState.value = false },
        rightButtonAction = {
            viewModel.removeAll()
            resetDialogState.value = false
            context.showToast(context.getString(R.string.resetDialogResetSuccess))
        })
    TwoButtonCommonDialog(dialogState = transactionInfoDialogState,
        icon = R.drawable.img_trade_clear_3x,
        content = stringResource(id = R.string.init_message),
        leftButtonText = stringResource(id = R.string.cancel),
        rightButtonText = stringResource(id = R.string.confirm),
        leftButtonAction = { transactionInfoDialogState.value = false },
        rightButtonAction = {
            viewModel.resetTransactionInfo()
            transactionInfoDialogState.value = false
        })
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = commonBackground())
    ) {
        item {
            ADSection()
        }

        item {
            SettingScreenLazyColumnItem(
                imgId = R.drawable.img_write,
                stringResource(id = R.string.write_review), clickAction = {
                    writeReviewAction(context)
                })
            SettingScreenLazyColumnItem(
                imgId = R.drawable.img_trade_clear,
                text = stringResource(id = R.string.init_title),
                clickAction = {
                    transactionInfoDialogState.value = true
                })
            SettingScreenLazyColumnItem(
                imgId = R.drawable.img_app_clear,
                text = stringResource(id = R.string.init_app),
                clickAction = {
                    resetDialogState.value = true
                })
            SettingScreenLazyColumnItem(
                imgId = R.drawable.img_brush,
                text = "테마 설정",
                clickAction = {
                    themeDialogState.value = true
                })
            SettingScreenLazyColumnItem(
                imgId = R.drawable.img_license,
                text = stringResource(id = R.string.open_source_license),
                clickAction = {
                    viewModel.state.openSourceState.value = true
                })
        }
    }
}

@Composable
private fun SettingScreenLazyColumnItem(imgId: Int, text: String, clickAction: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(10.dp, 20.dp, 10.dp, 0.dp)
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .background(portfolioMainBackground(), shape = RoundedCornerShape(size = 10.dp))
            .noRippleClickable { clickAction() }
            .padding(20.dp, 15.dp)
    ) {
        Icon(
            painter = painterResource(imgId),
            modifier = Modifier.size(20.dp),
            tint = commonTextColor(),
            contentDescription = ""
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 15.dp),
            style = TextStyle(
                fontSize = DpToSp(17.dp),
                color = commonTextColor()
            )
        )
    }
}

@Composable
private fun ThemeSettingDialog(
    dismissRequest: () -> Unit,
    currentSettingTheme: ThemeHelper.ThemeMode,
    setTheme: (theme: ThemeHelper.ThemeMode) -> Unit,
) {
    val selectedTheme = remember {
        mutableStateOf(currentSettingTheme)
    }
    Dialog(onDismissRequest = {
        selectedTheme.value = currentSettingTheme
        dismissRequest()
    }) {
        Column(
            modifier = Modifier.background(
                commonDialogBackground(),
                shape = RoundedCornerShape(10.dp)
            )
        ) {
            ThemeSettingDialogItem(
                icon = R.drawable.img_theme_light,
                "라이트 모드",
                clickAction = {
                    selectedTheme.value = ThemeHelper.ThemeMode.LIGHT
                },
                selectedValue = selectedTheme.value,
                theme = ThemeHelper.ThemeMode.LIGHT
            )
            ThemeSettingDialogItem(
                icon = R.drawable.img_theme_dark,
                "다크 모드",
                clickAction = {
                    selectedTheme.value = ThemeHelper.ThemeMode.DARK
                },
                selectedValue = selectedTheme.value,
                theme = ThemeHelper.ThemeMode.DARK
            )
            ThemeSettingDialogItem(
                icon = R.drawable.img_theme_system,
                "시스템 설정",
                clickAction = {
                    selectedTheme.value = ThemeHelper.ThemeMode.DEFAULT
                },
                selectedValue = selectedTheme.value,
                theme = ThemeHelper.ThemeMode.DEFAULT
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .background(
                        color = commonDialogButtonsBackground(),
                        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                    )
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.cancel), modifier = Modifier
                        .weight(1f)
                        .noRippleClickable {
                            selectedTheme.value = currentSettingTheme
                            dismissRequest()
                        }
                        .padding(0.dp, 10.dp),
                    style = TextStyle(
                        color = commonRejectTextColor(),
                        fontSize = DpToSp(dp = 17.dp),
                        textAlign = TextAlign.Center
                    )
                )
                Text(text = "적용",
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable {
                            setTheme(selectedTheme.value)
                            dismissRequest()
                        }
                        .padding(0.dp, 10.dp),
                    style = TextStyle(
                        color = commonTextColor(),
                        fontSize = DpToSp(17.dp),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

@Composable
private fun ThemeSettingDialogItem(
    icon: Int,
    text: String,
    clickAction: () -> Unit,
    selectedValue: ThemeHelper.ThemeMode,
    theme: ThemeHelper.ThemeMode,
) {
    val selectedColor = if (selectedValue == theme) {
        commonTextColor()
    } else {
        commonHintTextColor()
    }

    val selectedBorderColor = if (selectedValue == theme) {
        commonTextColor()
    } else {
        commonDialogBackground()
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
            .fillMaxWidth()
            .background(commonDialogBackground(), shape = RoundedCornerShape(10.dp))
            .border(1.dp, color = selectedBorderColor, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 25.dp, vertical = 20.dp)
            .noRippleClickable {
                clickAction.invoke()
            }
    ) {
        Icon(
            painter = painterResource(id = icon),
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.CenterVertically),
            tint = selectedColor,
            contentDescription = ""
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 25.dp)
                .align(Alignment.CenterVertically),
            style = TextStyle(
                fontSize = DpToSp(17.dp),
                fontWeight = FontWeight.W500,
                color = selectedColor
            )
        )
    }
}

@Composable
private fun ADSection() {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(top = 20.dp)
            .background(portfolioMainBackground(), shape = RoundedCornerShape(size = 10.dp))
            .padding(15.dp)
    ) {
        androidx.compose.material3.Text(
            text = "AD",
            style = TextStyle(
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W500,
                color = commonTextColor()
            )
        )

        NativeAdTemplateView()
    }
}

private fun writeReviewAction(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UrlConst.PLAY_STORE_URL))
    context.startActivity(intent)
}