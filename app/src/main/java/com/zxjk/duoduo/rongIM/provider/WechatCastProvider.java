package com.zxjk.duoduo.rongIM.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.rongIM.message.WechatCastMessage;
import com.zxjk.duoduo.ui.cast.WechatCastDetailActivity;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

@ProviderTag(messageContent = WechatCastMessage.class)
public class WechatCastProvider extends IContainerItemProvider.MessageProvider<WechatCastMessage> {

    @Override
    public void bindView(View view, int i, WechatCastMessage wechatCastMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();

        if (uiMessage.getMessageDirection() == Message.MessageDirection.RECEIVE) {
            holder.sendLayout.setBackgroundResource(io.rong.imkit.R.drawable.shape_rc_bubble_left);
            holder.llHilamg.setGravity(Gravity.END);
        } else {
            holder.sendLayout.setBackgroundResource(io.rong.imkit.R.drawable.shape_rc_bubble_right_white);
            holder.llHilamg.setGravity(Gravity.START);
        }

        holder.title.setText(wechatCastMessage.getTitle());
        if(wechatCastMessage.getType().equals("0")){
            holder.tv1.setText(R.string.wechat_cast);
        }else if(wechatCastMessage.getType().equals("1")){
            holder.tv1.setText(R.string.video_cast);
        }

        GlideUtil.loadCornerImg(holder.icon, wechatCastMessage.getIcon(), 1);
    }

    @Override
    public Spannable getContentSummary(WechatCastMessage WechatCastMessage) {
        return null;
    }

    @Override
    public Spannable getContentSummary(Context context, WechatCastMessage data) {
        return new SpannableString(context.getString(R.string.share_cast_card));
    }

    @Override
    public void onItemClick(View view, int i, WechatCastMessage wechatCastMessage, UIMessage uiMessage) {
        Intent intent = new Intent(view.getContext(), WechatCastDetailActivity.class);
        intent.putExtra("roomId", wechatCastMessage.getRoomID());
        view.getContext().startActivity(intent);
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wechatcast_msg, viewGroup, false);
        ViewHolder holder = new ViewHolder();
        holder.title = view.findViewById(R.id.news_card_title);
        holder.icon = view.findViewById(R.id.news_card_icon);
        holder.sendLayout = view.findViewById(R.id.news_card_send_layout);
        holder.llHilamg = view.findViewById(R.id.ll_hilamg);
        holder.tv1 = view.findViewById(R.id.tv1);
        view.setTag(holder);
        return view;
    }

    class ViewHolder {
        TextView title;
        ImageView icon;
        LinearLayout llHilamg;
        LinearLayout sendLayout;
        TextView tv1;
    }
}