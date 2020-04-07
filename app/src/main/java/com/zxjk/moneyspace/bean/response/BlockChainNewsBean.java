package com.zxjk.moneyspace.bean.response;

public class BlockChainNewsBean {

    /**
     * id : 475
     * author : 比推BitpushNews
     * authorAvatar : https://img.jinse.com/1673885_image20.png
     * title : 美国国税局将发布加密货币税务法律指导
     * article :
     * articleSource : 比推BitpushNews
     * articleHtml :
     * articleUrl : https://m.jinse.com/blockchain/424850.html
     * newsTime : 2019-07-29 09:14:53
     * platformUrl : https://www.jinse.com/
     * platform : 金色财经
     * platformIco : https://resource.jinse.com/www/v3/img/logo.svg?v=1869
     * source : spider
     * thumPic : https://img.jinse.com/2130075_small.png
     * tag : 政策法规
     * createTime : 1564734114198
     */

    private String id;
    private String author;
    private String authorAvatar;
    private String title;
    private String article;
    private String articleSource;
    private String articleHtml;
    private String articleUrl;
    private String newsTime;
    private String platformUrl;
    private String platform;
    private String platformIco;
    private String source;
    private String thumPic;
    private String tag;
    private String createTime;
    private String htmlUrl;

    private String disLike = "0";
    private String like = "0";
    private String dislikeCount = "0";
    private String likeCount = "0";

    private boolean showAll;

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public String getDisLike() {
        return disLike;
    }

    public void setDisLike(String disLike) {
        this.disLike = disLike;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(String dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getArticleSource() {
        return articleSource;
    }

    public void setArticleSource(String articleSource) {
        this.articleSource = articleSource;
    }

    public String getArticleHtml() {
        return articleHtml;
    }

    public void setArticleHtml(String articleHtml) {
        this.articleHtml = articleHtml;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public String getPlatformUrl() {
        return platformUrl;
    }

    public void setPlatformUrl(String platformUrl) {
        this.platformUrl = platformUrl;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformIco() {
        return platformIco;
    }

    public void setPlatformIco(String platformIco) {
        this.platformIco = platformIco;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getThumPic() {
        return thumPic;
    }

    public void setThumPic(String thumPic) {
        this.thumPic = thumPic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
