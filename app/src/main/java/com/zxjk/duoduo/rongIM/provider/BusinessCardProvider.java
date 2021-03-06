package com.zxjk.duoduo.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.rongIM.message.BusinessCardMessage;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = BusinessCardMessage.class,
        showPortrait = false
)
public class BusinessCardProvider extends IContainerItemProvider.MessageProvider<BusinessCardMessage> {

    class ViewHolder {
        TextView userName;
        TextView duoduoId;
        ImageView heardImage;
    }

    @Override
    public void bindView(View view, int i, BusinessCardMessage businessCardMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.userName.setText(businessCardMessage.getName());
        holder.duoduoId.setText(businessCardMessage.getDuoduo());
        GlideUtil.loadCircleImg(holder.heardImage, businessCardMessage.getIcon());
    }

    @Override
    public Spannable getContentSummary(BusinessCardMessage businessCardMessage) {
        if (!TextUtils.isEmpty(businessCardMessage.getSenderName()) &&
                !TextUtils.isEmpty(businessCardMessage.getSenderId()) &&
                !businessCardMessage.getSenderId().equals(Constant.userId)) {
            return new SpannableString(businessCardMessage.getSenderName() + "向你推荐了【" + businessCardMessage.getName() + "】");
        }
        return new SpannableString("[名片]");
    }

    @Override
    public void onItemClick(View view, int i, BusinessCardMessage businessCardMessage, UIMessage uiMessage) {
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_card_send, viewGroup, false);
        ViewHolder holder = new ViewHolder();
        holder.userName = view.findViewById(R.id.business_card_user_name);
        holder.duoduoId = view.findViewById(R.id.business_card_duoduo_id);
        holder.heardImage = view.findViewById(R.id.business_card_header);
        view.setTag(holder);
        return view;
    }
}
