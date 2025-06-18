package org.jeonfeel.moeuibit2.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite

@Dao
interface FavoriteDAO {

    @Query("SELECT * FROM Favorite WHERE exchange = :exchange")
    suspend fun getAllByExchange(exchange: String): List<Favorite>?

    @Query("INSERT INTO Favorite(market, exchange) VALUES(:market, :exchange)")
    suspend fun insert(market: String?, exchange: String?)

    @Query("DELETE FROM Favorite WHERE market = :market AND exchange = :exchange")
    suspend fun delete(market: String?, exchange: String?)

    @Query("SELECT * FROM Favorite WHERE market = :market AND exchange = :exchange")
    suspend fun select(market: String?, exchange: String?): Favorite?

    @Query("DELETE FROM Favorite")
    suspend fun deleteAll()
}