package org.jeonfeel.moeuibit2.data.local.room.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin

@Dao
interface MyCoinDAO {
    @get:Query("SELECT * FROM MYCOIN")
    val all: List<MyCoin?>?

    @Insert
    suspend fun insert(myCoin: MyCoin?)

    @Query("UPDATE MYCOIN SET purchasePrice = :price WHERE market = :market")
    suspend fun updatePurchasePrice(market: String?, price: Double?)

    @Query("UPDATE MYCOIN SET purchasePrice = :price WHERE market = :market")
    suspend fun updatePurchasePriceInt(market: String?, price: Int)

    @Query("UPDATE MYCOIN SET quantity = quantity + :afterQuantity  WHERE market = :market")
    suspend fun updatePlusQuantity(market: String?, afterQuantity: Double?)

    @Query("UPDATE MYCOIN SET quantity = quantity - :afterQuantity  WHERE market = :market")
    suspend fun updateMinusQuantity(market: String?, afterQuantity: Double?)

    @Query("UPDATE MYCOIN SET quantity = :afterQuantity  WHERE market = :market")
    suspend fun updateQuantity(market: String?, afterQuantity: Double?)

    @Query("SELECT * FROM MyCoin where market = :checkMarket")
    suspend fun isInsert(checkMarket: String?): MyCoin?

    @Query("DELETE FROM MyCoin where market = :market")
    suspend fun delete(market: String?)

    @Query("DELETE FROM MyCoin ")
    suspend fun deleteAll()

    @Query("UPDATE MYCOIN SET purchaseAverageBtcPrice = :price WHERE market = :market")
    suspend fun updatePurchaseAverageBtcPrice(market:String?, price: Double?)
}