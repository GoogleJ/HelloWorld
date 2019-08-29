package com.zxjk.duoduo.bean.response;

import java.util.List;

public class ReceiveGroupRedPackageResponse {
    /**
     * customerInfo : [{"nick":"中兴极客14","money":0.1037,"createTime":"1551326357494","customerId":14,"headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg"},{"money":14.0083,"createTime":"1551326377969","customerId":1}]
     * redPackageInfo : {"number":2,"receiveCount":"2","money":"15.0000"}
     * sendCustomerInfo : {"headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg","usernick":"中兴极客14"}
     */

    private RedPackageInfoBean redPackageInfo;
    private SendCustomerInfoBean sendCustomerInfo;
    private List<CustomerInfoBean> customerInfo;
    private String finish;

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public RedPackageInfoBean getRedPackageInfo() {
        return redPackageInfo;
    }

    public void setRedPackageInfo(RedPackageInfoBean redPackageInfo) {
        this.redPackageInfo = redPackageInfo;
    }

    public SendCustomerInfoBean getSendCustomerInfo() {
        return sendCustomerInfo;
    }

    public void setSendCustomerInfo(SendCustomerInfoBean sendCustomerInfo) {
        this.sendCustomerInfo = sendCustomerInfo;
    }

    public List<CustomerInfoBean> getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(List<CustomerInfoBean> customerInfo) {
        this.customerInfo = customerInfo;
    }

    public static class RedPackageInfoBean {
        /**
         * number : 2
         * receiveCount : 2
         * money : 15.0000
         */

        private int number;
        private String receiveCount;
        private String money;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getReceiveCount() {
            return receiveCount;
        }

        public void setReceiveCount(String receiveCount) {
            this.receiveCount = receiveCount;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }

    public static class SendCustomerInfoBean {
        /**
         * headPortrait : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg
         * usernick : 中兴极客14
         */

        private String headPortrait;
        private String usernick;

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }

        public String getUsernick() {
            return usernick;
        }

        public void setUsernick(String usernick) {
            this.usernick = usernick;
        }
    }

    public static class CustomerInfoBean {
        /**
         * nick : 中兴极客14
         * money : 0.1037
         * createTime : 1551326357494
         * customerId : 14
         * headPortrait : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg
         */

        private String nick;
        private double money;
        private String createTime;
        private int customerId;
        private String headPortrait;

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getCustomerId() {
            return customerId;
        }

        public void setCustomerId(int customerId) {
            this.customerId = customerId;
        }

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }
    }
}
