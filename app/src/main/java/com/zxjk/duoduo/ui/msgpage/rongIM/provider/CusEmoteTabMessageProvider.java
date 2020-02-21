package com.zxjk.duoduo.ui.msgpage.rongIM.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.ConversationInfo;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.CusEmoteTabMessage;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

@ProviderTag(
        messageContent = CusEmoteTabMessage.class,
        showProgress = false,
        showReadState = true)
public class CusEmoteTabMessageProvider extends IContainerItemProvider.MessageProvider<CusEmoteTabMessage> {
    private int[] tabIds = new int[]{io.rong.imkit.R.drawable.emoji1_1, io.rong.imkit.R.drawable.emoji1_2,
            io.rong.imkit.R.drawable.emoji1_3, io.rong.imkit.R.drawable.emoji1_4, io.rong.imkit.R.drawable.emoji1_5,
            io.rong.imkit.R.drawable.emoji1_6, io.rong.imkit.R.drawable.emoji1_7, io.rong.imkit.R.drawable.emoji1_8,
            io.rong.imkit.R.drawable.emoji1_9, io.rong.imkit.R.drawable.emoji1_10, io.rong.imkit.R.drawable.emoji2_1, io.rong.imkit.R.drawable.emoji2_2,
            io.rong.imkit.R.drawable.emoji2_3, io.rong.imkit.R.drawable.emoji2_4, io.rong.imkit.R.drawable.emoji2_5,
            io.rong.imkit.R.drawable.emoji2_6, io.rong.imkit.R.drawable.emoji2_7, io.rong.imkit.R.drawable.emoji2_8,
            io.rong.imkit.R.drawable.emoji2_9, io.rong.imkit.R.drawable.emoji2_10, io.rong.imkit.R.drawable.emoji2_11,
            io.rong.imkit.R.drawable.emoji2_12, io.rong.imkit.R.drawable.emoji2_13, io.rong.imkit.R.drawable.emoji2_14,
            io.rong.imkit.R.drawable.emoji1_11,io.rong.imkit.R.drawable.emoji1_12,io.rong.imkit.R.drawable.emoji1_13,
            io.rong.imkit.R.drawable.emoji1_14,io.rong.imkit.R.drawable.emoji1_15,io.rong.imkit.R.drawable.emoji1_16,
            io.rong.imkit.R.drawable.emoji1_17,io.rong.imkit.R.drawable.emoji1_18,io.rong.imkit.R.drawable.emoji1_19,
            io.rong.imkit.R.drawable.emoji1_20,io.rong.imkit.R.drawable.emoji1_21,io.rong.imkit.R.drawable.emoji1_22,
            io.rong.imkit.R.drawable.emoji1_23,io.rong.imkit.R.drawable.emoji1_24,io.rong.imkit.R.drawable.emoji1_25,
            io.rong.imkit.R.drawable.emoji1_26,io.rong.imkit.R.drawable.emoji1_27,io.rong.imkit.R.drawable.emoji1_28,
            io.rong.imkit.R.drawable.emoji1_29,io.rong.imkit.R.drawable.emoji1_30,io.rong.imkit.R.drawable.emoji1_31
    };

    @SuppressLint("CheckResult")
    @Override
    public void bindView(View v, int i, CusEmoteTabMessage content, UIMessage message) {
        ConversationInfo conversationInfo = null;
        if (!TextUtils.isEmpty(content.getExtra())) {
            conversationInfo = GsonUtils.fromJson(content.getExtra(), ConversationInfo.class);
        }
        CusEmoteTabMessageProvider.ViewHolder holder = (CusEmoteTabMessageProvider.ViewHolder) v.getTag();
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

        loadEmote(v, content, holder);

        holder.llLoading.setOnClickListener(v1 -> {
            if (holder.ivLoad.getVisibility() != View.VISIBLE) {
                loadEmote(v, content, holder);
            }
        });
    }

    private void loadEmote(View v, CusEmoteTabMessage content, ViewHolder holder) {
        holder.llLoading.setVisibility(View.VISIBLE);
        holder.tvTips.setText(R.string.loading);
        holder.ivLoad.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(content.getId())) {
            if (!TextUtils.isEmpty(content.getIsGif()) && content.getIsGif().equals("1")) {
                Glide.with(v.getContext()).asGif()
                        .load(tabIds[Integer.parseInt(content.getId()) - 1])
                        .listener(new RequestListener<GifDrawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                onError(holder);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                onSuccess(holder);
                                return false;
                            }
                        }).into(holder.ivContent);
            } else {
                Glide.with(v.getContext()).asBitmap()
                        .load(tabIds[Integer.parseInt(content.getId()) - 1])
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                onError(holder);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                onSuccess(holder);
                                return false;
                            }
                        }).into(holder.ivContent);
            }
        } else {
            if (!TextUtils.isEmpty(content.getIsGif()) && content.getIsGif().equals("1")) {
                Glide.with(v.getContext()).asGif()
                        .load(content.getUrl())
                        .listener(new RequestListener<GifDrawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                onError(holder);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                onSuccess(holder);
                                return false;
                            }
                        }).into(holder.ivContent);
            } else {
                Glide.with(v.getContext()).asBitmap()
                        .load(content.getUrl())
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                onError(holder);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                onSuccess(holder);
                                return false;
                            }
                        }).into(holder.ivContent);
            }
        }
    }

    private void onError(ViewHolder holder) {
        holder.ivContent.setVisibility(View.GONE);
        holder.llLoading.setVisibility(View.VISIBLE);
        holder.ivLoad.setVisibility(View.GONE);
        holder.tvTips.setText(R.string.retry);
        holder.flContent.setBackgroundColor(Color.parseColor("#e5e5e5"));
    }

    private void onSuccess(ViewHolder holder) {
        holder.ivContent.setVisibility(View.VISIBLE);
        holder.llLoading.setVisibility(View.GONE);
        holder.tvTips.setText(R.string.loading);
        holder.flContent.setBackground(null);
    }

    @Override
    public Spannable getContentSummary(CusEmoteTabMessage cusEmoteTabMessage) {
        return new SpannableString("[表情]");
    }

    @Override
    public void onItemClick(View view, int i, CusEmoteTabMessage cusEmoteTabMessage, UIMessage uiMessage) {
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_msg_cusemotetab, viewGroup, false);
        CusEmoteTabMessageProvider.ViewHolder holder = new CusEmoteTabMessageProvider.ViewHolder();
        holder.ivFireLeft = view.findViewById(R.id.ivFireLeft);
        holder.ivFireRight = view.findViewById(R.id.ivFireRight);
        holder.flContent = view.findViewById(R.id.flContent);
        holder.llLoading = view.findViewById(R.id.llLoading);
        holder.ivLoad = view.findViewById(R.id.ivLoad);
        holder.tvTips = view.findViewById(R.id.tvTips);
        holder.ivContent = view.findViewById(R.id.ivContent);

        view.setTag(holder);
        return view;
    }

    private static class ViewHolder {
        FrameLayout flContent;

        LinearLayout llLoading;
        SpinKitView ivLoad;
        TextView tvTips;

        ImageView ivContent;
        ImageView ivFireLeft;
        ImageView ivFireRight;

        private ViewHolder() {
        }
    }
}
