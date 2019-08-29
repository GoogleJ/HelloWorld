package com.zxjk.duoduo.bean;

import java.io.Serializable;

public class ConversationInfo implements Serializable {
    //阅后即焚 时间
    private int incinerationTime = -1;

    //截屏通知开关
    private int captureScreenEnabled = 0;

    public int getMessageBurnTime() {
        return incinerationTime;
    }

    public void setMessageBurnTime(int messageBurnTime) {
        this.incinerationTime = messageBurnTime;
    }

    public int getCaptureScreenEnabled() {
        return captureScreenEnabled;
    }

    public void setCaptureScreenEnabled(int captureScreenEnabled) {
        this.captureScreenEnabled = captureScreenEnabled;
    }

}
