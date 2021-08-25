package org.jeonfeel.moeuibit2.Database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import kotlin.jvm.Synchronized;

@androidx.room.Database(entities = {User.class, MyCoin.class,NotSigned.class,Favorite.class,TransactionInfo.class},version = 1)
public abstract class MoEuiBitDatabase extends RoomDatabase{

    public abstract UserDAO userDAO();
    public abstract MyCoinDAO myCoinDAO();
    public abstract FavoriteDAO favoriteDAO();
    public abstract TransactionInfoDAO transactionInfoDAO();

    private static MoEuiBitDatabase instance;

    private static final Object sLock = new Object();

    @Synchronized
    public static MoEuiBitDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (instance == null) {

                instance = Room.databaseBuilder(context.getApplicationContext()
                        , MoEuiBitDatabase.class
                        , "MoeuiBitDatabase")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
            }
            return instance;
        }
    }
}
