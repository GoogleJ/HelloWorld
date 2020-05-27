package com.zxjk.duoduo.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.rongIM.message.FakeC2CMessage;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(
        messageContent = FakeC2CMessage.class,
        showPortrait = false,
        showProgress = false,
        showWarning = false,
        centerInHorizontal = true,
        showSummaryWithName = false
)
public class FakeC2CMessageProvider extends IContainerItemProvider.MessageProvider<FakeC2CMessage> {
    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_fake_c2c, viewGroup, false);
        ViewHolder holder = new ViewHolder();
        holder.tvUserName = view.findViewById(R.id.tvUserName);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, int var2, FakeC2CMessage message, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvUserName.setText(message.getName());
    }

    @Override
    public Spannable getContentSummary(Context context, FakeC2CMessage data) {
        return new SpannableString(context.getString(R.string.c2cmessaging));
    }

    @Override
    public Spannable getContentSummary(FakeC2CMessage var1) {
        return null;
    }

    @Override
    public void onItemClick(View var1, int var2, FakeC2CMessage var3, UIMessage var4) {
    }

    static class ViewHolder {
        TextView tvUserName;
    }
}
