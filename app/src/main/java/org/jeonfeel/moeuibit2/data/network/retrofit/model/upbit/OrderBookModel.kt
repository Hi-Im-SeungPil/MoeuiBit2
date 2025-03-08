package org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit

import androidx.annotation.Keep
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import java.math.BigDecimal

@Keep
data class OrderBookModel(
    val price: BigDecimal,
    val size: Double,
    val kind: OrderBookKind
)
