package com.zxjk.duoduo.bean.response;

public class GetRedPackageStatusResponse {

    /**
     * usernick : 中兴极客14
     * redPackageState : 2
     * headPortrait : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg
     * message : 恭喜发财，大吉大利
     * type : 1
     * redPackageType : 1
     */

    private String usernick;
    private String redPackageState;
    private String headPortrait;
    private String message;
    private int type;
    private int redPackageType;

    public String getUsernick() {
        return usernick;
    }

    public void setUsernick(String usernick) {
        this.usernick = usernick;
    }

    public String getRedPackageState() {
        return redPackageState;
    }

    public void setRedPackageState(String redPackageState) {
        this.redPackageState = redPackageState;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRedPackageType() {
        return redPackageType;
    }

    public void setRedPackageType(int redPackageType) {
        this.redPackageType = redPackageType;
    }
}
