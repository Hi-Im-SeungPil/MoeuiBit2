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
    fun insertAll(vararg users: User?)

    @Delete
    fun delete(user: User)

    @Query("Insert into User values(10000000)")
    fun insert()

    @Query("UPDATE User SET krw = :money")
    fun update(money: Long)

    @Query("UPDATE User SET krw = krw + :money")
    fun updatePlusMoney(money: Long)

    @Query("UPDATE User SET krw = krw - :money")
    fun updateMinusMoney(money: Long)

    @Query("DELETE FROM User")
    fun deleteAll()
}