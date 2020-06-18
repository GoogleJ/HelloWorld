package com.zxjk.duoduo.bean.response;

public class GetImprovePaymentInformationResponse {
    /**
     * ALIPAY : 1
     * WEIXIN : 1
     * EBANK : 1
     * MOBILE : 1
     */

    private int ALIPAY;
    private int WEIXIN;
    private int EBANK;
    private int MOBILE;

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

    public int getEBANK() {
        return EBANK;
    }

    public void setEBANK(int EBANK) {
        this.EBANK = EBANK;
    }

    public int getMOBILE() {
        return MOBILE;
    }

    public void setMOBILE(int MOBILE) {
        this.MOBILE = MOBILE;
    }
}
