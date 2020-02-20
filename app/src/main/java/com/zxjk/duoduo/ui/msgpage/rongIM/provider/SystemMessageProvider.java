package com.zxjk.duoduo.ui.msgpage.rongIM.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.SystemMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = SystemMessage.class,
        centerInHorizontal = true,
        showPortrait = false,
        showSummaryWithName = false)
public class SystemMessageProvider extends IContainerItemProvider.MessageProvider<SystemMessage> {
    private SimpleDateFormat sdf;

    @Override
    public void bindView(View view, int i, SystemMessage systemMessage, UIMessage uiMessage) {
        if (sdf == null) {
            sdf = new SimpleDateFormat("MM-dd");
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.tvTitle.setText(systemMessage.getTitle());
        holder.tvContent.setText(systemMessage.getContent());
        if (!TextUtils.isEmpty(systemMessage.getDate())) {
            try {
                long date = Long.parseLong(systemMessage.getDate());
                holder.tvDate.setText(sdf.format(new Date(date)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(systemMessage.getShowDetail()) && systemMessage.getShowDetail().equals("1")) {
            holder.llBottom.setVisibility(View.VISIBLE);
        } else {
            holder.llBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public Spannable getContentSummary(Context context, SystemMessage data) {
        return new SpannableString(context.getString(R.string.system));
    }

    @Override
    public Spannable getContentSummary(SystemMessage systemMessage) {
        return null;
    }

    @Override
    public void onItemClick(View view, int i, SystemMessage systemMessage, UIMessage uiMessage) {
        if (TextUtils.isEmpty(systemMessage.getAction())) {
            return;
        }

        switch (systemMessage.getAction()) {
            case "1":
                if (!TextUtils.isEmpty(systemMessage.getUrl())) {
                    Intent intent = new Intent(view.getContext(), WebActivity.class);
                    intent.putExtra("title", view.getContext().getString(R.string.hilamg_official));
                    intent.putExtra("url", systemMessage.getUrl());
                    view.getContext().startActivity(intent);
                }
                break;
        }
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_im_msg_system, viewGroup, false);
        ViewHolder holder = new ViewHolder();

        holder.tvTitle = view.findViewById(R.id.tvTitle);
        holder.tvDate = view.findViewById(R.id.tvDate);
        holder.tvContent = view.findViewById(R.id.tvContent);
        holder.llBottom = view.findViewById(R.id.llBottom);

        view.setTag(holder);
        return view;
    }

    static class ViewHolder {
        private TextView tvTitle;
        private TextView tvDate;
        private TextView tvContent;
        private LinearLayout llBottom;
    }
}
