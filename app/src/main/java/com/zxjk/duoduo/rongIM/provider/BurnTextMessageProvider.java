package com.zxjk.duoduo.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.GsonUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.ConversationInfo;

import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

@ProviderTag(
        messageContent = TextMessage.class,
        showReadState = true,
        showPortrait = false
)
public class BurnTextMessageProvider extends IContainerItemProvider.MessageProvider<TextMessage> {
    public BurnTextMessageProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(io.rong.imkit.R.layout.rc_item_text_message, group, false);
        BurnTextMessageProvider.ViewHolder holder = new BurnTextMessageProvider.ViewHolder();
        holder.message = view.findViewById(android.R.id.text1);
        holder.ivFireLeft = view.findViewById(R.id.ivFireLeft);
        holder.ivFireRight = view.findViewById(R.id.ivFireRight);
        holder.content = view.findViewById(R.id.content);

        view.setTag(holder);
        return view;
    }

    public Spannable getContentSummary(TextMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, TextMessage data) {
        if (data == null) {
            return null;
        } else {
            String content = data.getContent();
            if (content != null) {
                if (content.length() > 100) {
                    content = content.substring(0, 100);
                }

                return new SpannableString(AndroidEmoji.ensure(content));
            } else {
                return null;
            }
        }
    }

    public void onItemClick(View view, int position, TextMessage content, UIMessage message) {
    }

    public void bindView(final View v, int position, TextMessage content, final UIMessage data) {
        ConversationInfo conversationInfo = null;
        if (!TextUtils.isEmpty(content.getExtra())) {
            conversationInfo = GsonUtils.fromJson(content.getExtra(), ConversationInfo.class);
        }
        BurnTextMessageProvider.ViewHolder holder = (BurnTextMessageProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.message.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));
            holder.content.setBackgroundResource(io.rong.imkit.R.drawable.shape_rc_bubble_right);
            if (conversationInfo != null && conversationInfo.getMessageBurnTime() != -1) {
                holder.ivFireRight.setVisibility(View.VISIBLE);
                holder.ivFireLeft.setVisibility(View.INVISIBLE);
            } else {
                holder.ivFireRight.setVisibility(View.INVISIBLE);
                holder.ivFireLeft.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.message.setTextColor(ContextCompat.getColor(v.getContext(), R.color.new_textcolor1));
            holder.content.setBackgroundResource(io.rong.imkit.R.drawable.shape_rc_bubble_left);
            if (conversationInfo != null && conversationInfo.getMessageBurnTime() != -1) {
                holder.ivFireRight.setVisibility(View.INVISIBLE);
                holder.ivFireLeft.setVisibility(View.VISIBLE);
            } else {
                holder.ivFireRight.setVisibility(View.INVISIBLE);
                holder.ivFireLeft.setVisibility(View.INVISIBLE);
            }
        }

        final TextView textView = holder.message;
        if (data.getTextMessageContent() != null) {
            int len = data.getTextMessageContent().length();
            if (v.getHandler() != null && len > 500) {
                v.getHandler().postDelayed(() -> textView.setText(data.getTextMessageContent()), 50L);
            } else {
                textView.setText(data.getTextMessageContent());
            }
        }

//        textView.stripUnderlines();
    }

    private static class ViewHolder {
        TextView message;
        ImageView ivFireLeft;
        ImageView ivFireRight;
        FrameLayout content;

        private ViewHolder() {
        }
    }
}
