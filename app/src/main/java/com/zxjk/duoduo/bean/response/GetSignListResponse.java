package com.zxjk.duoduo.bean.response;

import java.util.List;

public class GetSignListResponse {


    /**
     * pointsList : [{"activity":"微信分享","activityDesc":"微信分享","icon":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1578038430800.png","isComplete":"1"},{"activity":"添加一位好友","activityDesc":"添加一位好友","icon":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1578038430800.png","isComplete":"1"},{"activity":"在社群里发一次红包","activityDesc":"在社群里发一次红包","icon":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1578038430800.png","isComplete":"1"},{"activity":"往钱包地址充值一笔USDT","activityDesc":"往钱包地址充值一笔USDT","icon":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1578038430800.png","isComplete":"1"},{"activity":"新加入一个社群","activityDesc":"新加入一个社群","icon":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1578038430800.png","isComplete":"0"},{"activity":"扫码支付一次","activityDesc":"扫码支付一次","icon":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1578038430800.png","isComplete":"0"}]
     * customerSign : [{"lastModifyTime":"今日","signCount":0,"totalCount":0,"repay":"0.01","signStatus":"0"},{"lastModifyTime":"06.13","signCount":0,"totalCount":0,"repay":"0.02","signStatus":"0"},{"lastModifyTime":"06.14","signCount":0,"totalCount":0,"repay":"0.03","signStatus":"0"},{"lastModifyTime":"06.15","signCount":0,"totalCount":0,"repay":"0.04","signStatus":"0"},{"lastModifyTime":"06.16","signCount":0,"totalCount":0,"repay":"0.05","signStatus":"0"},{"lastModifyTime":"06.17","signCount":0,"totalCount":0,"repay":"0.06","signStatus":"0"},{"lastModifyTime":"06.18","signCount":0,"totalCount":0,"repay":"0.07","signStatus":"0"}]
     * activity : {"rewardAmount":"20","symbol":"USDT","isReceiveReward":"0","activityType":"1","hasNext":"1","nextRewardAmount":"30","nextSymbol":"USDT"}
     * count : 65
     */

    private ActivityBean activity;
    private int count;
    private List<PointsListBean> pointsList;
    private List<CustomerSignBean> customerSign;

    public ActivityBean getActivity() {
        return activity;
    }

    public void setActivity(ActivityBean activity) {
        this.activity = activity;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PointsListBean> getPointsList() {
        return pointsList;
    }

    public void setPointsList(List<PointsListBean> pointsList) {
        this.pointsList = pointsList;
    }

    public List<CustomerSignBean> getCustomerSign() {
        return customerSign;
    }

    public void setCustomerSign(List<CustomerSignBean> customerSign) {
        this.customerSign = customerSign;
    }

    public static class ActivityBean {
        /**
         * rewardAmount : 20
         * symbol : USDT
         * isReceiveReward : 0
         * activityType : 1
         * hasNext : 1
         * nextRewardAmount : 30
         * nextSymbol : USDT
         */

        private String rewardAmount;
        private String symbol;
        private String isReceiveReward;
        private String activityType;
        private String hasNext;
        private String nextRewardAmount;
        private String nextSymbol;

        public String getRewardAmount() {
            return rewardAmount;
        }

        public void setRewardAmount(String rewardAmount) {
            this.rewardAmount = rewardAmount;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getIsReceiveReward() {
            return isReceiveReward;
        }

        public void setIsReceiveReward(String isReceiveReward) {
            this.isReceiveReward = isReceiveReward;
        }

        public String getActivityType() {
            return activityType;
        }

        public void setActivityType(String activityType) {
            this.activityType = activityType;
        }

        public String getHasNext() {
            return hasNext;
        }

        public void setHasNext(String hasNext) {
            this.hasNext = hasNext;
        }

        public String getNextRewardAmount() {
            return nextRewardAmount;
        }

        public void setNextRewardAmount(String nextRewardAmount) {
            this.nextRewardAmount = nextRewardAmount;
        }

        public String getNextSymbol() {
            return nextSymbol;
        }

        public void setNextSymbol(String nextSymbol) {
            this.nextSymbol = nextSymbol;
        }
    }

    public static class PointsListBean {
        /**
         * activity : 微信分享
         * activityDesc : 微信分享
         * icon : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1578038430800.png
         * isComplete : 1
         */

        private String activity;
        private String activityDesc;
        private String icon;
        private String isComplete;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getActivity() {
            return activity;
        }

        public void setActivity(String activity) {
            this.activity = activity;
        }

        public String getActivityDesc() {
            return activityDesc;
        }

        public void setActivityDesc(String activityDesc) {
            this.activityDesc = activityDesc;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getIsComplete() {
            return isComplete;
        }

        public void setIsComplete(String isComplete) {
            this.isComplete = isComplete;
        }
    }

    public static class CustomerSignBean {
        /**
         * lastModifyTime : 今日
         * signCount : 0
         * totalCount : 0
         * repay : 0.01
         * signStatus : 0
         */

        private String lastModifyTime;
        private int signCount;
        private int totalCount;
        private String repay;
        private String signStatus;

        public String getLastModifyTime() {
            return lastModifyTime;
        }

        public void setLastModifyTime(String lastModifyTime) {
            this.lastModifyTime = lastModifyTime;
        }

        public int getSignCount() {
            return signCount;
        }

        public void setSignCount(int signCount) {
            this.signCount = signCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public String getRepay() {
            return repay;
        }

        public void setRepay(String repay) {
            this.repay = repay;
        }

        public String getSignStatus() {
            return signStatus;
        }

        public void setSignStatus(String signStatus) {
            this.signStatus = signStatus;
        }
    }
}
