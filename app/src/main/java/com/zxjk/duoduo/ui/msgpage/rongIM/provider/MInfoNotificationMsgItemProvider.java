package com.zxjk.duoduo.ui.msgpage.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxjk.duoduo.R;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;

@ProviderTag(
        messageContent = InformationNotificationMessage.class,
        showPortrait = false,
        showProgress = false,
        showWarning = false,
        centerInHorizontal = true,
        showSummaryWithName = false
)
public class MInfoNotificationMsgItemProvider extends IContainerItemProvider.MessageProvider<InformationNotificationMessage> {

    public MInfoNotificationMsgItemProvider() {
    }

    public void bindView(View v, int position, InformationNotificationMessage content, UIMessage message) {
        MInfoNotificationMsgItemProvider.ViewHolder viewHolder = (MInfoNotificationMsgItemProvider.ViewHolder) v.getTag();
        v.setVisibility(View.VISIBLE);
        viewHolder.iv_start.setVisibility(View.GONE);
        if (content != null && !TextUtils.isEmpty(content.getMessage())) {
            viewHolder.contentTextView.setText(content.getMessage());
        }
        if (!TextUtils.isEmpty(content.getExtra())) {
            if (content.getExtra().contains("焚")) {
                viewHolder.iv_start.setVisibility(View.VISIBLE);
                viewHolder.iv_start.setImageResource(io.rong.imkit.R.drawable.ic_msg_notifation_burn);
            } else if (content.getExtra().contains("截屏通知")) {
                viewHolder.iv_start.setVisibility(View.VISIBLE);
                viewHolder.iv_start.setImageResource(io.rong.imkit.R.drawable.ic_msg_notifation_capture);
            } else if (content.getExtra().contains("端对端加密")) {
                viewHolder.iv_start.setVisibility(View.VISIBLE);
                viewHolder.iv_start.setImageResource(io.rong.imkit.R.drawable.ic_msg_notifation_lock);
            } else if (content.getExtra().equals("对方截取了屏幕")) {
                viewHolder.iv_start.setVisibility(View.GONE);
                if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                    v.setVisibility(View.GONE);
                    RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, null);
                }
            } else if (content.getExtra().contains("慢速模式")) {
                viewHolder.iv_start.setVisibility(View.VISIBLE);
                viewHolder.iv_start.setImageResource(R.drawable.ic_msg_notifation_slowmode);
            }
        } else {
            viewHolder.iv_start.setVisibility(View.GONE);
        }
    }

    public Spannable getContentSummary(InformationNotificationMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, InformationNotificationMessage data) {
        return data != null && !TextUtils.isEmpty(data.getMessage()) ? (data.getMessage().equals("对方截取了屏幕") ? null : new SpannableString(data.getMessage())) : null;
    }

    public void onItemClick(View view, int position, InformationNotificationMessage content, UIMessage message) {
    }

    public void onItemLongClick(View view, int position, InformationNotificationMessage content, UIMessage message) {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(io.rong.imkit.R.layout.rc_item_information_notification_message, null);
        MInfoNotificationMsgItemProvider.ViewHolder viewHolder = new MInfoNotificationMsgItemProvider.ViewHolder();
        viewHolder.contentTextView = view.findViewById(io.rong.imkit.R.id.rc_msg);
        viewHolder.iv_start = view.findViewById(io.rong.imkit.R.id.iv_start);
        viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        view.setTag(viewHolder);
        return view;
    }

    private static class ViewHolder {
        TextView contentTextView;
        ImageView iv_start;

        private ViewHolder() {
        }
    }
}
