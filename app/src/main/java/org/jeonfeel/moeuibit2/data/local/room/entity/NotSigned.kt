package org.jeonfeel.moeuibit2.data.local.room.entity
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class NotSigned {
    @PrimaryKey
    @NonNull
    var market: String = ""

    @ColumnInfo
    var koreanCoinName: String? = null

    @ColumnInfo
    var symbol: String? = null

    @ColumnInfo
    var notSignedPrice: Double? = null

    @ColumnInfo
    var quantity: Double? = null
}