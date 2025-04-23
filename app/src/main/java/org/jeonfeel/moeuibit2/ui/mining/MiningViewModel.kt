package org.jeonfeel.moeuibit2.ui.mining

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradingview.lightweightcharts.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.network.retrofit.response.gitjson.GitJsonReferralItem
import org.jeonfeel.moeuibit2.data.usecase.MiningUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import javax.inject.Inject

@HiltViewModel
class MiningViewModel @Inject constructor(
    private val miningUseCase: MiningUseCase,
) : ViewModel() {
    private val isLoading = mutableStateOf(false)
//    private val appMiningInfo = mutableStateOf("")

    fun fetchAppMiningInfo() {
        viewModelScope.launch {
            miningUseCase.fetchAppMiningInfo().collect { res ->
                Logger.e(res.toString())
//                when (res) {
//                    is ResultState.Loading -> {
//                        isLoading.value = true
//                    }
//
//                    is ResultState.Success -> {
//                        isLoading.value = false
//                        Logger.e(res.data.toString())
//                        appMiningInfo.value = res.data
//                    }
//
//                    is ResultState.Error -> {
//                        isLoading.value = false
//                    }
//                }
            }
        }
    }
}