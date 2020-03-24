package com.zxjk.duoduo.bean.response;

public class PaymentDoneResponse {

    /**
     * customerId : 1837
     * transId : 20200303184136080523
     * collectionId : 308
     * state : 2
     * code : 200
     */

    private String customerId;
    private String transId;
    private String collectionId;
    private String state;
    private String code;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
