package com.zxjk.moneyspace.bean.response;

import java.util.List;

public class GetGroupPayInfoResponse {

    /**
     * payFee : 1
     * sumPayFee : 40
     * isOpen : 1
     * payFeeNumbers : 2
     * groupPayList : [{"createTime":"1567420374592","nick":"嗯哼","amount":"20"},{"createTime":"15674203745923","nick":"hgahaha","amount":"20"}]
     */

    private String payFee;
    private String sumPayFee;
    private String isOpen;
    private String symbol;
    private String logo;
    private String payFeeNumbers;
    private List<GroupPayListBean> groupPayList;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPayFee() {
        return payFee;
    }

    public void setPayFee(String payFee) {
        this.payFee = payFee;
    }

    public String getSumPayFee() {
        return sumPayFee;
    }

    public void setSumPayFee(String sumPayFee) {
        this.sumPayFee = sumPayFee;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getPayFeeNumbers() {
        return payFeeNumbers;
    }

    public void setPayFeeNumbers(String payFeeNumbers) {
        this.payFeeNumbers = payFeeNumbers;
    }

    public List<GroupPayListBean> getGroupPayList() {
        return groupPayList;
    }

    public void setGroupPayList(List<GroupPayListBean> groupPayList) {
        this.groupPayList = groupPayList;
    }

    public static class GroupPayListBean {
        /**
         * createTime : 1567420374592
         * nick : 嗯哼
         * amount : 20
         */

        private String createTime;
        private String nick;
        private String amount;
        private String symbol;

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

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getPayMot() {
            return amount;
        }

        public void setPayMot(String payMot) {
            this.amount = payMot;
        }
    }
}
