//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.rong.common.RLog;
import io.rong.imkit.R.string;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.message.CombineMessage;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.IRongCallback.ISendMediaMessageCallback;
import io.rong.imlib.IRongCallback.ISendMessageCallback;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient.SendImageMessageCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.MediaMessageContent;

public class ForwardManager {
  private static final String TAG = ForwardManager.class.getSimpleName();
  private static final int TIME_DELAY = 400;
  private static final int SUMMARY_MAX_SIZE = 4;
  private ExecutorService executor;

  private ForwardManager() {
    this.executor = this.getExecutor();
  }

  public static ForwardManager getInstance() {
    return ForwardManager.SingletonHolder.sInstance;
  }

  public static void forwardMessage(Activity activity, ArrayList<Conversation> conversations) {
    Intent intent = new Intent();
    intent.putExtra("index", activity.getIntent().getIntExtra("index", 0));
    intent.putIntegerArrayListExtra("messageIds", activity.getIntent().getIntegerArrayListExtra("messageIds"));
    intent.putParcelableArrayListExtra("conversations", conversations);
    activity.setResult(-1, intent);
    activity.finish();
  }

  public static List<Message> filterMessagesList(Context context, List<Message> messages) {
    List<Message> forwardMessagesList = new ArrayList();
    if (context == null) {
      RLog.e(TAG, "filterMessagesList context is null");
      return forwardMessagesList;
    } else {
      Iterator var3 = messages.iterator();

      while(var3.hasNext()) {
        Message message = (Message)var3.next();
        if (!allowForward(message)) {
          (new Builder(context)).setMessage(context.getString(string.rc_combine_unsupported)).setPositiveButton(context.getString(string.rc_dialog_ok), (OnClickListener)null).show();
          forwardMessagesList.clear();
          return forwardMessagesList;
        }

        forwardMessagesList.add(message);
      }

      Collections.sort(forwardMessagesList, new Comparator<Message>() {
        public int compare(Message o1, Message o2) {
          return (int)(o1.getSentTime() - o2.getSentTime());
        }
      });
      return forwardMessagesList;
    }
  }

  private static boolean allowForward(Message message) {
    if (message == null) {
      RLog.d(TAG, "Forwarding is not allowed, message is null");
      return false;
    } else if (message.getSentStatus() != SentStatus.SENDING && message.getSentStatus() != SentStatus.FAILED && message.getSentStatus() != SentStatus.CANCELED) {
      MessageContent messageContent = message.getContent();
      if (messageContent == null) {
        RLog.d(TAG, "Forwarding is not allowed, message:" + message);
        return false;
      } else if (messageContent.isDestruct()) {
        RLog.d(TAG, "Destruct message not allow forward");
        return false;
      } else {
        MessageTag tag = (MessageTag)messageContent.getClass().getAnnotation(MessageTag.class);
        if (tag == null) {
          RLog.d(TAG, "Forwarding is not allowed, tag is null");
          return false;
        } else {
          String var3 = tag.value();
          byte var4 = -1;
          switch(var3.hashCode()) {
            case -2042295573:
              if (var3.equals("RC:VcMsg")) {
                var4 = 1;
              }
              break;
            case -1835503925:
              if (var3.equals("RC:CombineMsg")) {
                var4 = 11;
              }
              break;
            case -1160730064:
              if (var3.equals("RC:VCSummary")) {
                var4 = 13;
              }
              break;
            case -961182724:
              if (var3.equals("RC:FileMsg")) {
                var4 = 6;
              }
              break;
            case -911587622:
              if (var3.equals("RC:ImgTextMsg")) {
                var4 = 7;
              }
              break;
            case 105394658:
              if (var3.equals("RC:HQVCMsg")) {
                var4 = 2;
              }
              break;
            case 447751656:
              if (var3.equals("RC:CardMsg")) {
                var4 = 9;
              }
              break;
            case 659653286:
              if (var3.equals("RC:GIFMsg")) {
                var4 = 5;
              }
              break;
            case 751141447:
              if (var3.equals("RC:ImgMsg")) {
                var4 = 4;
              }
              break;
            case 796721677:
              if (var3.equals("RC:LBSMsg")) {
                var4 = 10;
              }
              break;
            case 1044016768:
              if (var3.equals("RC:StkMsg")) {
                var4 = 8;
              }
              break;
            case 1076608122:
              if (var3.equals("RC:TxtMsg")) {
                var4 = 0;
              }
              break;
            case 1098742835:
              if (var3.equals("RC:VSTMsg")) {
                var4 = 12;
              }
              break;
            case 1310555117:
              if (var3.equals("RC:SightMsg")) {
                var4 = 3;
              }
          }

          switch(var4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
              return true;
            default:
              RLog.d(TAG, "Forwarding is not allowed, type:" + tag.value());
              return false;
          }
        }
      }
    } else {
      RLog.d(TAG, "Forwarding is not allowed, status:" + message.getSentStatus());
      return false;
    }
  }

  public void forwardMessages(int index, List<Conversation> conversations, List<Integer> messageIds, List<Message> messages) {
    List<Message> forwardMessages = new ArrayList();
    Iterator var6 = messages.iterator();

    while(var6.hasNext()) {
      Message msg = (Message)var6.next();
      if (messageIds.contains(msg.getMessageId())) {
        forwardMessages.add(msg);
      }
    }

    this.forwardMessages(index, conversations, forwardMessages);
  }

  private void forwardMessages(final int index, final List<Conversation> conversations, final List<Message> messages) {
    this.getExecutor().execute(new Runnable() {
      public void run() {
        if (index == 0) {
          ForwardManager.this.forwardMessageByStep(conversations, messages);
        } else if (index == 1) {
          ForwardManager.this.forwardMessageByCombine(conversations, messages);
        }

      }
    });
  }

  private void forwardMessageByStep(List<Conversation> conversations, List<Message> messages) {
    Iterator var3 = conversations.iterator();

    while(var3.hasNext()) {
      Conversation conversation = (Conversation)var3.next();
      Iterator var5 = messages.iterator();

      while(var5.hasNext()) {
        Message msg = (Message)var5.next();
        this.startForwardMessageByStep(conversation.getTargetId(), conversation.getConversationType(), msg);

        try {
          Thread.sleep(400L);
        } catch (InterruptedException var8) {
          RLog.e(TAG, "forwardMessageByStep e:" + var8.toString());
          Thread.currentThread().interrupt();
        }
      }
    }

  }

  private void startForwardMessageByStep(String id, ConversationType type, Message fwdMessage) {
    MessageContent messageContent = fwdMessage.getContent();
    messageContent.setUserInfo((UserInfo)null);
    Message message = Message.obtain(id, type, messageContent);
    if (messageContent instanceof ImageMessage) {
      ImageMessage imageMessage = (ImageMessage)messageContent;
      if (imageMessage.getRemoteUri() != null && !imageMessage.getRemoteUri().toString().startsWith("file")) {
        RongIM.getInstance().sendMessage(message, (String)null, (String)null, (ISendMediaMessageCallback)null);
      } else {
        RongIM.getInstance().sendImageMessage(message, (String)null, (String)null, (SendImageMessageCallback)null);
      }
    } else if (messageContent instanceof LocationMessage) {
      RongIM.getInstance().sendLocationMessage(message, (String)null, (String)null, (ISendMessageCallback)null);
    } else if (messageContent instanceof MediaMessageContent) {
      MediaMessageContent mediaMessageContent = (MediaMessageContent)messageContent;
      if (mediaMessageContent.getMediaUrl() != null) {
        RongIM.getInstance().sendMessage(message, (String)null, (String)null, (ISendMediaMessageCallback)null);
      } else {
        RongIM.getInstance().sendMediaMessage(message, (String)null, (String)null, (ISendMediaMessageCallback)null);
      }
    } else {
      RongIM.getInstance().sendMessage(message, (String)null, (String)null, (ISendMessageCallback)null);
    }

  }

  private void forwardMessageByCombine(List<Conversation> conversations, List<Message> messages) {
    Uri uri = CombineMessageUtils.getInstance().getUrlFromMessageList(messages);
    ConversationType type = ((Message)messages.get(0)).getConversationType();
    CombineMessage combine = CombineMessage.obtain(uri);
    combine.setConversationType(type);
    if (!ConversationType.GROUP.equals(type)) {
      combine.setNameList(this.getNameList(messages));
    }

    combine.setTitle(this.getTitle(combine));
    combine.setSummaryList(this.getSummaryList(messages));

    for(int i = 0; i < conversations.size(); ++i) {
      Conversation conversation = (Conversation)conversations.get(i);
      Message message = Message.obtain(conversation.getTargetId(), conversation.getConversationType(), combine);
      RongIM.getInstance().sendMediaMessage(message, (String)null, (String)null, (ISendMediaMessageCallback)null);

      try {
        Thread.sleep(400L);
      } catch (InterruptedException var10) {
        RLog.e(TAG, "forwardMessageByStep e:" + var10.toString());
        Thread.currentThread().interrupt();
      }
    }

  }

  private String getTitle(CombineMessage content) {
    Context context = RongContext.getInstance();
    String title = context.getString(string.rc_combine_chat_history);
    if (ConversationType.GROUP.equals(content.getConversationType())) {
      title = context.getString(string.rc_combine_group_chat);
    } else if (ConversationType.PRIVATE.equals(content.getConversationType())) {
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

    return title;
  }

  private List<String> getNameList(List<Message> messages) {
    List<String> names = new ArrayList();
    Iterator var3 = messages.iterator();

    while(var3.hasNext()) {
      Message msg = (Message)var3.next();
      if (names.size() == 2) {
        return names;
      }

      UserInfo info = RongUserInfoManager.getInstance().getUserInfo(msg.getSenderUserId());
      if (info == null) {
        RLog.d(TAG, "getNameList name is null, msg:" + msg);
        break;
      }

      String name = info.getName();
      if (name != null && !names.contains(name)) {
        names.add(name);
      }
    }

    return names;
  }

  private List<String> getSummaryList(List<Message> messages) {
    List<String> summaryList = new ArrayList();
    ConversationType type = ((Message)messages.get(0)).getConversationType();

    for(int i = 0; i < messages.size() && i < 4; ++i) {
      Message message = (Message)messages.get(i);
      MessageContent content = message.getContent();
      UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
      String userName = "";
      if (type.equals(ConversationType.GROUP)) {
        GroupUserInfo groupUserInfo = RongUserInfoManager.getInstance().getGroupUserInfo(message.getTargetId(), message.getSenderUserId());
        if (groupUserInfo != null) {
          userName = groupUserInfo.getNickname();
        }
      }

      if (TextUtils.isEmpty(userName) && userInfo != null) {
        userName = userInfo.getName();
      }

      MessageTag tag = (MessageTag)content.getClass().getAnnotation(MessageTag.class);
      String tagValue = tag != null ? tag.value() : null;
      String text;
      if ("RC:CardMsg".equals(tagValue)) {
        text = RongContext.getInstance().getString(string.rc_message_content_card);
      } else if ("RC:StkMsg".equals(tagValue)) {
        text = RongContext.getInstance().getString(string.rc_message_content_sticker);
      } else if (!"RC:VCSummary".equals(tagValue) && !"RC:VSTMsg".equals(tagValue)) {
        if ("RCJrmf:RpMsg".equals(tagValue)) {
          text = RongContext.getInstance().getString(string.rc_message_content_rp);
        } else {
          Spannable spannable = RongContext.getInstance().getMessageTemplate(content.getClass()).getContentSummary(RongContext.getInstance(), content);
          text = spannable.toString();
        }
      } else {
        text = RongContext.getInstance().getString(string.rc_message_content_vst);
      }

      summaryList.add(userName + " : " + text);
    }

    return summaryList;
  }

  private ExecutorService getExecutor() {
    if (this.executor == null) {
      this.executor = Executors.newSingleThreadExecutor();
    }

    return this.executor;
  }

  private static class SingletonHolder {
    static ForwardManager sInstance = new ForwardManager();

    private SingletonHolder() {
    }
  }
}