package com.zxjk.duoduo.bean;

import java.io.Serializable;

/**
 * 此包是关于某些工具类的实体
 * 这个是关于国家代码选择的实体
 */
public class CountryEntity implements Serializable {
    public String countryName;
    public String countryCode;
    public String pinyin;
    public String sortKey;

    public CountryEntity() {
    }
}
