package com.zxjk.moneyspace.bean.request;

public class SignTransactionRequest {

    /**
     * fromAddress : 0x424b9f1b44878fcac66ff1ee900721d946176a85
     * toAddress : 0xcd1B40eed4d3D5CF81AbEcA444A62C3B407C36CA
     * gasPrice : 5
     * balance : 0.1
     * contractAddress :
     * tokenDecimal : 4
     * serialType : 0
     * transType :
     * tokenName : MoT
     * rateBalance : 10
     * importMethod : 1
     */

    private String fromAddress;
    private String toAddress;
    private String gasPrice;
    private String balance;
    private String contractAddress;
    private String tokenDecimal;
    private String serialType;
    private String tokenName;
    private String transType;
    private String rate;

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getTokenDecimal() {
        return tokenDecimal;
    }

    public void setTokenDecimal(String tokenDecimal) {
        this.tokenDecimal = tokenDecimal;
    }

    public String getSerialType() {
        return serialType;
    }

    public void setSerialType(String serialType) {
        this.serialType = serialType;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

}
