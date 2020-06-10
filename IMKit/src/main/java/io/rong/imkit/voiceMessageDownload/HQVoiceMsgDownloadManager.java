//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.voiceMessageDownload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.rong.common.RLog;
import io.rong.imkit.RongIM;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.IRongCallback.IDownloadMediaMessageCallback;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.common.NetUtils;
import io.rong.imlib.model.Message;
import io.rong.message.HQVoiceMessage;

public class HQVoiceMsgDownloadManager {
    private static final String TAG = HQVoiceMsgDownloadManager.class.getSimpleName();
    private Context mContext;
    private final AutoDownloadQueue autoDownloadQueue;
    private ExecutorService executorService;
    private static final int REQUEST_MSG_DOWNLOAD_PERMISSION = 1001;
    private Future<?> future;
    private List<AutoDownloadEntry> errorList;

    private HQVoiceMsgDownloadManager() {
        this.autoDownloadQueue = new AutoDownloadQueue();
        this.future = null;
        this.errorList = null;
    }

    public void init(Context context) {
        AutoDownloadNetWorkChangeReceiver autoDownloadNetWorkChangeReceiver = new AutoDownloadNetWorkChangeReceiver();
        this.mContext = context.getApplicationContext();
        this.executorService = Executors.newSingleThreadExecutor();
        this.errorList = new ArrayList();

        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            context.getApplicationContext().registerReceiver(autoDownloadNetWorkChangeReceiver, intentFilter);
        } catch (Exception var4) {
            RLog.e(TAG, "registerReceiver Exception", var4);
        }

        this.downloadHQVoiceMessage();
    }

    public static HQVoiceMsgDownloadManager getInstance() {
        return HQVoiceMsgDownloadManager.HQVoiceMsgDownloadManagerHolder.instance;
    }

    public void enqueue(Fragment fragment, AutoDownloadEntry autoDownloadEntry) {
        if (autoDownloadEntry != null) {
            Message message = autoDownloadEntry.getMessage();
            if (message.getContent() instanceof HQVoiceMessage && (!this.ifMsgInHashMap(message) || fragment == null)) {
                String[] permission = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
                if (fragment != null && !PermissionCheckUtil.checkPermissions(fragment.getActivity(), permission)) {
                    PermissionCheckUtil.requestPermissions(fragment, permission, 1001);
                }

                HQVoiceMessage hqVoiceMessage = (HQVoiceMessage) message.getContent();
                if (hqVoiceMessage.getLocalPath() == null || TextUtils.isEmpty(hqVoiceMessage.getLocalPath().toString())) {
                    synchronized (this.autoDownloadQueue) {
                        boolean isEmpty = this.autoDownloadQueue.isEmpty();
                        this.autoDownloadQueue.enqueue(autoDownloadEntry);
                        if (isEmpty) {
                            this.autoDownloadQueue.notify();
                        }

                        if (this.future.isDone() && NetUtils.isNetWorkAvailable(this.mContext)) {
                            this.downloadHQVoiceMessage();
                        }

                    }
                }
            }
        }
    }

    private void enqueue(AutoDownloadEntry autoDownloadEntry) {
        this.enqueue((Fragment) null, autoDownloadEntry);
    }

    private Message dequeue() {
        return this.autoDownloadQueue.dequeue();
    }

    private void removeUidInHashMap(String uid) {
        this.autoDownloadQueue.getAutoDownloadEntryHashMap().remove(uid);
    }

    private boolean ifMsgInHashMap(Message message) {
        return this.autoDownloadQueue.ifMsgInHashMap(message);
    }

    private AutoDownloadEntry getMsgEntry(Message message) {
        if (message == null) {
            return null;
        } else {
            AutoDownloadEntry autoDownloadEntry = null;
            if (this.autoDownloadQueue.getAutoDownloadEntryHashMap().containsKey(message.getUId())) {
                autoDownloadEntry = (AutoDownloadEntry) this.autoDownloadQueue.getAutoDownloadEntryHashMap().get(message.getUId());
            }

            return autoDownloadEntry;
        }
    }

    private void downloadHQVoiceMessage() {
        this.future = this.executorService.submit(new Runnable() {
            public void run() {
                while (true) {
                    synchronized (HQVoiceMsgDownloadManager.this.autoDownloadQueue) {
                        if (HQVoiceMsgDownloadManager.this.autoDownloadQueue.isEmpty()) {
                            try {
                                HQVoiceMsgDownloadManager.this.autoDownloadQueue.wait();
                            } catch (InterruptedException var4) {
                                RLog.e(HQVoiceMsgDownloadManager.TAG, "downloadHQVoiceMessage e:" + var4.toString());
                                Thread.currentThread().interrupt();
                            }
                        }
                    }

                    Message message = HQVoiceMsgDownloadManager.this.dequeue();
                    RongIM.getInstance().downloadMediaMessage(message, new IDownloadMediaMessageCallback() {
                        public void onSuccess(Message message) {
                            RLog.d(HQVoiceMsgDownloadManager.TAG, "downloadMediaMessage success");
                            if (HQVoiceMsgDownloadManager.this.errorList != null) {
                                HQVoiceMsgDownloadManager.this.errorList.remove(HQVoiceMsgDownloadManager.this.getMsgEntry(message));
                            }

                            HQVoiceMsgDownloadManager.this.removeUidInHashMap(message.getUId());
                        }

                        public void onProgress(Message message, int progress) {
                            RLog.d(HQVoiceMsgDownloadManager.TAG, "downloadMediaMessage onProgress");
                        }

                        public void onError(Message message, ErrorCode code) {
                            if (HQVoiceMsgDownloadManager.this.errorList != null && !HQVoiceMsgDownloadManager.this.errorList.contains(HQVoiceMsgDownloadManager.this.getMsgEntry(message))) {
                                HQVoiceMsgDownloadManager.this.errorList.add(HQVoiceMsgDownloadManager.this.getMsgEntry(message));
                                RLog.i(HQVoiceMsgDownloadManager.TAG, "onError = " + code.getValue() + " errorList size = " + HQVoiceMsgDownloadManager.this.errorList.size());
                            }

                        }

                        public void onCanceled(Message message) {
                        }
                    });
                }
            }
        });
    }

    void pauseDownloadService() {
    }

    public void resumeDownloadService() {
        if (this.errorList != null && this.errorList.size() != 0) {
            if (this.future.isDone() && NetUtils.isNetWorkAvailable(this.mContext)) {
                this.downloadHQVoiceMessage();
            }

            for (int i = this.errorList.size() - 1; i >= 0; --i) {
                this.enqueue((AutoDownloadEntry) this.errorList.get(i));
            }

        }
    }

    private static class HQVoiceMsgDownloadManagerHolder {
        @SuppressLint({"StaticFieldLeak"})
        private static HQVoiceMsgDownloadManager instance = new HQVoiceMsgDownloadManager();

        private HQVoiceMsgDownloadManagerHolder() {
        }
    }
}