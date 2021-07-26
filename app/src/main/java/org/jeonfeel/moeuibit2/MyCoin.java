package org.jeonfeel.moeuibit2;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyCoin {

    @PrimaryKey
    @NonNull
    public String market;
    public Double purchasePrice;
    public String koreanCoinName;
    public String symbol;
    public Double quantity;

    public MyCoin(@NonNull String market, Double purchasePrice, String koreanCoinName, String symbol, Double quantity) {
        this.market = market;
        this.purchasePrice = purchasePrice;
        this.koreanCoinName = koreanCoinName;
        this.symbol = symbol;
        this.quantity = quantity;
    }

    @NonNull
    public String getMarket() {
        return market;
    }

    public void setMarket(@NonNull String market) {
        this.market = market;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getKoreanCoinName() {
        return koreanCoinName;
    }

    public void setKoreanCoinName(String koreanCoinName) {
        this.koreanCoinName = koreanCoinName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

}
