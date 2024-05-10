package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.NETWORK_ERROR
import org.jeonfeel.moeuibit2.constants.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_FAVORITE
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.remote.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.bitthumb.BitthumbCommonExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.upbit.MarketCodeModel
import org.jeonfeel.moeuibit2.data.remote.websocket.bitthumb.BitthumbTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.upbit.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.Utils

class BitThumb(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val gson: Gson,
    private val changeNetworkState: (Int) -> Unit,
    private val selectedMarketState: State<Int>,
    private val isUpdateExchange: State<Boolean>,
    private val swapList: (market: Int) -> Unit,
    private val changeIsUpdate: (Boolean) -> Unit,
    private val removeStateList: (index: Int) -> Unit,
    private val updateExchange: () -> Unit
) : BaseRootExchange(), OnTickerMessageReceiveListener {
    private val btcTradePrice = mutableDoubleStateOf(0.0)
    private var bitthumbCoinNameJson: JsonObject? = null

    private val krwMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val btcMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val krwMarketListStringBuffer = StringBuffer()
    private val btcMarketListStringBuffer = StringBuffer()

    private val krwExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    private val krwPreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    private val krwExchangeModelListPosition: HashMap<String, Int> = hashMapOf()

    private val btcExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    private val btcPreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    private val btcExchangeModelListPosition: HashMap<String, Int> = hashMapOf()

    private var favoritePreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    private var favoriteExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    private val favoriteExchangeModelListPosition: HashMap<String, Int> = hashMapOf()

    suspend fun initBitThumbData() {
        if (krwMarketCodeList.isEmpty()
//            || btcMarketCodeList.isEmpty()
        ) {
            requestCoinName()
            requestKRWMarketCode()
            requestBTCMarketCode()
            withContext(ioDispatcher) {
//                initFavoriteData()
            }
        }
//        setSocketFavoriteData()
        requestCoinListToWebSocket()
    }

    private suspend fun requestCoinName() {
        remoteRepository.getBitthumbCoinNameService().collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    bitthumbCoinNameJson = it.data
                }

                ApiResult.Status.API_ERROR -> {
                    changeNetworkState(NETWORK_ERROR)
                }

                ApiResult.Status.NETWORK_ERROR -> {
                    changeNetworkState(NO_INTERNET_CONNECTION)
                    NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
                }
            }
        }
    }

    /**
     * get market, koreanName, englishName, warning
     */
    private suspend fun requestKRWMarketCode() {
        remoteRepository.getBitThumbKRWMarketCodeService().collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    try {
                        val data = it.data ?: JsonObject()
                        val marketCodeList: List<String> = Utils.extractCryptoKeysWithGson(data)
//                        Logger.e(data.toString())
                        val indices = marketCodeList.size
                        Logger.e("indices -> ${indices}")
                        for (i in 0 until indices) {
                            val marketCode = marketCodeList[i]
                            val dataObject = data["data"] as JsonObject
                            val coinNames = parseCoinName(marketCode = marketCode)
                            MoeuiBitDataStore.bitThumbCoinName[marketCode] = coinNames
                            val bitthumbCommonExchangeModel = gson.fromJson(
                                dataObject[marketCode],
                                BitthumbCommonExchangeModel::class.java
                            ).apply {
                                this.market = "KRW-$marketCode"
                                this.symbol = marketCode
                                this.koreanName = coinNames.first
                                this.englishName = coinNames.second
                            }
                            krwMarketListStringBuffer.append("${marketCode}_KRW,")
                            krwMarketCodeList.add(
                                MarketCodeModel(
                                    market = "KRW-$marketCode",
                                    korean_name = coinNames.first,
                                    english_name = coinNames.second,
                                    market_warning = ""
                                )
                            )
                            krwExchangeModelList.add(
                                CommonExchangeModel(
                                    koreanName = bitthumbCommonExchangeModel.koreanName,
                                    englishName = bitthumbCommonExchangeModel.englishName,
                                    market = bitthumbCommonExchangeModel.market,
                                    symbol = bitthumbCommonExchangeModel.symbol,
                                    opening_price = bitthumbCommonExchangeModel.opening_price.toDouble(),
                                    tradePrice = bitthumbCommonExchangeModel.tradePrice.toDouble(),
                                    signedChangeRate = bitthumbCommonExchangeModel.signedChangeRate * 0.01,
                                    accTradePrice24h = bitthumbCommonExchangeModel.accTradePrice24h.toDouble(),
                                    warning = ""
                                )
                            )
                        }
                        krwMarketListStringBuffer.deleteCharAt(krwMarketListStringBuffer.lastIndex)
                        krwExchangeModelList.sortByDescending { model ->
                            model.accTradePrice24h
                        }
                        for (i in krwExchangeModelList.indices) {
                            krwExchangeModelListPosition[krwExchangeModelList[i].market] = i
                        }
                        MoeuiBitDataStore.bitThumbKrwMarkets = krwExchangeModelListPosition
                        swapList(SELECTED_KRW_MARKET)
                        krwPreItemArray.addAll(krwExchangeModelList)

                        Logger.e("list -> ${krwExchangeModelList.toString()}")
                    } catch (e: Exception) {
                        Logger.e("error -> ${e.message}")
                        changeNetworkState(NETWORK_ERROR)
                    }
                }

                ApiResult.Status.API_ERROR -> {
                    changeNetworkState(NETWORK_ERROR)
                }

                ApiResult.Status.NETWORK_ERROR -> {
                    changeNetworkState(NO_INTERNET_CONNECTION)
                    NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
                }
            }
        }
    }

    /**
     * get market, koreanName, englishName, warning
     */
    private suspend fun requestBTCMarketCode() {
        remoteRepository.getBitThumbBTCMarketCodeService().collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    try {
                        val data = it.data ?: JsonObject()
                        val marketCodeList: List<String> = Utils.extractCryptoKeysWithGson(data)

                        Logger.e(data.toString())
                        val indices = marketCodeList.size
                        for (i in 0 until indices) {
                            val marketCode = marketCodeList[i]
                            val bitthumbCommonExchangeModel = gson.fromJson(
                                data[marketCode],
                                BitthumbCommonExchangeModel::class.java
                            ).apply {
                                this.market = "BTC-$marketCode"
                                this.symbol = marketCode
                                this.koreanName =
                                    MoeuiBitDataStore.bitThumbCoinName[marketCode]?.first ?: ""
                                this.englishName =
                                    MoeuiBitDataStore.bitThumbCoinName[marketCode]?.second ?: ""
                            }
                            btcMarketListStringBuffer.append("${marketCode}_BTC,")
                            btcMarketCodeList.add(
                                MarketCodeModel(
                                    market = "BTC-$marketCode",
                                    korean_name = MoeuiBitDataStore.bitThumbCoinName[marketCode]?.first
                                        ?: "",
                                    english_name = MoeuiBitDataStore.bitThumbCoinName[marketCode]?.second
                                        ?: "",
                                    market_warning = ""
                                )
                            )
                            btcExchangeModelList.add(
                                CommonExchangeModel(
                                    koreanName = bitthumbCommonExchangeModel.koreanName,
                                    englishName = bitthumbCommonExchangeModel.englishName,
                                    market = bitthumbCommonExchangeModel.market,
                                    symbol = bitthumbCommonExchangeModel.symbol,
                                    opening_price = bitthumbCommonExchangeModel.opening_price.toDouble(),
                                    tradePrice = bitthumbCommonExchangeModel.tradePrice.toDouble(),
                                    signedChangeRate = bitthumbCommonExchangeModel.signedChangeRate * 0.01,
                                    accTradePrice24h = bitthumbCommonExchangeModel.accTradePrice24h.toDouble(),
                                    warning = ""
                                )
                            )
                        }
                        btcMarketListStringBuffer.deleteCharAt(btcMarketListStringBuffer.lastIndex)
                        btcExchangeModelList.sortByDescending { model ->
                            model.accTradePrice24h
                        }
                        for (i in btcExchangeModelList.indices) {
                            btcExchangeModelListPosition[btcExchangeModelList[i].market] = i
                        }
                        MoeuiBitDataStore.bitThumbBtcMarkets = btcExchangeModelListPosition
                        BitthumbTickerWebSocket.setMarkets(
                            krwMarkets = krwMarketListStringBuffer.toString(),
                            btcMarkets = btcMarketListStringBuffer.toString()
                        )
                        swapList(SELECTED_BTC_MARKET)
                        btcPreItemArray.addAll(btcExchangeModelList)
                    } catch (e: Exception) {
                        changeNetworkState(NETWORK_ERROR)
                    }
                }

                ApiResult.Status.API_ERROR -> {
                    changeNetworkState(NETWORK_ERROR)
                }

                ApiResult.Status.NETWORK_ERROR -> {
                    changeNetworkState(NO_INTERNET_CONNECTION)
                    NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
                }
            }
        }
    }

    fun getSwapTempList(marketState: Int): List<CommonExchangeModel> {
        if (marketState == SELECTED_KRW_MARKET) {
            return krwExchangeModelList.toList()
        }

        if (marketState == SELECTED_BTC_MARKET) {
            return btcExchangeModelList.toList()
        }

        if (marketState == SELECTED_FAVORITE) {
            return favoriteExchangeModelList.toList()
        }

        return emptyList()
    }

    fun getPreCoinListAndPosition(marketState: Int): Pair<ArrayList<CommonExchangeModel>, HashMap<String, Int>> {
        return when (marketState) {
            SELECTED_KRW_MARKET -> {
                Pair(krwPreItemArray, krwExchangeModelListPosition)
            }

            SELECTED_BTC_MARKET -> {
                Pair(btcPreItemArray, btcExchangeModelListPosition)
            }

            else -> {
                Pair(favoritePreItemArray, favoriteExchangeModelListPosition)
            }
        }
    }

    private fun requestCoinListToWebSocket() {
        BitthumbTickerWebSocket.tickerListener = this
        BitthumbTickerWebSocket.requestKrwCoinList(selectedMarketState.value)
    }

    private fun parseCoinName(marketCode: String): Pair<String, String> {
        return try {
            val names = bitthumbCoinNameJson?.let { json ->
                json[marketCode]
            }

            val krName = (names as JsonObject)["kr_name"].asString
            val engName = (names as JsonObject)["eng_name"].asString
//            Logger.e("$krName $engName")
            Pair(krName, engName)
        } catch (e: Exception) {
            Pair("", "")
        }
    }

    //                        UpBitTickerWebSocket.setMarkets(
//                            krwMarkets = krwMarketListStringBuffer.toString(),
//                            btcMarkets = "$btcMarketListStringBuffer,$BTC_MARKET"
//                        )

    //                        for (i in krwMarketCodeList.indices) {
//                            MoeuiBitDataStore.coinName[krwMarketCodeList[i].market] = Pair(
//                                krwMarketCodeList[i].korean_name, krwMarketCodeList[i].english_name
//                            )
//                        }
    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        Logger.e(tickerJsonObject.toString())
    }

}