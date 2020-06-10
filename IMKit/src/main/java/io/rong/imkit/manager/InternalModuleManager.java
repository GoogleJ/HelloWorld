//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.manager;

import android.content.Context;
import io.rong.common.RLog;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation.ConversationType;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class InternalModuleManager {
  private static final String TAG = "InternalModuleManager";
  private static IExternalModule callModule;
  private static IExternalModule callModule2;

  private InternalModuleManager() {
  }

  public static InternalModuleManager getInstance() {
    return InternalModuleManager.SingletonHolder.sInstance;
  }

  public static void init(Context context) {
    RLog.i("InternalModuleManager", "init");

    String moudleName2;
    Class cls2;
    Constructor constructor2;
    try {
      moudleName2 = "io.rong.callkit.RongCallModule";
      cls2 = Class.forName(moudleName2);
      constructor2 = cls2.getConstructor();
      callModule = (IExternalModule)constructor2.newInstance();
      callModule.onCreate(context);
    } catch (Exception var5) {
      RLog.i("InternalModuleManager", "Can not find RongCallModule.");
    }

    try {
      moudleName2 = "io.rong.signalingkit.RCSCallModule";
      cls2 = Class.forName(moudleName2);
      constructor2 = cls2.getConstructor();
      callModule2 = (IExternalModule)constructor2.newInstance();
      callModule2.onCreate(context);
    } catch (Exception var4) {
      RLog.i("InternalModuleManager", "Can not find RCSCallModule.");
    }

  }

  public void onInitialized(String appKey) {
    RLog.i("InternalModuleManager", "onInitialized");
    if (callModule != null) {
      callModule.onInitialized(appKey);
    }

    if (callModule2 != null) {
      callModule2.onInitialized(appKey);
    }

  }

  public List<IPluginModule> getInternalPlugins(ConversationType conversationType) {
    List<IPluginModule> pluginModules = new ArrayList();
    if (callModule != null && (conversationType.equals(ConversationType.PRIVATE) || conversationType.equals(ConversationType.DISCUSSION) || conversationType.equals(ConversationType.GROUP))) {
      pluginModules.addAll(callModule.getPlugins(conversationType));
    }

    if (callModule2 != null && (conversationType.equals(ConversationType.PRIVATE) || conversationType.equals(ConversationType.DISCUSSION) || conversationType.equals(ConversationType.GROUP))) {
      pluginModules.addAll(callModule2.getPlugins(conversationType));
    }

    return pluginModules;
  }

  public void onConnected(String token) {
    RLog.i("InternalModuleManager", "onConnected");
    if (callModule != null) {
      callModule.onConnected(token);
    }

    if (callModule2 != null) {
      callModule2.onConnected(token);
    }

  }

  public void onLoaded() {
    RLog.i("InternalModuleManager", "onLoaded");
    if (callModule != null) {
      callModule.onViewCreated();
    }

    if (callModule2 != null) {
      callModule2.onViewCreated();
    }

  }

  static class SingletonHolder {
    static InternalModuleManager sInstance = new InternalModuleManager();

    SingletonHolder() {
    }
  }
}
