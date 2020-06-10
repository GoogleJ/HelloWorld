//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.model;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.Conversation.ConversationType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConversationTypeFilter {
  ConversationTypeFilter.Level mLevel;
  List<ConversationType> mTypes = new ArrayList();

  public static ConversationTypeFilter obtain(ConversationType... conversationType) {
    return new ConversationTypeFilter(conversationType);
  }

  public static ConversationTypeFilter obtain(ConversationTypeFilter.Level level) {
    return new ConversationTypeFilter(level);
  }

  public static ConversationTypeFilter obtain() {
    return new ConversationTypeFilter();
  }

  private ConversationTypeFilter(ConversationType... type) {
    this.mTypes.addAll(Arrays.asList(type));
    this.mLevel = ConversationTypeFilter.Level.CONVERSATION_TYPE;
  }

  private ConversationTypeFilter() {
    this.mLevel = ConversationTypeFilter.Level.ALL;
  }

  private ConversationTypeFilter(ConversationTypeFilter.Level level) {
    this.mLevel = level;
  }

  public ConversationTypeFilter.Level getLevel() {
    return this.mLevel;
  }

  public List<ConversationType> getConversationTypeList() {
    return this.mTypes;
  }

  public boolean hasFilter(Message message) {
    if (this.mLevel == ConversationTypeFilter.Level.ALL) {
      return true;
    } else if (this.mLevel == ConversationTypeFilter.Level.CONVERSATION_TYPE) {
      return this.mTypes.contains(message.getConversationType());
    } else {
      return false;
    }
  }

  public static enum Level {
    ALL,
    CONVERSATION_TYPE,
    NONE;

    private Level() {
    }
  }
}
