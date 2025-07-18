package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.constants.ASK
import org.jeonfeel.moeuibit2.constants.BID
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.local.room.entity.User
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitOrderBookReq
import org.jeonfeel.moeuibit2.data.network.websocket.manager.upbit.OrderBookWebsocketManager
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.abs

enum class OrderBookKind {
    ASK, BID
}

class UpbitCoinOrderUseCase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository,
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
        return localRepository.getUserDao().getUserByExchange(EXCHANGE_UPBIT)
    }

    suspend fun getUserCoin(market: String): MyCoin? {
        return localRepository.getMyCoinDao().getCoin(market = market, exchange = EXCHANGE_UPBIT)
    }

    suspend fun getUserBtcCoin(): MyCoin? {
        return localRepository.getMyCoinDao()
            .getCoin(market = BTC_MARKET, exchange = EXCHANGE_UPBIT)
    }

    suspend fun requestKRWBid(
        market: String,
        coin: MyCoin,
        totalPrice: Double,
        userSeedMoney: Double,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userDao = localRepository.getUserDao()
        val userCoin = coinDao.getCoin(market = market, exchange = EXCHANGE_UPBIT)

        var krwTotalPrice = totalPrice

        krwTotalPrice = if (abs(userSeedMoney - krwTotalPrice) < 10) {
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
                coinDao.updatePurchasePriceInt(
                    market = market,
                    exchange = EXCHANGE_UPBIT,
                    price = purchaseAverage.toInt()
                )
            } else {
                coinDao.updatePurchasePrice(
                    market = market,
                    price = purchaseAverage,
                    exchange = EXCHANGE_UPBIT
                )
            }
            coinDao.increaseQuantity(
                market = market,
                amount = coin.quantity,
                exchange = EXCHANGE_UPBIT
            )
        }
        userDao.updateMinusMoney(exchange = EXCHANGE_UPBIT, money = krwTotalPrice)

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
        totalPrice: Double,
        userCoinQuantity: Double,
        currentPrice: BigDecimal,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userDao = localRepository.getUserDao()

        userDao.updatePlusMoney(exchange = EXCHANGE_UPBIT, money = totalPrice)
        if (BigDecimal(userCoinQuantity).minus(BigDecimal(quantity)) <= BigDecimal(0.00000002)
        ) {
            coinDao.delete(market = market, exchange = EXCHANGE_UPBIT)
        } else {
            coinDao.decreaseQuantity(market = market, exchange = EXCHANGE_UPBIT, amount = quantity)
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
        val userCoin = coinDao.getCoin(market = market, exchange = EXCHANGE_UPBIT)
        val btcTotalPrice = BigDecimal(totalPrice)
        val minusBTCQuantity = BigDecimal(userSeedBTC).minus(btcTotalPrice)

        if (userCoin == null) {
            coinDao.insert(coin)
            coinDao.updatePurchaseAverageBtcPrice(
                market = market,
                exchange = EXCHANGE_UPBIT,
                price = btcPrice
            )
            if (minusBTCQuantity < BigDecimal("0.00000002")) {
                coinDao.delete(market = BTC_MARKET, exchange = EXCHANGE_UPBIT)
            } else {
                coinDao.decreaseQuantity(
                    market = BTC_MARKET,
                    exchange = EXCHANGE_UPBIT,
                    amount = btcTotalPrice.toDouble()
                )
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


            coinDao.updatePurchasePrice(
                market = market,
                price = purchaseAverage,
                exchange = EXCHANGE_UPBIT
            )
            coinDao.increaseQuantity(
                market = market,
                amount = coin.quantity,
                exchange = EXCHANGE_UPBIT
            )
            if (minusBTCQuantity < BigDecimal("0.00000002")) {
                coinDao.delete(market = BTC_MARKET, exchange = EXCHANGE_UPBIT)
            } else {
                coinDao.decreaseQuantity(
                    market = BTC_MARKET,
                    amount = btcTotalPrice.toDouble(),
                    exchange = EXCHANGE_UPBIT
                )
            }
            coinDao.updatePurchaseAverageBtcPrice(
                market = market,
                exchange = EXCHANGE_UPBIT,
                price = purchaseAverageBtcPrice
            )
        }

        insertAndDeleteOver500(
            market = market,
            price = coin.purchasePrice,
            quantity = coin.quantity,
            transactionAmount = 0.0,
            transactionStatus = BID,
            transactionAmountBTC = totalPrice
        )
    }

    suspend fun requestBTCAsk(
        market: String,
        quantity: Double,
        totalPrice: Double,
        userCoinQuantity: Double,
        currentPrice: BigDecimal,
        btcPrice: Double,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userBTC = coinDao.getCoin(market = BTC_MARKET, exchange = EXCHANGE_UPBIT)
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
            coinDao.increaseQuantity(
                market = BTC_MARKET,
                amount = totalPrice,
                exchange = EXCHANGE_UPBIT
            )
            val purchaseBTCAverage = Calculator.averagePurchasePriceCalculator(
                currentPrice = btcPrice,
                currentQuantity = totalPrice,
                preAveragePurchasePrice = userBTC.purchasePrice,
                preCoinQuantity = userBTC.quantity,
                marketState = SELECTED_KRW_MARKET
            )
            coinDao.updatePurchasePrice(
                market = BTC_MARKET,
                price = purchaseBTCAverage,
                exchange = EXCHANGE_UPBIT
            )
        }

        coinDao.updatePurchaseAverageBtcPrice(
            market = BTC_MARKET,
            exchange = EXCHANGE_UPBIT,
            price = btcPrice
        )

        if (BigDecimal(userCoinQuantity).minus(BigDecimal(quantity)) <= BigDecimal(0.00_000_002)) {
            coinDao.delete(market = market, exchange = EXCHANGE_UPBIT)
        } else {
            coinDao.decreaseQuantity(market = market, amount = quantity, exchange = EXCHANGE_UPBIT)
        }

        insertAndDeleteOver500(
            market = market,
            price = currentPrice.toDouble(),
            quantity = quantity,
            transactionAmount = 0.0,
            transactionStatus = ASK,
            transactionAmountBTC = totalPrice
        )
    }

    suspend fun getTransactionInfoList(market: String): List<TransactionInfo> {
        return localRepository.getTransactionInfoDao()
            .select(market = market, exchange = EXCHANGE_UPBIT)
    }

    private suspend fun insertAndDeleteOver500(
        market: String,
        price: Double,
        quantity: Double,
        transactionAmount: Double,
        transactionStatus: String,
        transactionTime: Long = System.currentTimeMillis(),
        transactionAmountBTC: Double = 0.0,
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
                transactionAmountBTC = transactionAmountBTC,
                exchange = EXCHANGE_UPBIT
            )
        )

        val count = transactionInfoDao.getCount(market = market, exchange = EXCHANGE_UPBIT)
        val excess = count - 500

        if (excess > 0) {
            transactionInfoDao.deleteExcess(
                market = market,
                count = excess,
                exchange = EXCHANGE_UPBIT
            )
        }
    }
}