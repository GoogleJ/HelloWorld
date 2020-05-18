package com.zxjk.duoduo.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.rongIM.message.TransferMessage;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

@ProviderTag(messageContent = TransferMessage.class)
public class TransferProvider extends IContainerItemProvider.MessageProvider<TransferMessage> {
    class ViewHolder {
        TextView remark;
        TextView transferMoney;
        ImageView transferIconType;
        LinearLayout sendLayout;
    }

    @Override
    public void bindView(View view, int i, TransferMessage transferMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();

        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.sendLayout.setBackgroundResource(R.drawable.icon_red_packet_user);
        } else {
            holder.sendLayout.setBackgroundResource(R.drawable.icon_send_red_packet_friend);
        }

        if (!TextUtils.isEmpty(uiMessage.getExtra()) || !TextUtils.isEmpty(transferMessage.getExtra())) {
            // 已被领取
            holder.sendLayout.setAlpha(0.6f);
            if (transferMessage.getFromCustomerId().equals(Constant.userId)) {
                if (uiMessage.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                    holder.remark.setText(R.string.received);
                } else {
                    holder.remark.setText(R.string.received1);
                }
            } else {
                if (uiMessage.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                    holder.remark.setText(R.string.received1);
                } else {
                    holder.remark.setText(R.string.received);
                }
            }
        } else {
            // 未被领取
            holder.sendLayout.setAlpha(1f);

            holder.remark.setText(TextUtils.isEmpty(transferMessage.getRemark()) ? (
                    view.getContext().getString(R.string.transfer_to, transferMessage.getName())
            ) : transferMessage.getRemark());
        }
        holder.transferMoney.setText(transferMessage.getMoney() + transferMessage.getSymbol());
    }

    @Override
    public Spannable getContentSummary(TransferMessage transferMessage) {
        return null;
    }

    @Override
    public Spannable getContentSummary(Context context, TransferMessage data) {
        return new SpannableString(context.getString(R.string.transfer));
    }

    @Override
    public void onItemClick(View view, int i, TransferMessage transferMessage, UIMessage uiMessage) {
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transfer_send, null);
        ViewHolder holder = new ViewHolder();
        holder.remark = view.findViewById(R.id.remark);
        holder.transferMoney = view.findViewById(R.id.transfer_money);
        holder.transferIconType = view.findViewById(R.id.transfer_type_icon);
        holder.sendLayout = view.findViewById(R.id.send_red_packet_layout);
        view.setTag(holder);
        return view;
    }
}
