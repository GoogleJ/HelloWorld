//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.mention;

import org.json.JSONException;
import org.json.JSONObject;

public class MentionBlock {
  public String userId;
  public String name;
  public boolean offset;
  public int start;
  public int end;

  public JSONObject toJson() {
    JSONObject jsonObject = new JSONObject();

    try {
      return jsonObject.putOpt("userId", this.userId).putOpt("name", this.name).putOpt("offset", this.offset).putOpt("start", this.start).putOpt("end", this.end);
    } catch (JSONException var3) {
      return null;
    }
  }

  public String toString() {
    JSONObject jsonObject = new JSONObject();

    try {
      return jsonObject.putOpt("userId", this.userId).putOpt("name", this.name).putOpt("offset", this.offset).putOpt("start", this.start).putOpt("end", this.end).toString();
    } catch (JSONException var3) {
      return super.toString();
    }
  }

  MentionBlock() {
  }

  MentionBlock(String json) {
    try {
      JSONObject jsonObject = new JSONObject(json);
      this.userId = jsonObject.optString("userId");
      this.name = jsonObject.optString("name");
      this.offset = jsonObject.optBoolean("offset");
      this.start = jsonObject.optInt("start");
      this.end = jsonObject.optInt("end");
    } catch (JSONException var3) {
      var3.printStackTrace();
    }

  }
}
