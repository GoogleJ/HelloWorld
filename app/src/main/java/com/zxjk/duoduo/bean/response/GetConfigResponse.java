package com.zxjk.duoduo.bean.response;

public class GetConfigResponse {

    /**
     * auth : 1
     * ALIPAY : 1
     * WEIXIN : 1
     * mobile : 17629042525
     * EBANK : 1
     * userName :
     * MOBILE : 1
     */

    private int auth;
    private int ALIPAY;
    private int WEIXIN;
    private String mobile;
    private int EBANK;
    private String userName;
    private int MOBILE;

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public int getALIPAY() {
        return ALIPAY;
    }

    public void setALIPAY(int ALIPAY) {
        this.ALIPAY = ALIPAY;
    }

    public int getWEIXIN() {
        return WEIXIN;
    }

    public void setWEIXIN(int WEIXIN) {
        this.WEIXIN = WEIXIN;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getEBANK() {
        return EBANK;
    }

    public void setEBANK(int EBANK) {
        this.EBANK = EBANK;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getMOBILE() {
        return MOBILE;
    }

    public void setMOBILE(int MOBILE) {
        this.MOBILE = MOBILE;
    }
}
