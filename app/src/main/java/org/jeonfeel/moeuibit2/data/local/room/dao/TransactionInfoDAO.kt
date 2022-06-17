package org.jeonfeel.moeuibit2.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo

@Dao
interface TransactionInfoDAO {
    @Insert
    suspend fun insert(transactionInfo: TransactionInfo)

    @Query("SELECT * FROM TransactionInfo where market=:market")
    suspend fun select(market: String?): List<TransactionInfo>

    @Query("DELETE FROM TransactionInfo WHERE market=:market")
    suspend fun delete(market: String)

    @Query("DELETE FROM TransactionInfo ")
    suspend fun deleteAll()
}