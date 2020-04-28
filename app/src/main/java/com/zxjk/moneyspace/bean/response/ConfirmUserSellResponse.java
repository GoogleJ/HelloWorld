package com.zxjk.moneyspace.bean.response;

import java.io.Serializable;

public class ConfirmUserSellResponse implements Serializable {

    /**
     * buyId : 3
     * buyDuoduoId : 69826873
     * buyOrderId : 6655749379472764928
     * sellId : 2
     * sellDuoduoId : 78217769
     * bothOrderId : 158691547546032
     * number : 1
     * money : 7.01
     * payType : USDT
     * createTime : 1586915476402
     * status : 3
     * sellPayType : USDT
     * buyNick : 152****7865
     * sellNick : 1446035768
     * payPwd : 670b14728ad9902aecba32e22fa4f6bd
     */

    private String buyId;
    private String buyDuoduoId;
    private String buyOrderId;
    private String sellId;
    private String sellDuoduoId;
    private String bothOrderId;
    private String number;
    private String money;
    private String payType;
    private String createTime;
    private String status;
    private String sellPayType;
    private String buyNick;
    private String sellNick;
    private String payPwd;

    public String getBuyId() {
        return buyId;
    }

    public void setBuyId(String buyId) {
        this.buyId = buyId;
    }

    public String getBuyDuoduoId() {
        return buyDuoduoId;
    }

    public void setBuyDuoduoId(String buyDuoduoId) {
        this.buyDuoduoId = buyDuoduoId;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public String getSellId() {
        return sellId;
    }

    public void setSellId(String sellId) {
        this.sellId = sellId;
    }

    public String getSellDuoduoId() {
        return sellDuoduoId;
    }

    public void setSellDuoduoId(String sellDuoduoId) {
        this.sellDuoduoId = sellDuoduoId;
    }

    public String getBothOrderId() {
        return bothOrderId;
    }

    public void setBothOrderId(String bothOrderId) {
        this.bothOrderId = bothOrderId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSellPayType() {
        return sellPayType;
    }

    public void setSellPayType(String sellPayType) {
        this.sellPayType = sellPayType;
    }

    public String getBuyNick() {
        return buyNick;
    }

    public void setBuyNick(String buyNick) {
        this.buyNick = buyNick;
    }

    public String getSellNick() {
        return sellNick;
    }

    public void setSellNick(String sellNick) {
        this.sellNick = sellNick;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }
}
