package com.zxjk.moneyspace.ui.msgpage.adapter;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.PhoneInfo;

public class PhoneContactAdapter extends BaseQuickAdapter<PhoneInfo, BaseViewHolder> {
    private Context context;

    public PhoneContactAdapter(Context context) {
        super(R.layout.item_phone_contact);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, PhoneInfo item) {
        View view = helper.getView(R.id.apply_to_add);
        if (!item.isAdd()) {
            helper.setText(R.id.apply_to_add, R.string.add)
                    .setTextColor(R.id.apply_to_add, context.getColor(R.color.colorWhite));
            view.setBackgroundResource(R.drawable.shape_theme);
        } else {
            helper.setText(R.id.apply_to_add, R.string.added)
                    .setTextColor(R.id.apply_to_add, context.getColor(R.color.color6));
            view.setBackground(null);
        }
        helper.setText(R.id.user_name, item.getName()).addOnClickListener(R.id.apply_to_add);
    }
}
