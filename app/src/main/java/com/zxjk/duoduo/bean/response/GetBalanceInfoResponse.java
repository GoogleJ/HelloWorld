package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

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

    public static class BalanceListBean implements Parcelable {
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
        private String decimals;
        private String balanceAddress;
        private String rate;
        private String parentSymbol;

        public String getParentSymbol() {
            return parentSymbol;
        }

        public void setParentSymbol(String parentSymbol) {
            this.parentSymbol = parentSymbol;
        }

        public String getDecimals() {
            return decimals;
        }

        public void setDecimals(String decimals) {
            this.decimals = decimals;
        }

        public String getBalanceAddress() {
            return balanceAddress;
        }

        public void setBalanceAddress(String balanceAddress) {
            this.balanceAddress = balanceAddress;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public static Creator<BalanceListBean> getCREATOR() {
            return CREATOR;
        }

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

        public BalanceListBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.currencyName);
            dest.writeString(this.coin);
            dest.writeString(this.coinType);
            dest.writeString(this.contractAddress);
            dest.writeString(this.logo);
            dest.writeString(this.symbolId);
            dest.writeString(this.isAlready);
            dest.writeString(this.isDelete);
            dest.writeString(this.priceToCny);
            dest.writeString(this.balance);
            dest.writeString(this.decimals);
            dest.writeString(this.balanceAddress);
            dest.writeString(this.rate);
            dest.writeString(this.parentSymbol);
        }

        protected BalanceListBean(Parcel in) {
            this.currencyName = in.readString();
            this.coin = in.readString();
            this.coinType = in.readString();
            this.contractAddress = in.readString();
            this.logo = in.readString();
            this.symbolId = in.readString();
            this.isAlready = in.readString();
            this.isDelete = in.readString();
            this.priceToCny = in.readString();
            this.balance = in.readString();
            this.decimals = in.readString();
            this.balanceAddress = in.readString();
            this.rate = in.readString();
            this.parentSymbol = in.readString();
        }

        public static final Creator<BalanceListBean> CREATOR = new Creator<BalanceListBean>() {
            @Override
            public BalanceListBean createFromParcel(Parcel source) {
                return new BalanceListBean(source);
            }

            @Override
            public BalanceListBean[] newArray(int size) {
                return new BalanceListBean[size];
            }
        };
    }
}
