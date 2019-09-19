package com.zxjk.duoduo.ui.msgpage.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.blankj.utilcode.util.GsonUtils;
import com.dongtu.sdk.widget.DTImageView;
import com.dongtu.store.DongtuStore;
import com.dongtu.store.widget.DTStoreMessageView;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.ConversationInfo;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.EmoticonMessage;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

import static io.rong.imkit.utilities.RongUtils.dip2px;

@ProviderTag(messageContent = EmoticonMessage.class)
public class EmoticonMessageProvider extends IContainerItemProvider.MessageProvider<EmoticonMessage> {
    class ViewHolder {
        FrameLayout content;
        DTStoreMessageView messageView;
        DTImageView dtImageView;
        ImageView ivFireRight;
        ImageView ivFireLeft;
    }

    @Override
    public void bindView(View view, int i, EmoticonMessage emoticonMessage, UIMessage uiMessage) {
        EmoticonMessageProvider.ViewHolder holder = (EmoticonMessageProvider.ViewHolder) view.getTag();

        ConversationInfo conversationInfo = null;
        if (!TextUtils.isEmpty(emoticonMessage.getExtra())) {
            conversationInfo = GsonUtils.fromJson(emoticonMessage.getExtra(), ConversationInfo.class);
        }

        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {
            if (conversationInfo != null && conversationInfo.getMessageBurnTime() != -1) {
                holder.ivFireRight.setVisibility(View.VISIBLE);
                holder.ivFireLeft.setVisibility(View.INVISIBLE);
            } else {
                holder.ivFireRight.setVisibility(View.INVISIBLE);
                holder.ivFireLeft.setVisibility(View.INVISIBLE);
            }
        } else {
            if (conversationInfo != null && conversationInfo.getMessageBurnTime() != -1) {
                holder.ivFireRight.setVisibility(View.INVISIBLE);
                holder.ivFireLeft.setVisibility(View.VISIBLE);
            } else {
                holder.ivFireRight.setVisibility(View.INVISIBLE);
                holder.ivFireLeft.setVisibility(View.INVISIBLE);
            }
        }

        if (emoticonMessage.getIsAnimated().equals("0")) {
            holder.messageView.setVisibility(View.VISIBLE);
            holder.dtImageView.setVisibility(View.GONE);
            holder.messageView.showSticker(emoticonMessage.getIcon());
        } else {
            int height = Integer.parseInt(emoticonMessage.getHeight());
            int width = Integer.parseInt(emoticonMessage.getWidth());
            int dp148 = dip2px(148);
            DongtuStore.loadImageInto(holder.dtImageView, emoticonMessage.getIcon(), emoticonMessage.getIconId(), dp148,
                    Math.round(height * (float) dp148 / width));

            holder.messageView.setVisibility(View.GONE);
            holder.dtImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Spannable getContentSummary(EmoticonMessage emoticonMessage) {
        return new SpannableString("[" + emoticonMessage.getIconText() + "]");
    }

    @Override
    public void onItemClick(View view, int i, EmoticonMessage emoticonMessage, UIMessage uiMessage) {
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emoticon, null);
        EmoticonMessageProvider.ViewHolder holder = new EmoticonMessageProvider.ViewHolder();
        holder.dtImageView = view.findViewById(R.id.dtImageView);
        holder.ivFireLeft = view.findViewById(R.id.ivFireLeft);
        holder.ivFireRight = view.findViewById(R.id.ivFireRight);
        holder.content = view.findViewById(R.id.content);
        holder.messageView = new DTStoreMessageView(context, R.style.DTStoreMessageViewSent);
        holder.messageView.setStickerSize(dip2px(148));
        holder.content.addView(holder.messageView, 0);
        view.setTag(holder);
        return view;
    }
}
