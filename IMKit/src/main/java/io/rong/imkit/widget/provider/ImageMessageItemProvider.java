//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import io.rong.imkit.R;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.RongIMClient.DestructCountDownTimerListener;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.ImageMessage;

@ProviderTag(
        messageContent = ImageMessage.class,
        showProgress = false,
        showReadState = true
)
public class ImageMessageItemProvider extends MessageProvider<ImageMessage> {
    private static final String TAG = "ImageMessageItemProvider";

    public ImageMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(layout.rc_item_image_message, (ViewGroup) null);
        ImageMessageItemProvider.ViewHolder holder = new ImageMessageItemProvider.ViewHolder();
        holder.message = (TextView) view.findViewById(id.rc_msg);
        holder.img = (AsyncImageView) view.findViewById(id.rc_img);
        holder.fireView = (FrameLayout) view.findViewById(id.rc_destruct_click);
        holder.sendFire = (FrameLayout) view.findViewById(id.fl_send_fire);
        holder.receiverFire = (FrameLayout) view.findViewById(id.fl_receiver_fire);
        holder.receiverFireImg = (ImageView) view.findViewById(id.iv_receiver_fire);
        holder.receiverFireText = (TextView) view.findViewById(id.tv_receiver_fire);
        holder.clickHint = (TextView) view.findViewById(id.rc_destruct_click_hint);
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
        ImageMessageItemProvider.ViewHolder holder = (ImageMessageItemProvider.ViewHolder) v.getTag();
        if (content.isDestruct()) {
            this.bindFireView(v, position, content, message);
        } else {
            if (message.getMessageDirection() == MessageDirection.SEND) {
                v.setBackgroundResource(drawable.rc_ic_bubble_no_right);
            } else {
                v.setBackgroundResource(drawable.rc_ic_bubble_no_left);
            }

            holder.sendFire.setVisibility(8);
            holder.receiverFire.setVisibility(8);
            holder.fireView.setVisibility(8);
            holder.img.setVisibility(0);
            holder.img.setResource(content.getThumUri());
            int progress = message.getProgress();
            SentStatus status = message.getSentStatus();
            if (status.equals(SentStatus.SENDING) && progress < 100) {
                holder.message.setText(progress + "%");
                holder.message.setVisibility(0);
            } else {
                holder.message.setVisibility(8);
            }

        }
    }

    private void bindFireView(View v, int position, ImageMessage content, UIMessage message) {
        ImageMessageItemProvider.ViewHolder holder = (ImageMessageItemProvider.ViewHolder) v.getTag();
        holder.img.setVisibility(8);
        holder.fireView.setVisibility(0);
        Drawable drawable;
        if (message.getMessageDirection() == MessageDirection.SEND) {
            holder.sendFire.setVisibility(0);
            holder.receiverFire.setVisibility(8);
            holder.fireView.setBackgroundResource(R.drawable.rc_ic_bubble_no_right);
            drawable = v.getContext().getResources().getDrawable(R.drawable.rc_fire_sender_album);
            drawable.setBounds(0, 0, RongUtils.dip2px(31.0F), RongUtils.dip2px(26.0F));
            holder.clickHint.setCompoundDrawables((Drawable) null, drawable, (Drawable) null, (Drawable) null);
            holder.clickHint.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.sendFire.setVisibility(8);
            holder.receiverFire.setVisibility(0);
            holder.fireView.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
            drawable = v.getContext().getResources().getDrawable(R.drawable.rc_fire_receiver_album);
            drawable.setBounds(0, 0, RongUtils.dip2px(31.0F), RongUtils.dip2px(26.0F));
            holder.clickHint.setCompoundDrawables((Drawable) null, drawable, (Drawable) null, (Drawable) null);
            holder.clickHint.setTextColor(Color.parseColor("#F4B50B"));
            DestructManager.getInstance().addListener(message.getUId(), new ImageMessageItemProvider.DestructListener(holder, message.getUId()), "ImageMessageItemProvider");
            if (message.getMessage().getReadTime() > 0L) {
                holder.receiverFireText.setVisibility(0);
                holder.receiverFireImg.setVisibility(8);
                String unFinishTime;
                if (TextUtils.isEmpty(message.getUnDestructTime())) {
                    unFinishTime = DestructManager.getInstance().getUnFinishTime(message.getUId());
                } else {
                    unFinishTime = message.getUnDestructTime();
                }

                holder.receiverFireText.setText(unFinishTime);
                DestructManager.getInstance().startDestruct(message.getMessage());
            } else {
                holder.receiverFireText.setVisibility(8);
                holder.receiverFireImg.setVisibility(0);
            }
        }

    }

    public Spannable getContentSummary(ImageMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, ImageMessage data) {
        return data.isDestruct() ? new SpannableString(context.getString(string.rc_message_content_burn)) : new SpannableString(context.getString(string.rc_message_content_image));
    }

    private static class DestructListener implements DestructCountDownTimerListener {
        private WeakReference<ImageMessageItemProvider.ViewHolder> mHolder;
        private String mMessageId;

        public DestructListener(ImageMessageItemProvider.ViewHolder pHolder, String pMessageId) {
            this.mHolder = new WeakReference(pHolder);
            this.mMessageId = pMessageId;
        }

        public void onTick(long millisUntilFinished, String messageId) {
            if (this.mMessageId.equals(messageId)) {
                ImageMessageItemProvider.ViewHolder viewHolder = (ImageMessageItemProvider.ViewHolder) this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.receiverFireText.setVisibility(0);
                    viewHolder.receiverFireImg.setVisibility(8);
                    viewHolder.receiverFireText.setText(String.valueOf(Math.max(millisUntilFinished, 1L)));
                }
            }

        }

        public void onStop(String messageId) {
            if (this.mMessageId.equals(messageId)) {
                ImageMessageItemProvider.ViewHolder viewHolder = (ImageMessageItemProvider.ViewHolder) this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.receiverFireText.setVisibility(8);
                    viewHolder.receiverFireImg.setVisibility(0);
                }
            }

        }
    }

    private static class ViewHolder {
        AsyncImageView img;
        TextView message;
        FrameLayout fireView;
        FrameLayout sendFire;
        FrameLayout receiverFire;
        ImageView receiverFireImg;
        TextView receiverFireText;
        TextView clickHint;

        private ViewHolder() {
        }
    }
}