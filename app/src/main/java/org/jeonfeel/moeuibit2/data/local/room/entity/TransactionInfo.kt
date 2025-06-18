package org.jeonfeel.moeuibit2.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransactionInfo(
    var market: String,
    var exchange: String,
    var price: Double,
    var quantity: Double,
    var transactionAmount: Double,
    var transactionStatus: String,
    var transactionTime: Long,
    var transactionAmountBTC: Double = 0.0,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)