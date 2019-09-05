package com.zxjk.duoduo.bean;

import java.io.Serializable;

public class ConversationInfo implements Serializable {
    //阅后即焚 时间
    private int messageBurnTime = -1;

    //截屏通知开关
    private int captureScreenEnabled = 0;

    //对方是否开启截屏通知
    private int targetCaptureScreenEnabled = 0;

    public int getTargetCaptureScreenEnabled() {
        return targetCaptureScreenEnabled;
    }

    public void setTargetCaptureScreenEnabled(int targetCaptureScreenEnabled) {
        this.targetCaptureScreenEnabled = targetCaptureScreenEnabled;
    }

    public int getMessageBurnTime() {
        return messageBurnTime;
    }

    public void setMessageBurnTime(int messageBurnTime) {
        this.messageBurnTime = messageBurnTime;
    }

    public int getCaptureScreenEnabled() {
        return captureScreenEnabled;
    }

    public void setCaptureScreenEnabled(int captureScreenEnabled) {
        this.captureScreenEnabled = captureScreenEnabled;
    }

}
