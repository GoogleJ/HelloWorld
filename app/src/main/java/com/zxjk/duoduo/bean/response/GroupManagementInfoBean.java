package com.zxjk.duoduo.bean.response;

public class GroupManagementInfoBean {


    /**
     * id : 9
     * nick : 刘一
     * headPortrait : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/91556354875135.jpg
     * isBanned : 0
     */

    private String id;
    private String nick;
    private String headPortrait;
    private String isBanned;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(String isBanned) {
        this.isBanned = isBanned;
    }
}
