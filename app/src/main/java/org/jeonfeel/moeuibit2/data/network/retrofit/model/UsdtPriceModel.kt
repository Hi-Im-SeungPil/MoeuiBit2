package org.jeonfeel.moeuibit2.data.network.retrofit.model

import com.google.errorprone.annotations.Keep

@Keep
data class UsdtPriceModel (
    val date: String,
    val krw: Double
    )