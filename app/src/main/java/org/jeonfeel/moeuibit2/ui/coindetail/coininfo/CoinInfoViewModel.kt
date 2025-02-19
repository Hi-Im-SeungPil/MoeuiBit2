package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.math.BigDecimal

data class CoinInfoScreenUIState(
    val coinInfo: CoinInfoModel?,
    val coinLinkMap: List<CoinLinkModel>,
)

@HiltViewModel
class CoinInfoViewModel(private val coinInfoUseCase: CoinInfoUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultState<CoinInfoScreenUIState>>(ResultState.Loading)
    val uiState = _uiState.asStateFlow()

    private var coinInfo: FetchCoinInfoRes.Data? = null
    private var coinInfoTimeStamp: Long = 0
    private var usdPrice: BigDecimal = BigDecimal.ZERO
    private var coinLinkList: List<CoinLinkModel> = emptyList()

    fun init(engName: String, symbol: String) {
        viewModelScope.launch {
            combine(
                coinInfoUseCase.fetchUSDPrice(),
                coinInfoUseCase.fetchCoinInfo(engName = engName)
            ) { usdPriceResult, coinInfoResult ->
                Pair(usdPriceResult, coinInfoResult)
            }.collectLatest { (usdPriceResult, coinInfoResult) ->
                parseUsdPriceData(usdPriceResult)
                parseCoinInfoData(coinInfoResult)
                fetchCoinInfoFromFirebase(symbol)
            }
        }
    }

    private fun createUIState() {
        val coinInfoModel = coinInfo?.toCoinInfoModel(
            usdPrice = usdPrice,
            timeStamp = coinInfoTimeStamp
        )

        _uiState.update {
            ResultState.Success(
                CoinInfoScreenUIState(
                    coinInfo = coinInfoModel,
                    coinLinkMap = coinLinkList
                )
            )
        }
    }

    private fun parseUsdPriceData(usdPriceResult: ResultState<BigDecimal>) {
        usdPrice = when (usdPriceResult) {
            is ResultState.Success -> {
                usdPriceResult.data
            }

            else -> BigDecimal.ZERO
        }
    }

    private fun parseCoinInfoData(coinInfoRes: ResultState<FetchCoinInfoRes?>) {
        coinInfo = when (coinInfoRes) {
            is ResultState.Success -> {
                if (coinInfoRes.data?.data.isNullOrEmpty()) {
                    null
                } else {
                    coinInfoRes.data?.data?.get(0)
                }
            }

            else -> null
        }
    }

    private fun fetchCoinInfoFromFirebase(symbol: String) {
        coinInfoUseCase.fetchCoinInfoFromFirebase(symbol = symbol, callback = {
            coinLinkList = it
            createUIState()
        })
    }
}