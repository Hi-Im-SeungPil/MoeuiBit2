package org.jeonfeel.moeuibit2.ui.base

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager

enum class NetworkState {
    CONNECTED,
    API_ERROR,
    NETWORK_ERROR
}

abstract class BaseViewModel(
    preferenceManager: PreferencesManager
) : ViewModel() {
    private val _networkErrorState = mutableStateOf(NetworkState.NETWORK_ERROR)
    val networkErrorState: State<NetworkState> get() = _networkErrorState

    protected val _loadingState = mutableStateOf(false)
    val loadingState: State<Boolean> = _loadingState

    private val _errorDialogState = mutableStateOf(false)
    val errorDialogState: State<Boolean> = _errorDialogState
    private var errorMessage = ""

    var rootExchange: String? = null

    init {
        rootExchange = ROOT_EXCHANGE_UPBIT
    }

    fun changeNetworkErrorState(networkState: NetworkState) {
        _networkErrorState.value = networkState
    }

    protected suspend fun <T> executeUseCase(
        target: Flow<Any>,
        onLoading: ((result: ApiResult<*>) -> Unit)? = null,
        onComplete: (T) -> Unit,
        onApiError: ((result: ApiResult<*>) -> Unit)? = null,
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
                        Logger.e("api error" + result.message.toString())
                    }

                    ApiResult.Status.NETWORK_ERROR -> {
                        onNetworkError?.let {
                            it(result)
                        }
                        Logger.e("network error " + result.message.toString())
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

    fun showErrorDialog(message: String) {
        errorMessage = message
        _errorDialogState.value = true
    }

    fun hideErrorDialog() {
        _errorDialogState.value = false
        errorMessage = ""
    }

    protected fun rootExchangeBranch(
        upbitAction: () -> Unit,
        bitthumbAction: () -> Unit,
    ) {
        when (rootExchange) {
            ExchangeViewModel.ROOT_EXCHANGE_UPBIT -> {
                upbitAction()
            }

            ExchangeViewModel.ROOT_EXCHANGE_BITTHUMB -> {
                bitthumbAction()
            }
        }
    }

    protected fun rootExchangeCoroutineBranch(
        upbitAction: suspend () -> Unit,
        bitthumbAction: suspend () -> Unit,
        dispatcher: CoroutineDispatcher = Dispatchers.Main
    ) {
        when (rootExchange) {
            ExchangeViewModel.ROOT_EXCHANGE_UPBIT -> {
                viewModelScope.launch(dispatcher) {
                    upbitAction()
                }
            }

            ExchangeViewModel.ROOT_EXCHANGE_BITTHUMB -> {
                viewModelScope.launch(dispatcher) {
                    bitthumbAction()
                }
            }
        }
    }
}