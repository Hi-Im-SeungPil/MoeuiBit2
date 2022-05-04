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
    fun insert(myCoin: MyCoin?)

    @Query("UPDATE MYCOIN SET purchasePrice = :price WHERE market = :market")
    fun updatePurchasePrice(market: String?, price: Double?)

    @Query("UPDATE MYCOIN SET purchasePrice = :price WHERE market = :market")
    fun updatePurchasePriceInt(market: String?, price: Int)

    @Query("UPDATE MYCOIN SET quantity = quantity + :afterQuantity  WHERE market = :market")
    fun updatePlusQuantity(market: String?, afterQuantity: Double?)

    @Query("UPDATE MYCOIN SET quantity = quantity - :afterQuantity  WHERE market = :market")
    fun updateMinusQuantity(market: String?, afterQuantity: Double?)

    @Query("UPDATE MYCOIN SET quantity = :afterQuantity  WHERE market = :market")
    fun updateQuantity(market: String?, afterQuantity: Double?)

    @Query("SELECT * FROM MyCoin where market = :checkMarket")
    fun isInsert(checkMarket: String?): MyCoin?

    @Query("DELETE FROM MyCoin where market = :market")
    fun delete(market: String?)

    @Query("DELETE FROM MyCoin ")
    fun deleteAll()
}