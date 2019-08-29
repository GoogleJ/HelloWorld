package com.zxjk.duoduo.ui.msgpage.adapter;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.utils.GlideUtil;


public class NewFriendAdapter extends BaseQuickAdapter<FriendInfoResponse, BaseViewHolder> {
    public NewFriendAdapter() {
        super(R.layout.item_new_friend);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendInfoResponse item) {
        helper.setText(R.id.m_item_new_friend_user_name_text, item.getNick())
                .setText(R.id.m_item_new_friend_message_label, item.getRemark())
                .addOnClickListener(R.id.m_item_new_friend_type_btn)
                .addOnLongClickListener(R.id.m_add_btn_layout)
                .addOnClickListener(R.id.m_add_btn_layout);
        ImageView headerImage = helper.getView(R.id.m_item_new_friend_icon);
        GlideUtil.loadCircleImg(headerImage, item.getHeadPortrait());
        TextView typeBtn = helper.getView(R.id.m_item_new_friend_type_btn);
        if ("2".equals(item.getStatus())) {
            typeBtn.setText(mContext.getString(R.string.m_item_contact_type_text));
            typeBtn.setBackground(null);
            typeBtn.setTextColor(ContextCompat.getColor(typeBtn.getContext(), R.color.color9));
        } else if ("0".equals(item.getStatus())) {
            typeBtn.setText(R.string.add);
            typeBtn.setBackgroundResource(R.drawable.shape_circular_bead_btn);
            typeBtn.setTextColor(ContextCompat.getColor(typeBtn.getContext(), R.color.white));
        }
    }
}
