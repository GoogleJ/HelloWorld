//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.rong.imkit.R.color;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.RongContext;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.ProviderContainerView;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Conversation.ConversationType;

public class SubConversationListAdapter extends ConversationListAdapter {
    LayoutInflater mInflater;
    Context mContext;

    public long getItemId(int position) {
        UIConversation conversation = (UIConversation) this.getItem(position);
        return conversation == null ? 0L : (long) conversation.hashCode();
    }

    public SubConversationListAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    protected View newView(Context context, int position, ViewGroup group) {
        View result = this.mInflater.inflate(layout.rc_item_conversation, group, false);
        SubConversationListAdapter.ViewHolder holder = new SubConversationListAdapter.ViewHolder();
        holder.layout = this.findViewById(result, id.rc_item_conversation);
        holder.leftImageLayout = this.findViewById(result, id.rc_item1);
        holder.leftImageView = this.findViewById(result, id.rc_left);
        holder.contentView = this.findViewById(result, id.rc_content);
        result.setTag(holder);
        return result;
    }

    protected void bindView(View v, int position, UIConversation data) {
        SubConversationListAdapter.ViewHolder holder = (SubConversationListAdapter.ViewHolder) v.getTag();
        IContainerItemProvider provider = RongContext.getInstance().getConversationTemplate(data.getConversationType().getName());
        View view = holder.contentView.inflate(provider);
        provider.bindView(view, position, data);
        if (data.isTop()) {
            holder.layout.setBackgroundColor(this.mContext.getResources().getColor(color.rc_conversation_top_bg));
        } else {
            holder.layout.setBackgroundColor(this.mContext.getResources().getColor(color.rc_text_color_primary_inverse));
        }

        ConversationProviderTag tag = RongContext.getInstance().getConversationProviderTag(data.getConversationType().getName());
        int defaultId;
        if (data.getConversationType() == ConversationType.GROUP) {
            defaultId = drawable.rc_default_group_portrait;
        } else if (data.getConversationType() == ConversationType.DISCUSSION) {
            defaultId = drawable.rc_default_discussion_portrait;
        } else {
            defaultId = drawable.rc_default_portrait;
        }

        if (tag.portraitPosition() == 1) {
            holder.leftImageLayout.setVisibility(0);
            if (data.getIconUrl() != null) {
                holder.leftImageView.setAvatar(data.getIconUrl().toString(), defaultId);
            } else {
                holder.leftImageView.setAvatar((String) null, defaultId);
            }
        } else {
            if (tag.portraitPosition() != 3) {
                throw new IllegalArgumentException("the portrait position is wrong!");
            }

            holder.leftImageLayout.setVisibility(8);
        }

    }

    class ViewHolder {
        View layout;
        View leftImageLayout;
        AsyncImageView leftImageView;
        ProviderContainerView contentView;

        ViewHolder() {
        }
    }
}