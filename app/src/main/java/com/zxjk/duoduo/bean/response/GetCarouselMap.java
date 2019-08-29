package com.zxjk.duoduo.bean.response;

public class GetCarouselMap {

    /**
     * id : 1
     * imgName : 测试1
     * imgUrl : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/6497DE17-9B15-4B14-B829-7066A96FFC63.jpg
     * createTime : 1556342133727
     * linkUrl :
     * isDelete : 0
     */

    private String id;
    private String imgName;
    private String imgUrl;
    private String createTime;
    private String linkUrl;
    private String isDelete;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }
}
