package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinInfoModel
import org.jeonfeel.moeuibit2.ui.common.UIState
import javax.inject.Inject

data class CoinInfoScreenUIState(
    val coinInfoModel: CoinInfoModel? = null,
    val state: UIState = UIState.LOADING,
)

@HiltViewModel
class CoinInfoViewModel @Inject constructor(private val coinInfoUseCase: CoinInfoUseCase) :
    ViewModel() {

    private val _uiState: MutableState<CoinInfoScreenUIState> =
        mutableStateOf(CoinInfoScreenUIState())
    val uiState: State<CoinInfoScreenUIState> = _uiState

    fun init(symbol: String) {
        if (_uiState.value.state == UIState.SUCCESS) {
            return
        }

        viewModelScope.launch {
            fetchCoinInfoFromFirebase(symbol)
        }
    }

    private fun fetchCoinInfoFromFirebase(symbol: String) {
        coinInfoUseCase.fetchCoinInfoData(symbol = symbol, callback = {
            _uiState.value = CoinInfoScreenUIState(
                coinInfoModel = it,
                state = UIState.SUCCESS
            )
        }, onCancel = {
            dismissLoading()
        })
    }

    fun dismissLoading() {
        _uiState.value = CoinInfoScreenUIState(
            coinInfoModel = null,
            state = UIState.DISMISS_LOADING
        )
    }
}