package com.zxjk.moneyspace.bean.request;

public class AddBankInfoRequest {

    /**
     * cardholderName : 持卡人姓名
     * bank : 所属银行
     * bankNum : 银行卡号
     * subbranch : 所属支行
     */

    private String cardholderName;
    private String bank;
    private String bankNum;
    private String subbranch;

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
}
