package com.zxjk.duoduo.bean.response;

public class GetQuickOrderResponse {

    /**
     * bizCode : 0
     * order : {"otherOrderId":"103420200618172328","type":"BUY","total":"3520.0","coinAmount":"500.0","price":"7.04","orderStatus":"UNFINISHED","payType":"WEIXIN","weixinName":"HAILANGBIZ","weixinId":"1234567890","weixinUrl":"https://res.linkcoin.me//kycdata-linkcoin/qrcode/134fab193cee7c79.jpg","currency":"CNY","coinSymbol":"USDT","sellerNickName":"rightbtc","appealType":0,"appealRemark":"-","priceType":1,"createTime":"1592472208861"}
     */

    private int bizCode;
    private OrderBean order;

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public OrderBean getOrder() {
        return order;
    }

    public void setOrder(OrderBean order) {
        this.order = order;
    }

    public static class OrderBean {
        /**
         * otherOrderId : 103420200618172328
         * type : BUY
         * total : 3520.0
         * coinAmount : 500.0
         * price : 7.04
         * orderStatus : UNFINISHED
         * payType : WEIXIN
         * weixinName : HAILANGBIZ
         * weixinId : 1234567890
         * weixinUrl : https://res.linkcoin.me//kycdata-linkcoin/qrcode/134fab193cee7c79.jpg
         * currency : CNY
         * coinSymbol : USDT
         * sellerNickName : rightbtc
         * appealType : 0
         * appealRemark : -
         * priceType : 1
         * createTime : 1592472208861
         */

        private String otherOrderId;
        private String type;
        private String total;
        private String coinAmount;
        private String price;
        private String orderStatus;
        private String payType;
        private String weixinName;
        private String weixinId;
        private String weixinUrl;
        private String currency;
        private String coinSymbol;
        private String sellerNickName;
        private int appealType;
        private String appealRemark;
        private int priceType;
        private String createTime;

        public String getOtherOrderId() {
            return otherOrderId;
        }

        public void setOtherOrderId(String otherOrderId) {
            this.otherOrderId = otherOrderId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getCoinAmount() {
            return coinAmount;
        }

        public void setCoinAmount(String coinAmount) {
            this.coinAmount = coinAmount;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getWeixinName() {
            return weixinName;
        }

        public void setWeixinName(String weixinName) {
            this.weixinName = weixinName;
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

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getCoinSymbol() {
            return coinSymbol;
        }

        public void setCoinSymbol(String coinSymbol) {
            this.coinSymbol = coinSymbol;
        }

        public String getSellerNickName() {
            return sellerNickName;
        }

        public void setSellerNickName(String sellerNickName) {
            this.sellerNickName = sellerNickName;
        }

        public int getAppealType() {
            return appealType;
        }

        public void setAppealType(int appealType) {
            this.appealType = appealType;
        }

        public String getAppealRemark() {
            return appealRemark;
        }

        public void setAppealRemark(String appealRemark) {
            this.appealRemark = appealRemark;
        }

        public int getPriceType() {
            return priceType;
        }

        public void setPriceType(int priceType) {
            this.priceType = priceType;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
