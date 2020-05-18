package com.zxjk.duoduo.rongIM.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.rongIM.message.SocialGroupCardMessage;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = SocialGroupCardMessage.class)
public class SocialGroupCardProvider extends IContainerItemProvider.MessageProvider<SocialGroupCardMessage> {
    private int itemSize;

    public SocialGroupCardProvider() {
        itemSize = (int) (ScreenUtils.getScreenWidth() * 0.45f);
    }

    @Override
    public void bindView(View view, int i, SocialGroupCardMessage groupCardMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvCount.setText(groupCardMessage.getMemberNum());
        GlideUtil.loadNormalImg(holder.ivHead, groupCardMessage.getIcon());
        holder.ivPay.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(groupCardMessage.getMemberNum()) && Integer.parseInt(groupCardMessage.getMemberNum()) != 0) {
            holder.tvOwner.setText(view.getContext().getString(R.string.me_and_x_nums_friend_wait_for_you_at, groupCardMessage.getMemberNum(), groupCardMessage.getGroupName()));
        } else {
            holder.tvOwner.setText(view.getContext().getString(R.string.me_wait_for_you_at, groupCardMessage.getGroupName()));
        }
    }

    @Override
    public Spannable getContentSummary(SocialGroupCardMessage groupCardMessage) {
        return null;
    }

    @Override
    public Spannable getContentSummary(Context context, SocialGroupCardMessage data) {
        if (data.getInviterId().equals(Constant.userId)) {
            return new SpannableString(context.getString(R.string.social_card));
        } else {
            return new SpannableString(context.getString(R.string.xx_invite_join, data.getName(), data.getGroupName()));
        }
    }

    @Override
    public void onItemClick(View view, int i, SocialGroupCardMessage groupCardMessage, UIMessage uiMessage) {
        Intent intent = new Intent(view.getContext(), SocialHomeActivity.class);
        intent.putExtra("id", groupCardMessage.getGroupId());
        view.getContext().startActivity(intent);
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_social, viewGroup, false);
        SocialGroupCardProvider.ViewHolder holder = new SocialGroupCardProvider.ViewHolder();
        holder.socialCount = view.findViewById(R.id.socialCount);
        holder.ivHead = view.findViewById(R.id.ivHead);
        holder.ivPay = view.findViewById(R.id.ivPay);
        holder.tvTitle = view.findViewById(R.id.tvTitle);
        holder.tvOwner = view.findViewById(R.id.tvOwner);
        holder.llContent = view.findViewById(R.id.llContent);
        holder.tvCount = view.findViewById(R.id.tvCount);
        holder.tvTitle.setVisibility(View.GONE);
        holder.tvOwner.setMaxLines(3);
        holder.socialCount.setVisibility(View.GONE);
        ViewGroup.LayoutParams layoutParams = holder.llContent.getLayoutParams();
        layoutParams.width = itemSize;
        holder.llContent.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams1 = holder.ivHead.getLayoutParams();
        layoutParams1.height = itemSize;
        holder.ivHead.setLayoutParams(layoutParams1);
        holder.ivHead.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setTag(holder);
        return view;
    }

    class ViewHolder {
        private ImageView ivHead;
        private ImageView ivPay;
        private TextView tvTitle;
        private TextView tvOwner;
        private TextView tvCount;
        private LinearLayout llContent;
        private LinearLayout socialCount;
    }
}
