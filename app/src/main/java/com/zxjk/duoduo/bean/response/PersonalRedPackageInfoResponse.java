package com.zxjk.duoduo.bean.response;

public class PersonalRedPackageInfoResponse {

    /**
     * receiveInfo : {"time":"1554677443991","headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/521554538870512","usernick":"哈哈"}
     * sendUserInfo : {"headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/541554046323712","usernick":"阳"}
     * redPachageInfo : {"isOverdue":"0","money":"55.00","message":"55","status":"0"}
     */

    private ReceiveInfoBean receiveInfo;
    private SendUserInfoBean sendUserInfo;
    private RedPachageInfoBean redPachageInfo;

    public ReceiveInfoBean getReceiveInfo() {
        return receiveInfo;
    }

    public void setReceiveInfo(ReceiveInfoBean receiveInfo) {
        this.receiveInfo = receiveInfo;
    }

    public SendUserInfoBean getSendUserInfo() {
        return sendUserInfo;
    }

    public void setSendUserInfo(SendUserInfoBean sendUserInfo) {
        this.sendUserInfo = sendUserInfo;
    }

    public RedPachageInfoBean getRedPachageInfo() {
        return redPachageInfo;
    }

    public void setRedPachageInfo(RedPachageInfoBean redPachageInfo) {
        this.redPachageInfo = redPachageInfo;
    }

    public static class ReceiveInfoBean {
        /**
         * time : 1554677443991
         * headPortrait : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/521554538870512
         * usernick : 哈哈
         */

        private String time;
        private String headPortrait;
        private String usernick;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

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

    public static class SendUserInfoBean {
        /**
         * headPortrait : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/541554046323712
         * usernick : 阳
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

    public static class RedPachageInfoBean {
        /**
         * isOverdue : 0
         * money : 55.00
         * message : 55
         * status : 0
         */

        private String isOverdue;
        private String money;
        private String message;
        private String status;
        private String symbol;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getIsOverdue() {
            return isOverdue;
        }

        public void setIsOverdue(String isOverdue) {
            this.isOverdue = isOverdue;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
