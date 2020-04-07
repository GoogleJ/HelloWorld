package com.zxjk.moneyspace.bean.request;

public class PayPhoneRequest {
    private String token;
    private String id;

    public PayPhoneRequest(String token, String id) {
        this.token = token;
        this.id = id;
    }
}
