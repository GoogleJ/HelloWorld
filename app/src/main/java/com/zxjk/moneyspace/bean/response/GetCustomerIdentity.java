package com.zxjk.moneyspace.bean.response;

import java.io.Serializable;

public class GetCustomerIdentity implements Serializable {

    /**
     * identity : 0
     * maxSaleNum : 1.0
     * maxBuyNum : 1.0
     */

    private String identity;
    private double maxSaleNum;
    private double maxBuyNum;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public double getMaxSaleNum() {
        return maxSaleNum;
    }

    public void setMaxSaleNum(double maxSaleNum) {
        this.maxSaleNum = maxSaleNum;
    }

    public double getMaxBuyNum() {
        return maxBuyNum;
    }

    public void setMaxBuyNum(double maxBuyNum) {
        this.maxBuyNum = maxBuyNum;
    }
}
