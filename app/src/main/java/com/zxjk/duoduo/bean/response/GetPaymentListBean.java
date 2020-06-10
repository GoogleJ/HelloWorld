package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GetPaymentListBean implements Parcelable {

    /**
     * symbol : ETH
     * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D
     * balance : 0
     */

    private String symbol;
    private String logo;
    private String balance;
    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public GetPaymentListBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.symbol);
        dest.writeString(this.logo);
        dest.writeString(this.balance);
        dest.writeString(this.price);
    }

    protected GetPaymentListBean(Parcel in) {
        this.symbol = in.readString();
        this.logo = in.readString();
        this.balance = in.readString();
        this.price = in.readString();
    }

    public static final Creator<GetPaymentListBean> CREATOR = new Creator<GetPaymentListBean>() {
        @Override
        public GetPaymentListBean createFromParcel(Parcel source) {
            return new GetPaymentListBean(source);
        }

        @Override
        public GetPaymentListBean[] newArray(int size) {
            return new GetPaymentListBean[size];
        }
    };
}
