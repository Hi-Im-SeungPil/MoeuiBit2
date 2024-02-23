package org.jeonfeel.moeuibit2.data.repository.local

import org.jeonfeel.moeuibit2.data.local.room.MoeuiBitDatabase
import org.jeonfeel.moeuibit2.data.local.room.dao.FavoriteDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.MyCoinDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.TransactionInfoDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.UserDAO

class LocalRepository(private val moeuiBitDatabase: MoeuiBitDatabase) {

    suspend fun getFavoriteDao(): FavoriteDAO {
        return moeuiBitDatabase.favoriteDAO()
    }

    suspend fun getMyCoinDao(): MyCoinDAO {
        return moeuiBitDatabase.myCoinDAO()
    }

    suspend fun getTransactionInfoDao(): TransactionInfoDAO {
        return moeuiBitDatabase.transactionInfoDAO()
    }

    suspend fun getUserDao(): UserDAO {
        return moeuiBitDatabase.userDAO()
    }
}