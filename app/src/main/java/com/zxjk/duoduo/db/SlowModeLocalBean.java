package com.zxjk.duoduo.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SlowModeLocalBean {
    @Id(autoincrement = true)
    private Long id;
    private String groupId;
    private long lastMsgSentTime;
    @Generated(hash = 1243430333)
    public SlowModeLocalBean(Long id, String groupId, long lastMsgSentTime) {
        this.id = id;
        this.groupId = groupId;
        this.lastMsgSentTime = lastMsgSentTime;
    }
    @Generated(hash = 2092328860)
    public SlowModeLocalBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public long getLastMsgSentTime() {
        return this.lastMsgSentTime;
    }
    public void setLastMsgSentTime(long lastMsgSentTime) {
        this.lastMsgSentTime = lastMsgSentTime;
    }
}
