//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.voiceMessageDownload;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.rong.imkit.voiceMessageDownload.AutoDownloadEntry.DownloadPriority;
import io.rong.imlib.model.Message;

class AutoDownloadQueue {
  private ConcurrentLinkedQueue<AutoDownloadEntry> highPriority = new ConcurrentLinkedQueue();
  private ConcurrentLinkedQueue<AutoDownloadEntry> normalPriority = new ConcurrentLinkedQueue();
  private HashMap<String, AutoDownloadEntry> autoDownloadEntryHashMap = new HashMap();
  private static final int MAX_QUEUE_COUNT = 100;

  AutoDownloadQueue() {
  }

  void enqueue(AutoDownloadEntry autoDownloadEntry) {
    Message message = autoDownloadEntry.getMessage();
    if (autoDownloadEntry.getPriority() == DownloadPriority.NORMAL) {
      this.normalPriority.add(autoDownloadEntry);
    } else if (autoDownloadEntry.getPriority() == DownloadPriority.HIGH) {
      this.highPriority.add(autoDownloadEntry);
    }

    if (!this.autoDownloadEntryHashMap.containsKey(message.getUId())) {
      this.autoDownloadEntryHashMap.put(message.getUId(), autoDownloadEntry);
    }

    int doubleQueueSize = this.normalPriority.size() + this.highPriority.size();
    if (doubleQueueSize > 100) {
      if (!this.normalPriority.isEmpty()) {
        this.autoDownloadEntryHashMap.remove(((AutoDownloadEntry)this.normalPriority.poll()).getMessage().getUId());
      } else {
        AutoDownloadEntry highItem = (AutoDownloadEntry)this.highPriority.poll();
        if (highItem != null) {
          this.autoDownloadEntryHashMap.remove(highItem.getMessage().getUId());
        }
      }
    }

  }

  boolean ifMsgInHashMap(Message message) {
    return this.autoDownloadEntryHashMap.containsKey(message.getUId());
  }

  Message dequeue() {
    if (!this.highPriority.isEmpty()) {
      return ((AutoDownloadEntry)this.highPriority.poll()).getMessage();
    } else {
      return !this.normalPriority.isEmpty() ? ((AutoDownloadEntry)this.normalPriority.poll()).getMessage() : null;
    }
  }

  public boolean isEmpty() {
    return this.highPriority.isEmpty() && this.normalPriority.isEmpty();
  }

  HashMap<String, AutoDownloadEntry> getAutoDownloadEntryHashMap() {
    return this.autoDownloadEntryHashMap;
  }
}