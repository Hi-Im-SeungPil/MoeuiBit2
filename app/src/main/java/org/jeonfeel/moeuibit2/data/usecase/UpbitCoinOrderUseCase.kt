package org.jeonfeel.moeuibit2.data.usecase

import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.constants.ASK
import org.jeonfeel.moeuibit2.constants.BID
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.local.room.entity.User
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitOrderBookReq
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.OrderBookRequestTypeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.OrderBookTicketField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpbitOrderBookSocketService
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.round

enum class OrderBookKind {
    ASK, BID
}

class UpbitCoinOrderUseCase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository,
    private val upBitSocketService: UpbitOrderBookSocketService
) : BaseUseCase() {
    suspend fun getOrderBook(market: String): Flow<Any> {
        val getUpbitOrderBookReq = GetUpbitOrderBookReq(market = market)
        return requestApiResult(
            result = upbitRepository.getOrderBook(getUpbitOrderBookReq),
            onSuccess = {
                it[0].mapTo()
            }
        )
    }

    suspend fun requestSubscribeOrderBook(marketCodes: List<String>) {
        upBitSocketService.requestUpbitOrderBookRequest(
            listOf(
                OrderBookTicketField(ticket = UUID.randomUUID().toString()),
                OrderBookRequestTypeField(codes = marketCodes),
            )
        )
    }

    suspend fun requestObserveOrderBook(): Flow<UpbitSocketOrderBookRes> {
        return upBitSocketService.collectUpbitOrderBook()
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
        userSeedMoney: Long
    ) {
        val coinDao = localRepository.getMyCoinDao()
        val userDao = localRepository.getUserDao()
        val userCoin = coinDao.isInsert(market)
        val commission = (totalPrice * (COMMISSION_FEE)).newBigDecimal()
        var krwTotalPrice = (totalPrice + commission.toLong())
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
        localRepository.getTransactionInfoDao().insert(
            TransactionInfo(
                market,
                coin.purchasePrice,
                coin.quantity,
                totalPrice,
                BID,
                System.currentTimeMillis()
            )
        )
    }

    suspend fun requestKRWAsk(
        market: String,
        quantity: Double,
        totalPrice: Long,
        userCoinQuantity: BigDecimal,
        currentPrice: BigDecimal
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
        localRepository.getTransactionInfoDao().insert(
            TransactionInfo(
                market,
                currentPrice.toDouble(),
                quantity,
                totalPrice,
                ASK,
                System.currentTimeMillis()
            )
        )
    }
}