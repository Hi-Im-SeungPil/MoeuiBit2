
package org.jeonfeel.moeuibit2.ui.base
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult

open class BaseUseCase {
    fun <T> requestApiResult(
        result: ApiResult<T>,
        onSuccess: (result: T) -> Any
    ): Flow<Any> {
        return flow {
            emit(ApiResult.loading<T>())
            if (result.status == ApiResult.Status.SUCCESS) {
                result.data?.let {
                    val data = result.data as T
                    data?.let {
                        emit(onSuccess(it))
                    }
                }
            } else {
                emit(result)
            }
        }
    }
}