package org.jeonfeel.moeuibit2.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDAO {

    @Query("SELECT * FROM User")
    User getAll();

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Query("Insert into User values(5000000)")
    void insert();

    @Query("UPDATE User SET krw = :money")
    void  update(long money);

    @Query("UPDATE User SET krw = krw + :money")
    void  updatePlusMoney(long money);

    @Query("UPDATE User SET krw = krw - :money")
    void  updateMinusMoney(long money);
}
