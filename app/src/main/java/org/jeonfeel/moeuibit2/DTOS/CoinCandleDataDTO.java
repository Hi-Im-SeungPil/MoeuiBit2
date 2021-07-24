package org.jeonfeel.moeuibit2.DTOS;

public class CoinCandleDataDTO {

    private String candleDateTimeKst;
    private Double openingPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double tradePrice;
    private Double candleTransactionAmount;
    private Double candleTransactionVolume;

    public CoinCandleDataDTO(String candleDateTimeKst, Double openingPrice, Double highPrice
            , Double lowPrice, Double tradePrice, Double candleTransactionAmount, Double candleTransactionVolume) {
        this.candleDateTimeKst = candleDateTimeKst;
        this.openingPrice = openingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.tradePrice = tradePrice;
        this.candleTransactionAmount = candleTransactionAmount;
        this.candleTransactionVolume = candleTransactionVolume;
    }

    public String getCandleDateTimeKst() {
        return candleDateTimeKst;
    }

    public void setCandleDateTimeKst(String candleDateTimeKst) {
        this.candleDateTimeKst = candleDateTimeKst;
    }

    public Double getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(Double openingPrice) {
        this.openingPrice = openingPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public Double getCandleTransactionAmount() {
        return candleTransactionAmount;
    }

    public void setCandleTransactionAmount(Double candleTransactionAmount) {
        this.candleTransactionAmount = candleTransactionAmount;
    }

    public Double getCandleTransactionVolume() {
        return candleTransactionVolume;
    }

    public void setCandleTransactionVolume(Double candleTransactionVolume) {
        this.candleTransactionVolume = candleTransactionVolume;
    }
}
