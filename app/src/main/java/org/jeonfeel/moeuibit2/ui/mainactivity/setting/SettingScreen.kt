package org.jeonfeel.moeuibit2.ui.mainactivity.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.widget.ImageView
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.balloon.*
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.activity.opensource.OpenSourceLicense
import org.jeonfeel.moeuibit2.constant.noticeBoard
import org.jeonfeel.moeuibit2.constant.playStoreUrl
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog

@Composable
fun SettingScreen(mainViewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val balloon = remember {
        Balloon.Builder(context)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("· 별점 5개는 사랑입니다 ♥\n\n· 업비트가 점검중일때는 앱이 동작하지 않습니다.")
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

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = colorResource(id = R.color.design_default_color_background),
            ) {
                Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                    Text(
                        text = "설정",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp, 0.dp, 0.dp, 0.dp)
                            .fillMaxHeight()
                            .wrapContentHeight()
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 25.sp,
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
                            .align(Alignment.CenterVertically)
                            .size(25.dp)
                    )
                }

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
    val transactionInfoDialogState = remember {
        mutableStateOf(false)
    }
    ResetDialog(mainViewModel, resetDialogState, context)
    TwoButtonCommonDialog(dialogState = transactionInfoDialogState,
        title = "거래내역 초기화",
        content = "모든 코인의 거래 내역이 초기화됩니다\n\n초기화 하시겠습니까?",
        leftButtonText = "취소",
        rightButtonText = "확인",
        leftButtonAction = { transactionInfoDialogState.value = false },
        rightButtonAction = {
            mainViewModel.resetTransactionInfo()
            transactionInfoDialogState.value = false
        })
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            SettingScreenLazyColumnItem("별점 주기(리뷰 작성)", clickAction = {
                writeReviewAction(context)
            })
            SettingScreenLazyColumnItem(text = "공지사항", clickAction = {
                moveNoticeBoard(context)
            })
            SettingScreenLazyColumnItem(text = "거래내역 초기화", clickAction = {
                transactionInfoDialogState.value = true
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