package com.zxjk.duoduo.ui.msgpage.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GameResultResponse;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.DuoDuoMessage;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = DuoDuoMessage.class, showPortrait = false, centerInHorizontal = true, showSummaryWithName = false)
public class DuoDuoMessageProvider extends IContainerItemProvider.MessageProvider<DuoDuoMessage> {

    class ViewHolder {
        TextView tv1;
        TextView tv2;
    }

    @Override
    public void bindView(View view, int i, DuoDuoMessage gameResultMessage, UIMessage uiMessage) {
        GameResultResponse gameResultResponse = GsonUtils.fromJson(gameResultMessage.getContent(), GameResultResponse.class);
        DuoDuoMessageProvider.ViewHolder viewHolder = (DuoDuoMessageProvider.ViewHolder) view.getTag();
        viewHolder.tv1.setText(gameResultResponse.getTitle());
        viewHolder.tv2.setText(gameResultResponse.getContent());
    }

    @Override
    public Spannable getContentSummary(DuoDuoMessage gameResultMessage) {
        return new SpannableString("[多多官方]");
    }

    @Override
    public void onItemClick(View view, int i, DuoDuoMessage duoDuoMessage, UIMessage uiMessage) {

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_gameresult, viewGroup, false);
        DuoDuoMessageProvider.ViewHolder holder = new DuoDuoMessageProvider.ViewHolder();
        holder.tv1 = view.findViewById(R.id.tv1);
        holder.tv2 = view.findViewById(R.id.tv2);
        view.setTag(holder);
        return view;
    }

}
