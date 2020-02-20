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
@MessageTag(value = "HL:system", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class SystemMessage extends MessageContent {
    //1：跳转web链接
    private String action;
    //跳转链接
    private String url;
    //内容
    private String content;
    //标题
    private String title;
    //日期
    private String date;
    //是否显示底部详情，1显示 0不显示
    private String showDetail;

    public SystemMessage() {
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("action", getAction());
            jsonObj.put("url", getUrl());
            jsonObj.put("content", getContent());
            jsonObj.put("title", getTitle());
            jsonObj.put("date", getDate());
            jsonObj.put("showDetail", getShowDetail());
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

    public SystemMessage(byte[] data) {
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

            if (jsonObj.has("url")) {
                setUrl(jsonObj.optString("url"));
            }

            if (jsonObj.has("content")) {
                setContent(jsonObj.optString("content"));
            }

            if (jsonObj.has("title")) {
                setTitle(jsonObj.optString("title"));
            }

            if (jsonObj.has("date")) {
                setDate(jsonObj.optString("date"));
            }

            if (jsonObj.has("showDetail")) {
                setShowDetail(jsonObj.optString("showDetail"));
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
        ParcelUtils.writeToParcel(dest, action);
        ParcelUtils.writeToParcel(dest, url);
        ParcelUtils.writeToParcel(dest, content);
        ParcelUtils.writeToParcel(dest, title);
        ParcelUtils.writeToParcel(dest, date);
        ParcelUtils.writeToParcel(dest, showDetail);
    }

    public SystemMessage(Parcel in) {
        setAction(ParcelUtils.readFromParcel(in));
        setUrl(ParcelUtils.readFromParcel(in));
        setContent(ParcelUtils.readFromParcel(in));
        setTitle(ParcelUtils.readFromParcel(in));
        setDate(ParcelUtils.readFromParcel(in));
        setShowDetail(ParcelUtils.readFromParcel(in));
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<SystemMessage> CREATOR = new Creator<SystemMessage>() {

        @Override
        public SystemMessage createFromParcel(Parcel source) {
            return new SystemMessage(source);
        }

        @Override
        public SystemMessage[] newArray(int size) {
            return new SystemMessage[size];
        }
    };

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShowDetail() {
        return showDetail;
    }

    public void setShowDetail(String showDetail) {
        this.showDetail = showDetail;
    }
}
