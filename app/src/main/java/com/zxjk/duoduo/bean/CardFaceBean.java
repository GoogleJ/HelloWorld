package com.zxjk.duoduo.bean;


public class CardFaceBean {

    /**
     * address :
     * birth :
     * config_str :
     * face_rect :
     * face_rect_vertices :
     * name :
     * nationality :
     * num :
     * request_id :
     * sex :
     * success :
     */

    private String address;
    private String birth;
    private String name;
    private String nationality;
    private String num;
    private String sex;
    private boolean success;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
