package com.zxjk.duoduo.bean.response;

import java.util.ArrayList;

public class SearchCommunityBean {

    /**
     * ownerNick : 134****9876
     * groupId : 26
     * communityLogo : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/491574349585689
     * communityName : 社群空间哎
     * isPay : 0
     * members : 1
     * inGroup : 0
     */

    private String ownerNick;
    private String groupId;
    private String communityLogo;
    private String communityName;
    private String isPay;
    private String members;
    private String inGroup;
    private String code;
    private ArrayList<Integer> hilightIndex;

    public ArrayList<Integer> getHilightIndex() {
        return hilightIndex;
    }

    public void setHilightIndex(ArrayList<Integer> hilightIndex) {
        this.hilightIndex = hilightIndex;
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

    public String getInGroup() {
        return inGroup;
    }

    public void setInGroup(String inGroup) {
        this.inGroup = inGroup;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
