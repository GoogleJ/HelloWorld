package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class ThirdPartyPaymentOrderResponse implements Parcelable {

    /**
     * symbol : USDT
     * amount : 100
     * symbolLogo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571020793185.png?Expires=1886380789&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=40XcvkX5AftUJF8CLBtDrMWPlJE%3D
     * cnyAmount : 78.200
     * orderNumber : ordernumber12345678
     * businessName : 测试商家
     * balance : 700.00000
     * pay : 1
     */

    private String symbol;
    private String amount;
    private String symbolLogo;
    private String cnyAmount;
    private String orderNumber;
    private String businessName;
    private String balance;
    private int pay;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSymbolLogo() {
        return symbolLogo;
    }

    public void setSymbolLogo(String symbolLogo) {
        this.symbolLogo = symbolLogo;
    }

    public String getCnyAmount() {
        return cnyAmount;
    }

    public void setCnyAmount(String cnyAmount) {
        this.cnyAmount = cnyAmount;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.symbol);
        dest.writeString(this.amount);
        dest.writeString(this.symbolLogo);
        dest.writeString(this.cnyAmount);
        dest.writeString(this.orderNumber);
        dest.writeString(this.businessName);
        dest.writeString(this.balance);
        dest.writeInt(this.pay);
    }

    public ThirdPartyPaymentOrderResponse() {
    }

    protected ThirdPartyPaymentOrderResponse(Parcel in) {
        this.symbol = in.readString();
        this.amount = in.readString();
        this.symbolLogo = in.readString();
        this.cnyAmount = in.readString();
        this.orderNumber = in.readString();
        this.businessName = in.readString();
        this.balance = in.readString();
        this.pay = in.readInt();
    }

    public static final Parcelable.Creator<ThirdPartyPaymentOrderResponse> CREATOR = new Parcelable.Creator<ThirdPartyPaymentOrderResponse>() {
        @Override
        public ThirdPartyPaymentOrderResponse createFromParcel(Parcel source) {
            return new ThirdPartyPaymentOrderResponse(source);
        }

        @Override
        public ThirdPartyPaymentOrderResponse[] newArray(int size) {
            return new ThirdPartyPaymentOrderResponse[size];
        }
    };
}
