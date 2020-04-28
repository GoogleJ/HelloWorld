package com.zxjk.moneyspace.bean.response;


import java.io.Serializable;
import java.util.List;

public class GetOTCSymbolInfo implements Serializable {

    private List<CurrencyListBean> currencyList;
    private List<PayInfoListBean> payInfoList;
    private String defaultRenegeNumber;

    public String getDefaultRenegeNumber() {
        return defaultRenegeNumber;
    }

    public void setDefaultRenegeNumber(String defaultRenegeNumber) {
        this.defaultRenegeNumber = defaultRenegeNumber;
    }

    public List<CurrencyListBean> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<CurrencyListBean> currencyList) {
        this.currencyList = currencyList;
    }

    public List<PayInfoListBean> getPayInfoList() {
        return payInfoList;
    }

    public void setPayInfoList(List<PayInfoListBean> payInfoList) {
        this.payInfoList = payInfoList;
    }

    public static class CurrencyListBean implements Serializable {
        /**
         * currency : USDT
         * price : 7.07
         * balance : 89.0000
         * rate : 1
         */

        private String currency;
        private String price;
        private String balance;
        private String rate;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }
    }

    public static class PayInfoListBean implements Serializable {

        /**
         * id : 2
         * customerId : 2
         * duoduoId : 1
         * payType : 1
         * payPicture : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/4qnihrkux4nk4cqw3wq5.jpg
         * payNumber : 1
         * wechatNick : W1446035768
         * zhifubaoNumber : 15249047865
         * openBank :
         * state : 0
         * reason :
         * cerateTime :
         * isDelete : 0
         * payPwd :
         * realName :
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
        private String state;
        private String reason;
        private String cerateTime;
        private String isDelete;
        private String payPwd;
        private String realName;

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

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
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

        public String getPayPwd() {
            return payPwd;
        }

        public void setPayPwd(String payPwd) {
            this.payPwd = payPwd;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

    }
}