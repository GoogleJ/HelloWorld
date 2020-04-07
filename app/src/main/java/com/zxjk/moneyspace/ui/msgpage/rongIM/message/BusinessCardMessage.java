package com.zxjk.moneyspace.ui.msgpage.rongIM.message;

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
@MessageTag(value = "MMyCardMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class BusinessCardMessage extends MessageContent {
    private String icon;
    private String name;
    private String duoduo;
    private String extra;
    private String userId;
    private String senderName;
    private String senderId;

    public BusinessCardMessage() {
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("icon", getIcon());
            jsonObj.put("name", getName());
            jsonObj.put("duoduo", getDuoduo());
            jsonObj.put("extra", getExtra());
            jsonObj.put("userId", getUserId());
            jsonObj.put("senderName", getSenderName());
            jsonObj.put("senderId", getSenderId());
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

    public BusinessCardMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("icon")) {
                setIcon(jsonObj.optString("icon"));
            }

            if (jsonObj.has("name")) {
                setName(jsonObj.optString("name"));
            }

            if (jsonObj.has("duoduo")) {
                setDuoduo(jsonObj.optString("duoduo"));
            }

            if (jsonObj.has("extra")) {
                setExtra(jsonObj.optString("extra"));
            }

            if (jsonObj.has("userId")) {
                setUserId(jsonObj.optString("userId"));
            }

            if (jsonObj.has("senderName")) {
                setSenderName(jsonObj.optString("senderName"));
            }

            if (jsonObj.has("senderId")) {
                setSenderId(jsonObj.optString("senderId"));
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
        ParcelUtils.writeToParcel(dest, icon);
        ParcelUtils.writeToParcel(dest, name);
        ParcelUtils.writeToParcel(dest, duoduo);
        ParcelUtils.writeToParcel(dest, extra);
        ParcelUtils.writeToParcel(dest, userId);
        ParcelUtils.writeToParcel(dest, senderName);
        ParcelUtils.writeToParcel(dest, senderId);
    }

    //给消息赋值。
    public BusinessCardMessage(Parcel in) {
        setIcon(ParcelUtils.readFromParcel(in));
        //这里可继续增加你消息的属性
        setName(ParcelUtils.readFromParcel(in));
        setDuoduo(ParcelUtils.readFromParcel(in));
        setExtra(ParcelUtils.readFromParcel(in));
        setUserId(ParcelUtils.readFromParcel(in));
        setSenderName(ParcelUtils.readFromParcel(in));
        setSenderId(ParcelUtils.readFromParcel(in));
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<BusinessCardMessage> CREATOR = new Creator<BusinessCardMessage>() {

        @Override
        public BusinessCardMessage createFromParcel(Parcel source) {
            return new BusinessCardMessage(source);
        }

        @Override
        public BusinessCardMessage[] newArray(int size) {
            return new BusinessCardMessage[size];
        }
    };

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuoduo() {
        return duoduo;
    }

    public void setDuoduo(String duoduo) {
        this.duoduo = duoduo;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
