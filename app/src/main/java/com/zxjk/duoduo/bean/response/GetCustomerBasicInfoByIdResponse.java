package com.zxjk.duoduo.bean.response;

public class GetCustomerBasicInfoByIdResponse {

    /**
     * nick : 贺欢欢
     * id : 2
     * headPortrait : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/4qnihrkux4nk4cqw3wq5.jpg
     */

    private String nick;
    private int id;
    private String headPortrait;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }
}
