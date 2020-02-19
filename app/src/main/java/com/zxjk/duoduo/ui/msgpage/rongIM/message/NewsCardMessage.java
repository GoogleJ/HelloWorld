package com.zxjk.duoduo.ui.msgpage.rongIM.message;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

@SuppressLint("ParcelCreator")
@MessageTag(value = "MNewsCardMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class NewsCardMessage extends MessageContent {

    private String title;
    private String content;
    private String icon;
    private String url;
    private String platform;



    public NewsCardMessage(){}

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }


    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("title", getTitle());
            jsonObj.put("content", getContent());
            jsonObj.put("icon", getIcon());
            jsonObj.put("url", getUrl());
            jsonObj.put("platform", getPlatform());
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public NewsCardMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("title")) {
                setTitle(jsonObj.optString("title"));
            }

            if (jsonObj.has("content")) {
                setContent(jsonObj.optString("content"));
            }

            if (jsonObj.has("icon")) {
                setIcon(jsonObj.optString("icon"));
            }

            if (jsonObj.has("url")) {
                setUrl(jsonObj.optString("url"));
            }

            if (jsonObj.has("platform")) {
                setPlatform(jsonObj.optString("platform"));
            }

        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, title);
        ParcelUtils.writeToParcel(dest, content);
        ParcelUtils.writeToParcel(dest, icon);
        ParcelUtils.writeToParcel(dest, url);
        ParcelUtils.writeToParcel(dest, platform);
    }



    public NewsCardMessage(Parcel in) {
        setTitle(ParcelUtils.readFromParcel(in));
        setContent(ParcelUtils.readFromParcel(in));
        setIcon(ParcelUtils.readFromParcel(in));
        setUrl(ParcelUtils.readFromParcel(in));
        setPlatform(ParcelUtils.readFromParcel(in));
    }


    public static final Creator<NewsCardMessage> CREATOR = new Creator<NewsCardMessage>() {

        @Override
        public NewsCardMessage createFromParcel(Parcel source) {
            return new NewsCardMessage(source);
        }

        @Override
        public NewsCardMessage[] newArray(int size) {
            return new NewsCardMessage[size];
        }
    };

}