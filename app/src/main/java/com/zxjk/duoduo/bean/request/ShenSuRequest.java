package com.zxjk.duoduo.bean.request;

public class ShenSuRequest {

    /**
     * plaintiffId : 5
     * plaintiffDuoduoId : 10000005
     * indicteeId : 4
     * indicteeDuoduoId : 10000004
     * bothOrderId : 15518738012021000000510000004
     * appealReason : 4
     * currency : 1
     * pictureOne : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg
     * pictureTwo : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg
     * pictureThree : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg
     * contact : 17792249637
     */
    private String appealType;
    private String plaintiffId;
    private String plaintiffDuoduoId;
    private String indicteeId;
    private String indicteeDuoduoId;
    private String bothOrderId;
    private String appealReason;
    private String currency;
    private String pictureOne;
    private String pictureTwo;
    private String pictureThree;
    private String contact;

    public String getAppealType() {
        return appealType;
    }

    public void setAppealType(String appealType) {
        this.appealType = appealType;
    }

    public String getPlaintiffId() {
        return plaintiffId;
    }

    public void setPlaintiffId(String plaintiffId) {
        this.plaintiffId = plaintiffId;
    }

    public String getPlaintiffDuoduoId() {
        return plaintiffDuoduoId;
    }

    public void setPlaintiffDuoduoId(String plaintiffDuoduoId) {
        this.plaintiffDuoduoId = plaintiffDuoduoId;
    }

    public String getIndicteeId() {
        return indicteeId;
    }

    public void setIndicteeId(String indicteeId) {
        this.indicteeId = indicteeId;
    }

    public String getIndicteeDuoduoId() {
        return indicteeDuoduoId;
    }

    public void setIndicteeDuoduoId(String indicteeDuoduoId) {
        this.indicteeDuoduoId = indicteeDuoduoId;
    }

    public String getBothOrderId() {
        return bothOrderId;
    }

    public void setBothOrderId(String bothOrderId) {
        this.bothOrderId = bothOrderId;
    }

    public String getAppealReason() {
        return appealReason;
    }

    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPictureOne() {
        return pictureOne;
    }

    public void setPictureOne(String pictureOne) {
        this.pictureOne = pictureOne;
    }

    public String getPictureTwo() {
        return pictureTwo;
    }

    public void setPictureTwo(String pictureTwo) {
        this.pictureTwo = pictureTwo;
    }

    public String getPictureThree() {
        return pictureThree;
    }

    public void setPictureThree(String pictureThree) {
        this.pictureThree = pictureThree;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
