package com.zxjk.duoduo.ui.msgpage.rongIM.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.msgpage.GroupRedPacketActivity;
import com.zxjk.duoduo.ui.msgpage.PrivacyRedPacketActivity;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class RedPacketPlugin implements IPluginModule {
    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.icon_red_packet_btn);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.red_packet);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        if (rongExtension.getConversationType() == Conversation.ConversationType.PRIVATE) {
            Intent intent = new Intent(fragment.getContext(), PrivacyRedPacketActivity.class);
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(rongExtension.getTargetId());
            if (userInfo == null) {
                intent.putExtra("userId", rongExtension.getTargetId());
            } else {
                intent.putExtra("user", userInfo);
            }
            fragment.startActivity(intent);
        } else if (rongExtension.getConversationType() == Conversation.ConversationType.GROUP) {
            Intent intent = new Intent(fragment.getContext(), GroupRedPacketActivity.class);
            intent.putExtra("groupId", rongExtension.getTargetId());
            fragment.startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }

}
