package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class PermissionInfoBean implements Parcelable {

    /**
     * id :
     * groupId : 1
     * customerId : 1
     * isDelete :
     * createTime :
     * updateTime :
     * openBanned : 1
     * openForbidden : 1
     * nick : 丁
     * headPortrait : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/6497DE17-9B15-4B14-B829-7066A96FFC63.jpg
     */
    private String id;
    private String groupId;
    private String customerId;
    private String isDelete;
    private String createTime;
    private String updateTime;
    private String openBanned = "0";
    private String nick;
    private String headPortrait;
    private String openAudio = "0";
    private String openVideo = "0";
    private String forceRecall = "0";
    private boolean checked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOpenBanned() {
        return openBanned;
    }

    public void setOpenBanned(String openBanned) {
        this.openBanned = openBanned;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getOpenAudio() {
        return openAudio;
    }

    public void setOpenAudio(String openAudio) {
        this.openAudio = openAudio;
    }

    public String getOpenVideo() {
        return openVideo;
    }

    public void setOpenVideo(String openVideo) {
        this.openVideo = openVideo;
    }

    public String getForceRecall() {
        return forceRecall;
    }

    public void setForceRecall(String forceRecall) {
        this.forceRecall = forceRecall;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.groupId);
        dest.writeString(this.customerId);
        dest.writeString(this.isDelete);
        dest.writeString(this.createTime);
        dest.writeString(this.updateTime);
        dest.writeString(this.openBanned);
        dest.writeString(this.nick);
        dest.writeString(this.headPortrait);
        dest.writeString(this.openAudio);
        dest.writeString(this.openVideo);
        dest.writeString(this.forceRecall);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
    }

    public PermissionInfoBean() {
    }

    protected PermissionInfoBean(Parcel in) {
        this.id = in.readString();
        this.groupId = in.readString();
        this.customerId = in.readString();
        this.isDelete = in.readString();
        this.createTime = in.readString();
        this.updateTime = in.readString();
        this.openBanned = in.readString();
        this.nick = in.readString();
        this.headPortrait = in.readString();
        this.openAudio = in.readString();
        this.openVideo = in.readString();
        this.forceRecall = in.readString();
        this.checked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PermissionInfoBean> CREATOR = new Parcelable.Creator<PermissionInfoBean>() {
        @Override
        public PermissionInfoBean createFromParcel(Parcel source) {
            return new PermissionInfoBean(source);
        }

        @Override
        public PermissionInfoBean[] newArray(int size) {
            return new PermissionInfoBean[size];
        }
    };
}
