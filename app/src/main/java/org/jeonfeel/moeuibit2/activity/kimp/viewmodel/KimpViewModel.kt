package org.jeonfeel.moeuibit2.activity.kimp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.remote.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.exchange.MarketCodeModel
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import javax.inject.Inject

@HiltViewModel
class KimpViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
) : ViewModel() {
    val gson = Gson()
    val usdtPrice = mutableStateOf(0)
    val date = mutableStateOf("")

    val upBitKRWCoinList = mutableStateListOf<MarketCodeModel>()
    val upBitBTCCoinList = mutableStateListOf<MarketCodeModel>()
    val bithumbKRWCoinList = mutableStateListOf<MarketCodeModel>()
    val bithumbBTCCoinList = mutableStateListOf<MarketCodeModel>()
    val coinOneKRWCoinList = mutableStateListOf<MarketCodeModel>()
    val coinOneBTCCoinList = mutableStateListOf<MarketCodeModel>()
    val korBitKRWCoinList = mutableStateListOf<MarketCodeModel>()
    val korBitBTCCoinList = mutableStateListOf<MarketCodeModel>()
    val gopaxKRWCoinList = mutableStateListOf<MarketCodeModel>()
    val gopaxBTCCoinList = mutableStateListOf<MarketCodeModel>()

    val binanceUSDTCoinList = HashMap<String,String>()
    val binanceBTCCoinList = HashMap<String,String>()
    /**
     * usdt 정보 가져온다.
     */

    fun requestUpBitMarketList() {
        viewModelScope.launch {
            remoteRepository.getMarketCodeService().collect() {
                when (it.status) {
                    ApiResult.Status.LOADING -> {}
                    ApiResult.Status.SUCCESS -> {
                        val data = it.data
                        if(data != null) {
                            val indices = data.size()
                            for (i in 0 until indices) {
                                val model = gson.fromJson(data[i], MarketCodeModel::class.java)
                                if (model.market.contains("KRW")) {
                                    upBitKRWCoinList.add(model)
                                } else if (model.market.contains("BTC")) {
                                    upBitBTCCoinList.add(model)
                                }
                            }
                        }
                    }
                    ApiResult.Status.NETWORK_ERROR -> {}
                    ApiResult.Status.API_ERROR -> {}
                }
            }
        }
    }

    fun requestBinanceMarketList() {
        viewModelScope.launch {
            remoteRepository.getBinanceExchangeInfo().collect() {
                when (it.status) {
                    ApiResult.Status.LOADING -> {}
                    ApiResult.Status.SUCCESS -> {
//                        val data = it.data
//                        if(data != null) {
//                            val binanceSymbols = gson.fromJson(data.get("symbols"),BinanceSymbols::class.java)
//                            for (i in binanceSymbols.symbols.indices) {
//                                Log.e(binanceSymbols.symbols[i].baseAsset,"hello")
//                            }

                    }
                    ApiResult.Status.NETWORK_ERROR -> {}
                    ApiResult.Status.API_ERROR -> {}
                }
            }
        }
    }
}