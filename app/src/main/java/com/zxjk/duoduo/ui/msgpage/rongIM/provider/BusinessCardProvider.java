package com.zxjk.duoduo.ui.msgpage.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.BusinessCardMessage;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

@ProviderTag(messageContent = BusinessCardMessage.class)
public class BusinessCardProvider extends IContainerItemProvider.MessageProvider<BusinessCardMessage> {

    class ViewHolder {
        TextView userName;
        TextView duoduoId;
        ImageView heardImage;
        ConstraintLayout sendLayout;
    }

    Context context;

    @Override
    public void bindView(View view, int i, BusinessCardMessage businessCardMessage, UIMessage uiMessage) {
        if (context == null) {
            context = view.getContext();
        }
        ViewHolder holder = (ViewHolder) view.getTag();

        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {
            //消息方向，自己发送的
            holder.sendLayout.setBackgroundResource(R.drawable.icon_business_card_user);
        } else {
            holder.sendLayout.setBackgroundResource(R.drawable.icon_business_card_friend);
        }

        holder.userName.setText(businessCardMessage.getName());
        holder.duoduoId.setText(businessCardMessage.getDuoduo());
        GlideUtil.loadCircleImg(holder.heardImage, businessCardMessage.getIcon());
    }

    @Override
    public Spannable getContentSummary(BusinessCardMessage businessCardMessage) {
        return new SpannableString("[名片]");
    }

    @Override
    public void onItemClick(View view, int i, BusinessCardMessage businessCardMessage, UIMessage uiMessage) {
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_card_send, viewGroup, false);
        ViewHolder holder = new ViewHolder();
        holder.userName = (TextView) view.findViewById(R.id.business_card_user_name);
        holder.duoduoId = (TextView) view.findViewById(R.id.business_card_duoduo_id);
        holder.heardImage = (ImageView) view.findViewById(R.id.business_card_header);
        holder.sendLayout = (ConstraintLayout) view.findViewById(R.id.send_red_packet_layout);
        view.setTag(holder);
        return view;
    }
}
