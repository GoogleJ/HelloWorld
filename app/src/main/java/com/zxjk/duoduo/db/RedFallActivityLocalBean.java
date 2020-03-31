package com.zxjk.duoduo.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RedFallActivityLocalBean  {
    @Id(autoincrement = true)
    private Long id;
    private String lastPlayTime;
    private String openShare;
    private int shareCount;
    private String reward;
    private String symbol;
    private int receiveCount;
    @Generated(hash = 2119002143)
    public RedFallActivityLocalBean(Long id, String lastPlayTime, String openShare,
            int shareCount, String reward, String symbol, int receiveCount) {
        this.id = id;
        this.lastPlayTime = lastPlayTime;
        this.openShare = openShare;
        this.shareCount = shareCount;
        this.reward = reward;
        this.symbol = symbol;
        this.receiveCount = receiveCount;
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
    public String getOpenShare() {
        return this.openShare;
    }
    public void setOpenShare(String openShare) {
        this.openShare = openShare;
    }
    public int getShareCount() {
        return this.shareCount;
    }
    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }
    public String getReward() {
        return this.reward;
    }
    public void setReward(String reward) {
        this.reward = reward;
    }
    public String getSymbol() {
        return this.symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public int getReceiveCount() {
        return this.receiveCount;
    }
    public void setReceiveCount(int receiveCount) {
        this.receiveCount = receiveCount;
    }


}
