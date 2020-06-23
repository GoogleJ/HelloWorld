package com.zxjk.duoduo.bean.response;

public class GetquickOrderInfoResponse {
    /**
     * otherOrderId : 997720200615141533
     * type : BUY
     * total : 3545.0
     * coinAmount : 500.0
     * price : 7.09
     * orderStatus : FINISHED
     * currency : CNY
     * coinSymbol : USDT
     * createTime : 1
     */

    private String otherOrderId;
    private String type;
    private String total;
    private String coinAmount;
    private String price;
    private String orderStatus;
    private String currency;
    private String coinSymbol;
    private String createTime;

    public String getOtherOrderId() {
        return otherOrderId;
    }

    public void setOtherOrderId(String otherOrderId) {
        this.otherOrderId = otherOrderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(String coinAmount) {
        this.coinAmount = coinAmount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
