package com.zxjk.duoduo.bean.response;

public class GetThirdPartyPaymentOrderResponse {

    /**
     * appId : dd69wgmq921c
     * randomStr : p7o16
     * mchId : 123
     * goodsName : 商品名称
     * orderNumber : 123456789000
     * businessName : xxx第三方支付
     * hk : 100
     * attach : 附加信息
     * spbillCreateIp : 127.0.0.1
     * time : 1561598149311
     * failureTime : 60
     * sign : B63E4CCF2252880223F657F6B874CE1F
     */

    private String appId;
    private String randomStr;
    private String mchId;
    private String goodsName;
    private String orderNumber;
    private String businessName;
    private String hk;
    private String attach;
    private String spbillCreateIp;
    private String time;
    private String failureTime;
    private String sign;
    private String duoduoOrderId;

    public String getDuoduoOrderId() {
        return duoduoOrderId;
    }

    public void setDuoduoOrderId(String duoduoOrderId) {
        this.duoduoOrderId = duoduoOrderId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getRandomStr() {
        return randomStr;
    }

    public void setRandomStr(String randomStr) {
        this.randomStr = randomStr;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getHk() {
        return hk;
    }

    public void setHk(String hk) {
        this.hk = hk;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(String failureTime) {
        this.failureTime = failureTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
