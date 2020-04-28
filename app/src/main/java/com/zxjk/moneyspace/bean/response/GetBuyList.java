package com.zxjk.moneyspace.bean.response;

public class GetBuyList {

    /**
     * headPortrait : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1575277814072.png
     * nick : 152****7865
     * unSaleNum : 1
     * minNum : 7.01
     * maxNum : 10
     * payType : 1,2,3
     * price : 3
     * sellOrderId : 1446035768
     * currency : USDT
     */

    private String headPortrait;
    private String nick;
    private String unSaleNum;
    private String minNum;
    private String maxNum;
    private String payType;
    private String price;
    private String sellOrderId;
    private String currency;
    private String unBoughtNum;
    private String buyOrderId;

    public String getUnBoughtNum() {
        return unBoughtNum;
    }

    public void setUnBoughtNum(String unBoughtNum) {
        this.unBoughtNum = unBoughtNum;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getUnSaleNum() {
        return unSaleNum;
    }

    public void setUnSaleNum(String unSaleNum) {
        this.unSaleNum = unSaleNum;
    }

    public String getMinNum() {
        return minNum;
    }

    public void setMinNum(String minNum) {
        this.minNum = minNum;
    }

    public String getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(String maxNum) {
        this.maxNum = maxNum;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(String sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
