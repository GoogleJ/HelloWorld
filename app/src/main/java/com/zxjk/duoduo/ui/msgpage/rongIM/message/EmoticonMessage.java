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
@MessageTag(value = "app:emoticon", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class EmoticonMessage extends MessageContent {
    private String icon;
    private String iconId;
    private String iconText;
    private String width;
    private String height;
    //0:不是gif 1:是gif
    private String isAnimated;
    private String extra;

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("icon", getIcon());
            jsonObj.put("iconId", getIconId());
            jsonObj.put("iconText", getIconText());
            jsonObj.put("width", getWidth());
            jsonObj.put("height", getHeight());
            jsonObj.put("isAnimated", getIsAnimated());
            jsonObj.put("extra", getExtra());
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

    public EmoticonMessage(byte[] data) {
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

            if (jsonObj.has("iconId")) {
                setIconId(jsonObj.optString("iconId"));
            }

            if (jsonObj.has("iconText")) {
                setIconText(jsonObj.optString("iconText"));
            }

            if (jsonObj.has("width")) {
                setWidth(jsonObj.optString("width"));
            }

            if (jsonObj.has("height")) {
                setHeight(jsonObj.optString("height"));
            }

            if (jsonObj.has("isAnimated")) {
                setIsAnimated(jsonObj.optString("isAnimated"));
            }

            if (jsonObj.has("extra")) {
                setExtra(jsonObj.optString("extra"));
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
        ParcelUtils.writeToParcel(dest, iconId);
        ParcelUtils.writeToParcel(dest, iconText);
        ParcelUtils.writeToParcel(dest, width);
        ParcelUtils.writeToParcel(dest, height);
        ParcelUtils.writeToParcel(dest, isAnimated);
        ParcelUtils.writeToParcel(dest, extra);
    }

    //给消息赋值。
    public EmoticonMessage(Parcel in) {
        //这里可继续增加你消息的属性
        setIcon(ParcelUtils.readFromParcel(in));
        setIconId(ParcelUtils.readFromParcel(in));
        setIconText(ParcelUtils.readFromParcel(in));
        setWidth(ParcelUtils.readFromParcel(in));
        setHeight(ParcelUtils.readFromParcel(in));
        setIsAnimated(ParcelUtils.readFromParcel(in));
        setExtra(ParcelUtils.readFromParcel(in));
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<EmoticonMessage> CREATOR = new Creator<EmoticonMessage>() {

        @Override
        public EmoticonMessage createFromParcel(Parcel source) {
            return new EmoticonMessage(source);
        }

        @Override
        public EmoticonMessage[] newArray(int size) {
            return new EmoticonMessage[size];
        }
    };

    public EmoticonMessage() {
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getIconText() {
        return iconText;
    }

    public void setIconText(String iconText) {
        this.iconText = iconText;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIsAnimated() {
        return isAnimated;
    }

    public void setIsAnimated(String isAnimated) {
        this.isAnimated = isAnimated;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
