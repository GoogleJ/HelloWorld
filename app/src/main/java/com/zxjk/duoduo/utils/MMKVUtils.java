package com.zxjk.duoduo.utils;

import com.tencent.mmkv.MMKV;
import com.zxjk.duoduo.bean.response.LoginResponse;

public class MMKVUtils {
    private static MMKVUtils mmkvUtils;
    private MMKV mmkv;

    private MMKVUtils() {
        mmkv = MMKV.defaultMMKV();
    }

    public static MMKVUtils getInstance() {
        if (mmkvUtils == null) {
            synchronized (MMKVUtils.class) {
                if (mmkvUtils == null) {
                    mmkvUtils = new MMKVUtils();
                }
            }
        }
        return mmkvUtils;
    }

    public void enCode(String key, long value) {
        mmkv.encode(key, value);
    }

    public void enCode(String key, String value) {
        mmkv.encode(key, value);
    }

    public void enCode(String key, boolean value) {
        mmkv.encode(key, value);
    }

    public void enCode(String key, LoginResponse value) {
        mmkv.encode(key, value);
    }

    public long decodeLong(String key) {
        return mmkv.decodeLong(key, 0);
    }

    public String decodeString(String key) {
        return mmkv.decodeString(key, null);
    }

    public Boolean decodeBool(String key) {
        return mmkv.decodeBool(key, false);
    }

    public Boolean decodeBool(String key, boolean defaultValue) {
        return mmkv.decodeBool(key, defaultValue);
    }

    public LoginResponse decodeParcelable(String key) {
        return mmkv.decodeParcelable(key, LoginResponse.class, null);
    }

}