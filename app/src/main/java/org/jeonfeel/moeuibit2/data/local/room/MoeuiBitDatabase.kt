package org.jeonfeel.moeuibit2.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import org.jeonfeel.moeuibit2.data.local.room.dao.FavoriteDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.MyCoinDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.TransactionInfoDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.UserDAO
import org.jeonfeel.moeuibit2.data.local.room.entity.*

@Database(entities = [User::class, MyCoin::class, NotSigned::class, Favorite::class, TransactionInfo::class],
    version = 1)
abstract class MoeuiBitDatabase: RoomDatabase() {
    abstract fun userDAO(): UserDAO
    abstract fun myCoinDAO(): MyCoinDAO
    abstract fun favoriteDAO(): FavoriteDAO
    abstract fun transactionInfoDAO(): TransactionInfoDAO
}