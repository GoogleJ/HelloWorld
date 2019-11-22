package com.zxjk.duoduo.bean.response;

public class CommunityListBean {

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
}
