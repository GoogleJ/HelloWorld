package com.zxjk.duoduo.ui.minepage.scanuri;

public class BaseUri<T> {

    public BaseUri() {

    }

    public BaseUri(String action) {
        this.action = action;
    }

    public String schem = "com.zxjk.duoduo";
    public String action;
    public T data;
}
