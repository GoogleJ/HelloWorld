package com.zxjk.duoduo.bean.response;

import java.io.Serializable;

public class GetBetConutBygroupIdResponse implements Serializable {

    /**
     * money : 4.88
     * count : 2
     */

    private double money;
    private int count;

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
