package com.zxjk.duoduo.bean.response;

public class GetLiveInfoByGroupIdResponse {

    /**
     * roomId : 3
     * topic : CYTT中国财富讲解
     * livePoster : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1572619088734.png
     * startTime : 1584173692000
     * playUrl : 拉流地址
     */

    private String roomId;
    private String topic;
    private String livePoster;
    private String startTime;
    private String playUrl;

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

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }
}
