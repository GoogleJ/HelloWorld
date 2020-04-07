package com.zxjk.moneyspace.bean.request;

public class EditCommunityWebSiteRequest {

    /**
     * groupId : 81
     * type : add
     * websiteTitle : 百度
     * websiteContent : 百度（纳斯达克：BIDU），全球最大的中文搜索引擎及最大的中文网站，全球领先的人工智能公司..
     * websiteUrl : https://www.baidu.com/
     */

    private String groupId;
    private String type;
    private String websiteTitle;
    private String websiteContent;
    private String websiteUrl;
    private String websiteId;
    private String officialWebsiteOpen;

    public String getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
    }

    public String getOfficialWebsiteOpen() {
        return officialWebsiteOpen;
    }

    public void setOfficialWebsiteOpen(String officialWebsiteOpen) {
        this.officialWebsiteOpen = officialWebsiteOpen;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebsiteTitle() {
        return websiteTitle;
    }

    public void setWebsiteTitle(String websiteTitle) {
        this.websiteTitle = websiteTitle;
    }

    public String getWebsiteContent() {
        return websiteContent;
    }

    public void setWebsiteContent(String websiteContent) {
        this.websiteContent = websiteContent;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}
