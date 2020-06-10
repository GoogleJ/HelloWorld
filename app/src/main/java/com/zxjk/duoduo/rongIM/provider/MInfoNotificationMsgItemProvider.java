package com.zxjk.duoduo.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

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
        if (content != null && !TextUtils.isEmpty(content.getMessage())) {
            viewHolder.contentTextView.setText(content.getMessage());
        }
        if (!TextUtils.isEmpty(content.getExtra())) {
            if (content.getExtra().contains(v.getContext().getString(R.string.burn_after_read))) {
                String origin = content.getMessage();
                if (!origin.contains(v.getContext().getString(R.string.closeburn))) {
                    SpannableString spannableString = new SpannableString(origin);
                    spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(v.getContext(), R.color.colorTheme)),
                            15, origin.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.contentTextView.setText(spannableString);
                }
            } else if (content.getExtra().contains("截屏通知")) {

            } else if (content.getExtra().equals("对方截取了屏幕")) {
                if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                    v.setVisibility(View.GONE);
                    RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, null);
                }
            } else if (content.getExtra().contains("慢速模式")) {

            }
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
        viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        view.setTag(viewHolder);
        return view;
    }

    private static class ViewHolder {
        TextView contentTextView;

        private ViewHolder() {
        }
    }
}
