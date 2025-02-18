package org.jeonfeel.moeuibit2.ui.main.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.PLAY_STORE_URL
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun SettingScreen(settingViewModel: SettingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler(settingViewModel.state.openSourceState.value) {
        settingViewModel.state.openSourceState.value = false
    }
    AddLifecycleEvent(
        onCreateAction = {
            Logger.e("onCreate")
        },
        onPauseAction = {
            if (settingViewModel.state.openSourceState.value)
                settingViewModel.state.openSourceState.value = false
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
            SettingScreenLazyColumn(settingViewModel)
        }
    }

    if (settingViewModel.state.openSourceState.value) {
        OpenSourceLicenseLazyColumn(settingViewModel.state.openSourceState)
    }
}

@Composable
fun SettingScreenLazyColumn(settingViewModel: SettingViewModel) {
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
    ThemeDialog(themeDialogState, settingViewModel.preferenceManager)
    ResetDialog(settingViewModel, resetDialogState, context)
    TwoButtonCommonDialog(dialogState = transactionInfoDialogState,
        title = stringResource(id = R.string.init_title),
        content = stringResource(id = R.string.init_message),
        leftButtonText = stringResource(id = R.string.cancel),
        rightButtonText = stringResource(id = R.string.confirm),
        leftButtonAction = { transactionInfoDialogState.value = false },
        rightButtonAction = {
            settingViewModel.resetTransactionInfo()
            transactionInfoDialogState.value = false
        })
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = commonBackground())
    ) {
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
                    settingViewModel.state.openSourceState.value = true
                })
        }
    }
}

@Composable
fun SettingScreenLazyColumnItem(imgId: Int, text: String, clickAction: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(10.dp, 20.dp, 10.dp, 0.dp)
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .background(portfolioMainBackground(), shape = RoundedCornerShape(size = 10.dp))
            .clickable { clickAction() }
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
fun ResetDialog(
    settingViewModel: SettingViewModel,
    resetDialogState: MutableState<Boolean>,
    context: Context,
) {
    if (resetDialogState.value) {
        Dialog(onDismissRequest = {
            resetDialogState.value = false
        }) {
            Card(
                Modifier
                    .padding(20.dp, 0.dp)
                    .wrapContentSize()
                    .padding(0.dp, 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .wrapContentSize()
                ) {
                    Text(
                        text = stringResource(id = R.string.resetDialogTitle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 20.dp, 0.dp, 0.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = DpToSp(25.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                    )
                    Text(
                        text = stringResource(id = R.string.resetDialogContent),
                        modifier = Modifier.padding(10.dp, 40.dp),
                        fontSize = DpToSp(17.dp),
                        style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                    )
                    Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 1.dp)
                    Row {
                        Text(
                            text = stringResource(id = R.string.commonCancel),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    resetDialogState.value = false
                                }
                                .padding(0.dp, 10.dp),
                            fontSize = DpToSp(17.dp),
                            textAlign = TextAlign.Center,
                            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                        )
                        Text(
                            text = "",
                            fontSize = DpToSp(17.dp),
                            modifier = Modifier
                                .width(1.dp)
                                .border(1.dp, Color.LightGray)
                                .padding(0.dp, 10.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.commonAccept),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    removeAll(settingViewModel)
                                    Toast
                                        .makeText(
                                            context,
                                            context.getString(R.string.resetDialogResetSuccess),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                    resetDialogState.value = false
                                }
                                .padding(0.dp, 10.dp),
                            fontSize = DpToSp(15.dp),
                            textAlign = TextAlign.Center,
                            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                        )
                    }
                }
            }
        }
    }
}

fun writeReviewAction(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL))
    context.startActivity(intent)
}

fun removeAll(settingViewModel: SettingViewModel) {
    settingViewModel.removeAll()
}