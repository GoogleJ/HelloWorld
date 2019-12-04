package com.zxjk.duoduo.bean.response;

import java.util.List;

public class CommunityVideoListResponse {

    /**
     * video : [{"videoId":"1","videoName":"官方视频","videoPic":"http://img0.imgtn.bdimg.com/it/u=4224381039,2887274293&fm=26&gp=0.jpg","videoDuration":"7200","videoAddress":"https://www.apple.com/105/media/us/iphone-x/2017/01df5b43-28e4-4848-bf20-490c34a926a7/films/feature/iphone-x-feature-tpl-cc-us-20170912_1280x720h.mp4"},{"videoId":"2","videoName":"官方视频2","videoPic":"http://img0.imgtn.bdimg.com/it/u=2723201663,3984544667&fm=26&gp=0.jpg","videoDuration":"8600","videoAddress":"https://www.apple.com/105/media/cn/mac/family/2018/46c4b917_abfd_45a3_9b51_4e3054191797/films/bruce/mac-bruce-tpl-cn-2018_1280x720h.mp4"},{"videoId":"3","videoName":"官方视频3","videoPic":"http://img4.imgtn.bdimg.com/it/u=3109530586,794669292&fm=26&gp=0.jpg","videoDuration":"7600","videoAddress":"https://www.apple.com/105/media/us/mac/family/2018/46c4b917_abfd_45a3_9b51_4e3054191797/films/peter/mac-peter-tpl-cc-us-2018_1280x720h.mp4"}]
     * videoCreate : 3
     */

    private String videoCreate;
    private List<VideoBean> video;

    public String getVideoCreate() {
        return videoCreate;
    }

    public void setVideoCreate(String videoCreate) {
        this.videoCreate = videoCreate;
    }

    public List<VideoBean> getVideo() {
        return video;
    }

    public void setVideo(List<VideoBean> video) {
        this.video = video;
    }

    public static class VideoBean {
        /**
         * videoId : 1
         * videoName : 官方视频
         * videoPic : http://img0.imgtn.bdimg.com/it/u=4224381039,2887274293&fm=26&gp=0.jpg
         * videoDuration : 7200
         * videoAddress : https://www.apple.com/105/media/us/iphone-x/2017/01df5b43-28e4-4848-bf20-490c34a926a7/films/feature/iphone-x-feature-tpl-cc-us-20170912_1280x720h.mp4
         */

        private String videoId;
        private String videoName;
        private String videoPic;
        private String videoDuration;
        private String videoAddress;
        private String createTime;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

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
    }
}
