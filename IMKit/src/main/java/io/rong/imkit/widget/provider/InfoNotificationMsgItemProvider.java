//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.message.InformationNotificationMessage;

@ProviderTag(
        messageContent = InformationNotificationMessage.class,
        showPortrait = false,
        showProgress = false,
        showWarning = false,
        centerInHorizontal = true,
        showSummaryWithName = false
)
public class InfoNotificationMsgItemProvider extends MessageProvider<InformationNotificationMessage> {
  public InfoNotificationMsgItemProvider() {
  }

  public void bindView(View v, int position, InformationNotificationMessage content, UIMessage message) {
    InfoNotificationMsgItemProvider.ViewHolder viewHolder = (InfoNotificationMsgItemProvider.ViewHolder)v.getTag();
    if (content != null && !TextUtils.isEmpty(content.getMessage())) {
      viewHolder.contentTextView.setText(content.getMessage());
    }

  }

  public Spannable getContentSummary(InformationNotificationMessage data) {
    return null;
  }

  public Spannable getContentSummary(Context context, InformationNotificationMessage data) {
    return data != null && !TextUtils.isEmpty(data.getMessage()) ? new SpannableString(data.getMessage()) : null;
  }

  public void onItemClick(View view, int position, InformationNotificationMessage content, UIMessage message) {
  }

  public void onItemLongClick(View view, int position, InformationNotificationMessage content, UIMessage message) {
  }

  public View newView(Context context, ViewGroup group) {
    View view = LayoutInflater.from(context).inflate(layout.rc_item_information_notification_message, (ViewGroup)null);
    InfoNotificationMsgItemProvider.ViewHolder viewHolder = new InfoNotificationMsgItemProvider.ViewHolder();
    viewHolder.contentTextView = (TextView)view.findViewById(id.rc_msg);
    viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
    view.setTag(viewHolder);
    return view;
  }

  private static class ViewHolder {
    TextView contentTextView;

    private ViewHolder() {
    }
  }
}