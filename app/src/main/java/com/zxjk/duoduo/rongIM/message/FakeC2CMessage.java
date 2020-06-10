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
@MessageTag(value = "HL:FakeC2C", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class FakeC2CMessage extends MessageContent {
    private String extra;
    private String name;

    public FakeC2CMessage() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("extra", getExtra());
            jsonObj.put("name", getName());
        } catch (JSONException e) {
        }
        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FakeC2CMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("extra")) {
                setExtra(jsonObj.optString("extra"));
            }

            if (jsonObj.has("name")) {
                setName(jsonObj.optString("name"));
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
        ParcelUtils.writeToParcel(dest, extra);
        ParcelUtils.writeToParcel(dest, name);
    }

    public FakeC2CMessage(Parcel in) {
        setExtra(ParcelUtils.readFromParcel(in));
        setName(ParcelUtils.readFromParcel(in));
    }

    public static final Creator<FakeC2CMessage> CREATOR = new Creator<FakeC2CMessage>() {
        @Override
        public FakeC2CMessage createFromParcel(Parcel source) {
            return new FakeC2CMessage(source);
        }

        @Override
        public FakeC2CMessage[] newArray(int size) {
            return new FakeC2CMessage[size];
        }
    };
}
