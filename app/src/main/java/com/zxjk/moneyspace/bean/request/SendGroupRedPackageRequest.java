package com.zxjk.moneyspace.bean.request;

public class SendGroupRedPackageRequest {
    private String message;
    private String payPwd;
    private String groupId;
    private String number;
    private String money;
    private String type;
    private String totalAmount;
    private String isGame;
    private String symbol;
    private String sendRedPacketType;

    private String redSwitch;
    private String lastNumIs;
    private String lastNumNot;
    private String penultimateIs;
    private String penultimateNot;

    public String getRedSwitch() {
        return redSwitch;
    }

    public void setRedSwitch(String redSwitch) {
        this.redSwitch = redSwitch;
    }

    public String getLastNumIs() {
        return lastNumIs;
    }

    public void setLastNumIs(String lastNumIs) {
        this.lastNumIs = lastNumIs;
    }

    public String getLastNumNot() {
        return lastNumNot;
    }

    public void setLastNumNot(String lastNumNot) {
        this.lastNumNot = lastNumNot;
    }

    public String getPenultimateIs() {
        return penultimateIs;
    }

    public void setPenultimateIs(String penultimateIs) {
        this.penultimateIs = penultimateIs;
    }

    public String getPenultimateNot() {
        return penultimateNot;
    }

    public void setPenultimateNot(String penultimateNot) {
        this.penultimateNot = penultimateNot;
    }

    public String getSendRedPacketType() {
        return sendRedPacketType;
    }

    public void setSendRedPacketType(String sendRedPacketType) {
        this.sendRedPacketType = sendRedPacketType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getIsGame() {
        return isGame;
    }

    public void setIsGame(String isGame) {
        this.isGame = isGame;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
