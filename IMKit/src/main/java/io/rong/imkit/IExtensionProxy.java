package io.rong.imkit;

import java.util.List;

import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

public abstract interface IExtensionProxy
{
  public abstract void onPreLoadPlugins(Conversation.ConversationType paramConversationType, String paramString, List<IPluginModule> paramList);

  public abstract IExtensionModule onPreLoadEmoticons(Conversation.ConversationType paramConversationType, String paramString, IExtensionModule paramIExtensionModule);
}