package org.jeonfeel.moeuibit2.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TransactionInfo(
    var market: String,
    var price: Double,
    var quantity: Double,
    var transactionAmount: Long,
    var transactionStatus: String,
    var transactionTime: Long,
    var transactionAmountBTC: Double = 0.0
) {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}