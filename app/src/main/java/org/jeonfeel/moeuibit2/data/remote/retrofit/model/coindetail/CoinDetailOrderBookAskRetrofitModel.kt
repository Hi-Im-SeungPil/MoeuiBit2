package org.jeonfeel.moeuibit2.data.remote.retrofit.model.coindetail

import androidx.annotation.Keep

@Keep
data class CoinDetailOrderBookAskRetrofitModel(
    val ask_price: Double,
    val ask_size: Double,
)