package com.zxjk.duoduo.bean.request;

import java.util.List;

public class EditCommunityVideoRequest {

    /**
     * groupId : 65
     * type : add
     * videoList : [{"videoName":"videoName","videoPic":"videoPic","videoAddress":"videoAddress","videoDuration":"videoDuration"},{"videoName":"videoName1","videoPic":"videoPic1","videoAddress":"videoAddress1","videoDuration":"videoDuration1"}]
     */
    private String groupId;
    private String type;
    private List<VideoListBean> videoList;

    /**
     * videoName : videoName
     * videoId : 1
     * videoPic : videoPic
     * videoDuration : videoDuration
     * videoAddress : videoAddress
     */
    private String videoName;
    private String videoId;
    private String videoPic;
    private String videoDuration;
    private String videoAddress;

    /**
     * videoOpen : 1
     */
    private String videoOpen;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<VideoListBean> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<VideoListBean> videoList) {
        this.videoList = videoList;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoAddress() {
        return videoAddress;
    }

    public void setVideoAddress(String videoAddress) {
        this.videoAddress = videoAddress;
    }

    public String getVideoOpen() {
        return videoOpen;
    }

    public void setVideoOpen(String videoOpen) {
        this.videoOpen = videoOpen;
    }

    public static class VideoListBean {
        /**
         * videoName : videoName
         * videoPic : videoPic
         * videoAddress : videoAddress
         * videoDuration : videoDuration
         */

        private String videoName;
        private String videoPic;
        private String videoAddress;
        private String videoDuration;

        public String getVideoName() {
            return videoName;
        }

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public String getVideoPic() {
            return videoPic;
        }

        public void setVideoPic(String videoPic) {
            this.videoPic = videoPic;
        }

        public String getVideoAddress() {
            return videoAddress;
        }

        public void setVideoAddress(String videoAddress) {
            this.videoAddress = videoAddress;
        }

        public String getVideoDuration() {
            return videoDuration;
        }

        public void setVideoDuration(String videoDuration) {
            this.videoDuration = videoDuration;
        }
    }
}
