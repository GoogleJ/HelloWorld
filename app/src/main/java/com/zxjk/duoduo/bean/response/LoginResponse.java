package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginResponse implements Parcelable {

    private String id;
    private String duoduoId;
    private String nick;
    private String realname;
    private String mobile;
    private String address;
    private String email;
    private String headPortrait;
    private String sex;
    private String signature;
    private String isShowRealname;
    private String token;
    private String rongToken;
    private String isFirstLogin;
    private String isAuthentication;
    private String onlineService;
    private String openPhone;
    private String inviteCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDuoduoId() {
        return duoduoId;
    }

    public void setDuoduoId(String duoduoId) {
        this.duoduoId = duoduoId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getIsShowRealname() {
        return isShowRealname;
    }

    public void setIsShowRealname(String isShowRealname) {
        this.isShowRealname = isShowRealname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRongToken() {
        return rongToken;
    }

    public void setRongToken(String rongToken) {
        this.rongToken = rongToken;
    }

    public String getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(String isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public String getIsAuthentication() {
        return isAuthentication;
    }

    public void setIsAuthentication(String isAuthentication) {
        this.isAuthentication = isAuthentication;
    }

    public String getOnlineService() {
        return onlineService;
    }

    public void setOnlineService(String onlineService) {
        this.onlineService = onlineService;
    }

    public String getOpenPhone() {
        return openPhone;
    }

    public void setOpenPhone(String openPhone) {
        this.openPhone = openPhone;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.duoduoId);
        dest.writeString(this.nick);
        dest.writeString(this.realname);
        dest.writeString(this.mobile);
        dest.writeString(this.address);
        dest.writeString(this.email);
        dest.writeString(this.headPortrait);
        dest.writeString(this.sex);
        dest.writeString(this.signature);
        dest.writeString(this.isShowRealname);
        dest.writeString(this.token);
        dest.writeString(this.rongToken);
        dest.writeString(this.isFirstLogin);
        dest.writeString(this.isAuthentication);
        dest.writeString(this.onlineService);
        dest.writeString(this.openPhone);
        dest.writeString(this.inviteCode);
    }

    public LoginResponse() {
    }

    public LoginResponse(String id) {
        this.id = id;
    }

    protected LoginResponse(Parcel in) {
        this.id = in.readString();
        this.duoduoId = in.readString();
        this.nick = in.readString();
        this.realname = in.readString();
        this.mobile = in.readString();
        this.address = in.readString();
        this.email = in.readString();
        this.headPortrait = in.readString();
        this.sex = in.readString();
        this.signature = in.readString();
        this.isShowRealname = in.readString();
        this.token = in.readString();
        this.rongToken = in.readString();
        this.isFirstLogin = in.readString();
        this.isAuthentication = in.readString();
        this.onlineService = in.readString();
        this.openPhone = in.readString();
        this.inviteCode = in.readString();
    }

    public static final Parcelable.Creator<LoginResponse> CREATOR = new Parcelable.Creator<LoginResponse>() {
        @Override
        public LoginResponse createFromParcel(Parcel source) {
            return new LoginResponse(source);
        }

        @Override
        public LoginResponse[] newArray(int size) {
            return new LoginResponse[size];
        }
    };
}
