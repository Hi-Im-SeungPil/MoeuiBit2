package org.jeonfeel.moeuibit2;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDAO {

    @Query("SELECT * FROM Favorite")
    List<Favorite> getAll();

    @Query("INSERT INTO Favorite values(:market)")
    void insert(String market);

    @Query("DELETE FROM Favorite WHERE market = :market ")
    void delete(String market);

    @Query("SELECT * FROM Favorite WHERE market = :market")
    Favorite select(String market);

}
