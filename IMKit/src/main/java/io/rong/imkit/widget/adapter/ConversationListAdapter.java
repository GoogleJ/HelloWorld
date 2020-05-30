//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.RongContext;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.Event.MessageDeleteEvent;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.ProviderContainerView;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;

public class ConversationListAdapter extends BaseAdapter<UIConversation> {
    private static final String TAG = "ConversationListAdapter";
    LayoutInflater mInflater;
    Context mContext;
    private ConversationListAdapter.OnPortraitItemClick mOnPortraitItemClick;

    public long getItemId(int position) {
        UIConversation conversation = this.getItem(position);
        return conversation == null ? 0L : (long) conversation.hashCode();
    }

    public ConversationListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    public int findGatheredItem(ConversationType type) {
        int index = this.getCount();
        int position = -1;

        while (index-- > 0) {
            UIConversation uiConversation = (UIConversation) this.getItem(index);
            if (uiConversation.getConversationType().equals(type)) {
                position = index;
                break;
            }
        }

        return position;
    }

    public int findPosition(ConversationType type, String targetId) {
        int index = this.getCount();
        int position = -1;

        while (index-- > 0) {
            if (((UIConversation) this.getItem(index)).getConversationType().equals(type) && ((UIConversation) this.getItem(index)).getConversationTargetId().equals(targetId)) {
                position = index;
                break;
            }
        }

        return position;
    }

    protected View newView(Context context, int position, ViewGroup group) {
        View result = this.mInflater.inflate(layout.rc_item_conversation, null);
        ConversationListAdapter.ViewHolder holder = new ConversationListAdapter.ViewHolder();
        holder.leftImageLayout = this.findViewById(result, id.rc_item1);
        holder.leftImageView = this.findViewById(result, id.rc_left);
        holder.contentView = this.findViewById(result, id.rc_content);
        result.setTag(holder);
        return result;
    }

    protected void bindView(View v, int position, final UIConversation data) {
        ConversationListAdapter.ViewHolder holder = (ConversationListAdapter.ViewHolder) v.getTag();
        if (data != null) {
            IContainerItemProvider provider = RongContext.getInstance().getConversationTemplate(data.getConversationType().getName());
            if (provider == null) {
                RLog.e("ConversationListAdapter", "provider is null");
            } else {
                View view = holder.contentView.inflate(provider);
                provider.bindView(view, position, data);

                ConversationProviderTag tag = RongContext.getInstance().getConversationProviderTag(data.getConversationType().getName());
                int defaultId;
                if (data.getConversationType().equals(ConversationType.GROUP)) {
                    defaultId = drawable.rc_default_group_portrait;
                } else if (data.getConversationType().equals(ConversationType.DISCUSSION)) {
                    defaultId = drawable.rc_default_discussion_portrait;
                } else {
                    defaultId = drawable.rc_default_portrait;
                }

                if (tag.portraitPosition() == 1) {
                    holder.leftImageLayout.setVisibility(0);
                    holder.leftImageLayout.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if (ConversationListAdapter.this.mOnPortraitItemClick != null) {
                                ConversationListAdapter.this.mOnPortraitItemClick.onPortraitItemClick(v, data);
                            }

                        }
                    });
                    holder.leftImageLayout.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            if (ConversationListAdapter.this.mOnPortraitItemClick != null) {
                                ConversationListAdapter.this.mOnPortraitItemClick.onPortraitItemLongClick(v, data);
                            }

                            return true;
                        }
                    });
                    if (data.getConversationGatherState()) {
                        holder.leftImageView.setAvatar((String) null, defaultId);
                    } else if (data.getIconUrl() != null) {
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

                MessageContent content = data.getMessageContent();
                if (content != null && content.isDestruct()) {
                    RongIMClient.getInstance().getMessage(data.getLatestMessageId(), new ResultCallback<Message>() {
                        public void onSuccess(Message message) {
                            if (message == null) {
                                EventBus.getDefault().post(new MessageDeleteEvent(new int[]{data.getLatestMessageId()}));
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                }
            }
        }
    }

    public void setOnPortraitItemClick(ConversationListAdapter.OnPortraitItemClick onPortraitItemClick) {
        this.mOnPortraitItemClick = onPortraitItemClick;
    }

    public interface OnPortraitItemClick {
        void onPortraitItemClick(View var1, UIConversation var2);

        boolean onPortraitItemLongClick(View var1, UIConversation var2);
    }

    protected class ViewHolder {
        public View leftImageLayout;
        public AsyncImageView leftImageView;
        public ProviderContainerView contentView;

        protected ViewHolder() {
        }
    }
}