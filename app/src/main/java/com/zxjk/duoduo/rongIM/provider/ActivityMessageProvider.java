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
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.DataTemp628;
import com.zxjk.duoduo.rongIM.message.ActivityMessage;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = ActivityMessage.class,
        centerInHorizontal = true,
        showPortrait = false,
        showSummaryWithName = false)
public class ActivityMessageProvider extends IContainerItemProvider.MessageProvider<ActivityMessage> {

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_im_msg_activity, viewGroup, false);
        ViewHolder holder = new ViewHolder();

        holder.ivBanner = view.findViewById(R.id.ivBanner);
        holder.tvTitle = view.findViewById(R.id.tvTitle);
        holder.tvContent = view.findViewById(R.id.tvContent);

        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, int var2, ActivityMessage msg, UIMessage var4) {
        ViewHolder holder = (ViewHolder) view.getTag();

        if (!TextUtils.isEmpty(msg.getBannerUrl())) {
            GlideUtil.loadNormalImg(holder.ivBanner, msg.getBannerUrl());
        }

        if (!TextUtils.isEmpty(msg.getTitle())) {
            holder.tvTitle.setText(msg.getTitle());
        }

        if (!TextUtils.isEmpty(msg.getContent())) {
            holder.tvContent.setText(msg.getContent());
        }
    }

    @Override
    public Spannable getContentSummary(ActivityMessage msg) {
        if (!TextUtils.isEmpty(msg.getDescribe())) {
            return new SpannableString(msg.getDescribe());
        }
        return null;
    }

    @Override
    public void onItemClick(View view, int var2, ActivityMessage msg, UIMessage var4) {
        if (!TextUtils.isEmpty(msg.getAction())) {
            if (msg.getAction().equals("action1")) {
                DataTemp628 data = new DataTemp628();
                data.setId(Constant.currentUser.getId());
                data.setToken(Constant.currentUser.getToken());
                Intent intent = new Intent(view.getContext(), WebActivity.class);
                intent.putExtra("url", Constant.URL_628ACTIVITY + "/?" + AesUtil.getInstance().encrypt(GsonUtils.toJson(data)));
                ActivityUtils.startActivity(intent);
                return;
            }
        }

        if (TextUtils.isEmpty(msg.getDetailUrl())) {
            return;
        }
        Intent intent = new Intent(view.getContext(), WebActivity.class);
        intent.putExtra("url", msg.getDetailUrl());
        ActivityUtils.startActivity(intent);
    }

    static class ViewHolder {
        private ImageView ivBanner;
        private TextView tvTitle;
        private TextView tvContent;
    }
}
