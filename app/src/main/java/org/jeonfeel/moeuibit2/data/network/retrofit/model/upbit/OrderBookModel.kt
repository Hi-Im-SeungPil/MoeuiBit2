package org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit

import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import java.math.BigDecimal

data class OrderBookModel(
    val price: BigDecimal,
    val size: Double,
    val kind: OrderBookKind
)
