package com.zxjk.moneyspace.ui.msgpage.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.RedPacketMessage;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

@ProviderTag(messageContent = RedPacketMessage.class)
public class RedPacketProvider extends IContainerItemProvider.MessageProvider<RedPacketMessage> {

    class ViewHolder {
        TextView message;
        LinearLayout sendLayout;
    }

    @Override
    public void bindView(View view, int position, RedPacketMessage redPacketMessage, UIMessage uiMessage) {

        ViewHolder holder = (ViewHolder) view.getTag();

        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {
            //消息方向，自己发送的
            holder.sendLayout.setBackgroundResource(R.drawable.icon_red_packet_user);
        } else {
            holder.sendLayout.setBackgroundResource(R.drawable.icon_send_red_packet_friend);
        }

        if (TextUtils.isEmpty(redPacketMessage.getRemark())) {
            holder.message.setText(R.string.m_red_envelopes_label);
        } else {
            holder.message.setText(redPacketMessage.getRemark());
        }

        if (TextUtils.isEmpty(uiMessage.getExtra()) && TextUtils.isEmpty(redPacketMessage.getExtra())) {
            holder.sendLayout.setAlpha(1);
        } else {
            holder.sendLayout.setAlpha(0.6f);
        }
    }

    @Override
    public Spannable getContentSummary(RedPacketMessage redPacketMessage) {
        return new SpannableString("[红包]");
    }

    @Override
    public void onItemClick(View view, int i, RedPacketMessage redPacketMessage, UIMessage uiMessage) {
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_red_packet_send, viewGroup, false);
        ViewHolder holder = new ViewHolder();
        holder.message = view.findViewById(R.id.remark);
        holder.sendLayout = view.findViewById(R.id.send_red_packet_layout);
        view.setTag(holder);
        return view;
    }
}
