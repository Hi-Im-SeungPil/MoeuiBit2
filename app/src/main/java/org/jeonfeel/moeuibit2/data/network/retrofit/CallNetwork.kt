package org.jeonfeel.moeuibit2.data.network.retrofit

import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun <T> networkCall(call: suspend () -> Response<T>): Flow<ApiResult<T>> {
    return flow {
        emit(ApiResult.loading())
        kotlin.runCatching {
            call()
        }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    emit(ApiResult.success(data = response.body()))
                } else {
                    emit(
                        ApiResult.error(null, response.errorBody()?.string() ?: "")
                    )
                }
            },
            onFailure = { e ->
                Logger.e(e.message.toString() ?: "")
                emit(ApiResult.error(e))
            }
        )
    }
}

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