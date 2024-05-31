package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitUseCase
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.mapToMarketCodesRequest
import javax.inject.Inject

sealed class ExchangeInitState {
    object Wait : ExchangeInitState()

    object Loading : ExchangeInitState()

    object Success : ExchangeInitState()

    data class Error(
        val message: String? = null
    ) : ExchangeInitState()

    companion object {
        const val LIST_TYPE_KRW = "krw"
        const val LIST_TYPE_BTC = "btc"
        const val LIST_TYPE_FAV = "fav"
    }
}

class UpBit @Inject constructor(
    private val upbitUseCase: UpbitUseCase
) : BaseRootExchange() {
    private val marketCodeList = arrayListOf<UpbitMarketCodeRes>()

    private val krwList = arrayListOf<String>()
    private val krwExchangeModelListPosition = mutableMapOf<String, Int>()
    private val _krwExchangeModelList = mutableStateListOf<CommonExchangeModel>()
    val krwExchangeModelList: List<CommonExchangeModel> get() = _krwExchangeModelList

    private val btcList = arrayListOf<String>()
    private val btcExchangeModelPosition = mutableMapOf<String, Int>()
    private val _btcExchangeModelList = mutableStateListOf<CommonExchangeModel>()
    val btcExchangeModelList: List<CommonExchangeModel> get() = _btcExchangeModelList

    private val _tickerResponse = MutableStateFlow<UpbitSocketTickerRes?>(null)

    /**
     * 업비트 초기화
     */
    fun initUpBit(): Flow<ExchangeInitState> {
        return flow {
            emit(ExchangeInitState.Loading)
            runCatching {
                init()
            }.fold(
                onSuccess = {
                    emit(ExchangeInitState.Success)
                },
                onFailure = { emit(ExchangeInitState.Error(it.message)) }
            ).also { requestSubscribeTicker() }.also { collectTicker() }
        }
    }

    private suspend fun init() {
        requestMarketCode()
        requestTicker()
    }

    /**
     * 업비트 마켓 코드 요청
     */
    private suspend fun requestMarketCode() {
        executeUseCase<List<UpbitMarketCodeRes>>(
            target = upbitUseCase.getMarketCode(),
            onComplete = { result ->
                val codeListPair = Utils.divideKrwBtc(result)
                krwList.addAll(codeListPair.first)
                btcList.addAll(codeListPair.second)
                marketCodeList.addAll(result)
            }
        )
    }

    /**
     * 업비트 티커 요청
     */
    private suspend fun requestTicker() {
        val marketCodes = (krwList + btcList).mapToMarketCodesRequest()
        val req = GetUpbitMarketTickerReq(
            marketCodes = marketCodes
        )
        executeUseCase<Pair<List<CommonExchangeModel>, List<CommonExchangeModel>>>(
            target = upbitUseCase.getMarketTicker(
                getUpbitMarketTickerReq = req,
                upbitMarketCodeList = marketCodeList.toList(),
                addExchangeModelPosition = ::addExchangeModelPosition
            ),
            onComplete = { result ->
                _krwExchangeModelList.addAll(result.first)
                _btcExchangeModelList.addAll(result.second)
            }
        )
    }

    /**
     * 포지션 추가
     */
    private fun addExchangeModelPosition(
        market: String,
        position: Int,
        isKrw: Boolean
    ) {
        if (isKrw) {
            krwExchangeModelListPosition[market] = position
        } else {
            btcExchangeModelPosition[market] = position
        }
    }

    /**
     * 웹소켓 티커 구독 요청
     */
    private suspend fun requestSubscribeTicker() {
        upbitUseCase.requestSubscribeTicker(marketCodes = krwList)
    }

    /**
     * 웹소켓 티커 수신
     */
    private suspend fun collectTicker() {
        upbitUseCase.observeTickerResponse()().onEach { result ->
            _tickerResponse.update {
                result
            }
        }.collect { upbitSocketTickerRes ->
            val position = krwExchangeModelListPosition[upbitSocketTickerRes.code] ?: 0
            val upbitMarketCodeRes = marketCodeList[position]
            val commonExchangeModel = upbitSocketTickerRes.mapTo(upbitMarketCodeRes)
            _krwExchangeModelList[position] = commonExchangeModel
        }
    }
}