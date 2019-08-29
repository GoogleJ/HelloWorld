package com.zxjk.duoduo.ui.msgpage.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.ImageUtil;

import io.rong.imlib.model.Conversation;

public class ShareGroupQRAdapter extends BaseQuickAdapter<Conversation, BaseViewHolder> {

    public ShareGroupQRAdapter() {
        super(R.layout.item_share_group_qr);
    }

    @Override
    protected void convert(BaseViewHolder helper, Conversation item) {
        helper.addOnClickListener(R.id.ll);

        ImageView iv = helper.getView(R.id.iv);
        TextView tv = helper.getView(R.id.tv);

        tv.setText(item.getConversationTitle());

        if (item.getConversationType().equals(Conversation.ConversationType.GROUP)) {
            //群聊
            ImageUtil.loadGroupPortrait(iv, item.getPortraitUrl());
        } else {
            //单聊
            Glide.with(helper.itemView.getContext()).load(item.getPortraitUrl()).into(iv);
        }
    }
}
