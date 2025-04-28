package org.jeonfeel.moeuibit2.data.network.retrofit.service

import kotlinx.serialization.json.JsonArray
import org.jeonfeel.moeuibit2.data.network.retrofit.response.gitjson.GitJsonReferralItem
import retrofit2.Response
import retrofit2.http.GET

interface GitJsonService {
    @GET("Hi-Im-SeungPil/referral/refs/heads/main/referral.json")
    suspend fun getReferralJson(): Response<List<GitJsonReferralItem>>
}