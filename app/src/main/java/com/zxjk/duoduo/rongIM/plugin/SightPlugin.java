package com.zxjk.duoduo.rongIM.plugin;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.zxjk.duoduo.R;

import io.rong.imlib.model.Conversation;

public class SightPlugin extends io.rong.sight.SightPlugin {
    private Conversation.ConversationType conversationType;
    private String targetId;

    @Override
    public Drawable obtainDrawable(Context context) {
        super.obtainDrawable(context);
        return ContextCompat.getDrawable(context, R.drawable.ic_audio_sight);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.audio_video_sight);
    }

}
