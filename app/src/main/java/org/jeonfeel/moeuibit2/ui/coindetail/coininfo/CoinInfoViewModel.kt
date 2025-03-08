package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.network.retrofit.response.coincapio.FetchCoinInfoRes
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinInfoModel
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinLinkModel
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.common.UIState
import java.math.BigDecimal
import javax.inject.Inject

data class CoinInfoScreenUIState(
    val coinInfoModel: CoinInfoModel? = null,
    val coinLinkList: List<CoinLinkModel> = emptyList(),
    val state: UIState = UIState.LOADING,
)

@HiltViewModel
class CoinInfoViewModel @Inject constructor(private val coinInfoUseCase: CoinInfoUseCase) :
    ViewModel() {

    private val _uiState: MutableState<CoinInfoScreenUIState> =
        mutableStateOf(CoinInfoScreenUIState())
    val uiState: State<CoinInfoScreenUIState> = _uiState

    private var coinInfo: FetchCoinInfoRes.Data? = null
    private var coinInfoTimeStamp: Long = 0
    private var usdPrice: BigDecimal = BigDecimal.ZERO
    private var coinLinkList: List<CoinLinkModel> = emptyList()

    fun init(engName: String, symbol: String) {
        if (_uiState.value.state == UIState.SUCCESS) {
            return
        }
        viewModelScope.launch {
//            parseUsdPriceData()
//            parseCoinInfoData(engName)
            fetchCoinInfoFromFirebase(symbol)
        }
    }

    private fun createUIState() {
        val coinInfoModel = coinInfo?.toCoinInfoModel(
            usdPrice = usdPrice,
            timeStamp = coinInfoTimeStamp
        )

        _uiState.value =
            CoinInfoScreenUIState(
                coinInfoModel = coinInfoModel,
                coinLinkList = coinLinkList,
                state = UIState.SUCCESS
            )
    }

    private suspend fun parseUsdPriceData() {
        coinInfoUseCase.fetchUSDPrice().collect { usdPriceResult ->
            usdPrice = when (usdPriceResult) {
                is ResultState.Success -> {
                    usdPriceResult.data
                }

                else -> BigDecimal.ZERO
            }
        }
    }

    private suspend fun parseCoinInfoData(engName: String) {
        coinInfoUseCase.fetchCoinInfo(engName = engName).collect { coinInfoRes ->
            coinInfo = when (coinInfoRes) {
                is ResultState.Success -> {
                    if (coinInfoRes.data?.data.isNullOrEmpty()) {
                        null
                    } else {
                        coinInfoTimeStamp = coinInfoRes.data?.timestamp ?: 0
                        coinInfoRes.data?.data?.get(0)
                    }
                }

                else -> null
            }
        }
    }

    private fun fetchCoinInfoFromFirebase(symbol: String) {
        coinInfoUseCase.fetchCoinInfoFromFirebase(symbol = symbol, callback = {
            coinLinkList = it
            createUIState()
        })
    }

    fun dismissLoading() {
        _uiState.value = CoinInfoScreenUIState(
            coinInfoModel = null,
            coinLinkList = emptyList(),
            state = UIState.DISMISS_LOADING
        )
    }
}