package com.zxjk.moneyspace.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class CnyRechargeResponse implements Parcelable {

    /**
     * cnyRechargeMoney : 10,20,30,50,100,200
     * superBankInfo : {"cardholderName":"管理员","bank":"中行","bankNum":"fdsdfsaf","subbranch":"北京银行","mobile":"17328747734"}
     */

    private String cnyRechargeMoney;
    private SuperBankInfoBean superBankInfo;

    public String getCnyRechargeMoney() {
        return cnyRechargeMoney;
    }

    public void setCnyRechargeMoney(String cnyRechargeMoney) {
        this.cnyRechargeMoney = cnyRechargeMoney;
    }

    public SuperBankInfoBean getSuperBankInfo() {
        return superBankInfo;
    }

    public void setSuperBankInfo(SuperBankInfoBean superBankInfo) {
        this.superBankInfo = superBankInfo;
    }

    public static class SuperBankInfoBean implements Parcelable {
        /**
         * cardholderName : 管理员
         * bank : 中行
         * bankNum : fdsdfsaf
         * subbranch : 北京银行
         * mobile : 17328747734
         */

        private String cardholderName;
        private String bank;
        private String bankNum;
        private String subbranch;
        private String mobile;

        public String getCardholderName() {
            return cardholderName;
        }

        public void setCardholderName(String cardholderName) {
            this.cardholderName = cardholderName;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getBankNum() {
            return bankNum;
        }

        public void setBankNum(String bankNum) {
            this.bankNum = bankNum;
        }

        public String getSubbranch() {
            return subbranch;
        }

        public void setSubbranch(String subbranch) {
            this.subbranch = subbranch;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.cardholderName);
            dest.writeString(this.bank);
            dest.writeString(this.bankNum);
            dest.writeString(this.subbranch);
            dest.writeString(this.mobile);
        }

        public SuperBankInfoBean() {
        }

        protected SuperBankInfoBean(Parcel in) {
            this.cardholderName = in.readString();
            this.bank = in.readString();
            this.bankNum = in.readString();
            this.subbranch = in.readString();
            this.mobile = in.readString();
        }

        public static final Parcelable.Creator<SuperBankInfoBean> CREATOR = new Parcelable.Creator<SuperBankInfoBean>() {
            @Override
            public SuperBankInfoBean createFromParcel(Parcel source) {
                return new SuperBankInfoBean(source);
            }

            @Override
            public SuperBankInfoBean[] newArray(int size) {
                return new SuperBankInfoBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cnyRechargeMoney);
        dest.writeParcelable(this.superBankInfo, flags);
    }

    public CnyRechargeResponse() {
    }

    protected CnyRechargeResponse(Parcel in) {
        this.cnyRechargeMoney = in.readString();
        this.superBankInfo = in.readParcelable(SuperBankInfoBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<CnyRechargeResponse> CREATOR = new Parcelable.Creator<CnyRechargeResponse>() {
        @Override
        public CnyRechargeResponse createFromParcel(Parcel source) {
            return new CnyRechargeResponse(source);
        }

        @Override
        public CnyRechargeResponse[] newArray(int size) {
            return new CnyRechargeResponse[size];
        }
    };
}
