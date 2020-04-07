package com.zxjk.moneyspace.bean.response;

public class GroupManagementInfoBean {


    /**
     * customerId : 5
     * isBanned : 1
     * isKickOut : 1
     * nick : 小倩
     * headPortrait : https://zhongxingjike1.oss-cn-beijing.aliyuncs.com/upload/51582100118917
     */

    private String customerId;
    private String isBanned;
    private String isKickOut;
    private String nick;
    private String headPortrait;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(String isBanned) {
        this.isBanned = isBanned;
    }

    public String getIsKickOut() {
        return isKickOut;
    }

    public void setIsKickOut(String isKickOut) {
        this.isKickOut = isKickOut;
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
}
