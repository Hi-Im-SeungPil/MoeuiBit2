package org.jeonfeel.moeuibit2.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.jeonfeel.moeuibit2.data.local.room.entity.User

@Dao
interface UserDAO {
    // 거래소별 User 조회 (exchange가 기본키)
    @Query("SELECT * FROM User WHERE exchange = :exchange")
    suspend fun getUserByExchange(exchange: String): User?

    @Insert
    suspend fun insertAll(vararg users: User)

    @Delete
    suspend fun delete(user: User)

    // 거래소별 User row 삽입 (예시: UpBit, krw 값 지정)
    @Query("INSERT INTO User (exchange, krw) VALUES (:exchange, :krw)")
    suspend fun insert(exchange: String, krw: Double)

    // krw 값만 업데이트 (거래소별)
    @Query("UPDATE User SET krw = :money WHERE exchange = :exchange")
    suspend fun update(exchange: String, money: Double)

    // krw 증가 (거래소별)
    @Query("UPDATE User SET krw = krw + :money WHERE exchange = :exchange")
    suspend fun updatePlusMoney(exchange: String, money: Double)

    // krw 감소 (거래소별)
    @Query("UPDATE User SET krw = krw - :money WHERE exchange = :exchange")
    suspend fun updateMinusMoney(exchange: String, money: Double)

    // 거래소별 User row 삭제
    @Query("DELETE FROM User WHERE exchange = :exchange")
    suspend fun deleteByExchange(exchange: String)

    // 전체 삭제
    @Query("DELETE FROM User")
    suspend fun deleteAll()
}