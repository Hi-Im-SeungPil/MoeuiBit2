package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.constants.ASK
import org.jeonfeel.moeuibit2.constants.BID
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.local.room.entity.User
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.network.websocket.manager.upbit.OrderBookWebsocketManager
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.BiThumbRepository
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.abs

class BiThumbCoinOrderUseCase @Inject constructor(
    private val biThumbRepository: BiThumbRepository,
    private val localRepository: LocalRepository,
) {

    private val orderBookWebsocketManager = OrderBookWebsocketManager()

    suspend fun onStart(marketCodes: String) {
        orderBookWebsocketManager.updateIsBackground(false)
        orderBookWebsocketManager.connectWebSocketFlow(marketCodes)
    }

    suspend fun onStop() {
        orderBookWebsocketManager.updateIsBackground(true)
        orderBookWebsocketManager.onStop()
    }

    fun fetchOrderBook(market: String): Flow<ResultState<List<OrderBookModel>>> {
        return biThumbRepository.fetchBiThumbOrderBook(market).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        ResultState.Success(res.data.mapToOrderBookModel())
                    } else {
                        ResultState.Error("Error")
                    }
                }

                ApiResult.Status.API_ERROR,
                ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message.toString())
                }

                ApiResult.Status.LOADING -> {
                    ResultState.Loading
                }
            }
        }
    }

    fun requestObserveOrderBook(): Flow<UpbitSocketOrderBookRes?> {
        return orderBookWebsocketManager.tickerFlow
    }

    suspend fun getUserSeedMoney(): User? {
        return localRepository.getUserDao().getUserByExchange(exchange = EXCHANGE_BITTHUMB)
    }

    suspend fun getUserCoin(market: String): MyCoin? {
        return localRepository.getMyCoinDao().getCoin(market = market, exchange = EXCHANGE_BITTHUMB)
    }

    suspend fun getUserBtcCoin(): MyCoin? {
        return localRepository.getMyCoinDao()
            .getCoin(market = BTC_MARKET, exchange = EXCHANGE_BITTHUMB)
    }

    suspend fun requestKRWBid(
        market: String,
        coin: MyCoin,
        totalPrice: Double,
        userSeedMoney: Double,
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userDao = localRepository.getUserDao()
        val userCoin = coinDao.getCoin(market = market, exchange = EXCHANGE_BITTHUMB)

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
                    exchange = EXCHANGE_BITTHUMB,
                    price = purchaseAverage.toInt()
                )
            } else {
                coinDao.updatePurchasePrice(
                    market = market,
                    exchange = EXCHANGE_BITTHUMB,
                    price = purchaseAverage
                )
            }
            coinDao.increaseQuantity(
                market = market,
                exchange = EXCHANGE_BITTHUMB,
                amount = coin.quantity
            )
        }
        userDao.updateMinusMoney(exchange = EXCHANGE_BITTHUMB, money = krwTotalPrice)

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

        userDao.updatePlusMoney(exchange = EXCHANGE_BITTHUMB, money = totalPrice)
        if (BigDecimal(userCoinQuantity).minus(BigDecimal(quantity)) <= BigDecimal(0.00000002)
        ) {
            coinDao.delete(exchange = EXCHANGE_BITTHUMB, market = market)
        } else {
            coinDao.decreaseQuantity(
                market = market,
                exchange = EXCHANGE_BITTHUMB,
                amount = quantity
            )
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
        val userCoin = coinDao.getCoin(market = market, exchange = EXCHANGE_BITTHUMB)
        val btcTotalPrice = BigDecimal(totalPrice)
        val minusBTCQuantity = BigDecimal(userSeedBTC).minus(btcTotalPrice)

        if (userCoin == null) {
            coinDao.insert(coin)
            coinDao.updatePurchaseAverageBtcPrice(
                market = market,
                exchange = EXCHANGE_BITTHUMB,
                price = btcPrice
            )
            if (minusBTCQuantity < BigDecimal("0.00000002")) {
                coinDao.delete(market = BTC_MARKET, exchange = EXCHANGE_BITTHUMB)
            } else {
                coinDao.decreaseQuantity(
                    market = BTC_MARKET,
                    exchange = EXCHANGE_BITTHUMB,
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
                exchange = EXCHANGE_BITTHUMB,
                price = purchaseAverage
            )
            coinDao.increaseQuantity(
                market = market,
                exchange = EXCHANGE_BITTHUMB,
                amount = coin.quantity
            )
            if (minusBTCQuantity < BigDecimal("0.00000002")) {
                coinDao.delete(market = BTC_MARKET, exchange = EXCHANGE_BITTHUMB)
            } else {
                coinDao.decreaseQuantity(
                    market = BTC_MARKET,
                    exchange = EXCHANGE_BITTHUMB,
                    amount = btcTotalPrice.toDouble()
                )
            }
            coinDao.updatePurchaseAverageBtcPrice(
                market = market,
                exchange = EXCHANGE_BITTHUMB,
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
        val userBTC = coinDao.getCoin(market = BTC_MARKET, exchange = EXCHANGE_BITTHUMB)

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
                exchange = EXCHANGE_BITTHUMB
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
                exchange = EXCHANGE_BITTHUMB,
                price = purchaseBTCAverage
            )
        }

        coinDao.updatePurchaseAverageBtcPrice(
            market = BTC_MARKET,
            exchange = EXCHANGE_BITTHUMB,
            price = btcPrice
        )

        if (BigDecimal(userCoinQuantity).minus(BigDecimal(quantity)) <= BigDecimal(0.00_000_002)) {
            coinDao.delete(market = market, exchange = EXCHANGE_BITTHUMB)
        } else {
            coinDao.decreaseQuantity(
                market = market,
                amount = quantity,
                exchange = EXCHANGE_BITTHUMB
            )
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
            .select(market = market, exchange = EXCHANGE_BITTHUMB)
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
                exchange = EXCHANGE_BITTHUMB
            )
        )

        val count = transactionInfoDao.getCount(market = market, exchange = EXCHANGE_BITTHUMB)
        val excess = count - 500

        if (excess > 0) {
            transactionInfoDao.deleteExcess(
                market = market,
                count = excess,
                exchange = EXCHANGE_BITTHUMB
            )
        }
    }
}