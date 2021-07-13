package org.jeonfeel.moeuibit2;

public class CoinDTO {

    private String market;
    private String koreanName;
    private String englishName;
    private Double currentPrice;
    private Double dayToDay;
    private Double TransactionAmount;

    public CoinDTO(String market, String koreanName, String englishName, Double currentPrice, Double dayToDay, Double transactionAmount) {
        this.market = market;
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.currentPrice = currentPrice;
        this.dayToDay = dayToDay;
        TransactionAmount = transactionAmount;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public void setKoreanName(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
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

    public Double getTransactionAmount() {
        return TransactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        TransactionAmount = transactionAmount;
    }
}
