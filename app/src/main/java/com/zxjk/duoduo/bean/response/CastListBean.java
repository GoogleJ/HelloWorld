package com.zxjk.duoduo.bean.response;

public class CastListBean {

    /**
     * roomId : 1
     * topic : 直播主题
     * liveDetails : 直播详情
     * livePoster : 直播海报
     * startTime : 直播开始时间,时间戳(ms)
     * roomStatus : 状态：0
     */

    private String roomId;
    private String topic;
    private String liveDetails;
    private String livePoster;
    private String startTime;
    private String roomStatus;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }
}
