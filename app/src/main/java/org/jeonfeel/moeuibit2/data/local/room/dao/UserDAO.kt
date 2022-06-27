package org.jeonfeel.moeuibit2.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.jeonfeel.moeuibit2.data.local.room.entity.User

@Dao
interface UserDAO {
    @get:Query("SELECT * FROM User")
    val all: User?

    @Insert
    suspend fun insertAll(vararg users: User?)

    @Delete
    suspend fun delete(user: User)

    @Query("Insert into User values(10000000)")
    suspend fun insert()

    @Query("Insert into User values(1000000)")
    suspend fun errorInsert()

    @Query("UPDATE User SET krw = :money")
    suspend fun update(money: Long)

    @Query("UPDATE User SET krw = krw + :money")
    suspend fun updatePlusMoney(money: Long)

    @Query("UPDATE User SET krw = krw - :money")
    suspend fun updateMinusMoney(money: Long)

    @Query("DELETE FROM User")
    suspend fun deleteAll()
}