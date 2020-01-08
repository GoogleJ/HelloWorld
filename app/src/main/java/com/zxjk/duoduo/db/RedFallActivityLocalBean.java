package com.zxjk.duoduo.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RedFallActivityLocalBean {
    @Id(autoincrement = true)
    private Long id;
    private String lastPlayTime;
    @Generated(hash = 469689930)
    public RedFallActivityLocalBean(Long id, String lastPlayTime) {
        this.id = id;
        this.lastPlayTime = lastPlayTime;
    }
    @Generated(hash = 986676735)
    public RedFallActivityLocalBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getLastPlayTime() {
        return this.lastPlayTime;
    }
    public void setLastPlayTime(String lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }
}
