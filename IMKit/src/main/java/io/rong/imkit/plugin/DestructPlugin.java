//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import io.rong.imkit.R.drawable;
import io.rong.imkit.R.string;
import io.rong.imkit.RongExtension;
import io.rong.imkit.dialog.BurnHintDialog;

public class DestructPlugin implements IPluginModule {
  public DestructPlugin() {
  }

  public Drawable obtainDrawable(Context context) {
    return ContextCompat.getDrawable(context, drawable.rc_ext_plugin_fire_selector);
  }

  public String obtainTitle(Context context) {
    return context.getString(string.rc_plugin_destruct);
  }

  public void onClick(Fragment currentFragment, RongExtension extension) {
    if (!BurnHintDialog.isFirstClick(currentFragment.getContext())) {
      (new BurnHintDialog()).show(currentFragment.getFragmentManager());
    }

    extension.enterBurnMode();
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
  }
}
