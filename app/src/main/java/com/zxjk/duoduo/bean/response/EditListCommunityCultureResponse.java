package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class EditListCommunityCultureResponse implements Parcelable {

    /**
     * officialWebsite : {"title":"社群官网","officialWebsiteList":[{"websiteId":"1","websiteTitle":"百度官网","websiteContent":"全球最大的中文搜索引擎及最大的中文网站，全球领先的人工智能公司。百度愿景是：成为最懂用户，并能帮助人们成长的全球顶级高科技公司。 [1] \r\n\u201c百度\u201d二字，来自于八百年前南宋词人辛弃疾的一句词：众里寻他千百度。这句话描述了词人对理想的执着追求。1999年底，身在美国硅谷的李彦宏看到了中国互联网及中文搜索引擎服务的巨大发展潜力，抱着技术改变世界的梦想，他毅然辞掉硅谷的高薪工作，携搜索引擎专利技术，于 2000年1月1日在中关村创建了百度公司。","websiteUrl":"https://www.baidu.com/"}],"officialWebsiteOpen":"1"}
     * files : {"title":"社群资料","filesList":[{"fileId":"1","fileFormat":"pdf","fileName":"官方文档","fileAddress":"xxx"},{"fileId":"2","fileFormat":"ppt","fileName":"官方ppt","fileAddress":"xxx"},{"fileId":"3","fileFormat":"excel","fileName":"官方excel","fileAddress":"xxx"},{"fileId":"4","fileFormat":"word","fileName":"官方world文档","fileAddress":"xxx"}],"filesOpen":"1"}
     * video : {"title":"社群视频","videoList":[{"videoId":"1","videoName":"官方视频","videoPic":"http://img0.imgtn.bdimg.com/it/u=4224381039,2887274293&fm=26&gp=0.jpg","videoDuration":"7200","videoAddress":"xxx"},{"videoId":"2","videoName":"官方视频2","videoPic":"http://img0.imgtn.bdimg.com/it/u=2723201663,3984544667&fm=26&gp=0.jpg","videoDuration":"8600","videoAddress":"xxx"},{"videoId":"3","videoName":"官方视频3","videoPic":"http://img4.imgtn.bdimg.com/it/u=3109530586,794669292&fm=26&gp=0.jpg","videoDuration":"7600","videoAddress":"xxx"}],"videoOpen":"1"}
     * application : {"title":"社群应用","applicationList":[{"applicationId":"1","applicationName":"百度","applicationLogo":"http://img5.imgtn.bdimg.com/it/u=3092254507,1605408710&fm=26&gp=0.jpg","applicationAddress":"https://www.baidu.com/"},{"applicationId":"2","applicationName":"京东","applicationLogo":"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1388647553,511134846&fm=26&gp=0.jpg","applicationAddress":"https://www.jd.com/"},{"applicationId":"3","applicationName":"网易云音乐","applicationLogo":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575186121&di=6c0bb35bf9a312f0232bbddfe71a9403&imgtype=jpg&er=1&src=http%3A%2F%2Fimg1.gtimg.com%2Ftech%2Fpics%2Fhv1%2F56%2F157%2F1275%2F82946966.png","applicationAddress":"https://music.163.com/"},{"applicationId":"4","applicationName":"QQ音乐","applicationLogo":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574660733692&di=a50b609d71b0800e9d7feb9ecad44bda&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F031009157fcf46da84a0d304f579195.jpg","applicationAddress":"https://y.qq.com/"},{"applicationId":"5","applicationName":"keep","applicationLogo":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574660772646&di=4f610f972c96e5f17b3965290d5da1e6&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170308%2F99ef8eba95354f468a75eacdc1aa3f28_th.jpeg","applicationAddress":"https://www.gotokeep.com/"}],"applicationOpen":"1"}
     * activities : {"title":"社群活动","activityList":[{"activityId":"1","activityName":"周末爬山","activityAddress":"广东省深圳市xx山","activityPic":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574588595323&di=b147d4eab42f4d6b96f8a592e61f3aef&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn21%2F400%2Fw750h450%2F20180724%2Fb93e-hftenhz8804542.jpg","activityStartDate":"1574837637000","activityEndDate":"1575096837000","activityStatus":"0"}],"activityOpen":"0"}
     */

    private OfficialWebsiteBean officialWebsite;
    private FilesBean files;
    private VideoBean video;
    private ApplicationBean application;
    private ActivitiesBean activities;

    public OfficialWebsiteBean getOfficialWebsite() {
        return officialWebsite;
    }

    public void setOfficialWebsite(OfficialWebsiteBean officialWebsite) {
        this.officialWebsite = officialWebsite;
    }

    public FilesBean getFiles() {
        return files;
    }

    public void setFiles(FilesBean files) {
        this.files = files;
    }

    public VideoBean getVideo() {
        return video;
    }

    public void setVideo(VideoBean video) {
        this.video = video;
    }

    public ApplicationBean getApplication() {
        return application;
    }

    public void setApplication(ApplicationBean application) {
        this.application = application;
    }

    public ActivitiesBean getActivities() {
        return activities;
    }

    public void setActivities(ActivitiesBean activities) {
        this.activities = activities;
    }

    public static class OfficialWebsiteBean implements Parcelable {
        /**
         * title : 社群官网
         * officialWebsiteList : [{"websiteId":"1","websiteTitle":"百度官网","websiteContent":"全球最大的中文搜索引擎及最大的中文网站，全球领先的人工智能公司。百度愿景是：成为最懂用户，并能帮助人们成长的全球顶级高科技公司。 [1] \r\n\u201c百度\u201d二字，来自于八百年前南宋词人辛弃疾的一句词：众里寻他千百度。这句话描述了词人对理想的执着追求。1999年底，身在美国硅谷的李彦宏看到了中国互联网及中文搜索引擎服务的巨大发展潜力，抱着技术改变世界的梦想，他毅然辞掉硅谷的高薪工作，携搜索引擎专利技术，于 2000年1月1日在中关村创建了百度公司。","websiteUrl":"https://www.baidu.com/"}]
         * officialWebsiteOpen : 1
         */

        private String title;
        private String officialWebsiteOpen;
        private List<OfficialWebsiteListBean> officialWebsiteList;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOfficialWebsiteOpen() {
            return officialWebsiteOpen;
        }

        public void setOfficialWebsiteOpen(String officialWebsiteOpen) {
            this.officialWebsiteOpen = officialWebsiteOpen;
        }

        public List<OfficialWebsiteListBean> getOfficialWebsiteList() {
            return officialWebsiteList;
        }

        public void setOfficialWebsiteList(List<OfficialWebsiteListBean> officialWebsiteList) {
            this.officialWebsiteList = officialWebsiteList;
        }

        public static class OfficialWebsiteListBean implements Parcelable {
            /**
             * websiteId : 1
             * websiteTitle : 百度官网
             * websiteContent : 全球最大的中文搜索引擎及最大的中文网站，全球领先的人工智能公司。百度愿景是：成为最懂用户，并能帮助人们成长的全球顶级高科技公司。 [1]
             “百度”二字，来自于八百年前南宋词人辛弃疾的一句词：众里寻他千百度。这句话描述了词人对理想的执着追求。1999年底，身在美国硅谷的李彦宏看到了中国互联网及中文搜索引擎服务的巨大发展潜力，抱着技术改变世界的梦想，他毅然辞掉硅谷的高薪工作，携搜索引擎专利技术，于 2000年1月1日在中关村创建了百度公司。
             * websiteUrl : https://www.baidu.com/
             */

            private String websiteId;
            private String websiteTitle;
            private String websiteContent;
            private String websiteUrl;

            public String getWebsiteId() {
                return websiteId;
            }

            public void setWebsiteId(String websiteId) {
                this.websiteId = websiteId;
            }

            public String getWebsiteTitle() {
                return websiteTitle;
            }

            public void setWebsiteTitle(String websiteTitle) {
                this.websiteTitle = websiteTitle;
            }

            public String getWebsiteContent() {
                return websiteContent;
            }

            public void setWebsiteContent(String websiteContent) {
                this.websiteContent = websiteContent;
            }

            public String getWebsiteUrl() {
                return websiteUrl;
            }

            public void setWebsiteUrl(String websiteUrl) {
                this.websiteUrl = websiteUrl;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.websiteId);
                dest.writeString(this.websiteTitle);
                dest.writeString(this.websiteContent);
                dest.writeString(this.websiteUrl);
            }

            public OfficialWebsiteListBean() {
            }

            protected OfficialWebsiteListBean(Parcel in) {
                this.websiteId = in.readString();
                this.websiteTitle = in.readString();
                this.websiteContent = in.readString();
                this.websiteUrl = in.readString();
            }

            public static final Creator<OfficialWebsiteListBean> CREATOR = new Creator<OfficialWebsiteListBean>() {
                @Override
                public OfficialWebsiteListBean createFromParcel(Parcel source) {
                    return new OfficialWebsiteListBean(source);
                }

                @Override
                public OfficialWebsiteListBean[] newArray(int size) {
                    return new OfficialWebsiteListBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.officialWebsiteOpen);
            dest.writeList(this.officialWebsiteList);
        }

        public OfficialWebsiteBean() {
        }

        protected OfficialWebsiteBean(Parcel in) {
            this.title = in.readString();
            this.officialWebsiteOpen = in.readString();
            this.officialWebsiteList = new ArrayList<OfficialWebsiteListBean>();
            in.readList(this.officialWebsiteList, OfficialWebsiteListBean.class.getClassLoader());
        }

        public static final Creator<OfficialWebsiteBean> CREATOR = new Creator<OfficialWebsiteBean>() {
            @Override
            public OfficialWebsiteBean createFromParcel(Parcel source) {
                return new OfficialWebsiteBean(source);
            }

            @Override
            public OfficialWebsiteBean[] newArray(int size) {
                return new OfficialWebsiteBean[size];
            }
        };
    }

    public static class FilesBean implements Parcelable {
        /**
         * title : 社群资料
         * filesList : [{"fileId":"1","fileFormat":"pdf","fileName":"官方文档","fileAddress":"xxx"},{"fileId":"2","fileFormat":"ppt","fileName":"官方ppt","fileAddress":"xxx"},{"fileId":"3","fileFormat":"excel","fileName":"官方excel","fileAddress":"xxx"},{"fileId":"4","fileFormat":"word","fileName":"官方world文档","fileAddress":"xxx"}]
         * filesOpen : 1
         */

        private String title;
        private String filesOpen;
        private String fileCreate;
        private List<FilesListBean> filesList;

        public String getFileCreate() {
            return fileCreate;
        }

        public void setFileCreate(String fileCreate) {
            this.fileCreate = fileCreate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFilesOpen() {
            return filesOpen;
        }

        public void setFilesOpen(String filesOpen) {
            this.filesOpen = filesOpen;
        }

        public List<FilesListBean> getFilesList() {
            return filesList;
        }

        public void setFilesList(List<FilesListBean> filesList) {
            this.filesList = filesList;
        }

        public static class FilesListBean implements Parcelable {
            /**
             * fileId : 1
             * fileFormat : pdf
             * fileName : 官方文档
             * fileAddress : xxx
             */

            private String fileId;
            private String createTime;
            private String fileFormat;
            private String fileName;
            private String fileAddress;
            private String fileSize;

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getFileSize() {
                return fileSize;
            }

            public void setFileSize(String fileSize) {
                this.fileSize = fileSize;
            }

            public String getFileId() {
                return fileId;
            }

            public void setFileId(String fileId) {
                this.fileId = fileId;
            }

            public String getFileFormat() {
                return fileFormat;
            }

            public void setFileFormat(String fileFormat) {
                this.fileFormat = fileFormat;
            }

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            public String getFileAddress() {
                return fileAddress;
            }

            public void setFileAddress(String fileAddress) {
                this.fileAddress = fileAddress;
            }

            public FilesListBean() {
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.fileId);
                dest.writeString(this.createTime);
                dest.writeString(this.fileFormat);
                dest.writeString(this.fileName);
                dest.writeString(this.fileAddress);
                dest.writeString(this.fileSize);
            }

            protected FilesListBean(Parcel in) {
                this.fileId = in.readString();
                this.createTime = in.readString();
                this.fileFormat = in.readString();
                this.fileName = in.readString();
                this.fileAddress = in.readString();
                this.fileSize = in.readString();
            }

            public static final Creator<FilesListBean> CREATOR = new Creator<FilesListBean>() {
                @Override
                public FilesListBean createFromParcel(Parcel source) {
                    return new FilesListBean(source);
                }

                @Override
                public FilesListBean[] newArray(int size) {
                    return new FilesListBean[size];
                }
            };
        }

        public FilesBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.filesOpen);
            dest.writeString(this.fileCreate);
            dest.writeTypedList(this.filesList);
        }

        protected FilesBean(Parcel in) {
            this.title = in.readString();
            this.filesOpen = in.readString();
            this.fileCreate = in.readString();
            this.filesList = in.createTypedArrayList(FilesListBean.CREATOR);
        }

        public static final Creator<FilesBean> CREATOR = new Creator<FilesBean>() {
            @Override
            public FilesBean createFromParcel(Parcel source) {
                return new FilesBean(source);
            }

            @Override
            public FilesBean[] newArray(int size) {
                return new FilesBean[size];
            }
        };
    }

    public static class VideoBean implements Parcelable {
        /**
         * title : 社群视频
         * videoList : [{"videoId":"1","videoName":"官方视频","videoPic":"http://img0.imgtn.bdimg.com/it/u=4224381039,2887274293&fm=26&gp=0.jpg","videoDuration":"7200","videoAddress":"xxx"},{"videoId":"2","videoName":"官方视频2","videoPic":"http://img0.imgtn.bdimg.com/it/u=2723201663,3984544667&fm=26&gp=0.jpg","videoDuration":"8600","videoAddress":"xxx"},{"videoId":"3","videoName":"官方视频3","videoPic":"http://img4.imgtn.bdimg.com/it/u=3109530586,794669292&fm=26&gp=0.jpg","videoDuration":"7600","videoAddress":"xxx"}]
         * videoOpen : 1
         */

        private String title;
        private String videoOpen;
        private String videoCreate;
        private List<VideoListBean> videoList;

        public String getVideoCreate() {
            return videoCreate;
        }

        public void setVideoCreate(String videoCreate) {
            this.videoCreate = videoCreate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVideoOpen() {
            return videoOpen;
        }

        public void setVideoOpen(String videoOpen) {
            this.videoOpen = videoOpen;
        }

        public List<VideoListBean> getVideoList() {
            return videoList;
        }

        public void setVideoList(List<VideoListBean> videoList) {
            this.videoList = videoList;
        }

        public static class VideoListBean implements Parcelable {
            /**
             * videoId : 1
             * videoName : 官方视频
             * videoPic : http://img0.imgtn.bdimg.com/it/u=4224381039,2887274293&fm=26&gp=0.jpg
             * videoDuration : 7200
             * videoAddress : xxx
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

            public VideoListBean() {
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.videoId);
                dest.writeString(this.videoName);
                dest.writeString(this.videoPic);
                dest.writeString(this.videoDuration);
                dest.writeString(this.videoAddress);
                dest.writeString(this.createTime);
            }

            protected VideoListBean(Parcel in) {
                this.videoId = in.readString();
                this.videoName = in.readString();
                this.videoPic = in.readString();
                this.videoDuration = in.readString();
                this.videoAddress = in.readString();
                this.createTime = in.readString();
            }

            public static final Creator<VideoListBean> CREATOR = new Creator<VideoListBean>() {
                @Override
                public VideoListBean createFromParcel(Parcel source) {
                    return new VideoListBean(source);
                }

                @Override
                public VideoListBean[] newArray(int size) {
                    return new VideoListBean[size];
                }
            };
        }

        public VideoBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.videoOpen);
            dest.writeString(this.videoCreate);
            dest.writeTypedList(this.videoList);
        }

        protected VideoBean(Parcel in) {
            this.title = in.readString();
            this.videoOpen = in.readString();
            this.videoCreate = in.readString();
            this.videoList = in.createTypedArrayList(VideoListBean.CREATOR);
        }

        public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
            @Override
            public VideoBean createFromParcel(Parcel source) {
                return new VideoBean(source);
            }

            @Override
            public VideoBean[] newArray(int size) {
                return new VideoBean[size];
            }
        };
    }

    public static class ApplicationBean implements Parcelable {
        /**
         * title : 社群应用
         * applicationList : [{"applicationId":"1","applicationName":"百度","applicationLogo":"http://img5.imgtn.bdimg.com/it/u=3092254507,1605408710&fm=26&gp=0.jpg","applicationAddress":"https://www.baidu.com/"},{"applicationId":"2","applicationName":"京东","applicationLogo":"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1388647553,511134846&fm=26&gp=0.jpg","applicationAddress":"https://www.jd.com/"},{"applicationId":"3","applicationName":"网易云音乐","applicationLogo":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575186121&di=6c0bb35bf9a312f0232bbddfe71a9403&imgtype=jpg&er=1&src=http%3A%2F%2Fimg1.gtimg.com%2Ftech%2Fpics%2Fhv1%2F56%2F157%2F1275%2F82946966.png","applicationAddress":"https://music.163.com/"},{"applicationId":"4","applicationName":"QQ音乐","applicationLogo":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574660733692&di=a50b609d71b0800e9d7feb9ecad44bda&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F031009157fcf46da84a0d304f579195.jpg","applicationAddress":"https://y.qq.com/"},{"applicationId":"5","applicationName":"keep","applicationLogo":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574660772646&di=4f610f972c96e5f17b3965290d5da1e6&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170308%2F99ef8eba95354f468a75eacdc1aa3f28_th.jpeg","applicationAddress":"https://www.gotokeep.com/"}]
         * applicationOpen : 1
         */

        private String title;
        private String applicationOpen;
        private List<ApplicationListBean> applicationList;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getApplicationOpen() {
            return applicationOpen;
        }

        public void setApplicationOpen(String applicationOpen) {
            this.applicationOpen = applicationOpen;
        }

        public List<ApplicationListBean> getApplicationList() {
            return applicationList;
        }

        public void setApplicationList(List<ApplicationListBean> applicationList) {
            this.applicationList = applicationList;
        }

        public static class ApplicationListBean implements Parcelable {
            /**
             * applicationId : 1
             * applicationName : 百度
             * applicationLogo : http://img5.imgtn.bdimg.com/it/u=3092254507,1605408710&fm=26&gp=0.jpg
             * applicationAddress : https://www.baidu.com/
             */

            private String applicationId;
            private String applicationName;
            private String applicationLogo;
            private String applicationAddress;
            private String isOpen = "1";
            private String isOffical;

            public String getIsOffical() {
                return isOffical;
            }

            public void setIsOffical(String isOffical) {
                this.isOffical = isOffical;
            }

            public String getIsOpen() {
                return isOpen;
            }

            public void setIsOpen(String isOpen) {
                this.isOpen = isOpen;
            }

            public String getApplicationId() {
                return applicationId;
            }

            public void setApplicationId(String applicationId) {
                this.applicationId = applicationId;
            }

            public String getApplicationName() {
                return applicationName;
            }

            public void setApplicationName(String applicationName) {
                this.applicationName = applicationName;
            }

            public String getApplicationLogo() {
                return applicationLogo;
            }

            public void setApplicationLogo(String applicationLogo) {
                this.applicationLogo = applicationLogo;
            }

            public String getApplicationAddress() {
                return applicationAddress;
            }

            public void setApplicationAddress(String applicationAddress) {
                this.applicationAddress = applicationAddress;
            }

            public ApplicationListBean() {
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.applicationId);
                dest.writeString(this.applicationName);
                dest.writeString(this.applicationLogo);
                dest.writeString(this.applicationAddress);
                dest.writeString(this.isOpen);
                dest.writeString(this.isOffical);
            }

            protected ApplicationListBean(Parcel in) {
                this.applicationId = in.readString();
                this.applicationName = in.readString();
                this.applicationLogo = in.readString();
                this.applicationAddress = in.readString();
                this.isOpen = in.readString();
                this.isOffical = in.readString();
            }

            public static final Creator<ApplicationListBean> CREATOR = new Creator<ApplicationListBean>() {
                @Override
                public ApplicationListBean createFromParcel(Parcel source) {
                    return new ApplicationListBean(source);
                }

                @Override
                public ApplicationListBean[] newArray(int size) {
                    return new ApplicationListBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.applicationOpen);
            dest.writeList(this.applicationList);
        }

        public ApplicationBean() {
        }

        protected ApplicationBean(Parcel in) {
            this.title = in.readString();
            this.applicationOpen = in.readString();
            this.applicationList = new ArrayList<ApplicationListBean>();
            in.readList(this.applicationList, ApplicationListBean.class.getClassLoader());
        }

        public static final Creator<ApplicationBean> CREATOR = new Creator<ApplicationBean>() {
            @Override
            public ApplicationBean createFromParcel(Parcel source) {
                return new ApplicationBean(source);
            }

            @Override
            public ApplicationBean[] newArray(int size) {
                return new ApplicationBean[size];
            }
        };
    }

    public static class ActivitiesBean implements Parcelable {
        /**
         * title : 社群活动
         * activityList : [{"activityId":"1","activityName":"周末爬山","activityAddress":"广东省深圳市xx山","activityPic":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574588595323&di=b147d4eab42f4d6b96f8a592e61f3aef&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn21%2F400%2Fw750h450%2F20180724%2Fb93e-hftenhz8804542.jpg","activityStartDate":"1574837637000","activityEndDate":"1575096837000","activityStatus":"0"}]
         * activityOpen : 0
         */

        private String title;
        private String activityOpen;
        private List<ActivityListBean> activityList;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getActivityOpen() {
            return activityOpen;
        }

        public void setActivityOpen(String activityOpen) {
            this.activityOpen = activityOpen;
        }

        public List<ActivityListBean> getActivityList() {
            return activityList;
        }

        public void setActivityList(List<ActivityListBean> activityList) {
            this.activityList = activityList;
        }

        public static class ActivityListBean implements Parcelable {
            /**
             * activityId : 1
             * activityName : 周末爬山
             * activityAddress : 广东省深圳市xx山
             * activityPic : https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574588595323&di=b147d4eab42f4d6b96f8a592e61f3aef&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn21%2F400%2Fw750h450%2F20180724%2Fb93e-hftenhz8804542.jpg
             * activityStartDate : 1574837637000
             * activityEndDate : 1575096837000
             * activityStatus : 0
             */

            private String activityId;
            private String activityName;
            private String activityAddress;
            private String activityPic;
            private String activityStartDate;
            private String activityEndDate;
            private String activityStatus;

            public String getActivityId() {
                return activityId;
            }

            public void setActivityId(String activityId) {
                this.activityId = activityId;
            }

            public String getActivityName() {
                return activityName;
            }

            public void setActivityName(String activityName) {
                this.activityName = activityName;
            }

            public String getActivityAddress() {
                return activityAddress;
            }

            public void setActivityAddress(String activityAddress) {
                this.activityAddress = activityAddress;
            }

            public String getActivityPic() {
                return activityPic;
            }

            public void setActivityPic(String activityPic) {
                this.activityPic = activityPic;
            }

            public String getActivityStartDate() {
                return activityStartDate;
            }

            public void setActivityStartDate(String activityStartDate) {
                this.activityStartDate = activityStartDate;
            }

            public String getActivityEndDate() {
                return activityEndDate;
            }

            public void setActivityEndDate(String activityEndDate) {
                this.activityEndDate = activityEndDate;
            }

            public String getActivityStatus() {
                return activityStatus;
            }

            public void setActivityStatus(String activityStatus) {
                this.activityStatus = activityStatus;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.activityId);
                dest.writeString(this.activityName);
                dest.writeString(this.activityAddress);
                dest.writeString(this.activityPic);
                dest.writeString(this.activityStartDate);
                dest.writeString(this.activityEndDate);
                dest.writeString(this.activityStatus);
            }

            public ActivityListBean() {
            }

            protected ActivityListBean(Parcel in) {
                this.activityId = in.readString();
                this.activityName = in.readString();
                this.activityAddress = in.readString();
                this.activityPic = in.readString();
                this.activityStartDate = in.readString();
                this.activityEndDate = in.readString();
                this.activityStatus = in.readString();
            }

            public static final Creator<ActivityListBean> CREATOR = new Creator<ActivityListBean>() {
                @Override
                public ActivityListBean createFromParcel(Parcel source) {
                    return new ActivityListBean(source);
                }

                @Override
                public ActivityListBean[] newArray(int size) {
                    return new ActivityListBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.activityOpen);
            dest.writeList(this.activityList);
        }

        public ActivitiesBean() {
        }

        protected ActivitiesBean(Parcel in) {
            this.title = in.readString();
            this.activityOpen = in.readString();
            this.activityList = new ArrayList<ActivityListBean>();
            in.readList(this.activityList, ActivityListBean.class.getClassLoader());
        }

        public static final Creator<ActivitiesBean> CREATOR = new Creator<ActivitiesBean>() {
            @Override
            public ActivitiesBean createFromParcel(Parcel source) {
                return new ActivitiesBean(source);
            }

            @Override
            public ActivitiesBean[] newArray(int size) {
                return new ActivitiesBean[size];
            }
        };
    }

    public EditListCommunityCultureResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.officialWebsite, flags);
        dest.writeParcelable(this.files, flags);
        dest.writeParcelable(this.video, flags);
        dest.writeParcelable(this.application, flags);
        dest.writeParcelable(this.activities, flags);
    }

    protected EditListCommunityCultureResponse(Parcel in) {
        this.officialWebsite = in.readParcelable(OfficialWebsiteBean.class.getClassLoader());
        this.files = in.readParcelable(FilesBean.class.getClassLoader());
        this.video = in.readParcelable(VideoBean.class.getClassLoader());
        this.application = in.readParcelable(ApplicationBean.class.getClassLoader());
        this.activities = in.readParcelable(ActivitiesBean.class.getClassLoader());
    }

    public static final Creator<EditListCommunityCultureResponse> CREATOR = new Creator<EditListCommunityCultureResponse>() {
        @Override
        public EditListCommunityCultureResponse createFromParcel(Parcel source) {
            return new EditListCommunityCultureResponse(source);
        }

        @Override
        public EditListCommunityCultureResponse[] newArray(int size) {
            return new EditListCommunityCultureResponse[size];
        }
    };
}
