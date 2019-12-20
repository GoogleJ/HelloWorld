package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GetSerialBean implements Parcelable {
    /**
     * id : 19024
     * amount : 6.80
     * serialType : 1
     * symbol : MoT
     * createTime : 1571655233000
     * source : 11
     * serialTitle : 邀请注册
     * month : 2019.10
     */

    private String id;
    private String amount;
    private String serialType;
    private String symbol;
    private String createTime;
    private String source;
    private String serialTitle;
    private String serialNumber;
    private String month;
    private String logo;
    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public static Creator<GetSerialBean> getCREATOR() {
        return CREATOR;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSerialType() {
        return serialType;
    }

    public void setSerialType(String serialType) {
        this.serialType = serialType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSerialTitle() {
        return serialTitle;
    }

    public void setSerialTitle(String serialTitle) {
        this.serialTitle = serialTitle;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public GetSerialBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.amount);
        dest.writeString(this.serialType);
        dest.writeString(this.symbol);
        dest.writeString(this.createTime);
        dest.writeString(this.source);
        dest.writeString(this.serialTitle);
        dest.writeString(this.serialNumber);
        dest.writeString(this.month);
        dest.writeString(this.logo);
    }

    protected GetSerialBean(Parcel in) {
        this.id = in.readString();
        this.amount = in.readString();
        this.serialType = in.readString();
        this.symbol = in.readString();
        this.createTime = in.readString();
        this.source = in.readString();
        this.serialTitle = in.readString();
        this.serialNumber = in.readString();
        this.month = in.readString();
        this.logo = in.readString();
    }

    public static final Creator<GetSerialBean> CREATOR = new Creator<GetSerialBean>() {
        @Override
        public GetSerialBean createFromParcel(Parcel source) {
            return new GetSerialBean(source);
        }

        @Override
        public GetSerialBean[] newArray(int size) {
            return new GetSerialBean[size];
        }
    };
}
