package org.jeonfeel.moeuibit2.data.local.room.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["market", "exchange"])
data class Favorite(
    val market: String,
    val exchange: String
)