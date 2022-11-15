package org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.delay
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.manager.PreferenceManager
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.util.calculator.Calculator
import org.jeonfeel.moeuibit2.util.calculator.Calculator.averagePurchasePriceCalculator
import org.jeonfeel.moeuibit2.util.eighthDecimal
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.math.round

@ViewModelScoped
class OrderScreenUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val preferenceManager: PreferenceManager
) {
    val gson = Gson()
    var isTickerSocketRunning = true

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
    var coinDetailModel = CoinDetailTickerModel("", 0.0, 0.0, 0.0)
    val totalPriceDesignated = mutableStateOf("")
    val errorDialogState = mutableStateOf(false)
    val btcQuantity = mutableStateOf(0.0)
    val currentBTCPrice = mutableStateOf(0.0)
    val isShowAdjustFeeDialog = mutableStateOf(false)
    val feeStateList = mutableStateListOf<MutableState<Float>>()
    val krwBidFee = mutableStateOf(-1)
    val krwAskFee = mutableStateOf(-1)
    val btcBidFee = mutableStateOf(-1)
    val btcAskFee = mutableStateOf(-1)

    suspend fun initOrderScreen(market: String) {
        delay(700L)
        val requestMarket = if (market.startsWith(SYMBOL_BTC)) {
            market.plus(",$BTC_MARKET")
        } else {
            market
        }
        UpBitCoinDetailWebSocket.market = requestMarket
        UpBitOrderBookWebSocket.market = market
        try {
            Handler(Looper.getMainLooper()).post {
                UpBitCoinDetailWebSocket.requestCoinDetailData(requestMarket)
                UpBitOrderBookWebSocket.requestOrderBookList(market)
            }
        } catch (e: UnknownHostException) {
            errorDialogState.value = true
        } catch (e: SocketTimeoutException) {
            errorDialogState.value = true
        }
        Log.e("userseed", localRepository.getUserDao().all?.krw.toString())
        localRepository.getUserDao().all.let {
            userSeedMoney.value = it?.krw ?: 0L
        }
        localRepository.getMyCoinDao().isInsert(market).let {
            userCoinQuantity.value = it?.quantity ?: 0.0
        }
        localRepository.getMyCoinDao().isInsert(BTC_MARKET).let {
            btcQuantity.value = it?.quantity ?: 0.0
        }
        orderScreenLoadingState.value = false
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
                coinDao.insert(
                    MyCoin(
                        market,
                        currentPrice,
                        koreanName,
                        symbol,
                        quantity
                    )
                )
                if (totalPrice == (userSeedMoney.value - round(userSeedMoney.value * 0.0005)).toLong()) {
                    userDao.updateMinusMoney((totalPrice + round(userSeedMoney.value * 0.0005)).toLong())
                } else {
                    userDao.updateMinusMoney((totalPrice + round(totalPrice * 0.0005)).toLong())
                }
                userSeedMoney.value = userDao.all?.krw ?: 0L
            } else {
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
                if (btcQuantity.value - (btcTotalPrice + btcQuantity.value * 0.0025) < 0.00000001) {
                    coinDao.delete(BTC_MARKET)
                } else {
                    coinDao.updateMinusQuantity(BTC_MARKET,
                        (btcTotalPrice + btcQuantity.value * 0.0025).eighthDecimal().toDouble())
                }
                btcQuantity.value = coinDao.isInsert(BTC_MARKET)?.quantity ?: 0.0
            }

            bidQuantity.value = ""
            userCoinQuantity.value = coinDao.isInsert(market)?.quantity ?: 0.0
            localRepository.getTransactionInfoDao().insert(
                TransactionInfo(
                    market,
                    currentPrice,
                    quantity,
                    totalPrice,
                    BID,
                    System.currentTimeMillis()))
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
            val purchaseAverageBtcPrice = averagePurchasePriceCalculator(
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
                userDao.updateMinusMoney((totalPrice + round(totalPrice * 0.0005)).toLong())
                userSeedMoney.value = userDao.all?.krw ?: 0L
            } else {
                if (btcQuantity.value - (btcTotalPrice + btcQuantity.value * 0.0025) < 0.00000001) {
                    coinDao.delete(BTC_MARKET)
                } else {
                    coinDao.updateMinusQuantity(BTC_MARKET,
                        (btcTotalPrice + btcQuantity.value * 0.0025).eighthDecimal().toDouble())
                }
                coinDao.updatePurchaseAverageBtcPrice(market,purchaseAverageBtcPrice)
                btcQuantity.value = coinDao.isInsert(BTC_MARKET)?.quantity ?: 0.0
            }

            bidQuantity.value = ""
            userCoinQuantity.value = coinDao.isInsert(market)?.quantity ?: 0.0
            localRepository.getTransactionInfoDao().insert(
                TransactionInfo(
                    market,
                    currentPrice,
                    quantity,
                    totalPrice,
                    BID,
                    System.currentTimeMillis()))
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
            userDao.updatePlusMoney((totalPrice - round(totalPrice * 0.0005)).toLong())
            userSeedMoney.value = userDao.all?.krw ?: 0L
        } else {
            val btc = coinDao.isInsert(BTC_MARKET)
            if (btc == null) {
                coinDao.insert(MyCoin(
                    BTC_MARKET,
                    currentBTCPrice.value,
                    "비트코인",
                    SYMBOL_BTC,
                    (btcTotalPrice - btcTotalPrice * 0.0025)
                ))
            } else {
                val preAveragePurchasePrice = btc.purchasePrice
                val preCoinQuantity = btc.quantity
                val purchaseAverage = Calculator.averagePurchasePriceCalculator(
                    currentBTCPrice.value,
                    (btcTotalPrice - btcTotalPrice * 0.0025),
                    preAveragePurchasePrice,
                    preCoinQuantity,
                    marketState
                )
                coinDao.updatePurchasePrice(BTC_MARKET, purchaseAverage)
                coinDao.updatePlusQuantity(BTC_MARKET, (btcTotalPrice - btcTotalPrice * 0.0025))
            }
        }
        btcQuantity.value = coinDao.isInsert(BTC_MARKET)?.quantity ?: 0.0
        askQuantity.value = ""
        val currentCoin = coinDao.isInsert(market)
        if (currentCoin != null && marketState == SELECTED_KRW_MARKET && round(currentCoin.quantity * currentPrice) <= 0.0
            || currentCoin != null && marketState == SELECTED_BTC_MARKET && round(currentCoin.quantity * currentPrice * currentBTCPrice.value) <= 0.0
        ) {
            coinDao.delete(market)
            userCoinQuantity.value = 0.0
        } else {
            userCoinQuantity.value = currentCoin?.quantity ?: 0.0
        }
        localRepository.getTransactionInfoDao().insert(
            TransactionInfo(
                market,
                currentPrice,
                quantity,
                totalPrice,
                ASK,
                System.currentTimeMillis()))
    }

    suspend fun adjustFee() {
        preferenceManager.setValue(PREF_KEY_KRW_BID_FEE,30, completeAction = {
            krwBidFee.value = preferenceManager.getInt(PREF_KEY_KRW_BID_FEE)
        })
    }
}