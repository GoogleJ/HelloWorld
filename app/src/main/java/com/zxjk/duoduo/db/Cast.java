package com.zxjk.duoduo.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Cast {
    @Id(autoincrement = true)
    private Long id;
    private String castId;
    private long timeStamp;
    private String timeStr;
    private String type;//1:wechat
    private String wechatCastStatus;

    @Generated(hash = 683141846)
    public Cast(Long id, String castId, long timeStamp, String timeStr, String type,
            String wechatCastStatus) {
        this.id = id;
        this.castId = castId;
        this.timeStamp = timeStamp;
        this.timeStr = timeStr;
        this.type = type;
        this.wechatCastStatus = wechatCastStatus;
    }
    @Generated(hash = 971498007)
    public Cast() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCastId() {
        return this.castId;
    }
    public void setCastId(String castId) {
        this.castId = castId;
    }
    public long getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getTimeStr() {
        return this.timeStr;
    }
    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getWechatCastStatus() {
        return this.wechatCastStatus;
    }
    public void setWechatCastStatus(String wechatCastStatus) {
        this.wechatCastStatus = wechatCastStatus;
    }
}
