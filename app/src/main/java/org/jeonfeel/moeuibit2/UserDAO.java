package org.jeonfeel.moeuibit2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface UserDAO {

    @Query("SELECT * FROM User")
    User getAll();

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Query("UPDATE User SET krw = :money")
    void  update(long money);

    @Query("UPDATE User SET krw = krw + :money")
    void  updatePlusMoney(long money);

    @Query("UPDATE User SET krw = krw - :money")
    void  updateMinusMoney(long money);
}
