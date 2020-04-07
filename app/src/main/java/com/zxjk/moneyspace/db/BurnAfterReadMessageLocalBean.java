package com.zxjk.moneyspace.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BurnAfterReadMessageLocalBean {
    @Id(autoincrement = true)
    private Long id;
    private String conversationId;
    private String conversationType;
    private int messageId;
    private long burnTime;

    @Generated(hash = 495011232)
    public BurnAfterReadMessageLocalBean(Long id, String conversationId,
            String conversationType, int messageId, long burnTime) {
        this.id = id;
        this.conversationId = conversationId;
        this.conversationType = conversationType;
        this.messageId = messageId;
        this.burnTime = burnTime;
    }
    @Generated(hash = 1113356916)
    public BurnAfterReadMessageLocalBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getConversationId() {
        return this.conversationId;
    }
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    public String getConversationType() {
        return this.conversationType;
    }
    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }
    public int getMessageId() {
        return this.messageId;
    }
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    public long getBurnTime() {
        return this.burnTime;
    }
    public void setBurnTime(long burnTime) {
        this.burnTime = burnTime;
    }

}
