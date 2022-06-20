package org.jeonfeel.moeuibit2.data.local.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.jeonfeel.moeuibit2.data.local.room.dao.FavoriteDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.MyCoinDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.TransactionInfoDAO
import org.jeonfeel.moeuibit2.data.local.room.dao.UserDAO
import org.jeonfeel.moeuibit2.data.local.room.entity.*

@Database(
    entities = [User::class, MyCoin::class, NotSigned::class, Favorite::class, TransactionInfo::class],
    version = 2
)
abstract class MoeuiBitDatabase : RoomDatabase() {
    abstract fun userDAO(): UserDAO
    abstract fun myCoinDAO(): MyCoinDAO
    abstract fun favoriteDAO(): FavoriteDAO
    abstract fun transactionInfoDAO(): TransactionInfoDAO

    companion object {
        val migrations1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.version = 2
                database.execSQL(
                    "CREATE TABLE MyCoin_new (" +
                            "market TEXT NOT NULL, " +
                            "purchasePrice REAL NOT NULL, " +
                            "koreanCoinName TEXT NOT NULL, " +
                            "symbol TEXT NOT NULL, " +
                            "quantity REAL NOT NULL, " +
                            "PRIMARY KEY(market))"
                )

                database.execSQL(
                    "CREATE TABLE TransactionInfo_new (" +
                            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                            "market TEXT NOT NULL, " +
                            "price REAL NOT NULL, " +
                            "quantity REAL NOT NULL, " +
                            "transactionAmount INTEGER NOT NULL, " +
                            "transactionStatus TEXT NOT NULL, " +
                            "transactionTime INTEGER NOT NULL " +
                            ")"
                )

                database.execSQL(
                    "INSERT INTO MyCoin_new (market, purchasePrice, koreanCoinName, symbol, quantity) " +
                            "SELECT market, purchasePrice, koreanCoinName, symbol, quantity FROM MyCoin"
                )

                database.execSQL(
                    "INSERT INTO TransactionInfo_new (id, market, price, quantity, transactionAmount, transactionStatus, transactionTime) " +
                            "SELECT id, market, price, quantity, transactionAmount, transactionStatus, transactionTime FROM TransactionInfo"
                )

                database.execSQL("DROP TABLE MyCoin")
                database.execSQL("DROP TABLE TransactionInfo")

                database.execSQL("ALTER TABLE MyCoin_New RENAME TO MyCoin")
                database.execSQL("ALTER TABLE TransactionInfo_new RENAME TO TransactionInfo")

            }
        }
    }
}