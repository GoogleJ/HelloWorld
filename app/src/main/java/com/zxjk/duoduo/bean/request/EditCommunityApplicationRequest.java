package com.zxjk.duoduo.bean.request;

public class EditCommunityApplicationRequest {

    /**
     * groupId : 65
     * type : add
     * applicationName : 奥利力力给
     * applicationLogo : http://pics6.baidu.com/feed/78310a55b319ebc480b07f7c5d1865f91c1716ca.jpeg?token=51deb7b5572491e224d281838a80c84d&s=1F06D70448036ECC5A0601E90300A062
     * applicationAddress : 1111
     */

    private String groupId;
    private String type;
    private String applicationName;
    private String applicationLogo;
    private String applicationAddress;
    /**
     * applicationId : 5
     */

    private String applicationId;
    /**
     * applicationOpen : 0
     */

    private String applicationOpen;


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

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationLogo() {
        return applicationLogo;
    }

    public void setApplicationLogo(String applicationLogo) {
        this.applicationLogo = applicationLogo;
    }

    public String getApplicationAddress() {
        return applicationAddress;
    }

    public void setApplicationAddress(String applicationAddress) {
        this.applicationAddress = applicationAddress;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationOpen() {
        return applicationOpen;
    }

    public void setApplicationOpen(String applicationOpen) {
        this.applicationOpen = applicationOpen;
    }
}
