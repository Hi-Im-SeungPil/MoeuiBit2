package org.jeonfeel.moeuibit2.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite

@Dao
interface FavoriteDAO {
    @get:Query("SELECT * FROM Favorite")
    val all: List<Favorite?>?

    @Query("INSERT INTO Favorite values(:market)")
    suspend fun insert(market: String?)

    @Query("DELETE FROM Favorite WHERE market = :market ")
    suspend fun delete(market: String?)

    @Query("SELECT * FROM Favorite WHERE market = :market")
    suspend fun select(market: String?): Favorite?

    @Query("DELETE FROM Favorite ")
    suspend fun deleteAll()
}