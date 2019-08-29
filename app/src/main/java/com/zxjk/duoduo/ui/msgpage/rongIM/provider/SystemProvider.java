package com.zxjk.duoduo.ui.msgpage.rongIM.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetOverOrderResponse;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.SystemMessage;
import com.zxjk.duoduo.ui.walletpage.OverOrderActivity;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = SystemMessage.class, showPortrait = false, centerInHorizontal = true, showSummaryWithName = false)
public class SystemProvider extends IContainerItemProvider.MessageProvider<SystemMessage> {

    class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
    }

    @Override
    public void bindView(View view, int i, SystemMessage systemMessage, UIMessage uiMessage) {
        GetOverOrderResponse data = GsonUtils.fromJson(systemMessage.getContent(), GetOverOrderResponse.class);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tv1.setText(data.getMoney() + "CNY");
        holder.tv2.setText(data.getSellNick());
    }

    @Override
    public Spannable getContentSummary(SystemMessage systemMessage) {
        return new SpannableString("您有一条新的系统消息");
    }

    @Override
    public void onItemClick(View view, int i, SystemMessage systemMessage, UIMessage uiMessage) {
        GetOverOrderResponse g = GsonUtils.fromJson(systemMessage.getContent(), GetOverOrderResponse.class);
        Intent intent = new Intent(view.getContext(), OverOrderActivity.class);
        intent.putExtra("data", g);
        view.getContext().startActivity(intent);
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_system, viewGroup, false);
        SystemProvider.ViewHolder holder = new SystemProvider.ViewHolder();
        holder.tv1 = view.findViewById(R.id.tv1);
        holder.tv2 = view.findViewById(R.id.tv2);
        holder.tv3 = view.findViewById(R.id.tv3);
        view.setTag(holder);
        return view;
    }
}
