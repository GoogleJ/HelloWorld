package com.zxjk.duoduo.bean.response;

import java.util.List;

public class GetBalanceInfoResponse {

    /**
     * totalToBtc : 0.0
     * totalToCny : 0.0
     * balanceList : [{"currencyName":"ETH","coin":"","coinType":"0","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","symbolId":"ethereum","isAlready":"","isDelete":"","priceToCny":"0.00","balance":"0"},{"currencyName":"HKB","coin":"","coinType":"1","contractAddress":"","logo":"","symbolId":"","isAlready":"","isDelete":"","priceToCny":"0.00","balance":"0"}]
     */

    private String totalToBtc;
    private String totalToCny;
    private List<BalanceListBean> balanceList;

    public String getTotalToBtc() {
        return totalToBtc;
    }

    public void setTotalToBtc(String totalToBtc) {
        this.totalToBtc = totalToBtc;
    }

    public String getTotalToCny() {
        return totalToCny;
    }

    public void setTotalToCny(String totalToCny) {
        this.totalToCny = totalToCny;
    }

    public List<BalanceListBean> getBalanceList() {
        return balanceList;
    }

    public void setBalanceList(List<BalanceListBean> balanceList) {
        this.balanceList = balanceList;
    }

    public static class BalanceListBean {
        /**
         * currencyName : ETH
         * coin :
         * coinType : 0
         * contractAddress :
         * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D
         * symbolId : ethereum
         * isAlready :
         * isDelete :
         * priceToCny : 0.00
         * balance : 0
         */

        private String currencyName;
        private String coin;
        private String coinType;
        private String contractAddress;
        private String logo;
        private String symbolId;
        private String isAlready;
        private String isDelete;
        private String priceToCny;
        private String balance;

        public String getCurrencyName() {
            return currencyName;
        }

        public void setCurrencyName(String currencyName) {
            this.currencyName = currencyName;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getCoinType() {
            return coinType;
        }

        public void setCoinType(String coinType) {
            this.coinType = coinType;
        }

        public String getContractAddress() {
            return contractAddress;
        }

        public void setContractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getSymbolId() {
            return symbolId;
        }

        public void setSymbolId(String symbolId) {
            this.symbolId = symbolId;
        }

        public String getIsAlready() {
            return isAlready;
        }

        public void setIsAlready(String isAlready) {
            this.isAlready = isAlready;
        }

        public String getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }

        public String getPriceToCny() {
            return priceToCny;
        }

        public void setPriceToCny(String priceToCny) {
            this.priceToCny = priceToCny;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }
    }
}
