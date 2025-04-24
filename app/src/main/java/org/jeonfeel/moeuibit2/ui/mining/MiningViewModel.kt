package org.jeonfeel.moeuibit2.ui.mining

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.network.retrofit.response.gitjson.GitJsonReferralItem
import org.jeonfeel.moeuibit2.data.usecase.MiningUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.common.UIState
import javax.inject.Inject

data class MiningInfoUIState(
    val uiState: UIState = UIState.LOADING,
    val miningInfo: List<GitJsonReferralItem> = emptyList(),
)

@HiltViewModel
class MiningViewModel @Inject constructor(
    private val miningUseCase: MiningUseCase,
) : ViewModel() {

    private val _uiState: MutableState<MiningInfoUIState> = mutableStateOf(MiningInfoUIState())
    val uiState: State<MiningInfoUIState> = _uiState

    private val miningInfo = mutableStateOf(emptyList<GitJsonReferralItem>())

    fun init(
        type: String,
    ) {
        viewModelScope.launch {
            fetchMiningInfo(type)
            createUIState()
        }
    }

    private fun createUIState() {
        _uiState.value = MiningInfoUIState(
            uiState = UIState.SUCCESS,
            miningInfo = miningInfo.value,
        )
    }

    private suspend fun fetchMiningInfo(type: String) {
        val api = when (type) {
            "app" -> miningUseCase.fetchAppMiningInfo()
            "depin" -> miningUseCase.fetchAppMiningInfo()
            "tg" -> miningUseCase.fetchAppMiningInfo()
            else -> miningUseCase.fetchAppMiningInfo()
        }

        api.collect { res ->
            when (res) {
                is ResultState.Loading -> {
                    _uiState.value = MiningInfoUIState(uiState = UIState.LOADING)
                }

                is ResultState.Success -> {
                    miningInfo.value = res.data
                }

                is ResultState.Error -> {
                    _uiState.value = MiningInfoUIState(uiState = UIState.ERROR)
                }
            }
        }
    }
}