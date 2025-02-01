package org.jeonfeel.moeuibit2.data.usecase

import android.content.Context
import com.jeremy.thunder.Thunder
import com.jeremy.thunder.event.converter.ConverterType
import com.jeremy.thunder.makeWebSocketCore
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import org.jeonfeel.moeuibit2.constants.ASK
import org.jeonfeel.moeuibit2.constants.BID
import org.jeonfeel.moeuibit2.constants.BTC_COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.KRW_COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.local.room.entity.User
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitOrderBookReq
import org.jeonfeel.moeuibit2.data.network.websocket.manager.ExchangeWebsocketManager
import org.jeonfeel.moeuibit2.data.network.websocket.manager.OrderBookWebsocketManager
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.OrderBookRequestTypeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.OrderBookTicketField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpbitOrderBookSocketService
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import javax.inject.Inject

enum class OrderBookKind {
    ASK, BID
}

class UpbitCoinOrderUseCase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository
) : BaseUseCase() {

    private val orderBookWebsocketManager = OrderBookWebsocketManager()

    suspend fun onStart(marketCodes: String) {
        orderBookWebsocketManager.updateIsBackground(false)
        orderBookWebsocketManager.connectWebSocketFlow(marketCodes)
    }

    suspend fun onStop() {
        orderBookWebsocketManager.updateIsBackground(true)
        orderBookWebsocketManager.onStop()
    }

    suspend fun getOrderBook(market: String): Flow<Any> {
        val getUpbitOrderBookReq = GetUpbitOrderBookReq(market = market)
        return requestApiResult(
            result = upbitRepository.getOrderBook(getUpbitOrderBookReq),
            onSuccess = {
                it[0].mapTo()
            }
        )
    }

    suspend fun requestObserveOrderBook(): Flow<UpbitSocketOrderBookRes?> {
        return orderBookWebsocketManager.tickerFlow
    }

    suspend fun getUserSeedMoney(): User? {
        return localRepository.getUserDao().all
    }

    suspend fun getUserCoin(market: String): MyCoin? {
        return localRepository.getMyCoinDao().isInsert(market)
    }

    suspend fun getUserBtcCoin(): MyCoin? {
        return localRepository.getMyCoinDao().isInsert(BTC_MARKET)
    }

    suspend fun requestKRWBid(
        market: String,
        coin: MyCoin,
        totalPrice: Long,
        userSeedMoney: Long,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userDao = localRepository.getUserDao()
        val userCoin = coinDao.isInsert(market)

        var krwTotalPrice = totalPrice

        krwTotalPrice = if (userSeedMoney - krwTotalPrice < 10) {
            userSeedMoney
        } else {
            krwTotalPrice
        }

        if (userCoin == null) {
            coinDao.insert(coin)
        } else {
            val prePurchaseAveragePrice = userCoin.purchasePrice
            val preCoinQuantity = userCoin.quantity
            val purchaseAverage = Calculator.averagePurchasePriceCalculator(
                coin.purchasePrice,
                coin.quantity,
                prePurchaseAveragePrice,
                preCoinQuantity,
                SELECTED_KRW_MARKET
            )

            if (purchaseAverage >= 100) {
                coinDao.updatePurchasePriceInt(market, purchaseAverage.toInt())
            } else {
                coinDao.updatePurchasePrice(market, purchaseAverage)
            }
            coinDao.updatePlusQuantity(market, coin.quantity)
        }
        userDao.updateMinusMoney(krwTotalPrice)

        insertAndDeleteOver500(
            market = market,
            price = coin.purchasePrice,
            quantity = coin.quantity,
            transactionAmount = totalPrice,
            transactionStatus = BID,
        )
    }

    suspend fun requestKRWAsk(
        market: String,
        quantity: Double,
        totalPrice: Long,
        userCoinQuantity: BigDecimal,
        currentPrice: BigDecimal,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userDao = localRepository.getUserDao()

        userDao.updatePlusMoney(totalPrice)
        if (userCoinQuantity.minus(quantity.newBigDecimal(8, RoundingMode.FLOOR))
                .toDouble() <= 0.00000001
        ) {
            coinDao.delete(market)
        } else {
            coinDao.updateMinusQuantity(market, quantity)
        }

        insertAndDeleteOver500(
            market = market,
            price = currentPrice.toDouble(),
            quantity = quantity,
            transactionAmount = totalPrice,
            transactionStatus = ASK
        )
    }

    suspend fun requestBTCBid(
        market: String,
        coin: MyCoin,
        totalPrice: Double,
        userSeedBTC: Double,
        btcPrice: Double,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userCoin = coinDao.isInsert(market)
        val btcTotalPrice = BigDecimal(totalPrice)
        val minusBTCQuantity = BigDecimal(userSeedBTC).minus(btcTotalPrice)

        if (userCoin == null) {
            coinDao.insert(coin)
            coinDao.updatePurchaseAverageBtcPrice(market, btcPrice)
            if (minusBTCQuantity < BigDecimal("0.0000001")) {
                coinDao.delete(BTC_MARKET)
            } else {
                coinDao.updateMinusQuantity(BTC_MARKET, btcTotalPrice.toDouble())
            }
        } else {
            val preCoinQuantity = userCoin.quantity.toBigDecimal()
            val prePurchaseAveragePrice = userCoin.purchasePrice.toBigDecimal()

            val purchaseAverage = Calculator.averagePurchasePriceCalculator(
                currentPrice = coin.purchasePrice,
                currentQuantity = coin.quantity,
                preAveragePurchasePrice = prePurchaseAveragePrice.toDouble(),
                preCoinQuantity = preCoinQuantity.toDouble(),
                marketState = SELECTED_BTC_MARKET
            )
            val purchaseAverageBtcPrice = Calculator.averagePurchasePriceCalculator(
                currentPrice = btcPrice,
                currentQuantity = coin.quantity.toBigDecimal()
                    .multiply(coin.purchasePrice.toBigDecimal()).toDouble(),
                preAveragePurchasePrice = userCoin.purchaseAverageBtcPrice,
                preCoinQuantity = userCoin.quantity.toBigDecimal()
                    .multiply(userCoin.purchasePrice.toBigDecimal()).toDouble(),
                SELECTED_KRW_MARKET
            )

            Logger.e("purchaseAverage ${purchaseAverage} purchaseBTCAverage ${purchaseAverageBtcPrice}")

            coinDao.updatePurchasePrice(market, purchaseAverage)
            coinDao.updatePlusQuantity(market, coin.quantity)
            if (minusBTCQuantity < BigDecimal("0.00000001")) {
                coinDao.delete(BTC_MARKET)
            } else {
                coinDao.updateMinusQuantity(BTC_MARKET, btcTotalPrice.toDouble())
            }
            coinDao.updatePurchaseAverageBtcPrice(market, purchaseAverageBtcPrice)
        }

        insertAndDeleteOver500(
            market = market,
            price = coin.purchasePrice,
            quantity = coin.quantity,
            transactionAmount = 0,
            transactionStatus = BID,
            transactionAmountBTC = totalPrice
        )
    }

    suspend fun requestBTCAsk(
        market: String,
        quantity: Double,
        totalPrice: Double,
        userCoinQuantity: BigDecimal,
        currentPrice: BigDecimal,
        btcPrice: Double,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        Logger.e("$totalPrice")
        val userBTC = coinDao.isInsert(BTC_MARKET)
        if (userBTC == null) {
            coinDao.insert(
                myCoin = MyCoin(
                    market = BTC_MARKET,
                    purchasePrice = btcPrice,
                    koreanCoinName = "비트코인",
                    symbol = "BTC",
                    quantity = totalPrice,
                )
            )
        } else {
            coinDao.updatePlusQuantity(market = BTC_MARKET, afterQuantity = totalPrice)
            val purchaseBTCAverage = Calculator.averagePurchasePriceCalculator(
                currentPrice = btcPrice,
                currentQuantity = totalPrice,
                preAveragePurchasePrice = userBTC.purchasePrice,
                preCoinQuantity = userBTC.quantity,
                marketState = SELECTED_KRW_MARKET
            )
            coinDao.updatePurchasePrice(BTC_MARKET, purchaseBTCAverage)
        }

        coinDao.updatePurchaseAverageBtcPrice(BTC_MARKET, btcPrice)

        if (userCoinQuantity.minus(quantity.newBigDecimal(8, RoundingMode.FLOOR))
                .toDouble() <= 0.00000001
        ) {
            coinDao.delete(market = market)
        } else {
            coinDao.updateMinusQuantity(market = market, afterQuantity = quantity)
        }

        insertAndDeleteOver500(
            market = market,
            price = currentPrice.toDouble(),
            quantity = quantity,
            transactionAmount = 0,
            transactionStatus = ASK,
            transactionAmountBTC = totalPrice
        )
    }

    suspend fun getTransactionInfoList(market: String): List<TransactionInfo> {
        return localRepository.getTransactionInfoDao().select(market)
    }

    private suspend fun insertAndDeleteOver500(
        market: String,
        price: Double,
        quantity: Double,
        transactionAmount: Long,
        transactionStatus: String,
        transactionTime: Long = System.currentTimeMillis(),
        transactionAmountBTC: Double = 0.0
    ) {
        val transactionInfoDao = localRepository.getTransactionInfoDao()
        transactionInfoDao.insert(
            TransactionInfo(
                market = market,
                price = price,
                quantity = quantity,
                transactionAmount = transactionAmount,
                transactionStatus = transactionStatus,
                transactionTime = transactionTime,
                transactionAmountBTC = transactionAmountBTC
            )
        )

        val count = transactionInfoDao.getCount(market)
        val excess = count - 500

        if (excess > 0) {
            transactionInfoDao.deleteExcess(market, excess)
        }
    }
}