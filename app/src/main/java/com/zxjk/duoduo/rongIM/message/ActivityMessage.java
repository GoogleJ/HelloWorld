package com.zxjk.duoduo.rongIM.message;

import android.annotation.SuppressLint;
import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

@SuppressLint("ParcelCreator")
@MessageTag(value = "HL:activity", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class ActivityMessage extends MessageContent {
    private String action;
    private String bannerUrl;
    private String title;
    private String content;
    private String detailUrl;
    private String describe;

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("action", getAction());
            jsonObj.put("bannerUrl", getBannerUrl());
            jsonObj.put("title", getTitle());
            jsonObj.put("content", getContent());
            jsonObj.put("detailUrl", getDetailUrl());
            jsonObj.put("describe", getDescribe());
        } catch (JSONException e) {
        }
        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ActivityMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("action")) {
                setAction(jsonObj.optString("action"));
            }

            if (jsonObj.has("bannerUrl")) {
                setBannerUrl(jsonObj.optString("bannerUrl"));
            }

            if (jsonObj.has("title")) {
                setTitle(jsonObj.optString("title"));
            }

            if (jsonObj.has("content")) {
                setContent(jsonObj.optString("content"));
            }

            if (jsonObj.has("detailUrl")) {
                setDetailUrl(jsonObj.optString("detailUrl"));
            }

            if (jsonObj.has("describe")) {
                setDescribe(jsonObj.optString("describe"));
            }

        } catch (JSONException e) {
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, action);
        ParcelUtils.writeToParcel(dest, bannerUrl);
        ParcelUtils.writeToParcel(dest, title);
        ParcelUtils.writeToParcel(dest, content);
        ParcelUtils.writeToParcel(dest, detailUrl);
        ParcelUtils.writeToParcel(dest, describe);
    }

    public ActivityMessage(Parcel in) {
        setAction(ParcelUtils.readFromParcel(in));
        setBannerUrl(ParcelUtils.readFromParcel(in));
        setTitle(ParcelUtils.readFromParcel(in));
        setContent(ParcelUtils.readFromParcel(in));
        setDetailUrl(ParcelUtils.readFromParcel(in));
        setDescribe(ParcelUtils.readFromParcel(in));
    }

    public static final Creator<ActivityMessage> CREATOR = new Creator<ActivityMessage>() {
        @Override
        public ActivityMessage createFromParcel(Parcel source) {
            return new ActivityMessage(source);
        }

        @Override
        public ActivityMessage[] newArray(int size) {
            return new ActivityMessage[size];
        }
    };

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public ActivityMessage() {
    }
}
