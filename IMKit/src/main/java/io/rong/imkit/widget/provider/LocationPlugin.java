//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.fragment.app.Fragment;

import io.rong.imkit.R.drawable;
import io.rong.imkit.R.string;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.LocationProvider.LocationCallback;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.IRongCallback.ISendMessageCallback;
import io.rong.imlib.model.Message;
import io.rong.message.LocationMessage;

public class LocationPlugin implements IPluginModule {
    public LocationPlugin() {
    }

    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(drawable.rc_ext_plugin_location_selector);
    }

    public String obtainTitle(Context context) {
        return context.getString(string.rc_plugin_location);
    }

    public void onClick(Fragment currentFragment, final RongExtension extension) {
        if (RongContext.getInstance() != null && RongContext.getInstance().getLocationProvider() != null) {
            RongContext.getInstance().getLocationProvider().onStartLocation(currentFragment.getActivity().getApplicationContext(), new LocationCallback() {
                public void onSuccess(LocationMessage locationMessage) {
                    Message message = Message.obtain(extension.getTargetId(), extension.getConversationType(), locationMessage);
                    RongIM.getInstance().sendLocationMessage(message, (String) null, (String) null, (ISendMessageCallback) null);
                }

                public void onFailure(String msg) {
                }
            });
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}