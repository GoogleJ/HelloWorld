package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GetChatRoomInfoResponse implements Parcelable {


    /**
     * roomOwnerId : 6
     * roomName : CYTT中国社群直播
     * groupNikeName : CYTT中国社群
     * communityLogo : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg
     * topic : CYTT中国财富讲解
     * liveDetails : 本次嘉宾有：KOL社群联盟
     * livePoster : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1572619088734.png
     * roomStatus : 0
     * startTime : 1584173692000
     * liveContentImg : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1572619088734.png
     */

    private String roomOwnerId;
    private String roomName;
    private String groupNikeName;
    private String communityLogo;
    private String topic;
    private String liveDetails;
    private String livePoster;
    private String roomStatus;
    private String startTime;
    private String liveContentImg;
    private String roomId;
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomOwnerId() {
        return roomOwnerId;
    }

    public void setRoomOwnerId(String roomOwnerId) {
        this.roomOwnerId = roomOwnerId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getGroupNikeName() {
        return groupNikeName;
    }

    public void setGroupNikeName(String groupNikeName) {
        this.groupNikeName = groupNikeName;
    }

    public String getCommunityLogo() {
        return communityLogo;
    }

    public void setCommunityLogo(String communityLogo) {
        this.communityLogo = communityLogo;
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

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLiveContentImg() {
        return liveContentImg;
    }

    public void setLiveContentImg(String liveContentImg) {
        this.liveContentImg = liveContentImg;
    }

    public GetChatRoomInfoResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomOwnerId);
        dest.writeString(this.roomName);
        dest.writeString(this.groupNikeName);
        dest.writeString(this.communityLogo);
        dest.writeString(this.topic);
        dest.writeString(this.liveDetails);
        dest.writeString(this.livePoster);
        dest.writeString(this.roomStatus);
        dest.writeString(this.startTime);
        dest.writeString(this.liveContentImg);
        dest.writeString(this.roomId);
        dest.writeString(this.groupId);
    }

    protected GetChatRoomInfoResponse(Parcel in) {
        this.roomOwnerId = in.readString();
        this.roomName = in.readString();
        this.groupNikeName = in.readString();
        this.communityLogo = in.readString();
        this.topic = in.readString();
        this.liveDetails = in.readString();
        this.livePoster = in.readString();
        this.roomStatus = in.readString();
        this.startTime = in.readString();
        this.liveContentImg = in.readString();
        this.roomId = in.readString();
        this.groupId = in.readString();
    }

    public static final Creator<GetChatRoomInfoResponse> CREATOR = new Creator<GetChatRoomInfoResponse>() {
        @Override
        public GetChatRoomInfoResponse createFromParcel(Parcel source) {
            return new GetChatRoomInfoResponse(source);
        }

        @Override
        public GetChatRoomInfoResponse[] newArray(int size) {
            return new GetChatRoomInfoResponse[size];
        }
    };
}
