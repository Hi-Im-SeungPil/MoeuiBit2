package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.main.coinsite.ui_model.FearAndGreedyUIModel
import javax.inject.Inject

data class CoinMarketConditionUIState(
    val fearAndGreedyUIModel: FearAndGreedyUIModel = FearAndGreedyUIModel(),
)

@HiltViewModel
class CoinMarketConditionViewModel
@Inject constructor(private val alternativeUseCase: CoinMarketConditionUseCase) : ViewModel() {

    private val _uiState = mutableStateOf(CoinMarketConditionUIState())
    val uiState: State<CoinMarketConditionUIState> = _uiState

    private val fearAndGreedyUIModel = mutableStateOf(FearAndGreedyUIModel())

    fun onResume() {
        viewModelScope.launch {
            fetchFearAndGreedyIndex()
            parseToUiState()
        }
    }

    private suspend fun fetchFearAndGreedyIndex() {
        alternativeUseCase.fetchFearAndGreedyIndex().collect { res ->
            when (res) {
                is ResultState.Loading -> {

                }

                is ResultState.Success -> {
                    fearAndGreedyUIModel.value = res.data
                }

                else -> {
                    fearAndGreedyUIModel.value = FearAndGreedyUIModel(
                        index = 0,
                        indexDescription = "ERROR"
                    )
                }
            }
        }
    }

    private fun parseToUiState() {
        _uiState.value = CoinMarketConditionUIState(
            fearAndGreedyUIModel = fearAndGreedyUIModel.value
        )
    }
}