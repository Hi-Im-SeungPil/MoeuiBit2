package org.jeonfeel.moeuibit2.Database;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TransactionInfo {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String market;
    public Double price;
    public Double quantity;
    public long transactionAmount;
    public String transactionStatus;
    public String transactionTime;

    public TransactionInfo(String market, Double price, Double quantity, long transactionAmount, String transactionStatus,String transactionTime) {
        this.market = market;
        this.price = price;
        this.quantity = quantity;
        this.transactionAmount = transactionAmount;
        this.transactionStatus = transactionStatus;
        this.transactionTime = transactionTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
