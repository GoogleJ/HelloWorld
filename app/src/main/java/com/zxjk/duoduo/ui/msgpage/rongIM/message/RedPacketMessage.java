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
@MessageTag(value = "MRedPackageMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class RedPacketMessage extends MessageContent {
    /**
     * fromCustomer : 14
     * extra : 0:未领取 1:已领取
     * remark : 恭喜发财，大吉大利
     * redId : 123456
     */

    private String fromCustomer;
    private String remark;
    private String extra;
    private String redId;
    private String isGame;

    public RedPacketMessage() {

    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("fromCustomer", getFromCustomer());
            jsonObj.put("remark", getRemark());
            jsonObj.put("extra", getExtra());
            jsonObj.put("redId", getRedId());
            jsonObj.put("isGame", getIsGame());
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

    public RedPacketMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("fromCustomer")) {
                setFromCustomer(jsonObj.optString("fromCustomer"));
            }

            if (jsonObj.has("remark")) {
                setRemark(jsonObj.optString("remark"));
            }
            if (jsonObj.has("extra")) {
                setExtra(jsonObj.optString("extra"));
            }
            if (jsonObj.has("redId")) {
                setRedId(jsonObj.optString("redId"));
            }
            if (jsonObj.has("isGame")) {
                setIsGame(jsonObj.optString("isGame"));
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
        ParcelUtils.writeToParcel(dest, fromCustomer);
        ParcelUtils.writeToParcel(dest, remark);
        ParcelUtils.writeToParcel(dest, redId);
        ParcelUtils.writeToParcel(dest, extra);
        ParcelUtils.writeToParcel(dest, isGame);
    }

    //给消息赋值。
    public RedPacketMessage(Parcel in) {
        setFromCustomer(ParcelUtils.readFromParcel(in));
        setRemark(ParcelUtils.readFromParcel(in));
        setRedId(ParcelUtils.readFromParcel(in));
        setExtra(ParcelUtils.readFromParcel(in));
        setIsGame(ParcelUtils.readFromParcel(in));
    }
    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<RedPacketMessage> CREATOR = new Creator<RedPacketMessage>() {

        @Override
        public RedPacketMessage createFromParcel(Parcel source) {
            return new RedPacketMessage(source);
        }

        @Override
        public RedPacketMessage[] newArray(int size) {
            return new RedPacketMessage[size];
        }
    };



    public String getFromCustomer() {
        return fromCustomer;
    }

    public void setFromCustomer(String fromCustomer) {
        this.fromCustomer = fromCustomer;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getRedId() {
        return redId;
    }

    public void setRedId(String redId) {
        this.redId = redId;
    }

    public String getIsGame() {
        return isGame;
    }

    public void setIsGame(String isGame) {
        this.isGame = isGame;
    }
}
