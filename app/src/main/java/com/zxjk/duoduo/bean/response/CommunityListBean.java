package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class CommunityListBean implements Parcelable {

    /**
     * ownerNick : 海浪管理员
     * groupId : 14
     * communityLogo : hilamg.logo
     * communityName : 海浪官方社群
     * isPay : 1
     * members : 999
     */

    private String ownerNick;
    private String groupId;
    private String communityLogo;
    private String communityName;
    private String isPay;
    private String members;
    private String isInGroup;
    private String introduction;

    protected CommunityListBean(Parcel in) {
        ownerNick = in.readString();
        groupId = in.readString();
        communityLogo = in.readString();
        communityName = in.readString();
        isPay = in.readString();
        members = in.readString();
        isInGroup = in.readString();
        introduction = in.readString();
    }

    public static final Creator<CommunityListBean> CREATOR = new Creator<CommunityListBean>() {
        @Override
        public CommunityListBean createFromParcel(Parcel in) {
            return new CommunityListBean(in);
        }

        @Override
        public CommunityListBean[] newArray(int size) {
            return new CommunityListBean[size];
        }
    };

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIsInGroup() {
        return isInGroup;
    }

    public void setIsInGroup(String isInGroup) {
        this.isInGroup = isInGroup;
    }

    public String getOwnerNick() {
        return ownerNick;
    }

    public void setOwnerNick(String ownerNick) {
        this.ownerNick = ownerNick;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCommunityLogo() {
        return communityLogo;
    }

    public void setCommunityLogo(String communityLogo) {
        this.communityLogo = communityLogo;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getIsPay() {
        return isPay;
    }

    public void setIsPay(String isPay) {
        this.isPay = isPay;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ownerNick);
        dest.writeString(groupId);
        dest.writeString(communityLogo);
        dest.writeString(communityName);
        dest.writeString(isPay);
        dest.writeString(members);
        dest.writeString(isInGroup);
        dest.writeString(introduction);
    }
}
