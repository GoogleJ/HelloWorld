package com.zxjk.duoduo.bean;

public class ConversationTimeBean {
    private long dayTimeMills = 0;
    private long totalMills = 0;
    private boolean hasComplete;

    public boolean isHasComplete() {
        return hasComplete;
    }

    public void setHasComplete(boolean hasComplete) {
        this.hasComplete = hasComplete;
    }

    public long getDayTimeMills() {
        return dayTimeMills;
    }

    public void setDayTimeMills(long dayTimeMills) {
        this.dayTimeMills = dayTimeMills;
    }

    public long getTotalMills() {
        return totalMills;
    }

    public void setTotalMills(long totalMills) {
        this.totalMills = totalMills;
    }
}
