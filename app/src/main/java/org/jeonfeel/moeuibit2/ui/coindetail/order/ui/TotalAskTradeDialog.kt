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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
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
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDialogBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonFallColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForBtc
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForKRW
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForQuantity
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.ext.showToast
import java.math.BigDecimal

@Composable
fun TotalAskTradeDialog(
    dialogState: MutableState<Boolean>,
    userCoin: State<MyCoin>,
    isKrw: Boolean,
    requestAsk: (String, Double, Double, BigDecimal, Double) -> Unit,
    commonExchangeModelState: State<CommonExchangeModel?>,
) {
    val context = LocalContext.current

    val textFieldValue = remember {
        mutableStateOf("")
    }

    val userCoinValue = if (isKrw) {
        commonExchangeModelState.value?.let {
            userCoin.value.quantity * it.tradePrice.toDouble()
        } ?: 0.0
    } else {
        commonExchangeModelState.value?.let {
            userCoin.value.quantity * it.tradePrice.toDouble()
        } ?: 0.0
    }

    val sellingTotal = if (textFieldValue.value.isNotEmpty()) {
        val amount = textFieldValue.value.replace(",", "").toDouble()
        commonExchangeModelState.value?.let {
            (amount / it.tradePrice.toDouble()).eighthDecimal().toDouble()
        } ?: 0.0
    } else {
        0.0
    }

    if (dialogState.value) {
        Dialog(
            onDismissRequest = {
                textFieldValue.value = ""
                dialogState.value = false
            }
        ) {
            Column(
                modifier = Modifier
                    .background(
                        commonDialogBackground(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = "총액 지정하여 매도",
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    style = TextStyle(
                        fontWeight = FontWeight.W600,
                        fontSize = DpToSp(16.dp),
                        color = commonTextColor()
                    )
                )
                Item(
                    text = "보유",
                    value = if (isKrw) BigDecimal(userCoinValue).formattedStringForKRW() else BigDecimal(userCoinValue).formattedStringForBtc(),
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
                    value = BigDecimal(sellingTotal).formattedStringForQuantity() ?: "0",
                    symbol = commonExchangeModelState.value?.symbol ?: ""
                )

                Divider(
                    Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .height(2.dp), color = APP_PRIMARY_COLOR
                )

                ButtonList(
                    textFieldValue = textFieldValue,
                    isKrw = isKrw
                )

                Text(
                    text = "초기화",
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .align(Alignment.End)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 10.dp, horizontal = 14.dp)
                        .noRippleClickable {
                            textFieldValue.value = ""
                        },
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = DpToSp(14.dp),
                        color = Color.Black
                    )
                )

                Row(modifier = Modifier.padding(top = 15.dp)) {
                    Text(
                        "총액",
                        modifier = Modifier
                            .padding(end = 15.dp)
                            .align(Alignment.CenterVertically),
                        style = TextStyle(fontSize = DpToSp(15.dp), color = commonTextColor())
                    )
                    TransparentTextField(textFieldValue, isKrw = isKrw)
                    Text(
                        if (isKrw) " KRW" else " BTC",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = TextStyle(fontSize = DpToSp(15.dp), color = commonTextColor())
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
                                shape = RoundedCornerShape(10.dp)
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
                            .background(
                                color = commonFallColor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(vertical = 15.dp)
                            .noRippleClickable {
                                if (textFieldValue.value.isEmpty()) return@noRippleClickable

                                val total = textFieldValue.value
                                    .replace(",", "")
                                    .toDouble()

                                val minusCommission = if (isKrw) {
                                    total * 0.9995
                                } else {
                                    total * 0.9975
                                }

                                if (askConditionCheck(
                                        commonExchangeModelState = commonExchangeModelState,
                                        context = context,
                                        totalPrice = textFieldValue.value
                                            .replace(",", "")
                                            .toDouble(),
                                        userCoin = userCoin,
                                    )
                                ) {
                                    requestAsk(
                                        commonExchangeModelState.value?.market ?: "",
                                        sellingTotal.toDouble(),
                                        minusCommission,
                                        commonExchangeModelState.value?.tradePrice
                                            ?: BigDecimal.ZERO,
                                        minusCommission
                                    )
                                    textFieldValue.value = ""
                                    context.showToast("매도 주문이 완료 되었습니다.")
                                    dialogState.value = false
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

fun askConditionCheck(
    commonExchangeModelState: State<CommonExchangeModel?>,
    context: Context,
    totalPrice: Double,
    userCoin: State<MyCoin>,
): Boolean {
    val isKrw = (commonExchangeModelState.value?.market ?: "").startsWith(UPBIT_KRW_SYMBOL_PREFIX)
    val userCoinQuantity = userCoin.value.quantity
    val userCoinTotalPrice = commonExchangeModelState.value?.let {
        if (isKrw) {
            BigDecimal(userCoinQuantity * it.tradePrice.toDouble()).formattedStringForKRW()
                .replace(",", "").toDouble()
        } else {
            BigDecimal(userCoinQuantity * it.tradePrice.toDouble()).formattedStringForBtc()
                .replace(",", "").toDouble()
        }
    } ?: 0.0

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

        !Utils.isNetworkAvailable(context) || !NetworkConnectivityObserver.isNetworkAvailable.value -> {
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
