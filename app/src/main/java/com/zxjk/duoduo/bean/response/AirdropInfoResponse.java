package com.zxjk.duoduo.bean.response;

public class AirdropInfoResponse {
    private String receive;
    private String lastTime;
    private int receiveCount;
    private String openShare;
    private String increaseInShare;
    private int shareCount;
    private String reward;
    private String symbol;

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public String getOpenShare() {
        return openShare;
    }

    public void setOpenShare(String openShare) {
        this.openShare = openShare;
    }

    public String getIncreaseInShare() {
        return increaseInShare;
    }

    public void setIncreaseInShare(String increaseInShare) {
        this.increaseInShare = increaseInShare;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public int getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(int receiveCount) {
        this.receiveCount = receiveCount;
    }
}
