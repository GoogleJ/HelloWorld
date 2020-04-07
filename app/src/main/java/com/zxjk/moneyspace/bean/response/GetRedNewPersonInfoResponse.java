package com.zxjk.moneyspace.bean.response;

public class GetRedNewPersonInfoResponse {

    /**
     * redNewPersonStatus : 1
     * everyoneAwardCount :
     * awardSum :
     * receivedCount :
     * redNewPersonStartTime :
     * redNewPersonEndTime :
     * balance : 13.00
     * groupOwnerId :
     * groupId :
     */

    private String redNewPersonStatus;
    private String everyoneAwardCount;
    private String awardSum;
    private String receivedCount;
    private String redNewPersonStartTime;
    private String redNewPersonEndTime;
    private String balance;
    private String groupOwnerId;
    private String groupId;
    private String result = "0";
    private String symbol;
    private String logo;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRedNewPersonStatus() {
        return redNewPersonStatus;
    }

    public void setRedNewPersonStatus(String redNewPersonStatus) {
        this.redNewPersonStatus = redNewPersonStatus;
    }

    public String getEveryoneAwardCount() {
        return everyoneAwardCount;
    }

    public void setEveryoneAwardCount(String everyoneAwardCount) {
        this.everyoneAwardCount = everyoneAwardCount;
    }

    public String getAwardSum() {
        return awardSum;
    }

    public void setAwardSum(String awardSum) {
        this.awardSum = awardSum;
    }

    public String getReceivedCount() {
        return receivedCount;
    }

    public void setReceivedCount(String receivedCount) {
        this.receivedCount = receivedCount;
    }

    public String getRedNewPersonStartTime() {
        return redNewPersonStartTime;
    }

    public void setRedNewPersonStartTime(String redNewPersonStartTime) {
        this.redNewPersonStartTime = redNewPersonStartTime;
    }

    public String getRedNewPersonEndTime() {
        return redNewPersonEndTime;
    }

    public void setRedNewPersonEndTime(String redNewPersonEndTime) {
        this.redNewPersonEndTime = redNewPersonEndTime;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getGroupOwnerId() {
        return groupOwnerId;
    }

    public void setGroupOwnerId(String groupOwnerId) {
        this.groupOwnerId = groupOwnerId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
