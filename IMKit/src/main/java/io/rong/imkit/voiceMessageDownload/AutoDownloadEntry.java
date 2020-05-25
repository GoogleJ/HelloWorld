//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.voiceMessageDownload;

import io.rong.imlib.model.Message;

public class AutoDownloadEntry {
  private Message message;
  private AutoDownloadEntry.DownloadPriority priority;

  public AutoDownloadEntry(Message message, AutoDownloadEntry.DownloadPriority priority) {
    this.message = message;
    this.priority = priority;
  }

  AutoDownloadEntry.DownloadPriority getPriority() {
    return this.priority;
  }

  public Message getMessage() {
    return this.message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  public static enum DownloadPriority {
    NORMAL,
    HIGH;

    private DownloadPriority() {
    }
  }
}