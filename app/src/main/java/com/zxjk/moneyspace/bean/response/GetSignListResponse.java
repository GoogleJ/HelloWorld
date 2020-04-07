package com.zxjk.moneyspace.bean.response;

import java.util.List;

public class GetSignListResponse {

    private List<PointsListBean> pointsList;
    private List<CustomerSignBean> customerSign;
    private String count;
    private String sumPay;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getSumPay() {
        return sumPay;
    }

    public void setSumPay(String sumPay) {
        this.sumPay = sumPay;
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

    public static class PointsListBean {
        /**
         * id : 1
         * customerId :
         * createTime :
         * updateTime :
         * points : 0.0
         * receiveStatus :
         * pointType :
         * isDelete :
         * counts : 0
         * receivePoints : 0.0
         * activity : 实名验证
         * activityDesc : 用户实名验证审核通过能获得100MoT
         * rewardAmount : 100.0
         * number : 0
         * icon : ""
         */

        private String receiveStatus;
        private String pointType;
        private int counts;
        private String activity;
        private String activityDesc;
        private int number;
        private String icon;
        private String points;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getReceiveStatus() {
            return receiveStatus;
        }

        public void setReceiveStatus(String receiveStatus) {
            this.receiveStatus = receiveStatus;
        }

        public String getPointType() {
            return pointType;
        }

        public void setPointType(String pointType) {
            this.pointType = pointType;
        }

        public int getCounts() {
            return counts;
        }

        public void setCounts(int counts) {
            this.counts = counts;
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

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

    public static class CustomerSignBean {
        /**
         * id : 10
         * customerId : 5
         * createTime : 1566921600000
         * lastModifyTime : 08.28
         * signCount : 2
         * totalCount : 2
         * repay : 4
         * isDelete : 0
         * signStatus : 1
         */

        private String id;
        private String customerId;
        private String createTime;
        private String lastModifyTime;
        private String signCount;
        private String totalCount;
        private String repay;
        private String isDelete;
        private String signStatus;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getLastModifyTime() {
            return lastModifyTime;
        }

        public void setLastModifyTime(String lastModifyTime) {
            this.lastModifyTime = lastModifyTime;
        }

        public String getSignCount() {
            return signCount;
        }

        public void setSignCount(String signCount) {
            this.signCount = signCount;
        }

        public String getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(String totalCount) {
            this.totalCount = totalCount;
        }

        public String getRepay() {
            return repay;
        }

        public void setRepay(String repay) {
            this.repay = repay;
        }

        public String getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }

        public String getSignStatus() {
            return signStatus;
        }

        public void setSignStatus(String signStatus) {
            this.signStatus = signStatus;
        }
    }
}
