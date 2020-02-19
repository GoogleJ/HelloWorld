package com.zxjk.duoduo.ui.msgpage.rongIM.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.findpage.NewsDetailActivity;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.NewsCardMessage;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;


@ProviderTag(messageContent = NewsCardMessage.class)
public class NewsCardProvider extends IContainerItemProvider.MessageProvider<NewsCardMessage>{

    class ViewHolder {
        TextView title;
        TextView content;
        ImageView icon;
        LinearLayout sendLayout;
    }
    @Override
    public void bindView(View view, int i, NewsCardMessage newsCardMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();

        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.sendLayout.setBackgroundResource(R.drawable.icon_business_card_user);
        } else {
            holder.sendLayout.setBackgroundResource(R.drawable.icon_business_card_friend);
        }

        holder.title.setText(newsCardMessage.getPlatform() +"\u0020|\u0020"+ newsCardMessage.getTitle());
        holder.content.setText(newsCardMessage.getContent());
        GlideUtil.loadCornerImg(holder.icon, newsCardMessage.getIcon(), 1);
        holder.content.setText(newsCardMessage.getContent());
    }

    @Override
    public Spannable getContentSummary(NewsCardMessage newsCardMessage) {
        return null;
    }

    @Override
    public Spannable getContentSummary(Context context, NewsCardMessage data) {
        return new SpannableString(context.getString(R.string.share_news_card));
    }

    @Override
    public void onItemClick(View view, int i, NewsCardMessage newsCardMessage, UIMessage uiMessage) {
        Intent intent = new Intent(view.getContext(), NewsDetailActivity.class);
        intent.putExtra("title",newsCardMessage.getTitle());
        intent.putExtra("url", newsCardMessage.getUrl());
        intent.putExtra("icon", newsCardMessage.getIcon());
        intent.putExtra("article",newsCardMessage.getContent());
        intent.putExtra("platform",newsCardMessage.getPlatform());
        view.getContext().startActivity(intent);
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_card_send, viewGroup, false);
        ViewHolder holder = new ViewHolder();
        holder.title = view.findViewById(R.id.news_card_title);
        holder.content = view.findViewById(R.id.news_card_content);
        holder.icon = view.findViewById(R.id.news_card_icon);
        holder.sendLayout = view.findViewById(R.id.news_card_send_layout);
        view.setTag(holder);
        return view;
    }
}