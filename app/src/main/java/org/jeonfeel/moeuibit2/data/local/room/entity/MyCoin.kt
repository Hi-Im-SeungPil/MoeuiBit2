package org.jeonfeel.moeuibit2.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO

@Entity
class MyCoin(
    @field:PrimaryKey var market: String = "",
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
            warning = "",
            isFavorite = 0,
            market = this.market,
            purchaseAverageBtcPrice = this.purchaseAverageBtcPrice
        )
    }
}