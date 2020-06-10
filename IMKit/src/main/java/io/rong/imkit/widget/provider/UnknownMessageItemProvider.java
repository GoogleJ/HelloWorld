//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UnknownMessage;

@ProviderTag(
        messageContent = UnknownMessage.class,
        showPortrait = false,
        showWarning = false,
        centerInHorizontal = true,
        showSummaryWithName = false
)
public class UnknownMessageItemProvider extends MessageProvider<MessageContent> {
    public UnknownMessageItemProvider() {
    }

    public void bindView(View v, int position, MessageContent content, UIMessage message) {
        UnknownMessageItemProvider.ViewHolder viewHolder = (UnknownMessageItemProvider.ViewHolder) v.getTag();
        viewHolder.contentTextView.setText(string.rc_message_unknown);
    }

    public Spannable getContentSummary(MessageContent data) {
        return null;
    }

    public Spannable getContentSummary(Context context, MessageContent data) {
        return new SpannableString(context.getResources().getString(string.rc_message_unknown));
    }

    public void onItemClick(View view, int position, MessageContent content, UIMessage message) {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(layout.rc_item_information_notification_message, (ViewGroup) null);
        UnknownMessageItemProvider.ViewHolder viewHolder = new UnknownMessageItemProvider.ViewHolder();
        viewHolder.contentTextView = (TextView) view.findViewById(id.rc_msg);
        viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        view.setTag(viewHolder);
        return view;
    }

    public void onItemLongClick(View view, int position, MessageContent content, UIMessage message) {
    }

    private static class ViewHolder {
        TextView contentTextView;

        private ViewHolder() {
        }
    }
}