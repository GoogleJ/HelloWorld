package com.zxjk.duoduo.bean.request;

public class CreateLiveRequest {
    private String groupId;
    private String topic;
    private String liveDetails;
    private String livePoster;
    private String liveContentImg;
    private String startTime;
    private String groupNikeName;
    private String liveType;

    public String getLiveType() {
        return liveType;
    }

    public void setLiveType(String liveType) {
        this.liveType = liveType;
    }

    public String getGroupNikeName() {
        return groupNikeName;
    }

    public void setGroupNikeName(String groupNikeName) {
        this.groupNikeName = groupNikeName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLiveDetails() {
        return liveDetails;
    }

    public void setLiveDetails(String liveDetails) {
        this.liveDetails = liveDetails;
    }

    public String getLivePoster() {
        return livePoster;
    }

    public void setLivePoster(String livePoster) {
        this.livePoster = livePoster;
    }

    public String getLiveContentImg() {
        return liveContentImg;
    }

    public void setLiveContentImg(String liveContentImg) {
        this.liveContentImg = liveContentImg;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
