//package org.jeonfeel.moeuibit2.Database
//
//import android.content.Context
//import androidx.room.PrimaryKey
//import androidx.room.Dao
//import androidx.room.Delete
//import org.jeonfeel.moeuibit2.Database.MyCoin
//import androidx.room.ColumnInfo
//import org.jeonfeel.moeuibit2.Database.Favorite
//import androidx.room.Database
//import org.jeonfeel.moeuibit2.Database.NotSigned
//import org.jeonfeel.moeuibit2.Database.TransactionInfo
//import androidx.room.RoomDatabase
//import org.jeonfeel.moeuibit2.Database.UserDAO
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO
//import org.jeonfeel.moeuibit2.Database.FavoriteDAO
//import org.jeonfeel.moeuibit2.Database.TransactionInfoDAO
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase
//import kotlin.jvm.Synchronized
//import androidx.room.Room
//
//@Database(entities = [User::class, MyCoin::class, NotSigned::class, Favorite::class, TransactionInfo::class],
//    version = 1)
//abstract class MoEuiBitDatabase : RoomDatabase() {
//    abstract fun userDAO(): UserDAO?
//    abstract fun myCoinDAO(): MyCoinDAO?
//    abstract fun favoriteDAO(): FavoriteDAO?
//    abstract fun transactionInfoDAO(): TransactionInfoDAO?
//
//    companion object {
//        private var instance: MoEuiBitDatabase? = null
//        private val sLock = Any()
//        @JvmStatic
//        @Synchronized
//        fun getInstance(context: Context): MoEuiBitDatabase? {
//            synchronized(sLock) {
//                if (instance == null) {
//                    instance = Room.databaseBuilder(context.applicationContext,
//                        MoEuiBitDatabase::class.java,
//                        "MoeuiBitDatabase")
//                        .fallbackToDestructiveMigration()
//                        .allowMainThreadQueries()
//                        .build()
//                }
//                return instance
//            }
//        }
//    }
//}