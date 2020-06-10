package com.zxjk.duoduo.bean.response;

public class GetRecommendCommunity {

    /**
     * groupId : 637
     * groupNickName : 测试群成员
     * logo : https://zhongxingjike1.oss-cn-beijing.aliyuncs.com/upload/A929C903-0192-4E0E-A904-191F73D5F30C.jpg
     * introduction : 测试
     * nick : mag就
     * membersSize : 3
     */

    private String groupId;
    private String groupNickName;
    private String logo;
    private String introduction;
    private String nick;
    private String membersSize;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupNickName() {
        return groupNickName;
    }

    public void setGroupNickName(String groupNickName) {
        this.groupNickName = groupNickName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMembersSize() {
        return membersSize;
    }

    public void setMembersSize(String membersSize) {
        this.membersSize = membersSize;
    }
}
