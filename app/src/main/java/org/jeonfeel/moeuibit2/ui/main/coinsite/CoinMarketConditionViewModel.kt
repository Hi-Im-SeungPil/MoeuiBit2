package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.usecase.CoinMarketConditionUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.main.coinsite.ui_model.FearAndGreedyUIModel
import javax.inject.Inject

data class CoinMarketConditionUIState(
    val fearAndGreedyUIModel: FearAndGreedyUIModel = FearAndGreedyUIModel(),
    val marketConditionUIState: State<MarketConditionScreenState> = mutableStateOf(MarketConditionScreenState.COIN_MARKET_CONDITION)
)

@HiltViewModel
class CoinMarketConditionViewModel
@Inject constructor(private val alternativeUseCase: CoinMarketConditionUseCase) : ViewModel() {

    private val _uiState = mutableStateOf(CoinMarketConditionUIState())
    val uiState: State<CoinMarketConditionUIState> = _uiState

    private val _marketConditionUIState =
        mutableStateOf(MarketConditionScreenState.COIN_MARKET_CONDITION)

    private val fearAndGreedyUIModel = mutableStateOf(FearAndGreedyUIModel())

    fun onResume() {
        viewModelScope.launch {
            fetchFearAndGreedyIndex()
            parseToUiState()
        }
    }

    private fun parseToUiState() {
        _uiState.value = CoinMarketConditionUIState(
            fearAndGreedyUIModel = fearAndGreedyUIModel.value,
            marketConditionUIState = _marketConditionUIState
        )
    }

    fun updateScreenState(marketConditionScreenState: MarketConditionScreenState) {
        _marketConditionUIState.value = marketConditionScreenState
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

    private suspend fun fetchExchangeRate() {

    }
}