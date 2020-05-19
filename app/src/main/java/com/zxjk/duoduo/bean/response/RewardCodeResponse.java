package com.zxjk.duoduo.bean.response;

public class RewardCodeResponse {
    /**
     * symbol : USDT
     * num : 0.5264
     * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571020793185.png?Expires=1886380789&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=40XcvkX5AftUJF8CLBtDrMWPlJE%3D
     */

    private String symbol;
    private String num;
    private String logo;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
