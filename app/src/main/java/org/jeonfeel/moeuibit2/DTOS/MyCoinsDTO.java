package org.jeonfeel.moeuibit2.DTOS;

public class MyCoinsDTO {

    private String myCoinsKoreanName;
    private String myCoinsSymbol;
    private Double myCoinsQuantity;
    private Double myCoinsBuyingAverage;

    public MyCoinsDTO(String myCoinsKoreanName, String myCoinsSymbol, Double myCoinsQuantity, Double myCoinsBuyingAverage) {
        this.myCoinsKoreanName = myCoinsKoreanName;
        this.myCoinsSymbol = myCoinsSymbol;
        this.myCoinsQuantity = myCoinsQuantity;
        this.myCoinsBuyingAverage = myCoinsBuyingAverage;
    }

    public String getMyCoinsKoreanName() {
        return myCoinsKoreanName;
    }

    public void setMyCoinsKoreanName(String myCoinsKoreanName) {
        this.myCoinsKoreanName = myCoinsKoreanName;
    }

    public String getMyCoinsSymbol() {
        return myCoinsSymbol;
    }

    public void setMyCoinsSymbol(String myCoinsSymbol) {
        this.myCoinsSymbol = myCoinsSymbol;
    }

    public Double getMyCoinsQuantity() {
        return myCoinsQuantity;
    }

    public void setMyCoinsQuantity(Double myCoinsQuantity) {
        this.myCoinsQuantity = myCoinsQuantity;
    }

    public Double getMyCoinsBuyingAverage() {
        return myCoinsBuyingAverage;
    }

    public void setMyCoinsBuyingAverage(Double myCoinsBuyingAverage) {
        this.myCoinsBuyingAverage = myCoinsBuyingAverage;
    }
}
