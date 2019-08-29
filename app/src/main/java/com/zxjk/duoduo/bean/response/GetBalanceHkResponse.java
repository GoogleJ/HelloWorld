package com.zxjk.duoduo.bean.response;

public class GetBalanceHkResponse {

    /**
     * customerId :
     * balanceHk :
     * balanceHkb :
     * id : 4
     * updateTime :
     * createTime :
     * createBy :
     * updateBy :
     * page :
     * isDelete :
     */

    private String customerId;
    private String balanceMot;
    private String balanceMoToken;
    private String id;
    private String updateTime;
    private String createTime;
    private String createBy;
    private String updateBy;
    private String page;
    private String isDelete;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBalanceMot() {
        return balanceMot;
    }

    public void setBalanceMot(String balanceMot) {
        this.balanceMot = balanceMot;
    }

    public String getBalanceMoToken() {
        return balanceMoToken;
    }

    public void setBalanceMoToken(String balanceMoToken) {
        this.balanceMoToken = balanceMoToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }
}
