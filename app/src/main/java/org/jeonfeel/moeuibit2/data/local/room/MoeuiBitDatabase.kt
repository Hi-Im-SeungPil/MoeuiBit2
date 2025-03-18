package org.jeonfeel.moeuibit2.data.local.room

import android.os.Build
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
    version = 5
)
abstract class MoeuiBitDatabase : RoomDatabase() {
    abstract fun userDAO(): UserDAO
    abstract fun myCoinDAO(): MyCoinDAO
    abstract fun favoriteDAO(): FavoriteDAO
    abstract fun transactionInfoDAO(): TransactionInfoDAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.version = 3
                database.execSQL("ALTER TABLE MyCoin ADD COLUMN PurchaseAverageBtcPrice REAL NOT NULL DEFAULT 0.0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.version = 4
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    database.execSQL("ALTER TABLE MyCoin RENAME COLUMN PurchaseAverageBtcPrice TO purchaseAverageBtcPrice")
                    database.execSQL("ALTER TABLE TransactionInfo ADD COLUMN transactionAmountBTC REAL NOT NULL DEFAULT 0.0")
                } else {
                    database.execSQL(
                        "CREATE TABLE MyCoin_new (" +
                                "market TEXT NOT NULL, " +
                                "purchasePrice REAL NOT NULL, " +
                                "koreanCoinName TEXT NOT NULL, " +
                                "symbol TEXT NOT NULL, " +
                                "quantity REAL NOT NULL, " +
                                "purchaseAverageBtcPrice REAL NOT NULL DEFAULT 0.0, " +
                                "PRIMARY KEY(market))"
                    )

                    database.execSQL(
                        "INSERT INTO MyCoin_new (market, purchasePrice, koreanCoinName, symbol, quantity, purchaseAverageBtcPrice) " +
                                "SELECT market, purchasePrice, koreanCoinName, symbol, quantity, PurchaseAverageBtcPrice FROM MyCoin"
                    )

                    database.execSQL("DROP TABLE MyCoin")
                    database.execSQL("ALTER TABLE MyCoin_new RENAME TO MyCoin")
                    database.execSQL("ALTER TABLE TransactionInfo ADD COLUMN transactionAmountBTC REAL NOT NULL DEFAULT 0.0")
                }
            }
        }
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.version = 5
                database.execSQL("CREATE TABLE IF NOT EXISTS `User_new` (`krw` REAL NOT NULL, PRIMARY KEY(`krw`))")
                database.execSQL("INSERT INTO `User_new` (`krw`) SELECT `krw` * 1.0 FROM `User`")
                database.execSQL("DROP TABLE `User`")
                database.execSQL("ALTER TABLE `User_new` RENAME TO `User`")

                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS TransactionInfo_temp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                market TEXT NOT NULL,
                price REAL NOT NULL,
                quantity REAL NOT NULL,
                transactionAmount REAL NOT NULL,
                transactionStatus TEXT NOT NULL,
                transactionTime INTEGER NOT NULL,
                transactionAmountBTC REAL NOT NULL DEFAULT 0.0
            )
        """.trimIndent()
                )

                // 2. 기존 데이터 복사 (transactionAmount를 Long에서 Double로 변환)
                database.execSQL(
                    """
            INSERT INTO TransactionInfo_temp (
                id, market, price, quantity, transactionAmount, 
                transactionStatus, transactionTime, transactionAmountBTC
            )
            SELECT 
                id, market, price, quantity, 
                CAST(transactionAmount AS REAL), 
                transactionStatus, transactionTime, transactionAmountBTC
            FROM TransactionInfo
        """.trimIndent()
                )

                // 3. 기존 테이블 삭제
                database.execSQL("DROP TABLE TransactionInfo")

                // 4. 임시 테이블 이름을 원래 이름으로 변경
                database.execSQL("ALTER TABLE TransactionInfo_temp RENAME TO TransactionInfo")
            }
        }
    }
}