//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.rong.common.FileUtils;
import io.rong.imkit.RongContext;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.message.CombineMessage;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message.MessageDirection;
import java.io.File;
import java.util.List;

@ProviderTag(
        messageContent = CombineMessage.class,
        showReadState = true
)
public class CombineMessageItemProvider extends MessageProvider<CombineMessage> {
  private static final String TAG = CombineMessageItemProvider.class.getSimpleName();

  public CombineMessageItemProvider() {
  }

  public View newView(Context context, ViewGroup group) {
    View view = LayoutInflater.from(context).inflate(layout.rc_item_combine_message, (ViewGroup)null);
    CombineMessageItemProvider.ViewHolder holder = new CombineMessageItemProvider.ViewHolder();
    holder.message = (LinearLayout)view.findViewById(id.rc_message);
    holder.title = (TextView)view.findViewById(id.title);
    holder.summary = (TextView)view.findViewById(id.summary);
    view.setTag(holder);
    return view;
  }

  public void bindView(View v, int position, CombineMessage content, UIMessage message) {
    CombineMessageItemProvider.ViewHolder holder = (CombineMessageItemProvider.ViewHolder)v.getTag();
    if (message.getMessageDirection() == MessageDirection.SEND) {
      holder.message.setBackgroundResource(drawable.rc_ic_bubble_right);
    } else {
      holder.message.setBackgroundResource(drawable.rc_ic_bubble_left);
    }

    String title = this.getTitle(content);
    content.setTitle(title);
    holder.title.setText(content.getTitle());
    String summary = "";
    List<String> summarys = content.getSummaryList();

    for(int i = 0; i < summarys.size() && i < 4; ++i) {
      if (i == 0) {
        summary = (String)summarys.get(i);
      } else {
        summary = summary + "\n" + (String)summarys.get(i);
      }
    }

    holder.summary.setText(summary);
  }

  private String getTitle(CombineMessage content) {
    String title = "";
    Context context = RongContext.getInstance();
    if (ConversationType.GROUP.equals(content.getConversationType())) {
      title = context.getString(string.rc_combine_group_chat);
    } else {
      List<String> nameList = content.getNameList();
      if (nameList == null) {
        return title;
      }

      if (nameList.size() == 1) {
        title = String.format(context.getString(string.rc_combine_the_group_chat_of), nameList.get(0));
      } else if (nameList.size() == 2) {
        title = String.format(context.getString(string.rc_combine_the_group_chat_of), (String)nameList.get(0) + " " + context.getString(string.rc_combine_and) + " " + (String)nameList.get(1));
      }
    }

    if (TextUtils.isEmpty(title)) {
      title = context.getString(string.rc_combine_chat_history);
    }

    return title;
  }

  public Spannable getContentSummary(CombineMessage data) {
    return null;
  }

  public Spannable getContentSummary(Context context, CombineMessage data) {
    return new SpannableString(context.getString(string.rc_message_content_combine));
  }

  public void onItemClick(View view, int position, CombineMessage content, UIMessage message) {
    String type = "local";
    Uri uri = content.getLocalPath();
    if ((uri == null || !(new File(uri.toString().substring(7))).exists()) && content.getMediaUrl() != null) {
      String filePath = FileUtils.getCachePath(RongContext.getInstance()) + File.separator + "combine" + File.separator + RongUtils.md5(content.getMediaUrl().toString()) + ".html";
      if ((new File(filePath)).exists()) {
        uri = Uri.parse("file://" + filePath);
      } else {
        uri = content.getMediaUrl();
        type = "media";
      }
    }

    if (uri == null) {
      Context context = view.getContext();
      (new Builder(context)).setMessage(context.getString(string.rc_combine_history_deleted)).setPositiveButton(context.getString(string.rc_dialog_ok), (OnClickListener)null).show();
    } else {
      Intent intent = new Intent("io.rong.imkit.intent.action.combinewebview");
      intent.setPackage(view.getContext().getPackageName());
      intent.addFlags(268435456);
      intent.putExtra("messageId", message.getMessageId());
      intent.putExtra("uri", uri.toString());
      intent.putExtra("type", type);
      intent.putExtra("title", content.getTitle());
      view.getContext().startActivity(intent);
    }
  }

  private static class ViewHolder {
    LinearLayout message;
    TextView title;
    TextView summary;

    private ViewHolder() {
    }
  }
}