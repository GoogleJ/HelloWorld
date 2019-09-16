package com.zxjk.duoduo.bean.response;

public class GetInviteInfoResponse {


    /**
     * inviterCount : 0
     * mot : 0.00
     */

    private String inviterCount;
    private String mot;
    private String award;

    public String getInviterCount() {
        return inviterCount;
    }

    public void setInviterCount(String inviterCount) {
        this.inviterCount = inviterCount;
    }

    public String getMot() {
        return mot;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }
}