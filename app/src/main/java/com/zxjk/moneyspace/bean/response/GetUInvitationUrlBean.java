package com.zxjk.moneyspace.bean.response;

import java.util.List;

public class GetUInvitationUrlBean {

    /**
     * nick : 贺欢欢
     * inviteCode : 6BX8
     * invitationMessage : ["被邀请人注册可获得5USDT;","邀请新人注册可获得0.5USDT,不限次数;","邀请人奖励到账需要被邀请人实名认证后,即可到账;"]
     * headPortrait : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/4qnihrkux4nk4cqw3wq5.jpg
     * url : http://192.168.1.69:8886/redPage.html?id=2&communityId=30
     */

    private String nick;
    private String inviteCode;
    private String headPortrait;
    private String url;
    private List<String> invitationMessage;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getInvitationMessage() {
        return invitationMessage;
    }

    public void setInvitationMessage(List<String> invitationMessage) {
        this.invitationMessage = invitationMessage;
    }
}
