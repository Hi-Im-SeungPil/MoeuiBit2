//package org.jeonfeel.moeuibit2.Database
//
//import androidx.room.*
//import org.jeonfeel.moeuibit2.Database.MyCoin
//import org.jeonfeel.moeuibit2.Database.Favorite
//import org.jeonfeel.moeuibit2.Database.NotSigned
//import org.jeonfeel.moeuibit2.Database.TransactionInfo
//import org.jeonfeel.moeuibit2.Database.UserDAO
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO
//import org.jeonfeel.moeuibit2.Database.FavoriteDAO
//import org.jeonfeel.moeuibit2.Database.TransactionInfoDAO
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase
//import kotlin.jvm.Synchronized
//
//@Dao
//interface UserDAO {
//    @get:Query("SELECT * FROM User")
//    val all: User?
//
//    @Insert
//    fun insertAll(vararg users: User?)
//
//    @Delete
//    fun delete(user: User?)
//
//    @Query("Insert into User values(5000000)")
//    fun insert()
//
//    @Query("UPDATE User SET krw = :money")
//    fun update(money: Long)
//
//    @Query("UPDATE User SET krw = krw + :money")
//    fun updatePlusMoney(money: Long)
//
//    @Query("UPDATE User SET krw = krw - :money")
//    fun updateMinusMoney(money: Long)
//
//    @Query("DELETE FROM User")
//    fun deleteAll()
//}