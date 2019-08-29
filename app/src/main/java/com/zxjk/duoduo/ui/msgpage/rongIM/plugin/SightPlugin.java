package com.zxjk.duoduo.ui.msgpage.rongIM.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.zxjk.duoduo.R;
import java.io.File;

import io.rong.imkit.RongExtension;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utils.RongOperationPermissionUtils;
import io.rong.imlib.model.Conversation;
import io.rong.sight.record.SightRecordActivity;

public class SightPlugin extends io.rong.sight.SightPlugin {
    private Conversation.ConversationType conversationType;
    private String targetId;

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ic_audio_sight);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.audio_video_sight);
    }

    @Override
    public void onClick(Fragment currentFragment, RongExtension extension) {
        if (RongOperationPermissionUtils.isMediaOperationPermit(currentFragment.getActivity())) {
            String[] permissions = new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
            this.conversationType = extension.getConversationType();
            this.targetId = extension.getTargetId();
            if (PermissionCheckUtil.checkPermissions(currentFragment.getActivity(), permissions)) {
                this.startSightRecord(currentFragment);
            } else {
                extension.requestPermissionForPluginResult(permissions, 255, this);
            }
        }
    }

    private void startSightRecord(Fragment currentFragment) {
        File saveDir = null;
        if (ContextCompat.checkSelfPermission(currentFragment.getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            saveDir = new File(Environment.getExternalStorageDirectory(), currentFragment.getString(R.string.rc_media_message_default_save_path));
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }

        Intent intent = new Intent(currentFragment.getActivity(), SightRecordActivity.class);
        intent.putExtra("conversationType", this.conversationType.getValue());
        intent.putExtra("targetId", this.targetId);
        intent.putExtra("recordSightDir", saveDir.getAbsolutePath());
        int maxRecordDuration = 10;

        try {
            maxRecordDuration = currentFragment.getActivity().getResources().getInteger(R.integer.rc_sight_max_record_duration);
        } catch (Resources.NotFoundException var6) {
            var6.printStackTrace();
        }

        intent.putExtra("maxRecordDuration", maxRecordDuration);
        currentFragment.getActivity().startActivity(intent);
    }
}
