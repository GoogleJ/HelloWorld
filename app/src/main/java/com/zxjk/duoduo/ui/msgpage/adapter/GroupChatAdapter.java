package com.zxjk.duoduo.ui.msgpage.adapter;

import android.annotation.SuppressLint;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GroupChatResponse;
import com.zxjk.duoduo.utils.ImageUtil;

public class GroupChatAdapter extends BaseQuickAdapter<GroupChatResponse, BaseViewHolder> {

    public GroupChatAdapter() {
        super(R.layout.item_group_chat);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void convert(BaseViewHolder helper, GroupChatResponse item) {

        helper.setText(R.id.group_name, item.getGroupNikeName())
                .setText(R.id.group_message, item.getGroupSign())
                .addOnClickListener(R.id.m_group_chat);

        ImageUtil.loadGroupPortrait(helper.getView(R.id.group_chat_iamge), item.getHeadPortrait(), 56, 2);
    }
}
