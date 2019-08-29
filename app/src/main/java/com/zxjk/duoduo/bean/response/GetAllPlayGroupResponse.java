package com.zxjk.duoduo.bean.response;

import java.util.List;

public class GetAllPlayGroupResponse {

    private List<GroupListBean> groupList;
    private List<String> join;

    public List<GroupListBean> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupListBean> groupList) {
        this.groupList = groupList;
    }

    public List<String> getJoin() {
        return join;
    }

    public void setJoin(List<String> join) {
        this.join = join;
    }

    public static class GroupListBean {
        /**
         * id : 50
         * groupType : 1
         * groupNikeName : test1
         * groupHeadPortrait :
         * groupSign : 牛牛，带你买房买车
         * groupNotice : 游戏群牛牛，百家乐各种玩法这里应
         * groupOwnerId : 7
         * updateTime :
         * createTime : 1551863007000
         * isDelete : 0
         * isInviteConfirm : 0
         * headPortrait : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg
         * duoduoId : 10000007
         */

        private String id;
        private String groupType;
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
        private String duoduoId;
        private boolean hasJoined;

        public boolean isHasJoined() {
            return hasJoined;
        }

        public void setHasJoined(boolean hasJoined) {
            this.hasJoined = hasJoined;
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

        public String getDuoduoId() {
            return duoduoId;
        }

        public void setDuoduoId(String duoduoId) {
            this.duoduoId = duoduoId;
        }
    }
}
