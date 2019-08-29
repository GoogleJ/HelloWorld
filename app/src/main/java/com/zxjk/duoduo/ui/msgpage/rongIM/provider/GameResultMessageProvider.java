package com.zxjk.duoduo.ui.msgpage.rongIM.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GameResultResponse;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.GameResultMessage;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = GameResultMessage.class, showPortrait = false, centerInHorizontal = true, showSummaryWithName = false)
public class GameResultMessageProvider extends IContainerItemProvider.MessageProvider<GameResultMessage> {

    class ViewHolder {
        TextView tv1;
        TextView tv2;
    }

    @Override
    public void bindView(View view, int i, GameResultMessage gameResultMessage, UIMessage uiMessage) {
        GameResultResponse gameResultResponse = GsonUtils.fromJson(gameResultMessage.getContent(), GameResultResponse.class);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.tv1.setText(gameResultResponse.getTitle());
        viewHolder.tv2.setText(gameResultResponse.getContent());
    }

    @Override
    public Spannable getContentSummary(GameResultMessage gameResultMessage) {
        return new SpannableString("[游戏结局]");
    }

    @Override
    public void onItemClick(View view, int i, GameResultMessage gameResultMessage, UIMessage uiMessage) {
        ActivityUtils.getTopActivity().finish();
        GameResultResponse gameResultResponse = GsonUtils.fromJson(gameResultMessage.getContent(), GameResultResponse.class);
        RongIM.getInstance().startGroupChat(view.getContext(), gameResultResponse.getGroupId(), "对局结果");
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_gameresult, viewGroup, false);
        GameResultMessageProvider.ViewHolder holder = new GameResultMessageProvider.ViewHolder();
        holder.tv1 = view.findViewById(R.id.tv1);
        holder.tv2 = view.findViewById(R.id.tv2);
        view.setTag(holder);
        return view;
    }
}
