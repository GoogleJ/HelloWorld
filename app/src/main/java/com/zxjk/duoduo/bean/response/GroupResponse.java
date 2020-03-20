package com.zxjk.duoduo.bean.response;

import java.io.Serializable;

public class GroupResponse implements Serializable {


    /**
     * groupInfo : {"id":"71","groupType":"0","groupNikeName":"Zhaochen_4的讨论组","groupHeadPortrait":"","groupSign":"","groupNotice":"","groupOwnerId":"17","updateTime":"","createTime":"1553581323053","isDelete":"0","isInviteConfirm":"0","headPortrait":""}
     * customers : [{"id":"17","duoduoId":"","nick":"Zhaochen_4","realname":"","mobile":"","password":"","address":"","email":"","headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/A324124A-A577-4980-AF66-684D1CE44376.jpg","sex":"","signature":"","walletAddress":"","idCard":"","isShowRealname":"","updateTime":"","createTime":"","isDelete":"","token":"","remark":"","rongToken":"","payPwd":"","isFirstLogin":"","renegeNumber":"","isConfine":"","status":"","isAuthentication":"","onlineService":""},{"id":"14","duoduoId":"","nick":"14号用户","realname":"","mobile":"","password":"","address":"","email":"","headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg","sex":"","signature":"","walletAddress":"","idCard":"","isShowRealname":"","updateTime":"","createTime":"","isDelete":"","token":"","remark":"","rongToken":"","payPwd":"","isFirstLogin":"","renegeNumber":"","isConfine":"","status":"","isAuthentication":"","onlineService":""},{"id":"4","duoduoId":"","nick":"丁浩","realname":"","mobile":"","password":"","address":"","email":"","headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/E1795CE5-2A56-4C53-A9EE-E916FBBFC383.jpg","sex":"","signature":"","walletAddress":"","idCard":"","isShowRealname":"","updateTime":"","createTime":"","isDelete":"","token":"","remark":"","rongToken":"","payPwd":"","isFirstLogin":"","renegeNumber":"","isConfine":"","status":"","isAuthentication":"","onlineService":""},{"id":"26","duoduoId":"","nick":"王倩","realname":"","mobile":"","password":"","address":"","email":"","headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/C5B24F00-AD58-4DF4-83FD-DC6217CC24BA.jpg","sex":"","signature":"","walletAddress":"","idCard":"","isShowRealname":"","updateTime":"","createTime":"","isDelete":"","token":"","remark":"","rongToken":"","payPwd":"","isFirstLogin":"","renegeNumber":"","isConfine":"","status":"","isAuthentication":"","onlineService":""}]
     */

    private GroupInfoBean groupInfo;
    private String maxNumber;
    private String isAdmin;
    private PermissionBean groupPermission;
    private RedPacketInfoBean redPacketInfo;
    private String communityUpdateTime;

    public String getCommunityUpdateTime() {
        return communityUpdateTime;
    }

    public void setCommunityUpdateTime(String communityUpdateTime) {
        this.communityUpdateTime = communityUpdateTime;
    }

    public RedPacketInfoBean getRedPacketInfo() {
        return redPacketInfo;
    }

    public void setRedPacketInfo(RedPacketInfoBean redPacketInfo) {
        this.redPacketInfo = redPacketInfo;
    }

    public static class RedPacketInfoBean implements Serializable {

        /**
         * redNewPersonStatus : 1
         * isGetNewPersonRed : 2
         */

        private String redNewPersonStatus;
        private String isGetNewPersonRed;

        public String getRedNewPersonStatus() {
            return redNewPersonStatus;
        }

        public void setRedNewPersonStatus(String redNewPersonStatus) {
            this.redNewPersonStatus = redNewPersonStatus;
        }

        public String getIsGetNewPersonRed() {
            return isGetNewPersonRed;
        }

        public void setIsGetNewPersonRed(String isGetNewPersonRed) {
            this.isGetNewPersonRed = isGetNewPersonRed;
        }
    }

    public PermissionBean getGroupPermission() {
        return groupPermission;
    }

    public void setGroupPermission(PermissionBean groupPermission) {
        this.groupPermission = groupPermission;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(String maxNumber) {
        this.maxNumber = maxNumber;
    }

    public GroupInfoBean getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfoBean groupInfo) {
        this.groupInfo = groupInfo;
    }

    public static class PermissionBean implements Serializable {

        /**
         * id : 38
         * groupId : 278
         * customerId :
         * isDelete :
         * createTime :
         * updateTime :
         * openBanned : 0
         * openForbidden : 0
         * nick :
         * headPortrait :
         * openAudio : 0
         * openVideo : 0
         */

        private String id;
        private String groupId;
        private String customerId;
        private String isDelete;
        private String createTime;
        private String updateTime;
        private String openBanned;
        private String nick;
        private String headPortrait;
        private String openAudio;
        private String openVideo;
        private String forceRecall;

        public String getForceRecall() {
            return forceRecall;
        }

        public void setForceRecall(String forceRecall) {
            this.forceRecall = forceRecall;
        }

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
    }

    public static class GroupInfoBean implements Serializable {

        private String id;
        private String groupType;
        private String groupNikeName;
        private String groupNotice;
        private String groupOwnerId;
        private String isDelete;
        private String headPortrait;
        private String isBanned;
        private String banFriend;
        private String banSendPicture;
        private String banSendLink;
        private String isPublic;
        private String slowMode;
        private String limitNumber;
        private String customerNumber;
        private String groupOwnerNick;
        private String ownerHeadPortrait;
        private String banSendVoice;

        public String getBanSendVoice() {
            return banSendVoice;
        }

        public void setBanSendVoice(String banSendVoice) {
            this.banSendVoice = banSendVoice;
        }

        public String getCustomerNumber() {
            return customerNumber;
        }

        public void setCustomerNumber(String customerNumber) {
            this.customerNumber = customerNumber;
        }

        public String getGroupOwnerNick() {
            return groupOwnerNick;
        }

        public void setGroupOwnerNick(String groupOwnerNick) {
            this.groupOwnerNick = groupOwnerNick;
        }

        public String getOwnerHeadPortrait() {
            return ownerHeadPortrait;
        }

        public void setOwnerHeadPortrait(String ownerHeadPortrait) {
            this.ownerHeadPortrait = ownerHeadPortrait;
        }

        public String getLimitNumber() {
            return limitNumber;
        }

        public void setLimitNumber(String limitNumber) {
            this.limitNumber = limitNumber;
        }

        public String getSlowMode() {
            return slowMode;
        }

        public void setSlowMode(String slowMode) {
            this.slowMode = slowMode;
        }

        public String getIsPublic() {
            return isPublic;
        }

        public void setIsPublic(String isPublic) {
            this.isPublic = isPublic;
        }

        public String getBanSendPicture() {
            return banSendPicture;
        }

        public void setBanSendPicture(String banSendPicture) {
            this.banSendPicture = banSendPicture;
        }

        public String getBanSendLink() {
            return banSendLink;
        }

        public void setBanSendLink(String banSendLink) {
            this.banSendLink = banSendLink;
        }

        public String getIsBanned() {
            return isBanned;
        }

        public void setIsBanned(String isBanned) {
            this.isBanned = isBanned;
        }

        public String getBanFriend() {
            return banFriend;
        }

        public void setBanFriend(String banFriend) {
            this.banFriend = banFriend;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGroupType() {
            return groupType;
        }

        public void setGroupType(String groupType) {
            this.groupType = groupType;
        }

        public String getGroupNikeName() {
            return groupNikeName;
        }

        public void setGroupNikeName(String groupNikeName) {
            this.groupNikeName = groupNikeName;
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

        public String getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }
    }
}
