package com.zxjk.moneyspace.bean.response;

import java.io.Serializable;

public class ReleaseSaleResponse implements Serializable {

    /**
     * id : 49
     * customerId : 4
     * duoduoId : 10000004
     * sellOrderId : 15518705466054
     * number : 400
     * money : 400
     * createTime : 1551870546
     * currency : 1
     * payType : 1,2,3
     * closeTime :
     * isDelete :
     * payPwd :
     */

    private String id;
    private int customerId;
    private String duoduoId;
    private String sellOrderId;
    private String buyOrderId;
    private String bothOrderId;
    private String number;
    private String money;
    private String createTime;
    private String currency;
    private String payType;
    private String closeTime;
    private String isDelete;
    private String payPwd;
    private String nick;
    private String receiptNumber;
    private String receiptPicture;
    private String wechatNick;
    private String payTime;
    private String openBank;
    private String mobile;
    private String defaultLimitTransactions;
    private String alyQR;

    public String getAlyQR() {
        return alyQR;
    }

    public void setAlyQR(String alyQR) {
        this.alyQR = alyQR;
    }

    public String getDefaultLimitTransactions() {
        return defaultLimitTransactions;
    }

    public void setDefaultLimitTransactions(String defaultLimitTransactions) {
        this.defaultLimitTransactions = defaultLimitTransactions;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWechatNick() {
        return wechatNick;
    }

    public void setWechatNick(String wechatNick) {
        this.wechatNick = wechatNick;
    }

    public String getOpenBank() {
        return openBank;
    }

    public void setOpenBank(String openBank) {
        this.openBank = openBank;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public String getBothOrderId() {
        return bothOrderId;
    }

    public void setBothOrderId(String bothOrderId) {
        this.bothOrderId = bothOrderId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getReceiptPicture() {
        return receiptPicture;
    }

    public void setReceiptPicture(String receiptPicture) {
        this.receiptPicture = receiptPicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getDuoduoId() {
        return duoduoId;
    }

    public void setDuoduoId(String duoduoId) {
        this.duoduoId = duoduoId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(String sellOrderId) {
        this.sellOrderId = sellOrderId;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }
}
