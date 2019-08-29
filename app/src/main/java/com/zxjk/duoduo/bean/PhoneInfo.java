package com.zxjk.duoduo.bean;

import java.util.Objects;

public class PhoneInfo {
    private String name;
    private String number;
    private boolean isAdd;


    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneInfo phoneInfo = (PhoneInfo) o;
        return Objects.equals(name, phoneInfo.name) &&
                Objects.equals(number, phoneInfo.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, number);
    }

    public PhoneInfo(String name, String number) {
        this.name = name;
        this.number = number;

    }
}
