//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.destruct;

import io.rong.eventbus.EventBus;
import io.rong.imkit.model.Event.MessageDeleteEvent;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.DestructCountDownTimerListener;
import io.rong.imlib.destruct.DestructionTaskManager;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Conversation.ConversationType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DestructManager {
  private Map<String, Map<String, DestructCountDownTimerListener>> mMap;
  private Map<String, String> mUnFinishTimes;

  private DestructManager() {
    this.mMap = new HashMap();
    this.mUnFinishTimes = new HashMap();
  }

  public void addListener(String pUId, DestructCountDownTimerListener pDestructListener, String pTag) {
    if (this.mMap.containsKey(pUId)) {
      Map<String, DestructCountDownTimerListener> map = (Map)this.mMap.get(pUId);
      map.put(pTag, pDestructListener);
    } else {
      HashMap<String, DestructCountDownTimerListener> map = new HashMap();
      map.put(pTag, pDestructListener);
      this.mMap.put(pUId, map);
    }

  }

  public void deleteMessage(Message pMessage) {
    DestructionTaskManager.getInstance().deleteMessage(pMessage);
  }

  public void deleteMessages(ConversationType pConversationType, String pTargetId, Message[] pDeleteMessages) {
    DestructionTaskManager.getInstance().deleteMessages(pConversationType, pTargetId, pDeleteMessages);
  }

  public static DestructManager getInstance() {
    return DestructManager.DestructManagerHolder.instance;
  }

  public String getUnFinishTime(String pMessageId) {
    return (String)this.mUnFinishTimes.get(pMessageId);
  }

  public void startDestruct(final Message pMessage) {
    RongIMClient.getInstance().beginDestructMessage(pMessage, new DestructCountDownTimerListener() {
      public void onTick(long untilFinished, String messageId) {
        if (DestructManager.this.mMap.containsKey(messageId)) {
          Map<String, DestructCountDownTimerListener> map = (Map)DestructManager.this.mMap.get(messageId);
          Iterator var5 = map.keySet().iterator();

          while(var5.hasNext()) {
            String key = (String)var5.next();
            ((DestructCountDownTimerListener)map.get(key)).onTick(untilFinished, messageId);
          }

          if (untilFinished == 0L) {
            map.clear();
            DestructManager.this.mMap.remove(messageId);
            DestructManager.this.mUnFinishTimes.remove(messageId);
            EventBus.getDefault().post(new MessageDeleteEvent(new int[]{pMessage.getMessageId()}));
          } else {
            DestructManager.this.mUnFinishTimes.put(messageId, String.valueOf(untilFinished));
          }
        }

      }

      public void onStop(String messageId) {
        if (DestructManager.this.mMap.containsKey(messageId)) {
          Map<String, DestructCountDownTimerListener> map = (Map)DestructManager.this.mMap.get(messageId);
          Iterator var3 = map.keySet().iterator();

          while(var3.hasNext()) {
            String key = (String)var3.next();
            ((DestructCountDownTimerListener)map.get(key)).onStop(messageId);
          }

          DestructManager.this.mUnFinishTimes.remove(messageId);
        }

      }
    });
  }

  public void stopDestruct(Message pMessage) {
    RongIMClient.getInstance().stopDestructMessage(pMessage);
  }

  private static class DestructManagerHolder {
    private static DestructManager instance = new DestructManager();

    private DestructManagerHolder() {
    }
  }
}
