package org.jeonfeel.moeuibit2.data.remote.retrofit

data class ApiResult<T>(val status: Status,val data: T?,val message: String?) {

    enum class Status {
        LOADING,
        SUCCESS,
        API_ERROR,
        NETWORK_ERROR
    }

    companion object{
        fun <T> success(data: T?): ApiResult<T> {
            return ApiResult(Status.SUCCESS, data, null)
        }

        fun <T> error(data: T?, message: String): ApiResult<T> {
            return ApiResult(Status.API_ERROR, data, message)
        }

        fun <T> error(exception: Exception?): ApiResult<T> {
            return ApiResult(Status.NETWORK_ERROR, null, exception?.message)
        }

        fun <T> loading(): ApiResult<T> {
            return ApiResult(Status.LOADING, null, null)
        }

    }
}