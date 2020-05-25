//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.net.Uri;
import io.rong.common.RLog;
import io.rong.imkit.R.drawable;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;

@ConversationProviderTag(
        conversationType = "encrypted",
        portraitPosition = 1
)
public class EncryptedConversationProvider extends PrivateConversationProvider {
  private static final String TAG = EncryptedConversationProvider.class.getSimpleName();

  public EncryptedConversationProvider() {
  }

  public String getTitle(String targetId) {
    String userId = null;
    String[] str = targetId.split(";;;");
    if (str.length >= 2) {
      userId = str[1];
    }

    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
    if (userInfo == null) {
      RLog.i(TAG, "targetId: " + targetId + ", userId: " + userId);
    }

    return userInfo == null ? targetId : userInfo.getName();
  }

  public Uri getPortraitUri(String userId) {
    return Uri.parse(String.format("drawable://%d", drawable.rc_encrypted_conversation_portrait));
  }
}