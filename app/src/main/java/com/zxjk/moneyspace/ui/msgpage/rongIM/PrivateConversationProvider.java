package com.zxjk.moneyspace.ui.msgpage.rongIM;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.zxjk.moneyspace.R;

import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;

@ConversationProviderTag()
public class PrivateConversationProvider extends io.rong.imkit.widget.provider.PrivateConversationProvider {

    @Override
    public void bindView(View view, int position, UIConversation data) {
        super.bindView(view, position, data);

        TextView title = view.findViewById(io.rong.sight.R.id.rc_conversation_title);

        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getConversationTargetId());

        if (userInfo != null && !TextUtils.isEmpty(userInfo.getExtra()) && userInfo.getExtra().equals("system")) {
            title.setTextColor(ContextCompat.getColor(view.getContext(), R.color.title_offcial));
        } else {
            title.setTextColor(ContextCompat.getColor(view.getContext(), io.rong.imkit.R.color.rc_text_color_primary));
        }
    }
}
