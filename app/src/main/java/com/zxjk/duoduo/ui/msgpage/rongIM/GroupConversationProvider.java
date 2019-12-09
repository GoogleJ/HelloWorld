package com.zxjk.duoduo.ui.msgpage.rongIM;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zxjk.duoduo.R;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ConversationProviderTag(
        conversationType = "group"
)
public class GroupConversationProvider extends io.rong.imkit.widget.provider.GroupConversationProvider implements IContainerItemProvider.ConversationProvider<UIConversation> {

    private int colorSocialSign;
    private int colorText;

    private List<ViewHolder> holderList = new ArrayList<>();

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        if (colorSocialSign == 0) {
            colorSocialSign = Color.parseColor("#EC7A00");
            colorText = context.getColor(R.color.rc_text_color_primary);
        }
        View view = super.newView(context, viewGroup);
        ViewHolder holder = new ViewHolder();
        holder.title = view.findViewById(io.rong.imkit.R.id.rc_conversation_title);
        holder.tvSocialSign = view.findViewById(io.rong.imkit.R.id.tvSocialSign);
        holderList.add(holder);

        return view;
    }

    @Override
    public void bindView(View view, int position, UIConversation data) {
        super.bindView(view, position, data);
        ViewHolder holder = holderList.get(position);
        if (holder != null) {
            if (data.getUIConversationTitle().contains("おれは人间をやめるぞ！ジョジョ―――ッ!")) {
                holder.tvSocialSign.setVisibility(View.VISIBLE);
                holder.title.setTextColor(colorSocialSign);
                holder.title.setText(data.getUIConversationTitle().replace("おれは人间をやめるぞ！ジョジョ―――ッ!", ""));
            } else {
                holder.tvSocialSign.setVisibility(View.GONE);
                holder.title.setTextColor(colorText);
            }
        }
    }

    private class ViewHolder {
        private TextView title;
        private TextView tvSocialSign;
    }
}
