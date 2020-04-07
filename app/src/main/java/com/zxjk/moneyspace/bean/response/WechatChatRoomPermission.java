package com.zxjk.moneyspace.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class WechatChatRoomPermission implements Parcelable {

    /**
     * isBanned : 0
     * banSendVoice : 0
     * banSendLink : 0
     */

    private String isBanned;
    private String banSendVoice;
    private String banSendLink;

    public String getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(String isBanned) {
        this.isBanned = isBanned;
    }

    public String getBanSendVoice() {
        return banSendVoice;
    }

    public void setBanSendVoice(String banSendVoice) {
        this.banSendVoice = banSendVoice;
    }

    public String getBanSendLink() {
        return banSendLink;
    }

    public void setBanSendLink(String banSendLink) {
        this.banSendLink = banSendLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.isBanned);
        dest.writeString(this.banSendVoice);
        dest.writeString(this.banSendLink);
    }

    public WechatChatRoomPermission() {
    }

    protected WechatChatRoomPermission(Parcel in) {
        this.isBanned = in.readString();
        this.banSendVoice = in.readString();
        this.banSendLink = in.readString();
    }

    public static final Parcelable.Creator<WechatChatRoomPermission> CREATOR = new Parcelable.Creator<WechatChatRoomPermission>() {
        @Override
        public WechatChatRoomPermission createFromParcel(Parcel source) {
            return new WechatChatRoomPermission(source);
        }

        @Override
        public WechatChatRoomPermission[] newArray(int size) {
            return new WechatChatRoomPermission[size];
        }
    };
}
