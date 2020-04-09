package com.zxjk.moneyspace.bean.response;

public class GetAuthorizationTokenResponse {

    /**
     * hilamgId : 28545955
     * accessToken : e7JzMHZ3dvncJRduB0CUxQ==
     * expiresIn : 7200
     * refreshToken : Ajnb1NVrGC4Be+vxQ0BZGw==
     */

    private String hilamgId;
    private String accessToken;
    private String expiresIn;
    private String refreshToken;

    public String getHilamgId() {
        return hilamgId;
    }

    public void setHilamgId(String hilamgId) {
        this.hilamgId = hilamgId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
