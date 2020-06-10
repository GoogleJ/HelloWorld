package io.rong.imkit;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.PublicServiceMenuItem;

public abstract interface IPublicServiceMenuClickListener {
    public abstract boolean onClick(Conversation.ConversationType paramConversationType, String paramString, PublicServiceMenuItem paramPublicServiceMenuItem);
}