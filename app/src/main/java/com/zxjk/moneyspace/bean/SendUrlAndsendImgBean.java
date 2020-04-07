package com.zxjk.moneyspace.bean;

import com.zxjk.moneyspace.bean.response.GroupResponse;

public class SendUrlAndsendImgBean {
    private String sendUrl;
    private String sendImg;
    private String sendVoice;

    public SendUrlAndsendImgBean(GroupResponse g) {
        sendUrl = g.getGroupInfo().getBanSendLink();
        sendImg = g.getGroupInfo().getBanSendPicture();
    }

    public String getSendVoice() {
        return sendVoice;
    }

    public void setSendVoice(String sendVoice) {
        this.sendVoice = sendVoice;
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
