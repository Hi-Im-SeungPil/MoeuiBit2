package org.jeonfeel.moeuibit2.ui.main.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignBottom
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.playStoreUrl
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.viewmodels.SettingViewModel
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager

@Composable
fun SettingScreen(settingViewModel: SettingViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val balloon = remember {
        Balloon.Builder(context)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText(context.getString(R.string.settings_message))
            .setTextColorResource(R.color.white)
            .setTextGravity(Gravity.START)
            .setTextSize(15f)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setPadding(12)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.C6799FF)
            .setLifecycleOwner(lifecycleOwner)
            .build()
    }
    BackHandler(settingViewModel.state.openSourceState.value) {
        settingViewModel.state.openSourceState.value = false
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        10.dp,
                        ambientColor = MaterialTheme.colorScheme.onBackground,
                        spotColor = MaterialTheme.colorScheme.onBackground
                    ),
                backgroundColor = MaterialTheme.colorScheme.background,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Text(
                        text = stringResource(id = R.string.setting),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp, 0.dp, 0.dp, 0.dp)
                            .fillMaxHeight()
                            .wrapContentHeight()
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = DpToSp(25.dp),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    AndroidView(
                        factory = {
                            ImageView(it).apply {
                                val drawable = ContextCompat.getDrawable(it, R.drawable.img_info)
                                setImageDrawable(drawable)
                                setOnClickListener {
                                    showAlignBottom(balloon)
                                }
                            }
                        }, modifier = Modifier
                            .padding(0.dp, 0.dp, 10.dp, 0.dp)
                            .size(25.dp)
                            .wrapContentHeight()
                    )
                }

            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
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
    ThemeDialog(themeDialogState,settingViewModel.preferenceManager)
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
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        item {
            SettingScreenLazyColumnItem(stringResource(id = R.string.write_review), clickAction = {
                writeReviewAction(context)
            })
            SettingScreenLazyColumnItem(
                text = stringResource(id = R.string.init_title),
                clickAction = {
                    transactionInfoDialogState.value = true
                })
            SettingScreenLazyColumnItem(
                text = stringResource(id = R.string.init_app),
                clickAction = {
                    resetDialogState.value = true
                })
            SettingScreenLazyColumnItem(
                text = stringResource(id = R.string.open_source_license),
                clickAction = {
                    settingViewModel.state.openSourceState.value = true
                })
            SettingScreenLazyColumnItem(
                text = "테마 설정",
                clickAction = {
                    themeDialogState.value = true
                })
        }
    }
}

@Composable
fun SettingScreenLazyColumnItem(text: String, clickAction: () -> Unit) {
    Text(
        text = text, modifier = Modifier
            .padding(10.dp, 30.dp, 10.dp, 0.dp)
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onBackground)
            .clickable { clickAction() }
            .padding(10.dp, 10.dp),
        style = TextStyle(
            fontSize = DpToSp(25.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
    )
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
                Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background).wrapContentSize()) {
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
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl))
    context.startActivity(intent)
}

fun removeAll(settingViewModel: SettingViewModel) {
    settingViewModel.removeAll()
}