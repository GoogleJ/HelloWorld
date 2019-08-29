package com.zxjk.duoduo.ui.msgpage.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.utils.GlideUtil;


public class SearchAdapter extends BaseQuickAdapter<FriendInfoResponse, BaseViewHolder> {

    public SearchAdapter() {
        super(R.layout.item_consatnt_friend);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendInfoResponse item) {
        helper.setText(R.id.m_user_name, TextUtils.isEmpty(item.getRemark()) ? item.getNick() : item.getRemark())
                .setText(R.id.m_singture_text, helper.itemView.getContext().getString(R.string.bank_real_name) + ":" + item.getRealname());
        ImageView heardImage = helper.getView(R.id.m_constants_header_icon);
        GlideUtil.loadCircleImg(heardImage, item.getHeadPortrait());
    }
}
