//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.plugin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import io.rong.imkit.RongExtension;

public interface IPluginRequestPermissionResultCallback {
  int REQUEST_CODE_PERMISSION_PLUGIN = 255;

  boolean onRequestPermissionResult(Fragment var1, RongExtension var2, int var3, @NonNull String[] var4, @NonNull int[] var5);
}
