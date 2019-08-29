package com.zxjk.duoduo.bean.response;

public class PayInfoResponse {

    /**
     * id : 4
     * customerId : 4
     * duoduoId : 10000004
     * payType : 1
     * payPicture : 1.1
     * payNumber :
     * wechatNick : 1231231
     * zhifubaoNumber :
     * openBank :
     * status :
     * reason :
     * cerateTime : 1552290324695
     * isDelete : 0
     */

    private String id;
    private String customerId;
    private String duoduoId;
    private String payType;
    private String payPicture;
    private String payNumber;
    private String wechatNick;
    private String zhifubaoNumber;
    private String openBank;
    private String status;
    private String reason;
    private String cerateTime;
    private String isDelete;

    @Override
    public String toString() {
        return "PayInfoResponse{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", duoduoId='" + duoduoId + '\'' +
                ", payType='" + payType + '\'' +
                ", payPicture='" + payPicture + '\'' +
                ", payNumber='" + payNumber + '\'' +
                ", wechatNick='" + wechatNick + '\'' +
                ", zhifubaoNumber='" + zhifubaoNumber + '\'' +
                ", openBank='" + openBank + '\'' +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                ", cerateTime='" + cerateTime + '\'' +
                ", isDelete='" + isDelete + '\'' +
                '}';
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

    public String getDuoduoId() {
        return duoduoId;
    }

    public void setDuoduoId(String duoduoId) {
        this.duoduoId = duoduoId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayPicture() {
        return payPicture;
    }

    public void setPayPicture(String payPicture) {
        this.payPicture = payPicture;
    }

    public String getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(String payNumber) {
        this.payNumber = payNumber;
    }

    public String getWechatNick() {
        return wechatNick;
    }

    public void setWechatNick(String wechatNick) {
        this.wechatNick = wechatNick;
    }

    public String getZhifubaoNumber() {
        return zhifubaoNumber;
    }

    public void setZhifubaoNumber(String zhifubaoNumber) {
        this.zhifubaoNumber = zhifubaoNumber;
    }

    public String getOpenBank() {
        return openBank;
    }

    public void setOpenBank(String openBank) {
        this.openBank = openBank;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCerateTime() {
        return cerateTime;
    }

    public void setCerateTime(String cerateTime) {
        this.cerateTime = cerateTime;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }
}
