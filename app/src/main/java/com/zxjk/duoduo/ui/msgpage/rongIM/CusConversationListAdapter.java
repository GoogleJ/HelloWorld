package com.zxjk.duoduo.ui.msgpage.rongIM;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.utils.ImageUtil;

import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

public class CusConversationListAdapter extends ConversationListAdapter {

    public CusConversationListAdapter(Context context) {
        super(context);
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        return super.newView(context, position, group);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void bindView(View v, int position, UIConversation data) {
        super.bindView(v, position, data);
        v.findViewById(R.id.rc_left).setVisibility(View.GONE);
        ImageView groupHead = v.findViewById(R.id.rc_mask);

        if (data.getConversationType() != Conversation.ConversationType.GROUP) {
            v.findViewById(R.id.rc_left).setVisibility(View.VISIBLE);
            groupHead.setVisibility(View.GONE);
            return;
        }

        Group groupInfo = RongUserInfoManager.getInstance().getGroupInfo(data.getConversationTargetId());
        if (groupInfo != null && !TextUtils.isEmpty(groupInfo.getPortraitUri().toString())
                && !groupInfo.getName().contains("おれは人间をやめるぞ！ジョジョ―――ッ!")) {
            groupHead.setVisibility(View.VISIBLE);
            ImageUtil.loadGroupPortrait(groupHead, groupInfo.getPortraitUri().toString());
        } else {
            groupHead.setVisibility(View.GONE);
            v.findViewById(R.id.rc_left).setVisibility(View.VISIBLE);
        }
    }
}
