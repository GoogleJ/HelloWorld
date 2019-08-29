package com.zxjk.duoduo.bean.response;

public class ReceivePersonalRedPackageResponse {

    /**
     * receiveInfo : {}
     * sendUserInfo : {"money":"8.00","headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/541554046323712","message":"8","usernick":"阳"}
     */

    private ReceiveInfoBean receiveInfo;
    private SendUserInfoBean sendUserInfo;

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

    public static class ReceiveInfoBean {
    }

    public static class SendUserInfoBean {
        /**
         * money : 8.00
         * headPortrait : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/541554046323712
         * message : 8
         * usernick : 阳
         */

        private String money;
        private String headPortrait;
        private String message;
        private String usernick;

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUsernick() {
            return usernick;
        }

        public void setUsernick(String usernick) {
            this.usernick = usernick;
        }
    }
}
