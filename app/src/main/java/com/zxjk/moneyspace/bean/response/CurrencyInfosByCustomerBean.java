package com.zxjk.moneyspace.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrencyInfosByCustomerBean implements Parcelable {

    /**
     * currencyName : ETH
     * coin : ETH
     * coinType : 0
     * contractAddress :
     * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571020781968.png?Expires=1886380777&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=Y%2B0GZsKYIOpicO77N6i3UPvUGoI%3D
     * isAlready : 1
     */

    private String currencyName;
    private String coin;
    private String coinType;
    private String contractAddress;
    private String logo;
    private String isAlready;
    private String isDelete;

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public static Creator<CurrencyInfosByCustomerBean> getCREATOR() {
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

    public String getIsAlready() {
        return isAlready;
    }

    public void setIsAlready(String isAlready) {
        this.isAlready = isAlready;
    }

    public CurrencyInfosByCustomerBean() {
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
        dest.writeString(this.isAlready);
        dest.writeString(this.isDelete);
    }

    protected CurrencyInfosByCustomerBean(Parcel in) {
        this.currencyName = in.readString();
        this.coin = in.readString();
        this.coinType = in.readString();
        this.contractAddress = in.readString();
        this.logo = in.readString();
        this.isAlready = in.readString();
        this.isDelete = in.readString();
    }

    public static final Creator<CurrencyInfosByCustomerBean> CREATOR = new Creator<CurrencyInfosByCustomerBean>() {
        @Override
        public CurrencyInfosByCustomerBean createFromParcel(Parcel source) {
            return new CurrencyInfosByCustomerBean(source);
        }

        @Override
        public CurrencyInfosByCustomerBean[] newArray(int size) {
            return new CurrencyInfosByCustomerBean[size];
        }
    };
}
