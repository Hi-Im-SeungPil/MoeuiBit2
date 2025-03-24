package org.jeonfeel.moeuibit2.data.network.retrofit.response.alternative

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetFearAndGreedyIndexRes(
    val name: String,
    val data: List<Data>
) {
    @Serializable
    data class Data(
        val value: String,

        @SerialName("value_classification")
        val valueClassification: String,

        val timestamp: String,

        @SerialName("time_until_update")
        val timeUntilUpdate: String = ""
    ) {
        fun parseValueClassificationEngToKor(): String {
            return when (valueClassification) {
                "Extreme Fear" -> {
                    "극도의 공포"
                }

                "Fear" -> {
                    "공포"
                }

                "Neutral" -> {
                    "중립"
                }

                "Greed" -> {
                    "탐욕"
                }

                "Extreme Greed" -> {
                    "극도의 탐욕"
                }

                else -> {
                    ""
                }
            }
        }
    }
}