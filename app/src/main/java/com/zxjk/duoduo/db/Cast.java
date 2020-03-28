package com.zxjk.duoduo.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Cast {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String roomId;
    private long startTimeStamp;
    private String type;//1:wechat
    private String status;
    @Generated(hash = 2065490945)
    public Cast(Long id, String roomId, long startTimeStamp, String type,
            String status) {
        this.id = id;
        this.roomId = roomId;
        this.startTimeStamp = startTimeStamp;
        this.type = type;
        this.status = status;
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
    public String getRoomId() {
        return this.roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    public long getStartTimeStamp() {
        return this.startTimeStamp;
    }
    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
