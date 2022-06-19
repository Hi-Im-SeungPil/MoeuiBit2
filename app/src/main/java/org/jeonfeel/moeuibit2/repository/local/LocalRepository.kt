package org.jeonfeel.moeuibit2.repository.local

import org.jeonfeel.moeuibit2.data.local.room.MoeuiBitDatabase
import org.jeonfeel.moeuibit2.data.local.room.dao.FavoriteDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.MyCoinDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.TransactionInfoDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.UserDAO

class LocalRepository(private val moeuiBitDatabase: MoeuiBitDatabase) {

    fun getFavoriteDao(): FavoriteDAO {
        return moeuiBitDatabase.favoriteDAO()
    }

    fun getMyCoinDao(): MyCoinDAO {
        return moeuiBitDatabase.myCoinDAO()
    }

    fun getTransactionInfoDao(): TransactionInfoDAO {
        return moeuiBitDatabase.transactionInfoDAO()
    }

    fun getUserDao(): UserDAO {
        return moeuiBitDatabase.userDAO()
    }
}