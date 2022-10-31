package org.jeonfeel.moeuibit2.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MyCoin(
    @field:PrimaryKey var market: String,
    var purchasePrice: Double,
    var koreanCoinName: String,
    var symbol: String,
    var quantity: Double,
    @ColumnInfo(defaultValue = "0.0")var PurchaseAverageBtcPrice: Double = 0.0
)