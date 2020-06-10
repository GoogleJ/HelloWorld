package io.rong.imkit;

import java.util.List;

import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

public abstract interface IExtensionModule
{
  public abstract void onInit(String paramString);

  public abstract void onConnect(String paramString);

  public abstract void onAttachedToExtension(RongExtension paramRongExtension);

  public abstract void onDetachedFromExtension();

  public abstract void onReceivedMessage(Message paramMessage);

  public abstract List<IPluginModule> getPluginModules(Conversation.ConversationType paramConversationType);

  public abstract List<IEmoticonTab> getEmoticonTabs();

  public abstract void onDisconnect();
}