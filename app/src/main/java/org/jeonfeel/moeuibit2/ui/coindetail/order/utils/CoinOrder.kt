package org.jeonfeel.moeuibit2.ui.coindetail.order.utils

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnOrderBookMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookAskModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookBidModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.round

class CoinOrderState {
    val currentTradePriceState = mutableStateOf(0.0)
    val currentTradePriceStateForOrderBook = mutableStateOf(0.0)
    val orderBookMutableStateList = mutableStateListOf<CoinDetailOrderBookModel>()
    val orderScreenLoadingState = mutableStateOf(true)
    val askBidSelectedTab = mutableStateOf(1)
    val userSeedMoney = mutableStateOf(0L)
    val userCoinQuantity = mutableStateOf(0.0)
    val bidQuantity = mutableStateOf("")
    val askQuantity = mutableStateOf("")
    val askBidDialogState = mutableStateOf(false)
    val totalPriceDesignated = mutableStateOf("")
    val errorDialogState = mutableStateOf(false)
    val btcQuantity = mutableStateOf(0.0)
    val currentBTCPrice = mutableStateOf(0.0)
    val showAdjustFeeDialog = mutableStateOf(false)
    val commissionStateList = mutableStateListOf<MutableState<Float>>()
    val transactionInfoList = mutableStateListOf<TransactionInfo>()
    var maxOrderBookSize = mutableStateOf(0.0)
}

class CoinOrder @Inject constructor(
    private val localRepository: LocalRepository,
    private val preferenceManager: PreferenceManager
) : OnOrderBookMessageReceiveListener {
    val gson = Gson()
    var isTickerSocketRunning = true
    val state = CoinOrderState()
    var coinDetailModel = CoinDetailTickerModel("", 0.0, 0.0, 0.0)

    suspend fun initOrderScreen(market: String) {
        val requestMarket = if (market.startsWith(SYMBOL_BTC)) {
            market.plus(",$BTC_MARKET")
        } else {
            market
        }
        UpBitTickerWebSocket.detailMarket = requestMarket
        UpBitOrderBookWebSocket.market = market
        try {
            Handler(Looper.getMainLooper()).post {
                UpBitOrderBookWebSocket.getListener().setOrderBookMessageListener(this)
                UpBitOrderBookWebSocket.requestOrderBookList(market)
            }
        } catch (e: UnknownHostException) {
            state.errorDialogState.value = true
            Logger.e(e.message.toString())
        } catch (e: SocketTimeoutException) {
            state.errorDialogState.value = true
            Logger.e(e.message.toString())
        } catch (e: Exception) {
            Logger.e(e.message.toString())
        }

        localRepository.getUserDao().all?.let {
            state.userSeedMoney.value = it.krw
        }
        localRepository.getMyCoinDao().isInsert(market)?.let {
            state.userCoinQuantity.value = it.quantity
        }
        localRepository.getMyCoinDao().isInsert(BTC_MARKET)?.let {
            state.btcQuantity.value = it.quantity
        }
    }

    suspend fun bidRequest(
        market: String,
        koreanName: String,
        currentPrice: Double,
        quantity: Double,
        totalPrice: Long = 0L,
        btcTotalPrice: Double = 0.0,
        marketState: Int,
        currentBtcPrice: Double,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userDao = localRepository.getUserDao()
        val symbol = market.substring(4)
        val myCoin: MyCoin? = coinDao.isInsert(market)

        if (myCoin == null) {
            if (marketState == SELECTED_KRW_MARKET) {
                val fee = preferenceManager.getFloat(PREF_KEY_KRW_BID_COMMISSION)
                coinDao.insert(
                    MyCoin(
                        market,
                        currentPrice,
                        koreanName,
                        symbol,
                        quantity
                    )
                )
                userDao.updateMinusMoney((totalPrice + floor(totalPrice * (fee * 0.01))).toLong())
                state.userSeedMoney.value = userDao.all?.krw ?: 0L
            } else {
                val fee = preferenceManager.getFloat(PREF_KEY_BTC_BID_COMMISSION)
                coinDao.insert(
                    MyCoin(
                        market,
                        currentPrice,
                        koreanName,
                        symbol,
                        quantity,
                        currentBtcPrice
                    )
                )
                if (state.btcQuantity.value - (btcTotalPrice + (floor(btcTotalPrice * (fee * 0.01) * 100000000) * 0.00000001)) < 0.0000001) {
                    coinDao.delete(BTC_MARKET)
                } else {
                    coinDao.updateMinusQuantity(
                        BTC_MARKET,
                        (btcTotalPrice + (floor(btcTotalPrice * (fee * 0.01) * 100000000) * 0.00000001))
                    )
                }
                state.btcQuantity.value = coinDao.isInsert(BTC_MARKET)?.quantity ?: 0.0
            }

            state.bidQuantity.value = ""
            state.userCoinQuantity.value = coinDao.isInsert(market)?.quantity ?: 0.0
            localRepository.getTransactionInfoDao().insert(
                TransactionInfo(
                    market,
                    currentPrice,
                    quantity,
                    totalPrice,
                    BID,
                    System.currentTimeMillis()
                )
            )
        } else {
            val prePurchaseAveragePrice = myCoin.purchasePrice
            val preCoinQuantity = myCoin.quantity
            val prePurchaseAverageBtcPrice = myCoin.PurchaseAverageBtcPrice
            val purchaseAverage = Calculator.averagePurchasePriceCalculator(
                currentPrice,
                quantity,
                prePurchaseAveragePrice,
                preCoinQuantity,
                marketState
            )
            val purchaseAverageBtcPrice = Calculator.averagePurchasePriceCalculator(
                currentBtcPrice,
                quantity * currentPrice,
                prePurchaseAverageBtcPrice,
                preCoinQuantity * prePurchaseAveragePrice,
                SELECTED_KRW_MARKET
            )

            if (purchaseAverage >= 100) {
                coinDao.updatePurchasePriceInt(market, purchaseAverage.toInt())
            } else {
                coinDao.updatePurchasePrice(market, purchaseAverage)
            }

            coinDao.updatePlusQuantity(market, quantity)
            if (marketState == SELECTED_KRW_MARKET) {
                val fee = preferenceManager.getFloat(PREF_KEY_KRW_BID_COMMISSION)
                userDao.updateMinusMoney((totalPrice + floor(totalPrice * (fee * 0.01))).toLong())
                state.userSeedMoney.value = userDao.all?.krw ?: 0L
            } else {
                val fee = preferenceManager.getFloat(PREF_KEY_BTC_BID_COMMISSION)
                if (state.btcQuantity.value - (btcTotalPrice + (floor(btcTotalPrice * (fee * 0.01) * 100000000) * 0.00000001)) < 0.0000001) {
                    coinDao.delete(BTC_MARKET)
                } else {
                    coinDao.updateMinusQuantity(
                        BTC_MARKET,
                        (btcTotalPrice + (floor(btcTotalPrice * (fee * 0.01) * 100000000) * 0.00000001))
                    )
                }
                coinDao.updatePurchaseAverageBtcPrice(market, purchaseAverageBtcPrice)
                state.btcQuantity.value = coinDao.isInsert(BTC_MARKET)?.quantity ?: 0.0
            }
            state.bidQuantity.value = ""
            state.userCoinQuantity.value = coinDao.isInsert(market)?.quantity ?: 0.0
            localRepository.getTransactionInfoDao().insert(
                TransactionInfo(
                    market,
                    currentPrice,
                    quantity,
                    totalPrice,
                    BID,
                    System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun askRequest(
        market: String,
        quantity: Double,
        totalPrice: Long = 0,
        btcTotalPrice: Double = 0.0,
        currentPrice: Double,
        marketState: Int,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userDao = localRepository.getUserDao()
        coinDao.updateMinusQuantity(market, quantity)

        if (marketState == SELECTED_KRW_MARKET) {
            val commission = preferenceManager.getFloat(PREF_KEY_KRW_ASK_COMMISSION)
            userDao.updatePlusMoney((totalPrice - floor(totalPrice * (commission * 0.01))).toLong())
            state.userSeedMoney.value = userDao.all?.krw ?: 0L
        } else {
            val commission = preferenceManager.getFloat(PREF_KEY_BTC_ASK_COMMISSION)
            val btc = coinDao.isInsert(BTC_MARKET)
            if (btc == null) {
                coinDao.insert(
                    MyCoin(
                        market = BTC_MARKET,
                        purchasePrice = state.currentBTCPrice.value,
                        koreanCoinName = "비트코인",
                        symbol = SYMBOL_BTC,
                        quantity = (btcTotalPrice - btcTotalPrice * (commission * 0.01))
                    )
                )
            } else {
                val preAveragePurchasePrice = btc.purchasePrice
                val preCoinQuantity = btc.quantity
                val purchaseAverage = Calculator.averagePurchasePriceCalculator(
                    currentPrice = state.currentBTCPrice.value,
                    currentQuantity = (btcTotalPrice - btcTotalPrice * (commission * 0.01)),
                    preAveragePurchasePrice = preAveragePurchasePrice,
                    preCoinQuantity = preCoinQuantity,
                    marketState = marketState
                )
                coinDao.updatePurchasePrice(BTC_MARKET, purchaseAverage)
                coinDao.updatePlusQuantity(
                    BTC_MARKET,
                    (btcTotalPrice - btcTotalPrice * (commission * 0.01))
                )
            }
        }
        state.btcQuantity.value = coinDao.isInsert(BTC_MARKET)?.quantity ?: 0.0
        state.askQuantity.value = ""
        val currentCoin = coinDao.isInsert(market)
        if (currentCoin != null && marketState == SELECTED_KRW_MARKET && round(currentCoin.quantity * currentPrice) <= 0.0
            || currentCoin != null && marketState == SELECTED_BTC_MARKET && round(currentCoin.quantity * currentPrice * state.currentBTCPrice.value) <= 0.0
        ) {
            coinDao.delete(market)
            state.userCoinQuantity.value = 0.0
        } else {
            state.userCoinQuantity.value = currentCoin?.quantity ?: 0.0
        }
        localRepository.getTransactionInfoDao().insert(
            TransactionInfo(
                market,
                currentPrice,
                quantity,
                totalPrice,
                ASK,
                System.currentTimeMillis()
            )
        )
    }

    fun initAdjustCommission() {
        for (i in PREF_KEY_FEE_LIST.indices) {
            val fee = preferenceManager.getFloat(PREF_KEY_FEE_LIST[i])
            if (state.commissionStateList.size <= 3) {
                state.commissionStateList.add(mutableStateOf(fee))
            } else {
                state.commissionStateList[i] = mutableStateOf(fee)
            }
        }
    }

    suspend fun adjustCommission() {
        for (i in PREF_KEY_FEE_LIST.indices) {
            preferenceManager.setValue(PREF_KEY_FEE_LIST[i], state.commissionStateList[i].value)
        }
    }

    fun getCommission(key: String): Float {
        return preferenceManager.getFloat(key)
    }

    /**
     * transactionInfo
     */
    suspend fun getTransactionInfoList(market: String) {
        state.transactionInfoList.clear()
        val list = localRepository.getTransactionInfoDao().select(market)
        for(i in list) {
            state.transactionInfoList.add(i)
        }
    }

    override fun onOrderBookMessageReceiveListener(orderBookJsonObject: String) {
        Logger.e("onOrderBookMessage -> ${orderBookJsonObject}")
        if (isTickerSocketRunning) {
            var index = 0
            val model = gson.fromJson(orderBookJsonObject, JsonObject::class.java)
            val modelJsonArray = model.getAsJsonArray("obu")
            val indices = modelJsonArray.size()
            if (state.orderBookMutableStateList.isNotEmpty()) {
                for (i in indices - 1 downTo 0) {
                    val orderBookAskModel =
                        gson.fromJson(modelJsonArray[i], CoinDetailOrderBookAskModel::class.java)
                    state.orderBookMutableStateList[index] =
                        CoinDetailOrderBookModel(
                            orderBookAskModel.ask_price,
                            orderBookAskModel.ask_size,
                            0
                        )
                    index++
                }
                for (i in 0 until indices) {
                    val orderBookBidModel =
                        gson.fromJson(modelJsonArray[i], CoinDetailOrderBookBidModel::class.java)
                    state.orderBookMutableStateList[index] =
                        CoinDetailOrderBookModel(
                            orderBookBidModel.bid_price,
                            orderBookBidModel.bid_size,
                            1
                        )
                    index++
                }
            } else {
                for (i in indices - 1 downTo 0) {
                    val orderBookAskModel =
                        gson.fromJson(modelJsonArray[i], CoinDetailOrderBookAskModel::class.java)
                    state.orderBookMutableStateList.add(
                        CoinDetailOrderBookModel(
                            orderBookAskModel.ask_price,
                            orderBookAskModel.ask_size,
                            0
                        )
                    )
                    index++
                }
                for (i in 0 until indices) {
                    val orderBookBidModel =
                        gson.fromJson(modelJsonArray[i], CoinDetailOrderBookBidModel::class.java)
                    state.orderBookMutableStateList.add(
                        CoinDetailOrderBookModel(
                            orderBookBidModel.bid_price,
                            orderBookBidModel.bid_size,
                            1
                        )
                    )
                    index++
                }
            }
            state.maxOrderBookSize.value = state.orderBookMutableStateList.maxOf { it.size }
        }
    }
}