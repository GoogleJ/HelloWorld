package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class ReceiveAirdropResponse implements Parcelable {

    /**
     * id : 1
     * symbol : USDT
     * communityLogo : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg
     * communityName : 测试红包雨群
     * groupId : 111
     * symbolIntroduction : USDT是/.......
     * symbolLogo : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg
     * reward : 0.04442
     * lastTime : 1578463189000
     * nextReceive : 1
     */

    private String id;
    private String symbol;
    private String communityLogo;
    private String communityName;
    private String groupId;
    private String symbolIntroduction;
    private String symbolLogo;
    private String reward;
    private String lastTime;
    private String nextReceive;
    private String receive;
    private String lastReceive;
    private int shareCount;

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public String getLastReceive() {
        return lastReceive;
    }

    public void setLastReceive(String lastReceive) {
        this.lastReceive = lastReceive;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSymbolIntroduction() {
        return symbolIntroduction;
    }

    public void setSymbolIntroduction(String symbolIntroduction) {
        this.symbolIntroduction = symbolIntroduction;
    }

    public String getSymbolLogo() {
        return symbolLogo;
    }

    public void setSymbolLogo(String symbolLogo) {
        this.symbolLogo = symbolLogo;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getNextReceive() {
        return nextReceive;
    }

    public void setNextReceive(String nextReceive) {
        this.nextReceive = nextReceive;
    }

    public ReceiveAirdropResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.symbol);
        dest.writeString(this.communityLogo);
        dest.writeString(this.communityName);
        dest.writeString(this.groupId);
        dest.writeString(this.symbolIntroduction);
        dest.writeString(this.symbolLogo);
        dest.writeString(this.reward);
        dest.writeString(this.lastTime);
        dest.writeString(this.nextReceive);
        dest.writeString(this.receive);
        dest.writeString(this.lastReceive);
        dest.writeInt(this.shareCount);
    }

    protected ReceiveAirdropResponse(Parcel in) {
        this.id = in.readString();
        this.symbol = in.readString();
        this.communityLogo = in.readString();
        this.communityName = in.readString();
        this.groupId = in.readString();
        this.symbolIntroduction = in.readString();
        this.symbolLogo = in.readString();
        this.reward = in.readString();
        this.lastTime = in.readString();
        this.nextReceive = in.readString();
        this.receive = in.readString();
        this.lastReceive = in.readString();
        this.shareCount = in.readInt();
    }

    public static final Creator<ReceiveAirdropResponse> CREATOR = new Creator<ReceiveAirdropResponse>() {
        @Override
        public ReceiveAirdropResponse createFromParcel(Parcel source) {
            return new ReceiveAirdropResponse(source);
        }

        @Override
        public ReceiveAirdropResponse[] newArray(int size) {
            return new ReceiveAirdropResponse[size];
        }
    };
}
