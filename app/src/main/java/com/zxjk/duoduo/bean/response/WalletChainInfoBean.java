package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class WalletChainInfoBean implements Parcelable {

    /**
     * customerId : 365
     * symbol : ETH
     * walletAddress : 0x3bd8dee47b61ab24b1e4e264519819f2b4da9bbd
     * walletMnemonic :
     * walletPrivateKey :
     * walletKeystore :
     * createTime :
     * updateTime :
     * isDelete :
     * walletName :
     * importMethod : 0
     * balance : 0
     * balanceToCNY : 0.0
     * contractAddress :
     * coinType : 0
     */


    private String customerId;
    private String symbol;
    private String walletAddress;
    private String walletMnemonic;
    private String walletPrivateKey;
    private String walletKeystore;
    private String createTime;
    private String updateTime;
    private String isDelete;
    private String walletName;
    private String importMethod;
    private String balance;
    private String balanceToCNY;
    private String contractAddress;
    private String coinType;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getWalletMnemonic() {
        return walletMnemonic;
    }

    public void setWalletMnemonic(String walletMnemonic) {
        this.walletMnemonic = walletMnemonic;
    }

    public String getWalletPrivateKey() {
        return walletPrivateKey;
    }

    public void setWalletPrivateKey(String walletPrivateKey) {
        this.walletPrivateKey = walletPrivateKey;
    }

    public String getWalletKeystore() {
        return walletKeystore;
    }

    public void setWalletKeystore(String walletKeystore) {
        this.walletKeystore = walletKeystore;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBalanceToCNY() {
        return balanceToCNY;
    }

    public void setBalanceToCNY(String balanceToCNY) {
        this.balanceToCNY = balanceToCNY;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.customerId);
        dest.writeString(this.symbol);
        dest.writeString(this.walletAddress);
        dest.writeString(this.walletMnemonic);
        dest.writeString(this.walletPrivateKey);
        dest.writeString(this.walletKeystore);
        dest.writeString(this.createTime);
        dest.writeString(this.updateTime);
        dest.writeString(this.isDelete);
        dest.writeString(this.walletName);
        dest.writeString(this.importMethod);
        dest.writeString(this.balance);
        dest.writeString(this.balanceToCNY);
        dest.writeString(this.contractAddress);
        dest.writeString(this.coinType);
    }

    public WalletChainInfoBean() {
    }

    protected WalletChainInfoBean(Parcel in) {
        this.customerId = in.readString();
        this.symbol = in.readString();
        this.walletAddress = in.readString();
        this.walletMnemonic = in.readString();
        this.walletPrivateKey = in.readString();
        this.walletKeystore = in.readString();
        this.createTime = in.readString();
        this.updateTime = in.readString();
        this.isDelete = in.readString();
        this.walletName = in.readString();
        this.importMethod = in.readString();
        this.balance = in.readString();
        this.balanceToCNY = in.readString();
        this.contractAddress = in.readString();
        this.coinType = in.readString();
    }

    public static final Parcelable.Creator<WalletChainInfoBean> CREATOR = new Parcelable.Creator<WalletChainInfoBean>() {
        @Override
        public WalletChainInfoBean createFromParcel(Parcel source) {
            return new WalletChainInfoBean(source);
        }

        @Override
        public WalletChainInfoBean[] newArray(int size) {
            return new WalletChainInfoBean[size];
        }
    };
}
