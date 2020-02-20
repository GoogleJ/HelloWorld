package com.zxjk.duoduo.ui.msgpage.rongIM.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.msgpage.FileSendActivity;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class FilePlugin implements IPluginModule {
    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.icon_file);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getResources().getString(R.string.file);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        Intent intent = new Intent(fragment.getContext(), FileSendActivity.class);
        if (rongExtension.getConversationType() == Conversation.ConversationType.PRIVATE) {
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(rongExtension.getTargetId());
            if (userInfo == null) {
                intent.putExtra("userId", rongExtension.getTargetId());
            } else {
                intent.putExtra("user", userInfo);
            }
        } else if (rongExtension.getConversationType() == Conversation.ConversationType.GROUP) {
            intent.putExtra("groupId", rongExtension.getTargetId());
        }
        fragment.startActivity(intent);
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}