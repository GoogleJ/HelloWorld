package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CommunityInfoResponse implements Parcelable {

    /**
     * id : 10
     * groupId : 21
     * name : 我们要好好珍惜现在的生活状态的
     * logo : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg
     * bgi : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/4FA1FF20-DBD9-4E55-AB90-DC80D3969B59.jpg
     * code : 3148299
     * introduction : 简介。。。
     * introductionEditDate : 1574326587097
     * announcement : 公告。。。。
     * announcementEditDate : 1574326587097
     * status : 0
     * createTime : 1574149890291
     * members : [{"headPortrait":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/54CFA243-272B-43FF-80A1-8622F42316C9.jpg","identity":"2"}]
     * identity : 2
     * groupPermission : {"id":"","groupId":"","customerId":"","isDelete":"","createTime":"","updateTime":"","openBanned":"","nick":"","headPortrait":"","openAudio":"","openVideo":"","forceRecall":""}
     * ownerId : 4
     */

    private String id;
    private String groupId;
    private String name;
    private String logo;
    private String bgi;
    private String code;
    private String introduction;
    private String introductionEditDate;
    private String announcement;
    private String announcementEditDate;
    private String status;
    private String createTime;
    private String identity;
    private GroupPermissionBean groupPermission;
    private String ownerId;
    private List<MembersBean> members;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBgi() {
        return bgi;
    }

    public void setBgi(String bgi) {
        this.bgi = bgi;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIntroductionEditDate() {
        return introductionEditDate;
    }

    public void setIntroductionEditDate(String introductionEditDate) {
        this.introductionEditDate = introductionEditDate;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getAnnouncementEditDate() {
        return announcementEditDate;
    }

    public void setAnnouncementEditDate(String announcementEditDate) {
        this.announcementEditDate = announcementEditDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public GroupPermissionBean getGroupPermission() {
        return groupPermission;
    }

    public void setGroupPermission(GroupPermissionBean groupPermission) {
        this.groupPermission = groupPermission;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<MembersBean> getMembers() {
        return members;
    }

    public void setMembers(List<MembersBean> members) {
        this.members = members;
    }

    public static class GroupPermissionBean implements Parcelable {
        /**
         * id :
         * groupId :
         * customerId :
         * isDelete :
         * createTime :
         * updateTime :
         * openBanned :
         * nick :
         * headPortrait :
         * openAudio :
         * openVideo :
         * forceRecall :
         */

        private String id;
        private String groupId;
        private String customerId;
        private String isDelete;
        private String createTime;
        private String updateTime;
        private String openBanned;
        private String nick;
        private String headPortrait;
        private String openAudio;
        private String openVideo;
        private String forceRecall;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getOpenBanned() {
            return openBanned;
        }

        public void setOpenBanned(String openBanned) {
            this.openBanned = openBanned;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }

        public String getOpenAudio() {
            return openAudio;
        }

        public void setOpenAudio(String openAudio) {
            this.openAudio = openAudio;
        }

        public String getOpenVideo() {
            return openVideo;
        }

        public void setOpenVideo(String openVideo) {
            this.openVideo = openVideo;
        }

        public String getForceRecall() {
            return forceRecall;
        }

        public void setForceRecall(String forceRecall) {
            this.forceRecall = forceRecall;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.groupId);
            dest.writeString(this.customerId);
            dest.writeString(this.isDelete);
            dest.writeString(this.createTime);
            dest.writeString(this.updateTime);
            dest.writeString(this.openBanned);
            dest.writeString(this.nick);
            dest.writeString(this.headPortrait);
            dest.writeString(this.openAudio);
            dest.writeString(this.openVideo);
            dest.writeString(this.forceRecall);
        }

        public GroupPermissionBean() {
        }

        protected GroupPermissionBean(Parcel in) {
            this.id = in.readString();
            this.groupId = in.readString();
            this.customerId = in.readString();
            this.isDelete = in.readString();
            this.createTime = in.readString();
            this.updateTime = in.readString();
            this.openBanned = in.readString();
            this.nick = in.readString();
            this.headPortrait = in.readString();
            this.openAudio = in.readString();
            this.openVideo = in.readString();
            this.forceRecall = in.readString();
        }

        public static final Creator<GroupPermissionBean> CREATOR = new Creator<GroupPermissionBean>() {
            @Override
            public GroupPermissionBean createFromParcel(Parcel source) {
                return new GroupPermissionBean(source);
            }

            @Override
            public GroupPermissionBean[] newArray(int size) {
                return new GroupPermissionBean[size];
            }
        };
    }

    public static class MembersBean implements Parcelable {
        /**
         * headPortrait : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/54CFA243-272B-43FF-80A1-8622F42316C9.jpg
         * identity : 2
         */

        private String headPortrait;
        private String identity;

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.headPortrait);
            dest.writeString(this.identity);
        }

        public MembersBean() {
        }

        protected MembersBean(Parcel in) {
            this.headPortrait = in.readString();
            this.identity = in.readString();
        }

        public static final Creator<MembersBean> CREATOR = new Creator<MembersBean>() {
            @Override
            public MembersBean createFromParcel(Parcel source) {
                return new MembersBean(source);
            }

            @Override
            public MembersBean[] newArray(int size) {
                return new MembersBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.groupId);
        dest.writeString(this.name);
        dest.writeString(this.logo);
        dest.writeString(this.bgi);
        dest.writeString(this.code);
        dest.writeString(this.introduction);
        dest.writeString(this.introductionEditDate);
        dest.writeString(this.announcement);
        dest.writeString(this.announcementEditDate);
        dest.writeString(this.status);
        dest.writeString(this.createTime);
        dest.writeString(this.identity);
        dest.writeParcelable(this.groupPermission, flags);
        dest.writeString(this.ownerId);
        dest.writeList(this.members);
    }

    public CommunityInfoResponse() {
    }

    protected CommunityInfoResponse(Parcel in) {
        this.id = in.readString();
        this.groupId = in.readString();
        this.name = in.readString();
        this.logo = in.readString();
        this.bgi = in.readString();
        this.code = in.readString();
        this.introduction = in.readString();
        this.introductionEditDate = in.readString();
        this.announcement = in.readString();
        this.announcementEditDate = in.readString();
        this.status = in.readString();
        this.createTime = in.readString();
        this.identity = in.readString();
        this.groupPermission = in.readParcelable(GroupPermissionBean.class.getClassLoader());
        this.ownerId = in.readString();
        this.members = new ArrayList<MembersBean>();
        in.readList(this.members, MembersBean.class.getClassLoader());
    }

    public static final Creator<CommunityInfoResponse> CREATOR = new Creator<CommunityInfoResponse>() {
        @Override
        public CommunityInfoResponse createFromParcel(Parcel source) {
            return new CommunityInfoResponse(source);
        }

        @Override
        public CommunityInfoResponse[] newArray(int size) {
            return new CommunityInfoResponse[size];
        }
    };
}
