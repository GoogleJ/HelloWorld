package com.zxjk.moneyspace.bean.response;

public class ReleaseRecord {

    /**
     * symbol : ETH
     * airdrops : 0
     * laveCount : 1.00110
     * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D
     * status : 进行中
     */

    private String symbol;
    private String airdrops;
    private String laveCount;
    private String logo;
    private String status;
    private String airdropId;

    public String getAirdropId() {
        return airdropId;
    }

    public void setAirdropId(String airdropId) {
        this.airdropId = airdropId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAirdrops() {
        return airdrops;
    }

    public void setAirdrops(String airdrops) {
        this.airdrops = airdrops;
    }

    public String getLaveCount() {
        return laveCount;
    }

    public void setLaveCount(String laveCount) {
        this.laveCount = laveCount;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
