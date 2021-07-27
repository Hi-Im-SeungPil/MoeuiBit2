package org.jeonfeel.moeuibit2;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import kotlin.jvm.Synchronized;

@androidx.room.Database(entities = {User.class, MyCoin.class,NotSigned.class},version = 1)
public abstract class MoEuiBitDatabase extends RoomDatabase{

    public abstract UserDAO userDAO();
    public abstract MyCoinDAO myCoinDAO();

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
