package com.zxjk.moneyspace.utils;

import com.zxjk.moneyspace.ui.base.WebActivityToLogin;

public class WebDataUtils {
    private WebActivityToLogin webActivityToLogin;
    public WebDataUtils(){}

    public void setWebActivityToLogin(WebActivityToLogin webActivityToLogin){
        this.webActivityToLogin = webActivityToLogin;
    }

    public void webToLogin(String token){
        if(webActivityToLogin == null) return;
        webActivityToLogin.webToLogin(token);
    }
}
