package org.jeonfeel.moeuibit2.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MyCoin(
    @field:PrimaryKey var market: String,
    var purchasePrice: Double,
    var koreanCoinName: String,
    var symbol: String,
    var quantity: Double
)