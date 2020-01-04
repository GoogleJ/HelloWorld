package com.zxjk.duoduo.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class SocialLocalBean {
    @Id
    private String groupId;
    private String contentLastModifyTime;
    @Generated(hash = 682677373)
    public SocialLocalBean(String groupId, String contentLastModifyTime) {
        this.groupId = groupId;
        this.contentLastModifyTime = contentLastModifyTime;
    }
    @Generated(hash = 2111900349)
    public SocialLocalBean() {
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getContentLastModifyTime() {
        return this.contentLastModifyTime;
    }
    public void setContentLastModifyTime(String contentLastModifyTime) {
        this.contentLastModifyTime = contentLastModifyTime;
    }
}
