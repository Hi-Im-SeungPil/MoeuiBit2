package org.jeonfeel.moeuibit2.data.local.room.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin

@Dao
interface MyCoinDAO {
    @get:Query("SELECT * FROM MyCoin")
    val all: List<MyCoin>

    // 거래소별 전체 코인 조회
    @Query("SELECT * FROM MyCoin WHERE exchange = :exchange")
    suspend fun getAllByExchange(exchange: String): List<MyCoin>

    @Insert
    suspend fun insert(myCoin: MyCoin)

    // 구매 가격 업데이트
    @Query("UPDATE MyCoin SET purchasePrice = :price WHERE market = :market AND exchange = :exchange")
    suspend fun updatePurchasePrice(market: String, exchange: String, price: Double)

    // 구매 가격(정수형) 업데이트
    @Query("UPDATE MyCoin SET purchasePrice = :price WHERE market = :market AND exchange = :exchange")
    suspend fun updatePurchasePriceInt(market: String, exchange: String, price: Int)

    // 수량 증가
    @Query("UPDATE MyCoin SET quantity = quantity + :amount WHERE market = :market AND exchange = :exchange")
    suspend fun increaseQuantity(market: String, exchange: String, amount: Double)

    // 수량 감소
    @Query("UPDATE MyCoin SET quantity = quantity - :amount WHERE market = :market AND exchange = :exchange")
    suspend fun decreaseQuantity(market: String, exchange: String, amount: Double)

    // 수량 설정
    @Query("UPDATE MyCoin SET quantity = :quantity WHERE market = :market AND exchange = :exchange")
    suspend fun setQuantity(market: String, exchange: String, quantity: Double)

    // 특정 코인 조회
    @Query("SELECT * FROM MyCoin WHERE market = :market AND exchange = :exchange")
    suspend fun getCoin(market: String, exchange: String): MyCoin?

    // 특정 코인 삭제
    @Query("DELETE FROM MyCoin WHERE market = :market AND exchange = :exchange")
    suspend fun delete(market: String, exchange: String)

    // 전체 삭제
    @Query("DELETE FROM MyCoin")
    suspend fun deleteAll()

    // BTC 평균 구매가 업데이트
    @Query("UPDATE MyCoin SET purchaseAverageBtcPrice = :price WHERE market = :market AND exchange = :exchange")
    suspend fun updatePurchaseAverageBtcPrice(market: String, exchange: String, price: Double)

    // 거래소별 전체 코인 삭제
    @Query("DELETE FROM MyCoin WHERE exchange = :exchange")
    suspend fun deleteAllByExchange(exchange: String)
}