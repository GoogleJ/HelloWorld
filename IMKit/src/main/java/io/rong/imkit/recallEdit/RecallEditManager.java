//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.recallEdit;

import io.rong.imlib.model.Message;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class RecallEditManager {
  private Map<String, ConcurrentHashMap<Integer, RecallEditCountDownTimer>> timerMap;

  private RecallEditManager() {
    this.timerMap = new HashMap();
  }

  public static RecallEditManager getInstance() {
    return RecallEditManager.RecallEditManagerHolder.instance;
  }

  public void startCountDown(Message message, long millisInFuture, RecallEditCountDownCallBack callBack) {
    String key = message.getConversationType().getName() + message.getTargetId();
    ConcurrentHashMap<Integer, RecallEditCountDownTimer> recallEditTimerMap = (ConcurrentHashMap)this.timerMap.get(key);
    RecallEditCountDownTimer countDownTimer;
    if (recallEditTimerMap != null) {
      countDownTimer = (RecallEditCountDownTimer)recallEditTimerMap.get(message.getMessageId());
      if (countDownTimer != null) {
        countDownTimer.setListener(new RecallEditManager.RecallEditTimerListener(message, callBack));
        return;
      }
    }

    countDownTimer = new RecallEditCountDownTimer(String.valueOf(message.getMessageId()), new RecallEditManager.RecallEditTimerListener(message, callBack), millisInFuture);
    if (recallEditTimerMap == null) {
      ConcurrentHashMap<Integer, RecallEditCountDownTimer> timers = new ConcurrentHashMap();
      timers.put(message.getMessageId(), countDownTimer);
      this.timerMap.put(key, timers);
    } else {
      recallEditTimerMap.put(message.getMessageId(), countDownTimer);
    }

    countDownTimer.start();
  }

  public void cancelCountDownInConversation(String key) {
    ConcurrentHashMap<Integer, RecallEditCountDownTimer> timers = (ConcurrentHashMap)this.timerMap.get(key);
    if (timers != null && timers.size() > 0) {
      Set<Entry<Integer, RecallEditCountDownTimer>> entrySet = timers.entrySet();
      Iterator var4 = entrySet.iterator();

      while(var4.hasNext()) {
        Entry<Integer, RecallEditCountDownTimer> entry = (Entry)var4.next();
        RecallEditCountDownTimer timer = (RecallEditCountDownTimer)entry.getValue();
        if (timer != null) {
          timer.cancel();
        }
      }

      this.timerMap.remove(key);
    }

  }

  public void cancelCountDown(String messageId) {
    Set<Entry<String, ConcurrentHashMap<Integer, RecallEditCountDownTimer>>> timerEntrySet = this.timerMap.entrySet();
    Iterator var3 = timerEntrySet.iterator();

    while(var3.hasNext()) {
      Entry<String, ConcurrentHashMap<Integer, RecallEditCountDownTimer>> timerEntry = (Entry)var3.next();
      ConcurrentHashMap<Integer, RecallEditCountDownTimer> timers = (ConcurrentHashMap)timerEntry.getValue();
      if (timers != null && timers.size() > 0) {
        RecallEditCountDownTimer timer = (RecallEditCountDownTimer)timers.get(Integer.valueOf(messageId));
        if (timer != null) {
          timer.cancel();
          timers.remove(Integer.valueOf(messageId));
        }
      }
    }

  }

  private class RecallEditTimerListener implements RecallEditCountDownTimerListener {
    private Message message;
    private RecallEditCountDownCallBack callBack;

    public RecallEditTimerListener(Message message, RecallEditCountDownCallBack callBack) {
      this.message = message;
      this.callBack = callBack;
    }

    public void onTick(long untilFinished, String messageId) {
    }

    public void onFinish(String messageId) {
      Map<Integer, RecallEditCountDownTimer> value = (Map)RecallEditManager.this.timerMap.get(this.message.getConversationType().getName() + messageId);
      if (value != null && value.get(this.message.getMessageId()) != null) {
        value.remove(this.message.getMessageId());
      }

      if (this.callBack != null) {
        this.callBack.onFinish(messageId);
      }

    }
  }

  private static class RecallEditManagerHolder {
    private static RecallEditManager instance = new RecallEditManager();

    private RecallEditManagerHolder() {
    }
  }
}
