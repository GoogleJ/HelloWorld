package com.zxjk.duoduo.utils;

import com.zxjk.duoduo.ui.base.WebActivityToLogin;

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
