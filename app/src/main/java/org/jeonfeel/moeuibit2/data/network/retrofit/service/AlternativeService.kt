package org.jeonfeel.moeuibit2.data.network.retrofit.service

import org.jeonfeel.moeuibit2.data.network.retrofit.response.alternative.GetFearAndGreedyIndexRes
import retrofit2.Response
import retrofit2.http.GET

interface AlternativeService {
    @GET("fng")
    suspend fun getFearAndGreedyIndex(): Response<GetFearAndGreedyIndexRes>
}