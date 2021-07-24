package org.jeonfeel.moeuibit2.DTOS;

public class CoinDTO implements Comparable<CoinDTO>{

    private int id;
    private String market;
    private String koreanName;
    private String englishName;
    private Double currentPrice;
    private Double dayToDay;
    private Double transactionAmount;
    private String symbol;
    public static String orderStatus;

    public CoinDTO(String market, String koreanName, String englishName, Double currentPrice, Double dayToDay, Double transactionAmount,String symbol) {
        this.market = market;
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.currentPrice = currentPrice;
        this.dayToDay = dayToDay;
        this.transactionAmount = transactionAmount;
        this.symbol = symbol;
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
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public int compareTo(CoinDTO c) {

        switch (orderStatus) {
            case "currentPrice":
                return this.currentPrice.compareTo(c.getCurrentPrice());
            case "dayToDay":
                return this.dayToDay.compareTo(c.getDayToDay());
            case "transactionAmount":
                return this.transactionAmount.compareTo(c.getTransactionAmount());
        }
        return -1;
    }
}
