package com.zxjk.moneyspace.ui.msgpage.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.AllGroupMembersResponse;
import com.zxjk.moneyspace.utils.GlideUtil;

public class AllGroupMemebersAdapter1 extends BaseQuickAdapter<AllGroupMembersResponse, BaseViewHolder> {
    public AllGroupMemebersAdapter1() {
        super(R.layout.item_group_chat_information);
    }

    @Override
    protected void convert(BaseViewHolder helper, AllGroupMembersResponse item) {
        helper.getView(R.id.llBottom).setVisibility(View.VISIBLE);

        helper.setText(R.id.nick_name, item.getNick());
        ImageView heardImage = helper.getView(R.id.header_image);
        TextView nick_owner = helper.getView(R.id.nick_owner);
        GlideUtil.loadCircleImg(heardImage, item.getHeadPortrait());

        if (helper.getAdapterPosition() == 0) {
            nick_owner.setVisibility(View.VISIBLE);
        } else {
            nick_owner.setVisibility(View.GONE);
        }
    }
}
