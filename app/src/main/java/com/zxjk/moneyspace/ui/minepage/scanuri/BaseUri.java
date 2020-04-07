package com.zxjk.moneyspace.ui.minepage.scanuri;

public class BaseUri<T> {

    public BaseUri() {

    }

    public BaseUri(String action) {
        this.action = action;
    }

    public String schem = "com.zxjk.moneyspace";
    public String action;
    public T data;
}
