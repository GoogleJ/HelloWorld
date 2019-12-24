package com.zxjk.duoduo.ui.msgpage.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.utils.GlideUtil;

public class GlobalSearchAdapter extends BaseQuickAdapter<FriendInfoResponse, BaseViewHolder> {

    private Context context;

    public GlobalSearchAdapter(Context context) {
        super(R.layout.item_search);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendInfoResponse item) {
        helper.setText(R.id.m_item_search_text, item.getNick())
                .setText(R.id.m_item_search_dudu_id, context.getString(R.string.duoduo_id) + " " + item.getDuoduoId())
                .addOnClickListener(R.id.m_item_search_layout);
        ImageView heardImage = helper.getView(R.id.m_item_search_icon);
        GlideUtil.loadCircleImg(heardImage, item.getHeadPortrait());
    }
}
