package org.jeonfeel.moeuibit2.ui.base

import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult

open class BaseCommunicationModule {
    protected suspend fun <T> executeUseCase(
        target: Flow<Any>,
        onLoading: ((result: ApiResult<*>) -> Unit)? = null,
        onComplete: suspend (T) -> Unit,
        onApiError: suspend ((result: ApiResult<*>) -> Unit) = {},
        onNetworkError: ((result: ApiResult<*>) -> Unit)? = null,
    ) {
        target.collect { result ->
            if (result is ApiResult<*>) {
                when (result.status) {
                    ApiResult.Status.LOADING -> {
                        onLoading?.let {
                            it(result)
                        }
                    }

                    ApiResult.Status.SUCCESS -> {
                    }

                    ApiResult.Status.API_ERROR -> {
                        onApiError?.let {
                            it(result)
                        }
                    }

                    ApiResult.Status.NETWORK_ERROR -> {
                        onNetworkError?.let {
                            it(result)
                        }
                    }
                }
            } else {
                val data = result as T
                data?.let {
                    onComplete(it)
                }
            }
        }
    }
}