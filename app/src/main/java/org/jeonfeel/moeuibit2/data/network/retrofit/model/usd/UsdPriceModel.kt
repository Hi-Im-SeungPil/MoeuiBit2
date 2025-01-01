package org.jeonfeel.moeuibit2.data.network.retrofit.model.usd

import com.google.errorprone.annotations.Keep

@Keep
data class UsdPriceModel (
    val date: String,
    val krw: Double
    )