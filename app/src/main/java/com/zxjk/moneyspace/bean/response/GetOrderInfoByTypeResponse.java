package com.zxjk.moneyspace.bean.response;

public class GetOrderInfoByTypeResponse {


    /**
     * bothOrderId : 158692030659432
     * number : 1
     * money : 7.01
     * currency : USDT
     * createTime : 1586920306595
     * status : 3
     * buyOrSell : 0
     * sellOrderId : 222
     * buyOrderId : 6655780209062592512
     */

    private String bothOrderId;
    private String number;
    private String money;
    private String currency;
    private String createTime;
    private String status;
    private String buyOrSell;
    private String sellOrderId;
    private String buyOrderId;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public String getBuyOrSell() {
        return buyOrSell;
    }

    public void setBuyOrSell(String buyOrSell) {
        this.buyOrSell = buyOrSell;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(String sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
    }
}
