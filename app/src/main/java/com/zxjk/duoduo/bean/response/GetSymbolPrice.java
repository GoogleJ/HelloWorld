package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GetSymbolPrice implements Parcelable {

    /**
     * logo :
     * symbol :
     * price :
     */

    private String logo;
    private String symbol;
    private String price;

    protected GetSymbolPrice(Parcel in) {
        logo = in.readString();
        symbol = in.readString();
        price = in.readString();
    }

    public static final Creator<GetSymbolPrice> CREATOR = new Creator<GetSymbolPrice>() {
        @Override
        public GetSymbolPrice createFromParcel(Parcel in) {
            return new GetSymbolPrice(in);
        }

        @Override
        public GetSymbolPrice[] newArray(int size) {
            return new GetSymbolPrice[size];
        }
    };

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(logo);
        dest.writeString(symbol);
        dest.writeString(price);
    }
}
