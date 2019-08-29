package com.zxjk.duoduo.ui.msgpage.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.utils.GlideUtil;

public class BaseContactAdapter extends BaseQuickAdapter<FriendInfoResponse, BaseViewHolder> {
    public BaseContactAdapter() {
        super(R.layout.item_consatnt_friend);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendInfoResponse item) {
        helper.setText(R.id.m_user_name, TextUtils.isEmpty(item.getRemark()) ? item.getNick() : item.getRemark())
                .setText(R.id.m_singture_text, helper.itemView.getContext().getString(R.string.bank_real_name) + ":" + item.getRealname())
                .setText(R.id.tvFirstLetter, item.getSortLetters())
                .addOnClickListener(R.id.m_constacts_friend)
                .addOnLongClickListener(R.id.m_constacts_friend);

        if (item.getIsShowRealname().equals("0")) {
            helper.setGone(R.id.m_singture_text, false);
        } else {
            helper.setVisible(R.id.m_singture_text, true);
        }

        ImageView heardImage = helper.getView(R.id.m_constants_header_icon);

        View view = helper.getView(R.id.tvFirstLetter);
        if (helper.getAdapterPosition() == 1) {
            view.setVisibility(View.VISIBLE);
        } else if (item.getSortLetters().equals(getData().get(helper.getAdapterPosition() - 2).getSortLetters())) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }

        GlideUtil.loadCircleImg(heardImage, item.getHeadPortrait());
    }
}
