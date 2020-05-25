//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.model;

import io.rong.imlib.model.Conversation.ConversationType;

public class ConversationInfo {
  ConversationType conversationType;
  String targetId;

  ConversationInfo() {
  }

  public static ConversationInfo obtain(ConversationType type, String id) {
    ConversationInfo info = new ConversationInfo();
    info.conversationType = type;
    info.targetId = id;
    return info;
  }

  public ConversationType getConversationType() {
    return this.conversationType;
  }

  public String getTargetId() {
    return this.targetId;
  }
}
