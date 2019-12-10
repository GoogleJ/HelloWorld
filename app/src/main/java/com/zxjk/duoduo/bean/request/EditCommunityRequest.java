package com.zxjk.duoduo.bean.request;

public class EditCommunityRequest {
    private String groupId;
    private String introduction;
    private String announcement;
    private String name;

    /**
     * logo : logo url
     * bgi : bgi url
     */

    private String logo;
    private String bgi;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
}
