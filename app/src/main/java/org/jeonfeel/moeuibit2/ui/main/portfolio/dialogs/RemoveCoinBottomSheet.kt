package org.jeonfeel.moeuibit2.ui.main.portfolio.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.utils.OneTimeNetworkCheck
import org.jeonfeel.moeuibit2.utils.showToast
import kotlin.reflect.KFunction1

@Composable
fun RemoveCoinBottomSheet(
    dialogState: State<Boolean>,
    removeCoinList: List<Pair<String, String>>,
    hideSheet: () -> Unit,
    checkList: List<Boolean>,
    updateCheckedList: KFunction1<Int, Unit>
) {
    val context = LocalContext.current

    if (dialogState.value && removeCoinList.isEmpty()) {
        context.showToast("삭제할 코인이 없습니다.")
        hideSheet()
    } else if (OneTimeNetworkCheck.networkCheck(context) == null) {
        context.showToast("인터넷 상태를 확인해주세요.")
        hideSheet()
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            if (dialogState.value) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0x99000000))
                        .noRippleClickable { }
                )
            }

            AnimatedVisibility(
                visible = dialogState.value,
                enter = slideInVertically(
                    initialOffsetY = { it }
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                ) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .requiredHeightIn(max = 600.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(
                                topStart = 20.dp, topEnd = 20.dp
                            )
                        )
                        .padding(horizontal = 10.dp)
                        .noRippleClickable { },
                ) {
                    IconButton(
                        onClick = {
                            hideSheet()
                        }, modifier = Modifier
                            .padding(top = 15.dp)
                            .size(30.dp)
                            .align(Alignment.End)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }

                    Text(
                        text = "삭제할 코인을 선택해주세요",
                        modifier = Modifier
                            .padding(bottom = 15.dp)
                            .align(Alignment.CenterHorizontally),
                        style = TextStyle(
                            fontSize = DpToSp(23.dp),
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .noRippleClickable {
                            if (!checkList.all { it }) {
                                updateCheckedList(-1)
                            } else if (checkList.all { it }) {
                                updateCheckedList(-2)
                            }
                        }) {
                        Checkbox(
                            checked = checkList.all { it },
                            onCheckedChange = {
                                if (!checkList.all { it }) {
                                    updateCheckedList(-1)
                                } else if (checkList.all { it }) {
                                    updateCheckedList(-2)
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "전체선택",
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            style = TextStyle(
                                fontSize = DpToSp(20.dp),
                                color = if (checkList.all { it }) Color.Black else Color.LightGray,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Text(
                            text = "(${checkList.count { it }} / ${removeCoinList.size} )",
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .align(Alignment.CenterVertically),
                            style = TextStyle(fontSize = DpToSp(16.dp))
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        itemsIndexed(removeCoinList) { index, it ->
                            val (market, reason) = it

                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable {
                                    updateCheckedList(index)
                                }) {
                                Checkbox(
                                    checked = checkList[index],
                                    onCheckedChange = {
                                        updateCheckedList(index)
                                    },
                                    modifier = Modifier.align(Alignment.Top)
                                )
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.CenterVertically)
                                ) {
                                    Text(
                                        text = market,
                                        modifier = Modifier
                                            .padding(start = 10.dp),
                                        style = TextStyle(
                                            fontSize = DpToSp(17.dp),
                                            color = if (checkList[index]) Color.Black else Color.LightGray
                                        )
                                    )
                                    Text(
                                        text = reason,
                                        modifier = Modifier.padding(start = 10.dp),
                                        style = TextStyle(
                                            fontSize = DpToSp(13.dp),
                                            color = Color.LightGray
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth()
                            .background(
                                color = if (checkList.none { it }) Color(0xFFFFB4B4) else Color(
                                    0xFFFF4848
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(vertical = 20.dp)
                            .noRippleClickable {
                                if (checkList.none { it }) return@noRippleClickable


                                // 코인 삭제
                            }
                    ) {
                        Text(
                            text = "삭제",
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center),
                            style = TextStyle(color = Color.White, fontSize = DpToSp(22.dp))
                        )
                    }
                }
            }
        }
    }
}