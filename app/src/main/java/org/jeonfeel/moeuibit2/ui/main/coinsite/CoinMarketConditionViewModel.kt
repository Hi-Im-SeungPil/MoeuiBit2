package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.ui.common.ResultState
import javax.inject.Inject

@HiltViewModel
class CoinMarketConditionViewModel
@Inject constructor(private val alternativeUseCase: CoinMarketConditionUseCase) : ViewModel() {

    val index = mutableIntStateOf(0)

    fun getFearAndGreedyIndex() {
        viewModelScope.launch {
            alternativeUseCase.getFearAndGreedyIndex().collect { res ->
                when (res) {
                    is ResultState.Success -> {
                        index.intValue = res.data?.data?.get(0)?.value?.toInt() ?: 0
                    }

                    else -> {

                    }
                }
            }
        }
    }
}