package com.zxjk.moneyspace.bean.request;

public class SendRedSingleRequest {
    private String receiveCustomerId;
    private double money;
    private String message;
    private String payPwd;
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getReceiveCustomerId() {
        return receiveCustomerId;
    }

    public void setReceiveCustomerId(String receiveCustomerId) {
        this.receiveCustomerId = receiveCustomerId;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }
}
