package org.jeonfeel.moeuibit2;

public class CoinArcadeDTO {

    private double coinArcadePrice;
    private double coinArcadeSize;
    private String arcadeStatus;

    public CoinArcadeDTO(double coinArcadePrice, double coinArcadeSize, String arcadeStatus) {
        this.coinArcadePrice = coinArcadePrice;
        this.coinArcadeSize = coinArcadeSize;
        this.arcadeStatus = arcadeStatus;
    }

    public double getCoinArcadePrice() {
        return coinArcadePrice;
    }

    public void setCoinArcadePrice(double coinArcadePrice) {
        this.coinArcadePrice = coinArcadePrice;
    }

    public double getCoinArcadeSize() {
        return coinArcadeSize;
    }

    public void setCoinArcadeSize(double coinArcadeSize) {
        this.coinArcadeSize = coinArcadeSize;
    }

    public String getArcadeStatus() {
        return arcadeStatus;
    }

    public void setArcadeStatus(String arcadeStatus) {
        this.arcadeStatus = arcadeStatus;
    }
}
