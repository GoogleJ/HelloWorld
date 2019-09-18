package com.zxjk.duoduo.bean;

import com.zxjk.duoduo.bean.response.GroupResponse;

public class SendUrlAndsendImgBean {
    private String sendUrl;
    private String sendImg;

    public SendUrlAndsendImgBean(GroupResponse g) {
        sendUrl = g.getGroupInfo().getBanSendLink();
        sendImg = g.getGroupInfo().getBanSendPicture();
    }

    public String getSendUrl() {
        return sendUrl;
    }

    public void setSendUrl(String sendUrl) {
        this.sendUrl = sendUrl;
    }

    public String getSendImg() {
        return sendImg;
    }

    public void setSendImg(String sendImg) {
        this.sendImg = sendImg;
    }
}
