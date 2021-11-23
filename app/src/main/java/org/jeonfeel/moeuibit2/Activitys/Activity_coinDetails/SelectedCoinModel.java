package org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails;

import com.google.gson.annotations.SerializedName;

public class SelectedCoinModel {

    @SerializedName("market")
    private String market;
    @SerializedName("trade_price")
    private Double currentPrice;
    @SerializedName("signed_change_rate")
    private Double dayToDay;
    @SerializedName("signed_change_price")
    private Double changePrice;

    public SelectedCoinModel(String market, Double currentPrice, Double dayToDay, Double changePrice) {
        this.market = market;
        this.currentPrice = currentPrice;
        this.dayToDay = dayToDay;
        this.changePrice = changePrice;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getDayToDay() {
        return dayToDay;
    }

    public void setDayToDay(Double dayToDay) {
        this.dayToDay = dayToDay;
    }

    public Double getChangePrice() {
        return changePrice;
    }

    public void setChangePrice(Double changePrice) {
        this.changePrice = changePrice;
    }
}
