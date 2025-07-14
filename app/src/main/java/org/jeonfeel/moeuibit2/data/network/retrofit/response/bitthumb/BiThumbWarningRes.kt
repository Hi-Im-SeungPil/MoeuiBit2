package org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BiThumbWarningRes(
    val market: String,

    @SerialName("warning_type")
    val warningType: String,

    @SerialName("end_date")
    val endDate: String
)
