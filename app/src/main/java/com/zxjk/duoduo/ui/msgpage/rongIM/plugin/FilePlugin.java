package com.zxjk.duoduo.ui.msgpage.rongIM.plugin;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.zxjk.duoduo.R;

public class FilePlugin extends io.rong.imkit.widget.provider.FilePlugin {
    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.icon_file);
    }
}
