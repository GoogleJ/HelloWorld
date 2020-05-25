//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Iterator;

import io.rong.common.LibStorageUtils;
import io.rong.common.RLog;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.string;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.activity.FileManagerActivity;
import io.rong.imkit.model.FileInfo;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.IPluginRequestPermissionResultCallback;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.IRongCallback.ISendMediaMessageCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

public class FilePlugin implements IPluginModule, IPluginRequestPermissionResultCallback {
    private static final String TAG = "FileInputProvider";
    private static final int REQUEST_FILE = 100;
    private static final int REQUEST_FILE_Q = 101;
    private static final int TIME_DELAY = 400;
    private ConversationType conversationType;
    private String targetId;

    public FilePlugin() {
    }

    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, drawable.rc_ic_files_selector);
    }

    public String obtainTitle(Context context) {
        return context.getString(string.rc_plugins_files);
    }

    public void onClick(Fragment currentFragment, RongExtension extension) {
        this.conversationType = extension.getConversationType();
        this.targetId = extension.getTargetId();
        if (LibStorageUtils.isBuildAndTargetForQ(currentFragment.getContext())) {
            Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("*/*");
            extension.startActivityForPluginResult(intent, 101, this);
        } else {
            String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
            if (PermissionCheckUtil.checkPermissions(currentFragment.getContext(), permissions)) {
                Intent intent = new Intent(currentFragment.getActivity(), FileManagerActivity.class);
                extension.startActivityForPluginResult(intent, 100, this);
            } else {
                extension.requestPermissionForPluginResult(permissions, 255, this);
            }
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (data != null) {
                HashSet<FileInfo> selectedFileInfos = (HashSet) data.getSerializableExtra("sendSelectedFiles");
                FilePlugin.SendMediaMessageThread thread = new FilePlugin.SendMediaMessageThread(this.conversationType, this.targetId, selectedFileInfos);
                thread.start();
            }
        } else if (requestCode == 101 && data != null) {
            Uri uri = data.getData();
            FilePlugin.SendMediaMessageThreadForQ thread = new FilePlugin.SendMediaMessageThreadForQ(this.conversationType, this.targetId, uri);
            thread.start();
        }

    }

    public boolean onRequestPermissionResult(Fragment currentFragment, RongExtension extension, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionCheckUtil.checkPermissions(currentFragment.getActivity(), permissions)) {
            Intent intent = new Intent(currentFragment.getActivity(), FileManagerActivity.class);
            extension.startActivityForPluginResult(intent, 100, this);
        } else {
            extension.showRequestPermissionFailedAlter(PermissionCheckUtil.getNotGrantedPermissionMsg(currentFragment.getActivity(), permissions, grantResults));
        }

        return true;
    }

    private static class SendMediaMessageThread extends Thread {
        private ConversationType conversationType;
        private String targetId;
        private HashSet<FileInfo> selectedFileInfos;

        private SendMediaMessageThread(ConversationType conversationType, String targetId, HashSet<FileInfo> selectedFileInfos) {
            this.conversationType = conversationType;
            this.targetId = targetId;
            this.selectedFileInfos = selectedFileInfos;
        }

        public void run() {
            Iterator var1 = this.selectedFileInfos.iterator();

            while (var1.hasNext()) {
                FileInfo fileInfo = (FileInfo) var1.next();
                Uri filePath = Uri.parse("file://" + fileInfo.getFilePath());
                FileMessage fileMessage = FileMessage.obtain(filePath);
                if (fileMessage != null) {
                    fileMessage.setType(fileInfo.getSuffix());
                    Message message = Message.obtain(this.targetId, this.conversationType, fileMessage);
                    RongIM.getInstance().sendMediaMessage(message, (String) null, (String) null, (ISendMediaMessageCallback) null);

                    try {
                        Thread.sleep(400L);
                    } catch (InterruptedException var7) {
                        RLog.e("FileInputProvider", "sendMediaMessage e:" + var7.toString());
                        Thread.currentThread().interrupt();
                    }
                }
            }

        }
    }

    private static class SendMediaMessageThreadForQ extends Thread {
        private ConversationType conversationType;
        private String targetId;
        private Uri uri;

        private SendMediaMessageThreadForQ(ConversationType conversationType, String targetId, Uri uri) {
            this.conversationType = conversationType;
            this.targetId = targetId;
            this.uri = uri;
        }

        public void run() {
            FileMessage fileMessage = FileMessage.obtain(this.uri);
            if (fileMessage != null) {
                DocumentFile documentFile = DocumentFile.fromSingleUri(RongContext.getInstance(), this.uri);
                if (documentFile == null) {
                    RLog.e("FileInputProvider", "sendMediaMessage DocumentFile is null");
                    return;
                }

                fileMessage.setName(documentFile.getName());
                fileMessage.setType(documentFile.getType());
                fileMessage.setSize(documentFile.length());
                Message message = Message.obtain(this.targetId, this.conversationType, fileMessage);
                RongIM.getInstance().sendMediaMessage(message, (String) null, (String) null, (ISendMediaMessageCallback) null);

                try {
                    Thread.sleep(400L);
                } catch (InterruptedException var5) {
                    RLog.e("FileInputProvider", "sendMediaMessage e:" + var5.toString());
                    Thread.currentThread().interrupt();
                }
            }

        }
    }
}