package com.zxjk.duoduo.ui.msgpage.rongIM.message;

import android.annotation.SuppressLint;
import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

@SuppressLint("ParcelCreator")
@MessageTag(value = "MLiveCardMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class WechatCastMessage extends MessageContent {
    private String title;
    private String content;
    private String icon;
    private String roomID;
    private String type; //0微信直播

    public WechatCastMessage(){}

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("title", getTitle());
            jsonObj.put("content", getContent());
            jsonObj.put("icon", getIcon());
            jsonObj.put("roomID", getRoomID());
            jsonObj.put("type", getType());
        } catch (JSONException e) {
        }
        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public WechatCastMessage(byte[] data) {
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

            if (jsonObj.has("roomID")) {
                setRoomID(jsonObj.optString("roomID"));
            }

            if (jsonObj.has("type")) {
                setType(jsonObj.optString("type"));
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
        ParcelUtils.writeToParcel(dest, title);
        ParcelUtils.writeToParcel(dest, content);
        ParcelUtils.writeToParcel(dest, icon);
        ParcelUtils.writeToParcel(dest, roomID);
        ParcelUtils.writeToParcel(dest, type);
    }

    public WechatCastMessage(Parcel in) {
        setTitle(ParcelUtils.readFromParcel(in));
        setContent(ParcelUtils.readFromParcel(in));
        setIcon(ParcelUtils.readFromParcel(in));
        setRoomID(ParcelUtils.readFromParcel(in));
        setType(ParcelUtils.readFromParcel(in));
    }

    public static final Creator<WechatCastMessage> CREATOR = new Creator<WechatCastMessage>() {

        @Override
        public WechatCastMessage createFromParcel(Parcel source) {
            return new WechatCastMessage(source);
        }

        @Override
        public WechatCastMessage[] newArray(int size) {
            return new WechatCastMessage[size];
        }
    };
}
