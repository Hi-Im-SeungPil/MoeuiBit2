package org.jeonfeel.moeuibit2.ui.mainactivity.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.MainViewModel
import org.jeonfeel.moeuibit2.activity.opensource.OpenSourceLicense
import org.jeonfeel.moeuibit2.constant.noticeBoard
import org.jeonfeel.moeuibit2.constant.playStoreUrl

@Composable
fun SettingScreen(mainViewModel: MainViewModel = viewModel()) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = colorResource(id = R.color.design_default_color_background),
            ) {
                Text(
                    text = "설정",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp, 0.dp, 0.dp, 0.dp)
                        .fillMaxHeight()
                        .wrapContentHeight(),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            SettingScreenLazyColumn(mainViewModel)
        }
    }
}

@Composable
fun SettingScreenLazyColumn(mainViewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val resetDialogState = remember {
        mutableStateOf(false)
    }
    if (resetDialogState.value) {
        ResetDialog(mainViewModel, resetDialogState, context)
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            SettingScreenLazyColumnItem("별점 주기(리뷰 작성)", clickAction = {
                writeReviewAction(context)
            })
            SettingScreenLazyColumnItem(text = "공지사항", clickAction = {
                moveNoticeBoard(context)
            })
            SettingScreenLazyColumnItem(text = "앱 초기화", clickAction = {
                resetDialogState.value = true
            })
            SettingScreenLazyColumnItem(text = "오픈소스 라이선스", clickAction = {
                openLicense(context)
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
            .border(1.dp, Color.DarkGray)
            .clickable { clickAction() }
            .padding(10.dp, 10.dp),
        style = TextStyle(fontSize = 25.sp)
    )
}

@Composable
fun ResetDialog(
    mainViewModel: MainViewModel,
    resetDialogState: MutableState<Boolean>,
    context: Context
) {
    Dialog(onDismissRequest = {
        resetDialogState.value = false
    }) {
        Card(
            Modifier
                .padding(20.dp, 0.dp)
                .wrapContentSize()
                .padding(0.dp, 20.dp)
        ) {
            Column(modifier = Modifier.wrapContentSize()) {
                Text(
                    text = stringResource(id = R.string.resetDialogTitle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 20.dp, 0.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.resetDialogContent),
                    modifier = Modifier.padding(10.dp, 40.dp),
                    fontSize = 17.sp
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
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "",
                        fontSize = 17.sp,
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
                                resetAll(mainViewModel)
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
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun writeReviewAction(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl))
    context.startActivity(intent)
}

fun moveNoticeBoard(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(noticeBoard))
    context.startActivity(intent)
}

fun resetAll(mainViewModel: MainViewModel) {
    mainViewModel.resetAll()
}

fun openLicense(context: Context) {
    val intent = Intent(context, OpenSourceLicense::class.java)
    context.startActivity(intent)
}