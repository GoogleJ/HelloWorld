package com.zxjk.duoduo.bean.response;

import java.util.List;

public class CommunityInfoResponse {

    /**
     * id : 2
     * groupId : 30
     * name : name
     * logo : logo
     * bgi : 1
     * code : 472109
     * introduction : 这个群主太懒了,什么都没有说：）
     * announcement : 这个群主太懒了,什么都没有说：）
     * status : 0
     * createTime : 1573698404740
     * members : [{"headPortrait":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/31573119859032","identity":"2"}]
     */

    private String id;
    private String groupId;
    private String name;
    private String logo;
    private String bgi;
    private String code;
    private String introduction;
    private String announcement;
    private String status;
    private String createTime;
    private List<MembersBean> members;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBgi() {
        return bgi;
    }

    public void setBgi(String bgi) {
        this.bgi = bgi;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<MembersBean> getMembers() {
        return members;
    }

    public void setMembers(List<MembersBean> members) {
        this.members = members;
    }

    public static class MembersBean {
        /**
         * headPortrait : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/31573119859032
         * identity : 2
         */

        private String headPortrait;
        private String identity;

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }
    }
}
