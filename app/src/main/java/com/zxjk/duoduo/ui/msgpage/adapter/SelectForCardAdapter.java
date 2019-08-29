package com.zxjk.duoduo.ui.msgpage.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.utils.GlideUtil;

public class SelectForCardAdapter extends BaseQuickAdapter<FriendInfoResponse, BaseViewHolder> {
    public SelectForCardAdapter() {
        super(R.layout.item_select_for_card);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendInfoResponse item) {
        helper.setText(R.id.user_name, TextUtils.isEmpty(item.getRemark()) ? item.getNick() : item.getRemark()).addOnClickListener(R.id.select_for_card_item);
        ImageView heardImage = helper.getView(R.id.remove_headers);
        GlideUtil.loadCircleImg(heardImage, item.getHeadPortrait());

    }
}
