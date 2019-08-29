package com.zxjk.duoduo.bean.request;

public class UpdateChatConfigRequest {

    /**
     * type : group
     * targetId : 262
     * screenCapture : 0
     * incinerationTime : 30
     */

    private String type;
    private String targetId;
    private int screenCapture;
    private int incinerationTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getScreenCapture() {
        return screenCapture;
    }

    public void setScreenCapture(int screenCapture) {
        this.screenCapture = screenCapture;
    }

    public int getIncinerationTime() {
        return incinerationTime;
    }

    public void setIncinerationTime(int incinerationTime) {
        this.incinerationTime = incinerationTime;
    }
}
