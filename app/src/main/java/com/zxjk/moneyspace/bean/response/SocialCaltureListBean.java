package com.zxjk.moneyspace.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class SocialCaltureListBean implements MultiItemEntity, Parcelable {

    private int itemType;

    public static final int TYPE_WEB = 1;
    public static final int TYPE_FILE = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_APP = 4;
    public static final int TYPE_ACTIVITY = 5;

    private EditListCommunityCultureResponse.OfficialWebsiteBean officialWebsite;

    private EditListCommunityCultureResponse.FilesBean files;

    private EditListCommunityCultureResponse.VideoBean video;

    private EditListCommunityCultureResponse.ApplicationBean application;

    private EditListCommunityCultureResponse.ActivitiesBean activities;

    public SocialCaltureListBean(int itemType) {
        this.itemType = itemType;
    }

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

    @Override
    public int getItemType() {
        return itemType;
    }

    public SocialCaltureListBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.itemType);
        dest.writeParcelable(this.officialWebsite, flags);
        dest.writeParcelable(this.files, flags);
        dest.writeParcelable(this.video, flags);
        dest.writeParcelable(this.application, flags);
        dest.writeParcelable(this.activities, flags);
    }

    protected SocialCaltureListBean(Parcel in) {
        this.itemType = in.readInt();
        this.officialWebsite = in.readParcelable(EditListCommunityCultureResponse.OfficialWebsiteBean.class.getClassLoader());
        this.files = in.readParcelable(EditListCommunityCultureResponse.FilesBean.class.getClassLoader());
        this.video = in.readParcelable(EditListCommunityCultureResponse.VideoBean.class.getClassLoader());
        this.application = in.readParcelable(EditListCommunityCultureResponse.ApplicationBean.class.getClassLoader());
        this.activities = in.readParcelable(EditListCommunityCultureResponse.ActivitiesBean.class.getClassLoader());
    }

    public static final Creator<SocialCaltureListBean> CREATOR = new Creator<SocialCaltureListBean>() {
        @Override
        public SocialCaltureListBean createFromParcel(Parcel source) {
            return new SocialCaltureListBean(source);
        }

        @Override
        public SocialCaltureListBean[] newArray(int size) {
            return new SocialCaltureListBean[size];
        }
    };
}
