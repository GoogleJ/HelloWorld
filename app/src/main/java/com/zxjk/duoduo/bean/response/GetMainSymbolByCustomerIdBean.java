package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GetMainSymbolByCustomerIdBean implements Parcelable {

    /**
     * symbol : ETH
     * walletName : ETH wallet
     * importMethod : 0
     * walletAddress : 0x26014d4b627b40e180c4f28bfce323679bfa7115
     */


    private String symbol;
    private String walletName;
    private String importMethod;
    private String walletAddress;
    private String logo;

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

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getImportMethod() {
        return importMethod;
    }

    public void setImportMethod(String importMethod) {
        this.importMethod = importMethod;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.symbol);
        dest.writeString(this.walletName);
        dest.writeString(this.importMethod);
        dest.writeString(this.walletAddress);
        dest.writeString(this.logo);
    }

    public GetMainSymbolByCustomerIdBean() {
    }

    protected GetMainSymbolByCustomerIdBean(Parcel in) {
        this.symbol = in.readString();
        this.walletName = in.readString();
        this.importMethod = in.readString();
        this.walletAddress = in.readString();
        this.logo = in.readString();
    }

    public static final Parcelable.Creator<GetMainSymbolByCustomerIdBean> CREATOR = new Parcelable.Creator<GetMainSymbolByCustomerIdBean>() {
        @Override
        public GetMainSymbolByCustomerIdBean createFromParcel(Parcel source) {
            return new GetMainSymbolByCustomerIdBean(source);
        }

        @Override
        public GetMainSymbolByCustomerIdBean[] newArray(int size) {
            return new GetMainSymbolByCustomerIdBean[size];
        }
    };
}
