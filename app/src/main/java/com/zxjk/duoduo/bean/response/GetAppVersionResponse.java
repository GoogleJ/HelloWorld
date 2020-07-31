package com.zxjk.duoduo.bean.response;

public class GetAppVersionResponse {

    /**
     * version : 0.0.2
     * updateContent : 1.回归正式版本
     * updateAddress : www.360.cn
     * isEnforcement : 0
     * id : 2
     * updateTime : 1557283795896
     * createTime :
     * createBy :
     * updateBy :
     * page :
     * isDelete : 0
     */

    private String version;
    private String updateContent;
    private String updateAddress;
    private String isEnforcement;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getUpdateAddress() {
        return updateAddress;
    }

    public void setUpdateAddress(String updateAddress) {
        this.updateAddress = updateAddress;
    }

    public String getIsEnforcement() {
        return isEnforcement;
    }

    public void setIsEnforcement(String isEnforcement) {
        this.isEnforcement = isEnforcement;
    }

}
