package org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constant.btcMarket
import org.jeonfeel.moeuibit2.constant.ioDispatcher
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.util.calculator.Calculator
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.math.round

@ViewModelScoped
class OrderScreenUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
) {
    val gson = Gson()
    var isTickerSocketRunning = true

    val currentTradePriceState = mutableStateOf(0.0)
    val currentTradePriceStateForOrderBook = mutableStateOf(0.0)
    val orderBookMutableStateList = mutableStateListOf<CoinDetailOrderBookModel>()

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

    fun initOrderScreen(market: String) {
        val requestMarket = if (market.startsWith("BTC-")) {
             market.plus(",KRW-BTC")
        } else {
            market
        }
        if (currentTradePriceState.value == 0.0 && orderBookMutableStateList.isEmpty()) {
            UpBitCoinDetailWebSocket.market = requestMarket
            UpBitOrderBookWebSocket.market = requestMarket
            try {
                UpBitCoinDetailWebSocket.requestCoinDetailData(requestMarket)
                UpBitOrderBookWebSocket.requestOrderBookList(requestMarket)
            } catch (e: UnknownHostException) {
                errorDialogState.value = true
            } catch (e: SocketTimeoutException) {
                errorDialogState.value = true
            }
            CoroutineScope(ioDispatcher).launch {
                localRepository.getUserDao().all.let {
                    userSeedMoney.value = it?.krw ?: 0L
                }
                localRepository.getMyCoinDao().isInsert(market).let {
                    userCoinQuantity.value = it?.quantity ?: 0.0
                }
                localRepository.getMyCoinDao().isInsert(btcMarket).let{
                    btcQuantity.value = it?.quantity ?: 0.0
                }
            }
        } else {
            try {
                UpBitCoinDetailWebSocket.requestCoinDetailData(requestMarket)
                UpBitOrderBookWebSocket.requestOrderBookList(requestMarket)
            } catch (e: UnknownHostException) {
                errorDialogState.value = true
            } catch (e: SocketTimeoutException) {
                errorDialogState.value = true
            }
        }
    }

    suspend fun bidRequest(
        market: String,
        koreanName: String,
        currentPrice: Double,
        quantity: Double,
        totalPrice: Long,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userDao = localRepository.getUserDao()
        val symbol = market.substring(4)
        val myCoin: MyCoin? = coinDao.isInsert(market)

        if (myCoin == null) {
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
            bidQuantity.value = ""
            userCoinQuantity.value = coinDao.isInsert(market)?.quantity ?: 0.0
            localRepository.getTransactionInfoDao().insert(TransactionInfo(market,
                currentPrice,
                quantity,
                totalPrice,
                "bid",
                System.currentTimeMillis()))
        } else {
            val preAveragePurchasePrice = myCoin.purchasePrice
            val preCoinQuantity = myCoin.quantity
            val purchaseAverage = Calculator.averagePurchasePriceCalculator(
                currentPrice,
                quantity,
                preAveragePurchasePrice,
                preCoinQuantity
            )

            if (purchaseAverage >= 100) {
                coinDao.updatePurchasePriceInt(market, purchaseAverage.toInt())
            } else {
                coinDao.updatePurchasePrice(market, purchaseAverage)
            }

            coinDao.updatePlusQuantity(market, quantity)
            userDao.updateMinusMoney((totalPrice + round(totalPrice * 0.0005)).toLong())
            userSeedMoney.value = userDao.all?.krw ?: 0L
            bidQuantity.value = ""
            userCoinQuantity.value = coinDao.isInsert(market)?.quantity ?: 0.0
            localRepository.getTransactionInfoDao().insert(TransactionInfo(market,
                currentPrice,
                quantity,
                totalPrice,
                "bid",
                System.currentTimeMillis()))
        }
    }

    suspend fun askRequest(
        market: String,
        quantity: Double,
        totalPrice: Long,
        currentPrice: Double,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userDao = localRepository.getUserDao()

        coinDao.updateMinusQuantity(market, quantity)
        userDao.updatePlusMoney((totalPrice - round(totalPrice * 0.0005)).toLong())
        userSeedMoney.value = userDao.all?.krw ?: 0L
        askQuantity.value = ""
        val currentCoin = coinDao.isInsert(market)
        if (currentCoin != null && currentCoin.quantity == 0.0) {
            coinDao.delete(market)
            userCoinQuantity.value = 0.0
        } else {
            userCoinQuantity.value = currentCoin?.quantity ?: 0.0
        }
        localRepository.getTransactionInfoDao().insert(TransactionInfo(market,
            currentPrice,
            quantity,
            totalPrice,
            "ask",
            System.currentTimeMillis()))
    }
}