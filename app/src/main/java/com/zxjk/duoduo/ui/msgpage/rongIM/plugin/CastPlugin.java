package com.zxjk.duoduo.ui.msgpage.rongIM.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.fragment.app.Fragment;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.cast.CreateChooseCastTypeActivity;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

public class CastPlugin implements IPluginModule {
    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getDrawable(R.drawable.icon_plugin_cast);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.cast);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        Intent intent = new Intent(fragment.getActivity(), CreateChooseCastTypeActivity.class);
        intent.putExtra("groupId", rongExtension.getTargetId());
        fragment.startActivity(intent);
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
