package com.zxjk.duoduo.bean.response;

public class GetCreateLiveResponse {

    /**
     * roomId : 1
     * rtmpAddress : rtmp://58.200.131.2:1935/livetv/cctv1
     * liveCode : liveCode
     */

    private String roomId;
    private String rtmpAddress;
    private String liveCode;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRtmpAddress() {
        return rtmpAddress;
    }

    public void setRtmpAddress(String rtmpAddress) {
        this.rtmpAddress = rtmpAddress;
    }

    public String getLiveCode() {
        return liveCode;
    }

    public void setLiveCode(String liveCode) {
        this.liveCode = liveCode;
    }
}
