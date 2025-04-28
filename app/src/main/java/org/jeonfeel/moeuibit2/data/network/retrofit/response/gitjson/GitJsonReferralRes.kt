package org.jeonfeel.moeuibit2.data.network.retrofit.response.gitjson

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class GitJsonReferralItem(
    val title: String = "",
    val url: String = "",
    val imageUrl: String = "",
    val type: String = "",
)