package com.zxjk.duoduo.bean.response;

import java.io.Serializable;

public class GetLinkCoinOrdersOrderDetails implements Serializable {
    /**
     * otherOrderId : 997720200615141533
     * type : BUY
     * total : 3545.0
     * coinAmount : 500.0
     * price : 7.09
     * orderStatus : FINISHED
     * payType : ALIPAY
     * alipayName : HAILANGBIZ
     * alipayId : hailangbiz的支付宝13121366644
     * alipayUrl : https://res.linkcoin.me//kycdata-linkcoin/qrcode/578b0c90786e473.jpg
     * currency : CNY
     * coinSymbol : USDT
     * sellerNickName : rightbtc
     * appealType : 0
     * appealRemark : -
     * priceType : 1
     * createTime : 12321312
     */


    private String payMoneyTime;
    private int showDispute;
    private String otherOrderId;
    private String type;
    private String total;
    private String coinAmount;
    private String price;
    private String orderStatus;
    private String payType;
    private String alipayName;
    private String alipayId;
    private String alipayUrl;
    private String currency;
    private String coinSymbol;
    private String sellerNickName;
    private int appealType;
    private String appealRemark;
    private int priceType;
    private String createTime;
    private String orderId;
    private String accountId;
    private String otherUserId;
    private String weixinName;
    private String weixinId;
    private String weixinUrl;
    private String cardCode;
    private String cardBank;
    private String cardUserName;
    private String timeoutMinute;
    private String disputeMinute;
    private String cardAddress;
    private int autoPayCoin;

    public int getAutoPayCoin() {
        return autoPayCoin;
    }

    public void setAutoPayCoin(int autoPayCoin) {
        this.autoPayCoin = autoPayCoin;
    }

    public String getCardAddress() {
        return cardAddress;
    }

    public void setCardAddress(String cardAddress) {
        this.cardAddress = cardAddress;
    }

    public String getPayMoneyTime() {
        return payMoneyTime;
    }

    public void setPayMoneyTime(String payMoneyTime) {
        this.payMoneyTime = payMoneyTime;
    }

    public int getShowDispute() {
        return showDispute;
    }

    public void setShowDispute(int showDispute) {
        this.showDispute = showDispute;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getWeixinName() {
        return weixinName;
    }

    public void setWeixinName(String weixinName) {
        this.weixinName = weixinName;
    }

    public String getWeixinId() {
        return weixinId;
    }

    public void setWeixinId(String weixinId) {
        this.weixinId = weixinId;
    }

    public String getWeixinUrl() {
        return weixinUrl;
    }

    public void setWeixinUrl(String weixinUrl) {
        this.weixinUrl = weixinUrl;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getCardBank() {
        return cardBank;
    }

    public void setCardBank(String cardBank) {
        this.cardBank = cardBank;
    }

    public String getCardUserName() {
        return cardUserName;
    }

    public void setCardUserName(String cardUserName) {
        this.cardUserName = cardUserName;
    }

    public String getTimeoutMinute() {
        return timeoutMinute;
    }

    public void setTimeoutMinute(String timeoutMinute) {
        this.timeoutMinute = timeoutMinute;
    }

    public String getDisputeMinute() {
        return disputeMinute;
    }

    public void setDisputeMinute(String disputeMinute) {
        this.disputeMinute = disputeMinute;
    }

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

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getAlipayName() {
        return alipayName;
    }

    public void setAlipayName(String alipayName) {
        this.alipayName = alipayName;
    }

    public String getAlipayId() {
        return alipayId;
    }

    public void setAlipayId(String alipayId) {
        this.alipayId = alipayId;
    }

    public String getAlipayUrl() {
        return alipayUrl;
    }

    public void setAlipayUrl(String alipayUrl) {
        this.alipayUrl = alipayUrl;
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

    public String getSellerNickName() {
        return sellerNickName;
    }

    public void setSellerNickName(String sellerNickName) {
        this.sellerNickName = sellerNickName;
    }

    public int getAppealType() {
        return appealType;
    }

    public void setAppealType(int appealType) {
        this.appealType = appealType;
    }

    public String getAppealRemark() {
        return appealRemark;
    }

    public void setAppealRemark(String appealRemark) {
        this.appealRemark = appealRemark;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
