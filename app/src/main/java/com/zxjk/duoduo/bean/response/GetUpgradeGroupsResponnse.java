package com.zxjk.duoduo.bean.response;

import java.util.List;

public class GetUpgradeGroupsResponnse {

    /**
     * groupLevelInfo : {"groupTag":"3","limitNumber":"1000"}
     * groupLevelsInfoList : [{"groupTag":"5","limitNumber":"10000","fee":"2000","isHot":"0","isUpgrade":"0"},{"groupTag":"4","limitNumber":"5000","fee":"1000","isHot":"1","isUpgrade":"0"},{"groupTag":"3","limitNumber":"1000","fee":"500","isHot":"0","isUpgrade":"1"},{"groupTag":"2","limitNumber":"500","fee":"100","isHot":"0","isUpgrade":"1"}]
     */

    private GroupLevelInfoBean groupLevelInfo;
    private List<GroupLevelsInfoListBean> groupLevelsInfoList;

    public GroupLevelInfoBean getGroupLevelInfo() {
        return groupLevelInfo;
    }

    public void setGroupLevelInfo(GroupLevelInfoBean groupLevelInfo) {
        this.groupLevelInfo = groupLevelInfo;
    }

    public List<GroupLevelsInfoListBean> getGroupLevelsInfoList() {
        return groupLevelsInfoList;
    }

    public void setGroupLevelsInfoList(List<GroupLevelsInfoListBean> groupLevelsInfoList) {
        this.groupLevelsInfoList = groupLevelsInfoList;
    }

    public static class GroupLevelInfoBean {
        /**
         * groupTag : 3
         * limitNumber : 1000
         */

        private String groupTag;
        private String limitNumber;

        public String getGroupTag() {
            return groupTag;
        }

        public void setGroupTag(String groupTag) {
            this.groupTag = groupTag;
        }

        public String getLimitNumber() {
            return limitNumber;
        }

        public void setLimitNumber(String limitNumber) {
            this.limitNumber = limitNumber;
        }
    }

    public static class GroupLevelsInfoListBean {
        /**
         * groupTag : 5
         * limitNumber : 10000
         * fee : 2000
         * isHot : 0
         * isUpgrade : 0
         */

        private String groupTag;
        private String limitNumber;
        private String fee;
        private String isHot;
        private String isUpgrade;

        public String getGroupTag() {
            return groupTag;
        }

        public void setGroupTag(String groupTag) {
            this.groupTag = groupTag;
        }

        public String getLimitNumber() {
            return limitNumber;
        }

        public void setLimitNumber(String limitNumber) {
            this.limitNumber = limitNumber;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getIsHot() {
            return isHot;
        }

        public void setIsHot(String isHot) {
            this.isHot = isHot;
        }

        public String getIsUpgrade() {
            return isUpgrade;
        }

        public void setIsUpgrade(String isUpgrade) {
            this.isUpgrade = isUpgrade;
        }
    }
}
