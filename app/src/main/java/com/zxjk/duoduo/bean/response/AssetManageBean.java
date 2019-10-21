package com.zxjk.duoduo.bean.response;

public class AssetManageBean {

    /**
     * customerId :
     * symbol : ETH
     * isDelete : 0
     * parentSymbol : ETH
     * createTime :
     * updateTime :
     * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D
     * contractAddress :
     */

    private String customerId;
    private String symbol;
    private String isDelete;
    private String parentSymbol;
    private String createTime;
    private String updateTime;
    private String logo;
    private String contractAddress;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getParentSymbol() {
        return parentSymbol;
    }

    public void setParentSymbol(String parentSymbol) {
        this.parentSymbol = parentSymbol;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
}
