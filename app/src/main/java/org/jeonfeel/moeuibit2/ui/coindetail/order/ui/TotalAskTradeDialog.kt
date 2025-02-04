package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.coinsite.item.BYBIT_COLOR
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForBtc
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForKRW
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForQuantity
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.ext.showToast
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun TotalAskTradeDialog(
    dialogState: MutableState<Boolean>,
    userCoin: State<MyCoin>,
    isKrw: Boolean,
    requestAsk: (String, Double, Long, BigDecimal, Double) -> Unit,
    commonExchangeModelState: State<CommonExchangeModel?>,
) {
    val textFieldValue = remember {
        mutableStateOf("")
    }

    val sellingTotal = if (textFieldValue.value.isNotEmpty()) {
        if (isKrw) {
            commonExchangeModelState.value?.let {
                userCoin.value.quantity.toBigDecimal().multiply(it.tradePrice)
            } ?: BigDecimal.ZERO
        } else {
            commonExchangeModelState.value?.let {
                userCoin.value.quantity.toBigDecimal().multiply(it.tradePrice)
            } ?: BigDecimal.ZERO
        }
    } else {
        BigDecimal.ZERO
    }

    val context = LocalContext.current

    if (dialogState.value) {
        Dialog(
            onDismissRequest = {
                textFieldValue.value = ""
                dialogState.value = false
            }
        ) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White, shape = RoundedCornerShape(15.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.background(Color.White)) {
                    Text(
                        text = "총액 지정하여 매도",
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                        style = TextStyle(fontWeight = FontWeight.W600, fontSize = DpToSp(16.dp))
                    )
                    Item(
                        text = "보유",
                        value = if (isKrw) sellingTotal.formattedStringForKRW() else sellingTotal.formattedStringForBtc(),
                        symbol = if (isKrw) "KRW" else "BTC"
                    )
                    Item(
                        text = "현재가",
                        value = if (isKrw) commonExchangeModelState.value?.tradePrice?.formattedStringForKRW()
                            ?: "0" else commonExchangeModelState.value?.tradePrice?.formattedStringForBtc()
                            ?: "0",
                        symbol = if (isKrw) "KRW" else "BTC"
                    )
                    Item(
                        text = "매도 수량",
                        value = sellingTotal?.formattedStringForQuantity() ?: "0",
                        symbol = commonExchangeModelState.value?.symbol ?: ""
                    )

                    Divider(
                        Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .height(2.dp), color = Color(BYBIT_COLOR)
                    )

                    ButtonList(
                        textFieldValue = textFieldValue,
                        isKrw = isKrw
                    )

                    Row(modifier = Modifier.padding(top = 10.dp)) {
                        Text(
                            "총액",
                            modifier = Modifier
                                .padding(end = 15.dp)
                                .align(Alignment.CenterVertically)
                        )
                        TransparentTextField(textFieldValue, isKrw = isKrw)
                        Text(
                            if (isKrw) " KRW" else " BTC",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "취소",
                            modifier = Modifier
                                .weight(2f)
                                .background(
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(999.dp)
                                )
                                .padding(vertical = 15.dp)
                                .noRippleClickable {
                                    dialogState.value = false
                                    textFieldValue.value = ""
                                },
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                fontSize = DpToSp(14.dp)
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "매도",
                            modifier = Modifier
                                .weight(3f)
                                .background(color = Color.Red, shape = RoundedCornerShape(999.dp))
                                .padding(vertical = 15.dp)
                                .noRippleClickable {
                                    if (textFieldValue.value.isEmpty()) return@noRippleClickable

                                    if (askConditionCheck(
                                            commonExchangeModelState = commonExchangeModelState,
                                            context = context,
                                            totalPrice = textFieldValue.value
                                                .replace(",", "")
                                                .toBigDecimal(),
                                            userCoin = userCoin,
                                        )
                                    ) {
//                                        requestAsk(
//                                            commonExchangeModelState.value?.market ?: "",
//                                            buyingQuantity?.toDouble() ?: 0.0,
//                                            commonExchangeModelState.value?.tradePrice
//                                                ?: BigDecimal.ZERO,
//                                            textFieldValue.value
//                                                .replace(",", "")
//                                                .toDouble()
//                                        )
                                    }
                                },
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = DpToSp(14.dp),
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

fun askConditionCheck(
    commonExchangeModelState: State<CommonExchangeModel?>,
    context: Context,
    totalPrice: BigDecimal,
    userCoin: State<MyCoin>,
): Boolean {
    val isKrw = (commonExchangeModelState.value?.market ?: "").startsWith(UPBIT_KRW_SYMBOL_PREFIX)
    val userCoinQuantity = userCoin.value.quantity.toBigDecimal()
    val userCoinTotalPrice = commonExchangeModelState.value?.let {
        if (isKrw) {
            userCoinQuantity.multiply(it.tradePrice).formattedStringForKRW().replace(",", "")
                .toBigDecimal()
        } else {
            userCoinQuantity.multiply(it.tradePrice).formattedStringForBtc().replace(",", "")
                .toBigDecimal()
        }
    } ?: BigDecimal.ZERO

    when {
        commonExchangeModelState.value == null -> {
            context.showToast("인터넷 연결을 확인한 후 다시 시도해 주세요.")
            return false
        }

        commonExchangeModelState.value != null && commonExchangeModelState.value?.tradePrice?.toDouble() == 0.0 -> {
            context.showToast("가격이 0원 입니다. 정상화 후 다시 시도해 주세요")
            return false
        }

        totalPrice > userCoinTotalPrice -> {
            context.showToast("보유하신 수량이 매도 수량보다 적습니다.")
            return false
        }

        !Utils.isNetworkAvailable(context) -> {
            context.showToast("인터넷 연결을 확인한 후 다시 시도해 주세요.")
            return false
        }

        else -> {}
    }

    when {
        isKrw -> {
            when {
                totalPrice.toDouble() < 5000 -> {
                    context.showToast("최소 매도 총액은 5000원 입니다.")
                    return false
                }

                else -> {}
            }
        }

        !isKrw -> {
            when {
                totalPrice.toDouble() < 0.00005 -> {
                    context.showToast("최소 매도 총액은 0.00005BTC 입니다.")
                    return false
                }

                else -> {}
            }
        }

        else -> {}
    }

    return true
}
