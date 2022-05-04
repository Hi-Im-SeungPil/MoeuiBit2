package org.jeonfeel.moeuibit2.repository.local

import org.jeonfeel.moeuibit2.data.local.room.AppDatabase
import org.jeonfeel.moeuibit2.data.local.room.dao.FavoriteDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.MyCoinDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.TransactionInfoDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.UserDAO

class LocalRepository(private val appDatabase: AppDatabase) {

    fun getFavoriteDao(): FavoriteDAO {
        return appDatabase.favoriteDAO()
    }

    fun getMyCoinDao(): MyCoinDAO {
        return appDatabase.myCoinDAO()
    }

    fun getTransactionInfoDao(): TransactionInfoDAO {
        return appDatabase.transactionInfoDAO()
    }

    fun getUserDao(): UserDAO {
        return appDatabase.userDAO()
    }
}