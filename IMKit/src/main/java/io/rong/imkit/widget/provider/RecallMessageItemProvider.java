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
import android.view.View.OnClickListener;
import android.widget.TextView;
import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.R.id;
import io.rong.imkit.R.integer;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.model.Event.RecallMessageEditClickEvent;
import io.rong.imkit.recallEdit.RecallEditCountDownCallBack;
import io.rong.imkit.recallEdit.RecallEditManager;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.model.UserInfo;
import io.rong.message.RecallNotificationMessage;
import java.lang.ref.WeakReference;

@ProviderTag(
        messageContent = RecallNotificationMessage.class,
        showPortrait = false,
        showProgress = false,
        showWarning = false,
        centerInHorizontal = true,
        showSummaryWithName = false
)
public class RecallMessageItemProvider extends MessageProvider<RecallNotificationMessage> {
  public static final String TAG = RecallMessageItemProvider.class.getSimpleName();

  public RecallMessageItemProvider() {
  }

  public void onItemClick(View view, int position, RecallNotificationMessage content, UIMessage message) {
  }

  public void bindView(View v, int position, RecallNotificationMessage content, final UIMessage message) {
    Object tag = v.getTag();
    if (tag instanceof RecallMessageItemProvider.ViewHolder && content != null) {
      RecallMessageItemProvider.ViewHolder viewHolder = (RecallMessageItemProvider.ViewHolder)tag;
      viewHolder.contentTextView.setText(this.getInformation(content));
      long validTime = (long)RongContext.getInstance().getResources().getInteger(integer.rc_message_recall_edit_interval);
      long countDownTime = System.currentTimeMillis() - content.getRecallActionTime();
      if (!TextUtils.isEmpty(viewHolder.messageId)) {
        RecallEditManager.getInstance().cancelCountDown(viewHolder.messageId);
      }

      viewHolder.messageId = String.valueOf(message.getMessageId());
      if (content.getRecallActionTime() > 0L && countDownTime < validTime * 1000L) {
        viewHolder.editTextView.setVisibility(0);
        RecallEditManager.getInstance().startCountDown(message.getMessage(), validTime * 1000L - countDownTime, new RecallMessageItemProvider.RecallEditCountDownListener(viewHolder));
        viewHolder.editTextView.setOnClickListener(new OnClickListener() {
          public void onClick(View view) {
            EventBus.getDefault().post(new RecallMessageEditClickEvent(message.getMessage()));
          }
        });
      } else {
        viewHolder.editTextView.setVisibility(8);
      }
    }

  }

  public void onItemLongClick(View view, int position, RecallNotificationMessage content, UIMessage message) {
  }

  public Spannable getContentSummary(RecallNotificationMessage data) {
    return null;
  }

  public Spannable getContentSummary(Context context, RecallNotificationMessage data) {
    return data != null ? new SpannableString(this.getInformation(data)) : null;
  }

  public View newView(Context context, ViewGroup group) {
    View view = LayoutInflater.from(context).inflate(layout.rc_item_information_notification_message, (ViewGroup)null);
    RecallMessageItemProvider.ViewHolder viewHolder = new RecallMessageItemProvider.ViewHolder();
    viewHolder.contentTextView = (TextView)view.findViewById(id.rc_msg);
    viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
    viewHolder.editTextView = (TextView)view.findViewById(id.rc_edit);
    view.setTag(viewHolder);
    return view;
  }

  private String getInformation(RecallNotificationMessage content) {
    String operatorId = content.getOperatorId();
    String information;
    if (TextUtils.isEmpty(operatorId)) {
      RLog.e(TAG, "RecallMessageItemProvider bindView - operatorId is empty");
      information = RongContext.getInstance().getString(string.rc_recalled_a_message);
    } else if (content.isAdmin()) {
      information = RongContext.getInstance().getString(string.rc_admin_recalled_message);
    } else if (operatorId.equals(RongIM.getInstance().getCurrentUserId())) {
      information = RongContext.getInstance().getString(string.rc_you_recalled_a_message);
    } else {
      UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);
      if (userInfo != null && userInfo.getName() != null) {
        information = userInfo.getName() + RongContext.getInstance().getString(string.rc_recalled_a_message);
      } else {
        information = operatorId + RongContext.getInstance().getString(string.rc_recalled_a_message);
      }
    }

    return information;
  }

  private static class RecallEditCountDownListener implements RecallEditCountDownCallBack {
    private WeakReference<RecallMessageItemProvider.ViewHolder> mHolder;

    public RecallEditCountDownListener(RecallMessageItemProvider.ViewHolder holder) {
      this.mHolder = new WeakReference(holder);
    }

    public void onFinish(String messageId) {
      RecallMessageItemProvider.ViewHolder viewHolder = (RecallMessageItemProvider.ViewHolder)this.mHolder.get();
      if (viewHolder != null && messageId.equals(viewHolder.messageId)) {
        viewHolder.editTextView.setVisibility(8);
      }

    }
  }

  private static class ViewHolder {
    TextView contentTextView;
    TextView editTextView;
    String messageId;

    private ViewHolder() {
    }
  }
}