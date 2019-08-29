package com.zxjk.duoduo.bean.response;

public class GetFriendsByMobilesResponse {

    /**
     * id : 3
     * customerId :
     * friendId :
     * friendRemarkName :
     * updateTime :
     * createTime :
     * isDelete :
     * nick : 小明
     * headPortrait : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/47142D65-89B0-4D91-B27C-C3F81F135742.jpg
     */

    private String id;
    private String customerId;
    private String friendId;
    private String friendRemarkName;
    private String updateTime;
    private String createTime;
    private String isDelete;
    private String nick;
    private String headPortrait;
    private String sortLetters;
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

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

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendRemarkName() {
        return friendRemarkName;
    }

    public void setFriendRemarkName(String friendRemarkName) {
        this.friendRemarkName = friendRemarkName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
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
