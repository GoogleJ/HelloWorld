package com.zxjk.duoduo.bean.response;

public class GetSerialBean {
    /**
     * id : 19024
     * amount : 6.80
     * serialType : 1
     * symbol : MoT
     * createTime : 1571655233000
     * source : 11
     * serialTitle : 邀请注册
     * month : 2019.10
     */

    private String id;
    private String amount;
    private String serialType;
    private String symbol;
    private String createTime;
    private String source;
    private String serialTitle;
    private String month;
    private String logo;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSerialType() {
        return serialType;
    }

    public void setSerialType(String serialType) {
        this.serialType = serialType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSerialTitle() {
        return serialTitle;
    }

    public void setSerialTitle(String serialTitle) {
        this.serialTitle = serialTitle;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
