package org.jeonfeel.moeuibit2.DTOS;

public class CoinCandleDataDTO {

    private String candleDateTimeKst;
    private Double candleTransactionAmount;

    public CoinCandleDataDTO(String candleDateTimeKst, Double candleTransactionAmount) {
        this.candleDateTimeKst = candleDateTimeKst;
        this.candleTransactionAmount = candleTransactionAmount;
    }

    public String getCandleDateTimeKst() {
        return candleDateTimeKst;
    }

    public void setCandleDateTimeKst(String candleDateTimeKst) {
        this.candleDateTimeKst = candleDateTimeKst;
    }

    public Double getCandleTransactionAmount() {
        return candleTransactionAmount;
    }

    public void setCandleTransactionAmount(Double candleTransactionAmount) {
        this.candleTransactionAmount = candleTransactionAmount;
    }

}
