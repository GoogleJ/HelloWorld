package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GenerateMnemonicResponse implements Parcelable {

    /**
     * customerId : 365
     * symbol : ETH
     * walletAddress : 0x1f8e3f1b94b08fc6e9335749ff0928487f19bbd9
     * walletMnemonic : hoMiPTThgk74LX2TP1dR7aLnq0zZxJ1EN1BgHm8S6GMUNmP5WM+dsoKnVGGqfIHq86ZuH2YcXd7B
     iJ37p/rexrx0+HikE3KzC06wlTU2axCBoYFob7InSFo92ZvvZqv8
     * walletPrivateKey : b1a69VHkeBkqYoRz94ItIqZ2z31XGXOij5oPW4F8B/IcU+1TqY8ClAygR61X1hmCQoySW0LiN+II
     ALN/nJ75lBD9Hu4lQ6sYrv8wEeni8LA=
     * walletKeystore : MbUNJxSZnFFdteE30OUqkAO7eMD/7DTLHUo3eogG3N3GCkSqqv11cv2xwURYSHxMKTxOP4Pqap1g
     BQt6+9zM78hG1zccYDnIakKlGhVL/y7K7bxGto1u2JADRLWfRHRHKrz7n4vvol/bgqk1gW7PKAer
     i61sW3HUI+rHoe5FL9+AcVY8zmPYnIyO3mrgWAVNewlEJSrmAt1LqzH0ofUyUmjlerLGk3//mDqf
     B37OBDYZk+iKc6iW/jWXPu8zF53BZZuMxaP/s7sX1e0uxFMcdpaZfLcQezfmEdsLTgjqjdvVPle+
     l7Ky52LmWbAOGcIAO7tUy4KwGdqG4Q8QntJilLOJ/mPAErGCvlR8lgsf5Bvy5ITqSFAcP1ckX+FR
     MCyV25QGCvOrZQJIdOU/BiTuUr8TC+LGWFE2P4POd8hYJH7p9wme0hya/SeN48cw6M0naO3XH76E
     gAvD6wZ1YrXOIu1AAXrLqn/X8PS3ZL+btroucb7BHfVHs4RiLDBJO2bxmiDrfsw2WZ18Q+AOBH+A
     j+8CUvzOO53DFGN1EHBNcOGaOqD1g90GSviyEEFXvbqhvmcWYxNmtfETjkPUXuwpdBEIn1YyeN2b
     4+enSGay5dnY0PfVPyUQheRy+qSGrsqxtZWuDkpOvQJfv2GUa8/r8g==
     * createTime : 1571021640442
     * updateTime :
     * isDelete :
     * walletName : ETH wallet
     * importMethod : 3
     * balance :
     * balanceToCNY :
     * contractAddress :
     * coinType :
     * decimals : 0
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
    private int decimals;


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

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
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
        dest.writeInt(this.decimals);
    }

    public GenerateMnemonicResponse() {
    }

    protected GenerateMnemonicResponse(Parcel in) {
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
        this.decimals = in.readInt();
    }

    public static final Parcelable.Creator<GenerateMnemonicResponse> CREATOR = new Parcelable.Creator<GenerateMnemonicResponse>() {
        @Override
        public GenerateMnemonicResponse createFromParcel(Parcel source) {
            return new GenerateMnemonicResponse(source);
        }

        @Override
        public GenerateMnemonicResponse[] newArray(int size) {
            return new GenerateMnemonicResponse[size];
        }
    };
}
