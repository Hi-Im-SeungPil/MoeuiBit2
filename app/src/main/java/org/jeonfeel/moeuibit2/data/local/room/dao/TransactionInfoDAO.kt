package org.jeonfeel.moeuibit2.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo

@Dao
interface TransactionInfoDAO {
    @Query("INSERT INTO TransactionInfo values(null, :market,:price,:quantity,:transactionAmount,:transactionStatus,:transactionTime)")
    fun insert(
        market: String,
        price: Double,
        quantity: Double,
        transactionAmount: Long,
        transactionStatus: String,
        transactionTime: Long
    )

    @Query("SELECT * FROM TransactionInfo where market=:market")
    fun select(market: String?): List<TransactionInfo>

    @Query("DELETE FROM TransactionInfo ")
    fun deleteAll()
}