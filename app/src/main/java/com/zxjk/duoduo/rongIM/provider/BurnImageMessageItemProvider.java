package com.zxjk.duoduo.rongIM.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.zxjk.duoduo.bean.ConversationInfo;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.utils.BitmapUtil;

@ProviderTag(
        messageContent = ImageMessage.class,
        showProgress = false,
        showReadState = true,
        showPortrait = false
)
public class BurnImageMessageItemProvider extends IContainerItemProvider.MessageProvider<ImageMessage> {

    public BurnImageMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(io.rong.imkit.R.layout.rc_item_image_message, group, false);
        BurnImageMessageItemProvider.ViewHolder holder = new BurnImageMessageItemProvider.ViewHolder();
        holder.message = view.findViewById(io.rong.imkit.R.id.rc_msg);
        holder.img = view.findViewById(io.rong.imkit.R.id.rc_img);
        holder.ivFireLeft = view.findViewById(io.rong.imkit.R.id.ivFireLeft);
        holder.ivFireRight = view.findViewById(io.rong.imkit.R.id.ivFireRight);
        view.setTag(holder);
        return view;
    }

    public void onItemClick(View view, int position, ImageMessage content, UIMessage message) {
        if (content != null) {
            Intent intent = new Intent("io.rong.imkit.intent.action.picturepagerview");
            intent.setPackage(view.getContext().getPackageName());
            intent.putExtra("message", message.getMessage());
            view.getContext().startActivity(intent);
        }
    }

    public void bindView(View v, int position, ImageMessage content, UIMessage message) {
        v.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_no_right_new);

        ConversationInfo conversationInfo = null;
        if (!TextUtils.isEmpty(content.getExtra())) {
            conversationInfo = GsonUtils.fromJson(content.getExtra(), ConversationInfo.class);
        }
        BurnImageMessageItemProvider.ViewHolder holder = (BurnImageMessageItemProvider.ViewHolder) v.getTag();
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
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

        if (content.isDestruct()) {
            Bitmap bitmap = ImageLoader.getInstance().loadImageSync(content.getThumUri().toString());
            if (bitmap != null) {
                Bitmap blurryBitmap = BitmapUtil.getBlurryBitmap(v.getContext(), bitmap, 5.0F, 0.25F);
                holder.img.setBitmap(blurryBitmap);
            }
        } else {
            holder.img.setResource(content.getThumUri());
        }

        int progress = message.getProgress();
        Message.SentStatus status = message.getSentStatus();
        if (status.equals(Message.SentStatus.SENDING) && progress < 100) {
            holder.message.setText(progress + "%");
            holder.message.setVisibility(View.VISIBLE);
        } else {
            holder.message.setVisibility(View.GONE);
        }
    }

    public Spannable getContentSummary(ImageMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, ImageMessage data) {
        return new SpannableString(context.getString(io.rong.imkit.R.string.rc_message_content_image));
    }

    private static class ViewHolder {
        AsyncImageView img;
        TextView message;
        ImageView ivFireLeft;
        ImageView ivFireRight;

        private ViewHolder() {
        }
    }
}
