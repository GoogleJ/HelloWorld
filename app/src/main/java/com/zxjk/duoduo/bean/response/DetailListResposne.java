package com.zxjk.duoduo.bean.response;

import java.io.Serializable;

public class DetailListResposne implements Serializable {

    /**
     * serialNumber :
     * fromCustomerId :
     * toCustomerId :
     * redPacgageId :
     * source : 5
     * hk : 100.00
     * serialType :
     * customerId :
     * carateTime :
     * sum : 1500.00
     * id : 81
     * updateTime :
     * createTime :
     * createBy :
     * updateBy :
     * page :
     * isDelete :
     */

    private String serialNumber;
    private String fromCustomerId;
    private String toCustomerId;
    private String redPacgageId;
    private int source;
    private String hk;
    private String mot;
    private String serialType;
    private String customerId;
    private String carateTime;
    private String sum;
    private String id;
    private String updateTime;
    private String createTime;
    private String createBy;
    private String updateBy;
    private String page;
    private String isDelete;
    private String remarks;

    public String getMot() {
        return mot;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getFromCustomerId() {
        return fromCustomerId;
    }

    public void setFromCustomerId(String fromCustomerId) {
        this.fromCustomerId = fromCustomerId;
    }

    public String getToCustomerId() {
        return toCustomerId;
    }

    public void setToCustomerId(String toCustomerId) {
        this.toCustomerId = toCustomerId;
    }

    public String getRedPacgageId() {
        return redPacgageId;
    }

    public void setRedPacgageId(String redPacgageId) {
        this.redPacgageId = redPacgageId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getHk() {
        return hk;
    }

    public void setHk(String hk) {
        this.hk = hk;
    }

    public String getSerialType() {
        return serialType;
    }

    public void setSerialType(String serialType) {
        this.serialType = serialType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCarateTime() {
        return carateTime;
    }

    public void setCarateTime(String carateTime) {
        this.carateTime = carateTime;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
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
