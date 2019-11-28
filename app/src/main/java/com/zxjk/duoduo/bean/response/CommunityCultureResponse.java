package com.zxjk.duoduo.bean.response;

public class CommunityCultureResponse {

    /**
     * pay : {"paySymbol":"USDT","payFee":"0.001","payLogo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571020793185.png?Expires=1886380789&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=40XcvkX5AftUJF8CLBtDrMWPlJE%3D"}
     * type : pay
     */
    private String type;

    //pay social
    private PayBean pay;

    //social calutre
    private EditListCommunityCultureResponse.OfficialWebsiteBean officialWebsite;
    private EditListCommunityCultureResponse.FilesBean files;
    private EditListCommunityCultureResponse.VideoBean video;
    private EditListCommunityCultureResponse.ApplicationBean application;
    private EditListCommunityCultureResponse.ActivitiesBean activities;

    public EditListCommunityCultureResponse.OfficialWebsiteBean getOfficialWebsite() {
        return officialWebsite;
    }

    public void setOfficialWebsite(EditListCommunityCultureResponse.OfficialWebsiteBean officialWebsite) {
        this.officialWebsite = officialWebsite;
    }

    public EditListCommunityCultureResponse.FilesBean getFiles() {
        return files;
    }

    public void setFiles(EditListCommunityCultureResponse.FilesBean files) {
        this.files = files;
    }

    public EditListCommunityCultureResponse.VideoBean getVideo() {
        return video;
    }

    public void setVideo(EditListCommunityCultureResponse.VideoBean video) {
        this.video = video;
    }

    public EditListCommunityCultureResponse.ApplicationBean getApplication() {
        return application;
    }

    public void setApplication(EditListCommunityCultureResponse.ApplicationBean application) {
        this.application = application;
    }

    public EditListCommunityCultureResponse.ActivitiesBean getActivities() {
        return activities;
    }

    public void setActivities(EditListCommunityCultureResponse.ActivitiesBean activities) {
        this.activities = activities;
    }

    public PayBean getPay() {
        return pay;
    }

    public void setPay(PayBean pay) {
        this.pay = pay;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class PayBean {
        /**
         * paySymbol : USDT
         * payFee : 0.001
         * payLogo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571020793185.png?Expires=1886380789&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=40XcvkX5AftUJF8CLBtDrMWPlJE%3D
         */

        private String paySymbol;
        private String payFee;
        private String payLogo;

        public String getPaySymbol() {
            return paySymbol;
        }

        public void setPaySymbol(String paySymbol) {
            this.paySymbol = paySymbol;
        }

        public String getPayFee() {
            return payFee;
        }

        public void setPayFee(String payFee) {
            this.payFee = payFee;
        }

        public String getPayLogo() {
            return payLogo;
        }

        public void setPayLogo(String payLogo) {
            this.payLogo = payLogo;
        }
    }
}
