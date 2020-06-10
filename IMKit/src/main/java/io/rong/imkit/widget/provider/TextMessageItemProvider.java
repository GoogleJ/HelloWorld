//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import io.rong.imkit.RongContext;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongIM.ConversationBehaviorListener;
import io.rong.imkit.RongIM.ConversationClickListener;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.AutoLinkTextView;
import io.rong.imkit.widget.ILinkClickListener;
import io.rong.imkit.widget.LinkTextViewMovementMethod;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.RongIMClient.DestructCountDownTimerListener;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.message.TextMessage;
import java.lang.ref.WeakReference;

@ProviderTag(
        messageContent = TextMessage.class,
        showReadState = true
)
public class TextMessageItemProvider extends MessageProvider<TextMessage> {
  private static final String TAG = "TextMessageItemProvider";

  public TextMessageItemProvider() {
  }

  public View newView(Context context, ViewGroup group) {
    View view = LayoutInflater.from(context).inflate(layout.rc_item_destruct_text_message, (ViewGroup)null);
    TextMessageItemProvider.ViewHolder holder = new TextMessageItemProvider.ViewHolder();
    holder.message = (AutoLinkTextView)view.findViewById(16908308);
    holder.unRead = (TextView)view.findViewById(id.tv_unread);
    holder.sendFire = (FrameLayout)view.findViewById(id.fl_send_fire);
    holder.receiverFire = (FrameLayout)view.findViewById(id.fl_receiver_fire);
    holder.receiverFireImg = (ImageView)view.findViewById(id.iv_receiver_fire);
    holder.receiverFireText = (TextView)view.findViewById(id.tv_receiver_fire);
    view.setTag(holder);
    return view;
  }

  public Spannable getContentSummary(TextMessage data) {
    return null;
  }

  public Spannable getContentSummary(Context context, TextMessage data) {
    if (data == null) {
      return null;
    } else if (data.isDestruct()) {
      return new SpannableString(context.getString(string.rc_message_content_burn));
    } else {
      String content = data.getContent();
      if (content != null) {
        if (content.length() > 100) {
          content = content.substring(0, 100);
        }

        return new SpannableString(AndroidEmoji.ensure(content));
      } else {
        return null;
      }
    }
  }

  public void onItemClick(View view, int position, TextMessage content, UIMessage message) {
    TextMessageItemProvider.ViewHolder holder = (TextMessageItemProvider.ViewHolder)view.getTag();
    if (content != null && content.isDestruct() && message.getMessage().getReadTime() <= 0L) {
      holder.unRead.setVisibility(8);
      holder.message.setVisibility(0);
      holder.receiverFireText.setVisibility(0);
      holder.receiverFireImg.setVisibility(8);
      this.processTextView(view, position, content, message, holder.message);
      DestructManager.getInstance().startDestruct(message.getMessage());
    }

  }

  public void bindView(View v, int position, TextMessage content, UIMessage data) {
    TextMessageItemProvider.ViewHolder holder = (TextMessageItemProvider.ViewHolder)v.getTag();
    if (data.getMessageDirection() == MessageDirection.SEND) {
      holder.message.setBackgroundResource(drawable.rc_ic_bubble_right);
    } else {
      holder.message.setBackgroundResource(drawable.rc_ic_bubble_left);
    }

    if (content.isDestruct()) {
      this.bindFireView(v, position, content, data);
    } else {
      holder.sendFire.setVisibility(8);
      holder.receiverFire.setVisibility(8);
      holder.unRead.setVisibility(8);
      holder.message.setVisibility(0);
      AutoLinkTextView textView = holder.message;
      this.processTextView(v, position, content, data, textView);
    }

  }

  private void processTextView(final View v, int position, TextMessage content, final UIMessage data, final AutoLinkTextView pTextView) {
    if (data.getTextMessageContent() != null) {
      int len = data.getTextMessageContent().length();
      if (v.getHandler() != null && len > 500) {
        v.getHandler().postDelayed(new Runnable() {
          public void run() {
            pTextView.setText(data.getTextMessageContent());
          }
        }, 50L);
      } else {
        pTextView.setText(data.getTextMessageContent());
      }
    }

    pTextView.setMovementMethod(new LinkTextViewMovementMethod(new ILinkClickListener() {
      public boolean onLinkClick(String link) {
        ConversationBehaviorListener listener = RongContext.getInstance().getConversationBehaviorListener();
        ConversationClickListener clickListener = RongContext.getInstance().getConversationClickListener();
        boolean result = false;
        if (listener != null) {
          result = listener.onMessageLinkClick(v.getContext(), link);
        } else if (clickListener != null) {
          result = clickListener.onMessageLinkClick(v.getContext(), link, data.getMessage());
        }

        if (listener == null && clickListener == null || !result) {
          String str = link.toLowerCase();
          if (str.startsWith("http") || str.startsWith("https")) {
            Intent intent = new Intent("io.rong.imkit.intent.action.webview");
            intent.setPackage(v.getContext().getPackageName());
            intent.putExtra("url", link);
            v.getContext().startActivity(intent);
            result = true;
          }
        }

        return result;
      }
    }));
    pTextView.setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        v.performClick();
      }
    });
    pTextView.setOnLongClickListener(new OnLongClickListener() {
      public boolean onLongClick(View view) {
        return v.performLongClick();
      }
    });
    pTextView.stripUnderlines();
  }

  private void bindFireView(View pV, int pPosition, TextMessage pContent, UIMessage pData) {
    TextMessageItemProvider.ViewHolder holder = (TextMessageItemProvider.ViewHolder)pV.getTag();
    if (pData.getMessageDirection() == MessageDirection.SEND) {
      holder.sendFire.setVisibility(0);
      holder.receiverFire.setVisibility(8);
      holder.unRead.setVisibility(8);
      holder.message.setVisibility(0);
      this.processTextView(pV, pPosition, pContent, pData, holder.message);
    } else {
      holder.sendFire.setVisibility(8);
      holder.receiverFire.setVisibility(0);
      DestructManager.getInstance().addListener(pData.getUId(), new TextMessageItemProvider.DestructListener(holder, pData), "TextMessageItemProvider");
      if (pData.getMessage().getReadTime() > 0L) {
        holder.unRead.setVisibility(8);
        holder.message.setVisibility(0);
        holder.receiverFireText.setVisibility(0);
        String unFinishTime;
        if (TextUtils.isEmpty(pData.getUnDestructTime())) {
          unFinishTime = DestructManager.getInstance().getUnFinishTime(pData.getUId());
        } else {
          unFinishTime = pData.getUnDestructTime();
        }

        holder.receiverFireText.setText(unFinishTime);
        holder.receiverFireImg.setVisibility(8);
        this.processTextView(pV, pPosition, pContent, pData, holder.message);
        DestructManager.getInstance().startDestruct(pData.getMessage());
      } else {
        holder.unRead.setVisibility(0);
        holder.message.setVisibility(8);
        holder.receiverFireText.setVisibility(8);
        holder.receiverFireImg.setVisibility(0);
      }
    }

  }

  private static class DestructListener implements DestructCountDownTimerListener {
    private WeakReference<TextMessageItemProvider.ViewHolder> mHolder;
    private UIMessage mUIMessage;

    public DestructListener(TextMessageItemProvider.ViewHolder pHolder, UIMessage pUIMessage) {
      this.mHolder = new WeakReference(pHolder);
      this.mUIMessage = pUIMessage;
    }

    public void onTick(long millisUntilFinished, String pMessageId) {
      if (this.mUIMessage.getUId().equals(pMessageId)) {
        TextMessageItemProvider.ViewHolder viewHolder = (TextMessageItemProvider.ViewHolder)this.mHolder.get();
        if (viewHolder != null) {
          viewHolder.receiverFireText.setVisibility(0);
          viewHolder.receiverFireImg.setVisibility(8);
          String unDestructTime = String.valueOf(Math.max(millisUntilFinished, 1L));
          viewHolder.receiverFireText.setText(unDestructTime);
          this.mUIMessage.setUnDestructTime(unDestructTime);
        }
      }

    }

    public void onStop(String messageId) {
      if (this.mUIMessage.getUId().equals(messageId)) {
        TextMessageItemProvider.ViewHolder viewHolder = (TextMessageItemProvider.ViewHolder)this.mHolder.get();
        if (viewHolder != null) {
          viewHolder.receiverFireText.setVisibility(8);
          viewHolder.receiverFireImg.setVisibility(0);
          this.mUIMessage.setUnDestructTime((String)null);
        }
      }

    }

    public void setHolder(TextMessageItemProvider.ViewHolder pHolder) {
      this.mHolder = new WeakReference(pHolder);
    }

    public void setUIMessage(UIMessage pUIMessage) {
      this.mUIMessage = pUIMessage;
    }
  }

  private static class ViewHolder {
    AutoLinkTextView message;
    TextView unRead;
    FrameLayout sendFire;
    FrameLayout receiverFire;
    ImageView receiverFireImg;
    TextView receiverFireText;

    private ViewHolder() {
    }
  }
}