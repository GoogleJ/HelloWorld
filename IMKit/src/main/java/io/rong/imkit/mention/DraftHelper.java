//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.mention;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DraftHelper {
  private static final String CONTENT = "content";
  private static final String MENTION = "mention";
  private String content;
  private List<MentionBlock> mentionBlocks;

  public DraftHelper(String s) {
    if (!TextUtils.isEmpty(s)) {
      try {
        JSONObject jsonObject = new JSONObject(s);
        this.content = jsonObject.getString("content");
        String mentionInfo = jsonObject.optString("mention");
        this.mentionBlocks = this.getMentionBlocks(mentionInfo);
      } catch (JSONException var4) {
        this.content = s;
      }
    }

  }

  public static String encode(String content, String mentionBlocks) {
    if (TextUtils.isEmpty(mentionBlocks)) {
      return content;
    } else {
      JSONObject jsonObject = new JSONObject();

      try {
        return jsonObject.putOpt("content", content).putOpt("mention", mentionBlocks).toString();
      } catch (JSONException var4) {
        return content;
      }
    }
  }

  public String decode() {
    return this.content;
  }

  public void restoreMentionInfo() {
    if (this.mentionBlocks != null) {
      Iterator var1 = this.mentionBlocks.iterator();

      while(var1.hasNext()) {
        MentionBlock mentionBlock = (MentionBlock)var1.next();
        RongMentionManager.getInstance().addMentionBlock(mentionBlock);
      }
    }

  }

  public static String getDraftContent(String json) {
    try {
      JSONObject jsonObject = new JSONObject(json);
      return jsonObject.getString("content");
    } catch (Exception var2) {
      return json;
    }
  }

  @Nullable
  private List<MentionBlock> getMentionBlocks(String mentionInfo) {
    try {
      JSONArray jsonArray = new JSONArray(mentionInfo);
      List<MentionBlock> list = new ArrayList();

      for(int i = 0; i < jsonArray.length(); ++i) {
        String s = jsonArray.getString(i);
        MentionBlock mentionBlock = new MentionBlock(s);
        list.add(mentionBlock);
      }

      return list;
    } catch (Exception var7) {
      return null;
    }
  }
}
