package org.jeonfeel.moeuibit2.data.network.retrofit

import retrofit2.Response

 fun <T> networkCall(response: Response<T>): ApiResult<T> {
    return try {
        if (response.isSuccessful) {
            ApiResult.success(response.body())
        } else {
            ApiResult.error(null, response.errorBody()?.string() ?: "")
        }
    } catch (e: Exception) {
        ApiResult.error(e)
    }
}