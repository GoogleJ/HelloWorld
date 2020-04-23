package com.zxjk.moneyspace.bean.response;

public class GetCustomerBankInfoResponse {

    /**
     * cardholderName : 小Q
     * bank : 工商银行
     * bankNum : 123456
     * subbranch : 西安支行
     * mobile : 15249047865
     */

    private String cardholderName;
    private String bank;
    private String bankNum;
    private String subbranch;
    private String mobile;

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
    }

    public String getSubbranch() {
        return subbranch;
    }

    public void setSubbranch(String subbranch) {
        this.subbranch = subbranch;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
