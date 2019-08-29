package com.zxjk.duoduo.bean.response;

import java.io.Serializable;

public class GroupChatResponse implements Serializable {

    private String id;
    private String goupType;
    private String groupNikeName;
    private String groupHeadPortrait;
    private String groupSign;
    private String groupNotice;
    private String groupOwnerId;
    private String updateTime;
    private String createTime;
    private String isDelete;
    private String isInviteConfirm;
    private String headPortrait;

    public GroupChatResponse(GroupChatResponse data ) {
        this.id =data. id;
        this.goupType =data. goupType;
        this.groupNikeName =data. groupNikeName;
        this.groupHeadPortrait =data. groupHeadPortrait;
        this.groupSign =data. groupSign;
        this.groupNotice =data. groupNotice;
        this.groupOwnerId =data. groupOwnerId;
        this.updateTime =data. updateTime;
        this.createTime =data. createTime;
        this.isDelete =data. isDelete;
        this.isInviteConfirm =data. isInviteConfirm;
        this.headPortrait =data. headPortrait;
    }

    public GroupChatResponse(String id) {
        this.id = id;
    }


    public GroupChatResponse() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoupType() {
        return goupType;
    }

    public void setGoupType(String goupType) {
        this.goupType = goupType;
    }

    public String getGroupNikeName() {
        return groupNikeName;
    }

    public void setGroupNikeName(String groupNikeName) {
        this.groupNikeName = groupNikeName;
    }

    public String getGroupHeadPortrait() {
        return groupHeadPortrait;
    }

    public void setGroupHeadPortrait(String groupHeadPortrait) {
        this.groupHeadPortrait = groupHeadPortrait;
    }

    public String getGroupSign() {
        return groupSign;
    }

    public void setGroupSign(String groupSign) {
        this.groupSign = groupSign;
    }

    public String getGroupNotice() {
        return groupNotice;
    }

    public void setGroupNotice(String groupNotice) {
        this.groupNotice = groupNotice;
    }

    public String getGroupOwnerId() {
        return groupOwnerId;
    }

    public void setGroupOwnerId(String groupOwnerId) {
        this.groupOwnerId = groupOwnerId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getIsInviteConfirm() {
        return isInviteConfirm;
    }

    public void setIsInviteConfirm(String isInviteConfirm) {
        this.isInviteConfirm = isInviteConfirm;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String GroupString() {

        return "GroupChatResponse{" +
                "id='" + id + '\'' +
                ", goupType='" + goupType + '\'' +
                ", groupNikeName='" + groupNikeName + '\'' +
                ", groupHeadPortrait='" + groupHeadPortrait + '\'' +
                ", groupSign='" + groupSign + '\'' +
                ", groupNotice='" + groupNotice + '\'' +
                ", groupOwnerId='" + groupOwnerId + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", isDelete='" + isDelete + '\'' +
                ", isInviteConfirm='" + isInviteConfirm + '\'' +
                ", headPortrait='" + headPortrait + '\'' +
                '}';
    }

}
