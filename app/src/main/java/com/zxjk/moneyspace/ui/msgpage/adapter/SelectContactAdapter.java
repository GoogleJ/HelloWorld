package com.zxjk.moneyspace.ui.msgpage.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.FriendInfoResponse;
import com.zxjk.moneyspace.utils.GlideUtil;


public class SelectContactAdapter extends BaseQuickAdapter<FriendInfoResponse, BaseViewHolder> {
    public SelectContactAdapter() {
        super(R.layout.item_add_group);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendInfoResponse item) {
        helper.setText(R.id.user_name, TextUtils.isEmpty(item.getRemark()) ? item.getNick() : item.getRemark())
                .addOnClickListener(R.id.add_del_group_layout);
        ImageView heardImage = helper.getView(R.id.remove_headers);
        GlideUtil.loadCircleImg(heardImage, item.getHeadPortrait());

    }
}
