package org.jeonfeel.moeuibit2.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO

@Entity(primaryKeys = ["market", "exchange"])
data class MyCoin(
    var market: String = "",
    var exchange: String = "",
    var purchasePrice: Double = 0.0,
    var koreanCoinName: String = "",
    var symbol: String = "",
    var quantity: Double = 0.0,
    @ColumnInfo(defaultValue = "0.0") var purchaseAverageBtcPrice: Double = 0.0
) {
    fun parseUserHoldsModel(): UserHoldCoinDTO {
        return UserHoldCoinDTO(
            myCoinKoreanName = "",
            myCoinEngName = "",
            myCoinsSymbol = this.symbol,
            myCoinsQuantity = quantity,
            myCoinsBuyingAverage = purchasePrice,
            currentPrice = 0.0,
            openingPrice = 0.0,
            warning = false,
            isFavorite = 0,
            caution = null,
            market = this.market,
            purchaseAverageBtcPrice = this.purchaseAverageBtcPrice
        )
    }

    fun isEmpty(): Boolean {
        return this.quantity == 0.0
                || this.purchasePrice == 0.0
                || this.market == ""
                || this.symbol == ""
    }
}