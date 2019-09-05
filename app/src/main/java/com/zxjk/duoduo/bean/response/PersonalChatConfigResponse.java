package com.zxjk.duoduo.bean.response;

public class PersonalChatConfigResponse {


    /**
     * customerForChat : {"nick":"丁","headPortrait":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/6497DE17-9B15-4B14-B829-7066A96FFC63.jpg","friendNick":""}
     * chatInfo : {"screenCapture":0,"incinerationTime":30}
     */

    private CustomerForChatBean customerForChat;
    private ChatInfoBean chatInfo;

    public CustomerForChatBean getCustomerForChat() {
        return customerForChat;
    }

    public void setCustomerForChat(CustomerForChatBean customerForChat) {
        this.customerForChat = customerForChat;
    }

    public ChatInfoBean getChatInfo() {
        return chatInfo;
    }

    public void setChatInfo(ChatInfoBean chatInfo) {
        this.chatInfo = chatInfo;
    }

    public static class CustomerForChatBean {
        /**
         * nick : 丁
         * headPortrait : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/6497DE17-9B15-4B14-B829-7066A96FFC63.jpg
         * friendNick :
         */

        private String nick;
        private String headPortrait;
        private String friendNick;

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

        public String getFriendNick() {
            return friendNick;
        }

        public void setFriendNick(String friendNick) {
            this.friendNick = friendNick;
        }
    }

    public static class ChatInfoBean {
        /**
         * screenCapture : 0
         * incinerationTime : 30
         */

        private int screenCapture;
        private int incinerationTime;
        private int screenCaptureHide;

        public int getScreenCaptureHide() {
            return screenCaptureHide;
        }

        public void setScreenCaptureHide(int screenCaptureHide) {
            this.screenCaptureHide = screenCaptureHide;
        }

        public int getScreenCapture() {
            return screenCapture;
        }

        public void setScreenCapture(int screenCapture) {
            this.screenCapture = screenCapture;
        }

        public int getIncinerationTime() {
            return incinerationTime;
        }

        public void setIncinerationTime(int incinerationTime) {
            this.incinerationTime = incinerationTime;
        }
    }
}
