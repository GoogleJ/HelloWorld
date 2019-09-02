package com.zxjk.duoduo.bean.response;

import java.util.List;

public class GetSignListResponse {

    private List<PointsListBean> pointsList;
    private List<CustomerSignBean> customerSign;

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
         * customerId : 5
         * createTime : 1567353600000
         * updateTime : 1560249868733
         * points : 100
         * receiveStatus : 0
         * pointType : 0
         * isDelete : 0
         * counts : 0
         */

        private String id;
        private String customerId;
        private long createTime;
        private String updateTime;
        private int points;
        private String receiveStatus;
        private String pointType;
        private String isDelete;
        private int counts;

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

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
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

        public String getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }

        public int getCounts() {
            return counts;
        }

        public void setCounts(int counts) {
            this.counts = counts;
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
        private long createTime;
        private String lastModifyTime;
        private int signCount;
        private int totalCount;
        private int repay;
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

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

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

        public int getRepay() {
            return repay;
        }

        public void setRepay(int repay) {
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
