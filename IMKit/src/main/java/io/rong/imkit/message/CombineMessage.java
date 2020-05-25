//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.message;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.UserInfo;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.message.MediaMessageContent;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@MessageTag(
        value = "RC:CombineMsg",
        flag = 3
)
public class CombineMessage extends MediaMessageContent {
  private static final String TAG = CombineMessage.class.getSimpleName();
  private String title;
  private ConversationType conversationType;
  private List<String> nameList;
  private List<String> summaryList;
  public static final Creator<CombineMessage> CREATOR = new Creator<CombineMessage>() {
    public CombineMessage createFromParcel(Parcel source) {
      return new CombineMessage(source);
    }

    public CombineMessage[] newArray(int size) {
      return new CombineMessage[size];
    }
  };

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ConversationType getConversationType() {
    return this.conversationType;
  }

  public void setConversationType(ConversationType conversationType) {
    this.conversationType = conversationType;
  }

  public List<String> getNameList() {
    return this.nameList;
  }

  public void setNameList(List<String> nameList) {
    this.nameList = nameList;
  }

  public List<String> getSummaryList() {
    return this.summaryList;
  }

  public void setSummaryList(List<String> summaryList) {
    this.summaryList = summaryList;
  }

  protected CombineMessage() {
    this.conversationType = ConversationType.PRIVATE;
    this.nameList = new ArrayList();
    this.summaryList = new ArrayList();
  }

  public static CombineMessage obtain(Uri url) {
    CombineMessage model = new CombineMessage();
    if (url.toString().startsWith("file")) {
      model.setLocalPath(url);
    } else {
      model.setMediaUrl(url);
    }

    return model;
  }

  public CombineMessage(byte[] data) {
    this.conversationType = ConversationType.PRIVATE;
    this.nameList = new ArrayList();
    this.summaryList = new ArrayList();
    String jsonStr = null;

    try {
      jsonStr = new String(data, "UTF-8");
    } catch (UnsupportedEncodingException var8) {
      RLog.e(TAG, "UnsupportedEncodingException", var8);
    }

    try {
      JSONObject jsonObj = new JSONObject(jsonStr);
      if (jsonObj.has("title")) {
        this.setTitle(jsonObj.optString("title"));
      }

      if (jsonObj.has("name")) {
        this.setName(jsonObj.optString("name"));
      }

      if (jsonObj.has("localPath")) {
        this.setLocalPath(Uri.parse(jsonObj.optString("localPath")));
      }

      if (jsonObj.has("remoteUrl")) {
        this.setMediaUrl(Uri.parse(jsonObj.optString("remoteUrl")));
      }

      if (jsonObj.has("extra")) {
        this.setExtra(jsonObj.optString("extra"));
      }

      if (jsonObj.has("user")) {
        this.setUserInfo(this.parseJsonToUserInfo(jsonObj.getJSONObject("user")));
      }

      this.setConversationType(ConversationType.setValue(jsonObj.optInt("conversationType")));
      JSONArray jsonArray = jsonObj.optJSONArray("nameList");
      List<String> nameList = new ArrayList();

      for(int i = 0; i < jsonArray.length(); ++i) {
        nameList.add((String)jsonArray.get(i));
      }

      this.setNameList(nameList);
      jsonArray = jsonObj.optJSONArray("summaryList");
      List<String> summaryList = new ArrayList();

      for(int i = 0; i < jsonArray.length(); ++i) {
        summaryList.add((String)jsonArray.get(i));
      }

      this.setSummaryList(summaryList);
    } catch (JSONException var9) {
      RLog.e(TAG, "JSONException " + var9.getMessage());
    }

  }

  public byte[] encode() {
    JSONObject jsonObj = new JSONObject();

    try {
      if (!TextUtils.isEmpty(this.getTitle())) {
        jsonObj.put("title", this.getTitle());
      }

      if (!TextUtils.isEmpty(this.getName())) {
        jsonObj.put("name", this.getName());
      }

      if (this.getLocalPath() != null) {
        jsonObj.put("localPath", this.getLocalPath().toString());
      }

      if (this.getMediaUrl() != null) {
        jsonObj.put("remoteUrl", this.getMediaUrl().toString());
      }

      if (!TextUtils.isEmpty(this.getExtra())) {
        jsonObj.put("extra", this.getExtra());
      }

      if (this.getJSONUserInfo() != null) {
        jsonObj.putOpt("user", this.getJSONUserInfo());
      }

      jsonObj.put("conversationType", this.conversationType.getValue());
      JSONArray jsonArray = new JSONArray();
      Iterator var3 = this.nameList.iterator();

      String summary;
      while(var3.hasNext()) {
        summary = (String)var3.next();
        jsonArray.put(summary);
      }

      jsonObj.put("nameList", jsonArray);
      jsonArray = new JSONArray();
      var3 = this.summaryList.iterator();

      while(var3.hasNext()) {
        summary = (String)var3.next();
        jsonArray.put(summary);
      }

      jsonObj.put("summaryList", jsonArray);
    } catch (JSONException var6) {
      RLog.e(TAG, "JSONException " + var6.getMessage());
    }

    try {
      return jsonObj.toString().getBytes("UTF-8");
    } catch (UnsupportedEncodingException var5) {
      RLog.e(TAG, "UnsupportedEncodingException", var5);
      return null;
    }
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    ParcelUtils.writeToParcel(dest, this.getExtra());
    ParcelUtils.writeToParcel(dest, this.getName());
    ParcelUtils.writeToParcel(dest, this.getLocalPath());
    ParcelUtils.writeToParcel(dest, this.getMediaUrl());
    ParcelUtils.writeToParcel(dest, this.getUserInfo());
    ParcelUtils.writeToParcel(dest, this.getConversationType().getValue());
    ParcelUtils.writeToParcel(dest, this.getNameList());
    ParcelUtils.writeToParcel(dest, this.getSummaryList());
  }

  public CombineMessage(Parcel in) {
    this.conversationType = ConversationType.PRIVATE;
    this.nameList = new ArrayList();
    this.summaryList = new ArrayList();
    this.setExtra(ParcelUtils.readFromParcel(in));
    this.setName(ParcelUtils.readFromParcel(in));
    this.setLocalPath((Uri)ParcelUtils.readFromParcel(in, Uri.class));
    this.setMediaUrl((Uri)ParcelUtils.readFromParcel(in, Uri.class));
    this.setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
    this.setConversationType(ConversationType.setValue(ParcelUtils.readIntFromParcel(in)));
    this.setNameList(ParcelUtils.readListFromParcel(in, String.class));
    this.setSummaryList(ParcelUtils.readListFromParcel(in, String.class));
  }
}
