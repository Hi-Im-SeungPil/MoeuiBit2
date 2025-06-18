package org.jeonfeel.moeuibit2.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["exchange"])
data class User(
    var exchange: String = "UpBit",
    var krw: Double
)