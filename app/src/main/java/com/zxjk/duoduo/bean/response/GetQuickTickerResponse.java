package com.zxjk.duoduo.bean.response;

public class GetQuickTickerResponse {

    /**
     * price : 7.06
     * coinSymbol : USDT
     * minQuota : 500.00
     * currencySymbol : CNY
     * maxQuota : 50000
     */

    private String price;
    private String coinSymbol;
    private String minQuota;
    private String currencySymbol;
    private String maxQuota;
    private String minAmount;
    private String maxAmount;
    private String balance;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(String minAmount) {
        this.minAmount = minAmount;
    }

    public String getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(String maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public String getMinQuota() {
        return minQuota;
    }

    public void setMinQuota(String minQuota) {
        this.minQuota = minQuota;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getMaxQuota() {
        return maxQuota;
    }

    public void setMaxQuota(String maxQuota) {
        this.maxQuota = maxQuota;
    }
}
