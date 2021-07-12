package org.jeonfeel.moeuibit2;

public class item_rvCoin {

    private String coinName;
    private String currentPrice;
    private String dayToDay;
    private String transactionAmount;

    public item_rvCoin(String coinName, String currentPrice, String dayToDay, String transactionAmount) {
        this.coinName = coinName;
        this.currentPrice = currentPrice;
        this.dayToDay = dayToDay;
        this.transactionAmount = transactionAmount;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getDayToDay() {
        return dayToDay;
    }

    public void setDayToDay(String dayToDay) {
        this.dayToDay = dayToDay;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
