package org.jeonfeel.moeuibit2.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo

@Dao
interface TransactionInfoDAO {
    @Insert
    suspend fun insert(transactionInfo: TransactionInfo)

    // 거래소별, 마켓별 최근 100개 트랜잭션 조회
    @Query("SELECT * FROM TransactionInfo WHERE market = :market AND exchange = :exchange ORDER BY id DESC LIMIT 100")
    suspend fun select(market: String, exchange: String): List<TransactionInfo>

    // 거래소별, 마켓별 트랜잭션 삭제
    @Query("DELETE FROM TransactionInfo WHERE market = :market AND exchange = :exchange")
    suspend fun delete(market: String, exchange: String)

    // 전체 삭제
    @Query("DELETE FROM TransactionInfo")
    suspend fun deleteAll()

    // 거래소별, 마켓별 오래된 트랜잭션 count만큼 삭제
    @Query(
        "DELETE FROM TransactionInfo WHERE transactionTime IN (" +
                "SELECT transactionTime FROM TransactionInfo WHERE market = :market AND exchange = :exchange ORDER BY transactionTime ASC LIMIT :count)"
    )
    suspend fun deleteExcess(market: String, exchange: String, count: Int)

    // 거래소별, 마켓별 트랜잭션 개수
    @Query("SELECT COUNT(*) FROM TransactionInfo WHERE market = :market AND exchange = :exchange")
    suspend fun getCount(market: String, exchange: String): Int
}