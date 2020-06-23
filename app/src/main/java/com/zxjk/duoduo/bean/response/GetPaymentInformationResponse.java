package com.zxjk.duoduo.bean.response;

public class GetPaymentInformationResponse {

    /**
     * alipayId : 17629042525
     * alipayUrl : https://www.showdoc.cc/server/api/common/visitfile/sign/f35ecef42bcb03f32e320f749f079695?showdoc=.jpg
     */

    private String alipayId;
    private String alipayUrl;
    /**
     * weixinId : 123456
     * weixinUrl : https://www.showdoc.cc/server/api/common/visitfile/sign/f35ecef42bcb03f32e320f749f079695?showdoc=.jpg
     */

    private String weixinId;
    private String weixinUrl;
    /**
     * cardCode : 6212262703000561339
     * cardUserName : 李白
     * cardAddress : 测试银行
     */

    private String cardCode;
    private String cardUserName;
    private String cardAddress;
    /**
     * mobile : 17629042500
     * countryCode : +86
     */

    private String mobile;
    private String countryCode;

    public String getAlipayId() {
        return alipayId;
    }

    public void setAlipayId(String alipayId) {
        this.alipayId = alipayId;
    }

    public String getAlipayUrl() {
        return alipayUrl;
    }

    public void setAlipayUrl(String alipayUrl) {
        this.alipayUrl = alipayUrl;
    }

    public String getWeixinId() {
        return weixinId;
    }

    public void setWeixinId(String weixinId) {
        this.weixinId = weixinId;
    }

    public String getWeixinUrl() {
        return weixinUrl;
    }

    public void setWeixinUrl(String weixinUrl) {
        this.weixinUrl = weixinUrl;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getCardUserName() {
        return cardUserName;
    }

    public void setCardUserName(String cardUserName) {
        this.cardUserName = cardUserName;
    }

    public String getCardAddress() {
        return cardAddress;
    }

    public void setCardAddress(String cardAddress) {
        this.cardAddress = cardAddress;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
