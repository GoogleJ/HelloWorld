package com.zxjk.moneyspace.ui.msgpage.rongIM.message;

import android.annotation.SuppressLint;
import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

@SuppressLint("ParcelCreator")
@MessageTag(value = "MCusEmoteTabMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class CusEmoteTabMessage extends MessageContent {
    //内置表情，资源存在 客户端/服务器 直接根据id加载（后续废弃）
    private String id;
    //是否是gif 1:是 0:否 不传/为空:否
    private String isGif;
    //远程表情(预留)
    private String url;
    //预留
    private String extra;
    //图片宽高（预留）
    private int width;
    private int height;

    public CusEmoteTabMessage() {
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", getId());
            jsonObj.put("isGif", getIsGif());
            jsonObj.put("url", getUrl());
            jsonObj.put("extra", getExtra());
            jsonObj.put("width", getWidth());
            jsonObj.put("height", getHeight());
        } catch (JSONException e) {
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public CusEmoteTabMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("id")) {
                setId(jsonObj.optString("id"));
            }

            if (jsonObj.has("isGif")) {
                setIsGif(jsonObj.optString("isGif"));
            }

            if (jsonObj.has("url")) {
                setUrl(jsonObj.optString("url"));
            }

            if (jsonObj.has("extra")) {
                setExtra(jsonObj.optString("extra"));
            }

            if (jsonObj.has("width")) {
                setWidth(jsonObj.optInt("width"));
            }

            if (jsonObj.has("height")) {
                setHeight(jsonObj.optInt("height"));
            }
        } catch (JSONException e) {
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        ParcelUtils.writeToParcel(dest, id);
        ParcelUtils.writeToParcel(dest, isGif);
        ParcelUtils.writeToParcel(dest, url);
        ParcelUtils.writeToParcel(dest, extra);
        ParcelUtils.writeToParcel(dest, width);
        ParcelUtils.writeToParcel(dest, height);
    }

    //给消息赋值。
    public CusEmoteTabMessage(Parcel in) {
        //这里可继续增加你消息的属性
        setId(ParcelUtils.readFromParcel(in));
        setIsGif(ParcelUtils.readFromParcel(in));
        setUrl(ParcelUtils.readFromParcel(in));
        setExtra(ParcelUtils.readFromParcel(in));
        setWidth(ParcelUtils.readIntFromParcel(in));
        setHeight(ParcelUtils.readIntFromParcel(in));
    }

    public static final Creator<CusEmoteTabMessage> CREATOR = new Creator<CusEmoteTabMessage>() {
        @Override
        public CusEmoteTabMessage createFromParcel(Parcel source) {
            return new CusEmoteTabMessage(source);
        }

        @Override
        public CusEmoteTabMessage[] newArray(int size) {
            return new CusEmoteTabMessage[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getIsGif() {
        return isGif;
    }

    public void setIsGif(String isGif) {
        this.isGif = isGif;
    }
}
