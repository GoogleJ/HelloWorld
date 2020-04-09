package com.zxjk.moneyspace.bean.response;

public class GetNumbeOfTransactionResponse {

    /**
     * hkPrice : 0.88
     * NumbeOfTransaction : ["100","500","1000","5004"]
     */

    private String exchangeRate;
    private String hkPrice;
    private String minExchangeFee;

    public String getMinExchangeFee() {
        return minExchangeFee;
    }

    public void setMinExchangeFee(String minExchangeFee) {
        this.minExchangeFee = minExchangeFee;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getHkPrice() {
        return hkPrice;
    }

    public void setHkPrice(String hkPrice) {
        this.hkPrice = hkPrice;
    }

}
