package org.jeonfeel.moeuibit2.DTOS;

public class RvTransactionInfoDTO {

    private String market;
    private Double price;
    private Double quantity;
    private long transactionAmount;
    private String transactionStatus;
    private String transactionTime;

    public RvTransactionInfoDTO(String market, Double price, Double quantity, long transactionAmount, String transactionStatus, String transactionTime) {
        this.market = market;
        this.price = price;
        this.quantity = quantity;
        this.transactionAmount = transactionAmount;
        this.transactionStatus = transactionStatus;
        this.transactionTime = transactionTime;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public long getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(long transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }
}
