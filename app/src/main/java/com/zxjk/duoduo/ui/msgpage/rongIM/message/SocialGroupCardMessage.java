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
@MessageTag(value = "M_CMGroupCardMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class SocialGroupCardMessage extends MessageContent {
    private String icon;
    private String groupName;
    private String memberNum;
    private String name;
    private String extra;
    private String inviterId;
    private String groupId;

    public SocialGroupCardMessage() {

    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("icon", getIcon());
            jsonObj.put("groupName", getGroupName());
            jsonObj.put("memberNum", getMemberNum());
            jsonObj.put("name", getName());
            jsonObj.put("extra", getExtra());
            jsonObj.put("inviterId", getInviterId());
            jsonObj.put("groupId", getGroupId());
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

    public SocialGroupCardMessage(byte[] data) {
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
            if (jsonObj.has("groupName")) {
                setGroupName(jsonObj.optString("groupName"));
            }
            if (jsonObj.has("memberNum")) {
                setMemberNum(jsonObj.optString("memberNum"));
            }
            if (jsonObj.has("name")) {
                setName(jsonObj.optString("name"));
            }
            if (jsonObj.has("extra")) {
                setExtra(jsonObj.optString("extra"));
            }
            if (jsonObj.has("inviterId")) {
                setInviterId(jsonObj.optString("inviterId"));
            }
            if (jsonObj.has("groupId")) {
                setGroupId(jsonObj.optString("groupId"));
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
        ParcelUtils.writeToParcel(dest, groupName);
        ParcelUtils.writeToParcel(dest, memberNum);
        ParcelUtils.writeToParcel(dest, name);
        ParcelUtils.writeToParcel(dest, extra);
        ParcelUtils.writeToParcel(dest, inviterId);
        ParcelUtils.writeToParcel(dest, groupId);
    }

    //给消息赋值。
    public SocialGroupCardMessage(Parcel in) {
        setIcon(ParcelUtils.readFromParcel(in));
        setGroupName(ParcelUtils.readFromParcel(in));
        setMemberNum(ParcelUtils.readFromParcel(in));
        setName(ParcelUtils.readFromParcel(in));
        setExtra(ParcelUtils.readFromParcel(in));
        setInviterId(ParcelUtils.readFromParcel(in));
        setGroupId(ParcelUtils.readFromParcel(in));
    }
    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<SocialGroupCardMessage> CREATOR = new Creator<SocialGroupCardMessage>() {

        @Override
        public SocialGroupCardMessage createFromParcel(Parcel source) {
            return new SocialGroupCardMessage(source);
        }

        @Override
        public SocialGroupCardMessage[] newArray(int size) {
            return new SocialGroupCardMessage[size];
        }
    };

    public String getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(String memberNum) {
        this.memberNum = memberNum;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getInviterId() {
        return inviterId;
    }

    public void setInviterId(String inviterId) {
        this.inviterId = inviterId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
