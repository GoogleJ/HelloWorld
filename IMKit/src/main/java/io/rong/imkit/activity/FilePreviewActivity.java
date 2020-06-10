//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongBaseActivity;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.Event.FileMessageEvent;
import io.rong.imkit.model.Event.RemoteMessageRecallEvent;
import io.rong.imkit.tools.RongWebviewActivity;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utils.FileTypeUtils;
import io.rong.imlib.IRongCallback.IDownloadMediaMessageCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.OperationCallback;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.FileInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.message.FileMessage;
import io.rong.message.MediaMessageContent;

public class FilePreviewActivity extends RongBaseActivity implements OnClickListener {
    private static final String TAG = "FilePreviewActivity";
    public static final int NOT_DOWNLOAD = 0;
    public static final int DOWNLOADED = 1;
    public static final int DOWNLOADING = 2;
    public static final int DELETED = 3;
    public static final int DOWNLOAD_ERROR = 4;
    public static final int DOWNLOAD_CANCEL = 5;
    public static final int DOWNLOAD_SUCCESS = 6;
    public static final int DOWNLOAD_PAUSE = 7;
    public static final int ON_SUCCESS_CALLBACK = 100;
    public static final int ON_PROGRESS_CALLBACK = 101;
    public static final int ON_CANCEL_CALLBACK = 102;
    public static final int ON_ERROR_CALLBACK = 103;
    public static final int REQUEST_CODE_PERMISSION = 104;
    private static final String TXT_FILE = ".txt";
    private static final String APK_FILE = ".apk";
    private ImageView mFileTypeImage;
    private TextView mFileNameView;
    private TextView mFileSizeView;
    private Button mFileButton;
    private ProgressBar mFileDownloadProgressBar;
    private LinearLayout mDownloadProgressView;
    protected TextView mDownloadProgressTextView;
    protected View mCancel;
    protected FilePreviewActivity.FileDownloadInfo mFileDownloadInfo;
    protected FileMessage mFileMessage;
    protected Message mMessage;
    private int mProgress;
    private String mFileName;
    private long mFileSize;
    private List<Toast> mToasts;
    private FrameLayout contentContainer;
    private FilePreviewActivity.SupportResumeStatus supportResumeTransfer;
    private FileInfo info;
    private String pausedPath;
    private long downloadedFileLength;

    public FilePreviewActivity() {
        this.supportResumeTransfer = FilePreviewActivity.SupportResumeStatus.NOT_SET;
        this.info = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(layout.rc_ac_file_download);
        this.initView();
        this.initData();
        this.getFileMessageStatus();
    }

    private void getFileMessageStatus() {
        Uri fileUrl = this.mFileMessage.getFileUrl();
        Uri localUri = this.mFileMessage.getLocalPath();
        boolean isLocalPathExist = false;
        if (localUri != null && FileUtils.isFileExistsWithUri(this, localUri)) {
            isLocalPathExist = true;
        }

        if (!isLocalPathExist && fileUrl != null && !TextUtils.isEmpty(fileUrl.toString())) {
            String url = fileUrl.toString();
            this.pausedPath = FileUtils.getTempFilePath(this, this.mMessage.getMessageId());
            RongIM.getInstance().supportResumeBrokenTransfer(url, new ResultCallback<Boolean>() {
                public void onSuccess(Boolean aBoolean) {
                    FilePreviewActivity.this.supportResumeTransfer = aBoolean ? FilePreviewActivity.SupportResumeStatus.SUPPORT : FilePreviewActivity.SupportResumeStatus.NOT_SUPPORT;
                    if (FilePreviewActivity.this.supportResumeTransfer == FilePreviewActivity.SupportResumeStatus.NOT_SUPPORT) {
                        FilePreviewActivity.this.setViewStatus();
                        FilePreviewActivity.this.getFileDownloadInfo();
                    } else {
                        FilePreviewActivity.this.getFileDownloadInfoInSubThread();
                    }

                }

                public void onError(ErrorCode e) {
                    FilePreviewActivity.this.setViewStatus();
                    FilePreviewActivity.this.getFileDownloadInfo();
                }
            });
        } else {
            this.setViewStatus();
            this.getFileDownloadInfo();
        }

    }

    public void setContentView(int resId) {
        this.contentContainer.removeAllViews();
        View view = LayoutInflater.from(this).inflate(resId, (ViewGroup) null);
        this.contentContainer.addView(view);
    }

    private void initData() {
        Intent intent = this.getIntent();
        if (intent != null) {
            this.mFileDownloadInfo = new FilePreviewActivity.FileDownloadInfo();
            this.mFileMessage = (FileMessage) this.getIntent().getParcelableExtra("FileMessage");
            this.mMessage = (Message) this.getIntent().getParcelableExtra("Message");
            this.mProgress = this.getIntent().getIntExtra("Progress", 0);
            this.mToasts = new ArrayList();
            this.mFileName = this.mFileMessage.getName();
            this.mFileTypeImage.setImageResource(FileTypeUtils.fileTypeImageId(this.mFileName));
            this.mFileNameView.setText(this.mFileName);
            this.mFileSize = this.mFileMessage.getSize();
            this.mFileSizeView.setText(FileTypeUtils.formatFileSize(this.mFileSize));
            this.mFileButton.setOnClickListener(this);
            this.mCancel.setOnClickListener(this);
            RongContext.getInstance().getEventBus().register(this);
        }
    }

    private void initView() {
        this.contentContainer = (FrameLayout) this.findViewById(id.rc_ac_ll_content_container);
        View view = LayoutInflater.from(this).inflate(layout.rc_ac_file_preview_content, (ViewGroup) null);
        this.contentContainer.addView(view);
        this.mFileTypeImage = (ImageView) this.findViewById(id.rc_ac_iv_file_type_image);
        this.mFileNameView = (TextView) this.findViewById(id.rc_ac_tv_file_name);
        this.mFileSizeView = (TextView) this.findViewById(id.rc_ac_tv_file_size);
        this.mFileButton = (Button) this.findViewById(id.rc_ac_btn_download_button);
        this.mDownloadProgressView = (LinearLayout) this.findViewById(id.rc_ac_ll_progress_view);
        this.mCancel = this.findViewById(id.rc_btn_cancel);
        this.mFileDownloadProgressBar = (ProgressBar) this.findViewById(id.rc_ac_pb_download_progress);
        this.mDownloadProgressTextView = (TextView) this.findViewById(id.rc_ac_tv_download_progress);
        TextView title = (TextView) this.findViewById(id.rc_action_bar_title);
        title.setText(string.rc_ac_file_download_preview);
        this.onCreateActionbar(new ActionBar());
    }

    private void setViewStatus() {
        if (this.mMessage.getMessageDirection() == MessageDirection.RECEIVE) {
            if (this.mProgress == 0) {
                this.mDownloadProgressView.setVisibility(8);
                this.mFileButton.setVisibility(0);
            } else if (this.mProgress == 100) {
                this.mDownloadProgressView.setVisibility(8);
                this.mFileButton.setVisibility(0);
            } else {
                this.mFileButton.setVisibility(8);
                this.mDownloadProgressView.setVisibility(0);
                this.mFileDownloadProgressBar.setProgress(this.mProgress);
            }
        }

    }

    private void setViewStatusForResumeTransfer() {
        this.mFileButton.setVisibility(0);
        this.mDownloadProgressTextView.setVisibility(8);
        this.mCancel.setVisibility(8);
    }

    public void onClick(View v) {
        if (v == this.mFileButton) {
            switch (this.mFileDownloadInfo.state) {
                case 0:
                case 3:
                case 4:
                case 5:
                    this.startToDownload();
                    break;
                case 1:
                case 6:
                    this.openFile(this.mFileName, this.mFileMessage.getLocalPath());
                    break;
                case 2:
                    if (this.supportResumeTransfer == FilePreviewActivity.SupportResumeStatus.SUPPORT) {
                        this.mFileDownloadInfo.state = 7;
                        RongIM.getInstance().pauseDownloadMediaMessage(this.mMessage, (OperationCallback) null);
                        this.info = this.getFileInfo();
                        if (this.info != null) {
                            this.downloadedFileLength = this.info.getFinished();
                        } else {
                            this.downloadedFileLength = (long) ((double) this.mFileMessage.getSize() * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                        }

                        this.mFileSizeView.setText(this.getString(string.rc_ac_file_download_progress_pause, new Object[]{FileTypeUtils.formatFileSize(this.downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileSize)}));
                        this.mFileButton.setText(this.getResources().getString(string.rc_ac_file_preview_download_resume));
                    }
                    break;
                case 7:
                    if (RongIM.getInstance().getCurrentConnectionStatus() == ConnectionStatus.NETWORK_UNAVAILABLE) {
                        Toast.makeText(this, this.getString(string.rc_notice_network_unavailable), 0).show();
                        return;
                    }

                    if (this.supportResumeTransfer == FilePreviewActivity.SupportResumeStatus.SUPPORT) {
                        this.mFileDownloadInfo.state = 2;
                        this.downloadFile();
                        if (this.mFileDownloadInfo.state != 4 && this.mFileDownloadInfo.state != 5) {
                            this.mFileButton.setText(this.getResources().getString(string.rc_cancel));
                        }
                    }
            }
        } else if (v == this.mCancel && this.mFileDownloadInfo.state != 5) {
            this.mFileDownloadInfo.state = 5;
            this.refreshDownloadState();
            RongIM.getInstance().cancelDownloadMediaMessage(this.mMessage, (OperationCallback) null);
        }

    }

    private void startToDownload() {
        if (this.mMessage.getContent() instanceof MediaMessageContent) {
            this.resetMediaMessageLocalPath();
            if (RongIM.getInstance().getCurrentConnectionStatus() != ConnectionStatus.CONNECTED) {
                Toast.makeText(this, this.getString(string.rc_notice_network_unavailable), 0).show();
            } else {
                MediaMessageContent mediaMessage = (MediaMessageContent) ((MediaMessageContent) this.mMessage.getContent());
                if (mediaMessage == null || mediaMessage.getMediaUrl() != null && !TextUtils.isEmpty(mediaMessage.getMediaUrl().toString())) {
                    if (this.supportResumeTransfer == FilePreviewActivity.SupportResumeStatus.NOT_SET) {
                        String url = ((FileMessage) this.mMessage.getContent()).getFileUrl().toString();
                        RongIM.getInstance().supportResumeBrokenTransfer(url, new ResultCallback<Boolean>() {
                            public void onSuccess(Boolean aBoolean) {
                                if (FilePreviewActivity.this.mFileDownloadInfo.state == 0 || FilePreviewActivity.this.mFileDownloadInfo.state == 3 || FilePreviewActivity.this.mFileDownloadInfo.state == 4 || FilePreviewActivity.this.mFileDownloadInfo.state == 5) {
                                    FilePreviewActivity.this.supportResumeTransfer = FilePreviewActivity.SupportResumeStatus.valueOf(aBoolean ? 1 : 0);
                                    FilePreviewActivity.this.downloadFile();
                                }

                            }

                            public void onError(ErrorCode e) {
                                FilePreviewActivity.this.mFileDownloadInfo.state = 4;
                                FilePreviewActivity.this.refreshDownloadState();
                            }
                        });
                    } else if (this.mFileDownloadInfo.state == 0 || this.mFileDownloadInfo.state == 4 || this.mFileDownloadInfo.state == 3 || this.mFileDownloadInfo.state == 5) {
                        this.downloadFile();
                    }

                } else {
                    Toast.makeText(this, this.getString(string.rc_ac_file_url_error), 0).show();
                    this.finish();
                }
            }
        } else {
            this.refreshDownloadState();
        }
    }

    protected void resetMediaMessageLocalPath() {
        if (this.mMessage.getContent() instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) this.mMessage.getContent();
            if (fileMessage.getLocalPath() != null && !TextUtils.isEmpty(fileMessage.getLocalPath().toString())) {
                ((FileMessage) this.mMessage.getContent()).setLocalPath((Uri) null);
                this.mFileMessage.setLocalPath((Uri) null);
                EventBus.getDefault().post(this.mMessage);
            }
        }

    }

    public void openFile(String fileName, Uri fileSavePath) {
        try {
            if (!this.openInsidePreview(fileName, fileSavePath)) {
                Intent intent = FileTypeUtils.getOpenFileIntent(fileName, fileSavePath);
                if (intent != null) {
                    intent.addFlags(1);
                    this.startActivity(intent);
                } else {
                    Toast.makeText(this, this.getString(string.rc_ac_file_preview_can_not_open_file), 0).show();
                }
            }
        } catch (Exception var4) {
            RLog.e("FilePreviewActivity", "openFile", var4);
            Toast.makeText(this, this.getString(string.rc_ac_file_preview_can_not_open_file), 0).show();
        }

    }

    protected boolean openInsidePreview(String fileName, Uri uri) {
        String fileSavePath = uri.toString();
        if (fileSavePath.endsWith(".txt")) {
            this.processTxtFile(fileName, uri);
            return true;
        } else if (fileSavePath.endsWith(".apk")) {
            this.processApkFile(uri);
            return true;
        } else {
            return false;
        }
    }

    private void processTxtFile(String fileName, Uri uri) {
        Intent webIntent = new Intent(this, RongWebviewActivity.class);
        webIntent.setPackage(this.getPackageName());
        if (FileUtils.uriStartWithContent(uri)) {
            webIntent.putExtra("url", uri);
        } else {
            String path = uri.toString();
            if (FileUtils.uriStartWithFile(uri)) {
                path = path.substring(7);
            }

            if (VERSION.SDK_INT >= 24) {
                Uri txtUri = FileProvider.getUriForFile(RongContext.getInstance(), RongContext.getInstance().getApplicationContext().getPackageName() + RongContext.getInstance().getResources().getString(string.rc_authorities_fileprovider), new File(path));
                webIntent.putExtra("url", txtUri.toString());
            } else {
                webIntent.putExtra("url", "file://" + path);
            }
        }

        webIntent.putExtra("title", fileName);
        this.startActivity(webIntent);
    }

    private void processApkFile(Uri uri) {
        if (FileUtils.uriStartWithContent(uri)) {
            Intent intent = (new Intent("android.intent.action.VIEW")).setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(1);
            this.startActivity(intent);
        } else {
            String path = uri.toString();
            if (FileUtils.uriStartWithFile(uri)) {
                path = path.substring(7);
            }

            File file = new File(path);
            if (!file.exists()) {
                Toast.makeText(this, this.getString(string.rc_file_not_exist), 0).show();
                return;
            }

            if (VERSION.SDK_INT >= 24) {
                Uri downloaded_apk;
                try {
                    downloaded_apk = FileProvider.getUriForFile(this, this.getPackageName() + this.getString(string.rc_authorities_fileprovider), file);
                } catch (Exception var6) {
                    throw new RuntimeException("Please check IMKit Manifest FileProvider config.");
                }

                Intent intent = (new Intent("android.intent.action.VIEW")).setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
                intent.addFlags(1);
                this.startActivity(intent);
            } else {
                Intent installIntent = new Intent("android.intent.action.VIEW");
                installIntent.setFlags(268435456);
                installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                this.startActivity(installIntent);
            }
        }

    }

    @TargetApi(23)
    private void downloadFile() {
        String[] permission = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
        if (!PermissionCheckUtil.checkPermissions(this, permission)) {
            PermissionCheckUtil.requestPermissions(this, permission, 104);
        } else {
            this.mFileDownloadInfo.state = 2;
            if (this.supportResumeTransfer == FilePreviewActivity.SupportResumeStatus.SUPPORT) {
                this.mFileButton.setText(this.getResources().getString(string.rc_cancel));
                this.mCancel.setVisibility(8);
                this.mDownloadProgressView.setVisibility(0);
                this.mDownloadProgressTextView.setVisibility(8);
                this.info = this.getFileInfo();
                if (this.info != null) {
                    this.downloadedFileLength = this.info.getFinished();
                } else {
                    this.downloadedFileLength = (long) ((double) this.mFileMessage.getSize() * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                }

                this.mFileSizeView.setText(this.getString(string.rc_ac_file_download_progress_tv, new Object[]{FileTypeUtils.formatFileSize(this.downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileSize)}));
            } else {
                this.mFileButton.setVisibility(8);
                this.mDownloadProgressView.setVisibility(0);
                this.mDownloadProgressTextView.setText(this.getString(string.rc_ac_file_download_progress_tv, new Object[]{FileTypeUtils.formatFileSize(0L), FileTypeUtils.formatFileSize(this.mFileSize)}));
            }

            RongIM.getInstance().downloadMediaMessage(this.mMessage, (IDownloadMediaMessageCallback) null);
        }
    }

    private void getFileDownloadInfo() {
        if (this.mFileMessage.getLocalPath() != null) {
            if (FileUtils.isFileExistsWithUri(this, this.mFileMessage.getLocalPath())) {
                this.mFileDownloadInfo.state = 1;
            } else {
                this.mFileDownloadInfo.state = 3;
            }
        } else if (this.mProgress > 0 && this.mProgress < 100) {
            this.mFileDownloadInfo.state = 2;
            this.mFileDownloadInfo.progress = this.mProgress;
        } else {
            this.mFileDownloadInfo.state = 0;
        }

        this.refreshDownloadState();
    }

    private void getFileDownloadInfoForResumeTransfer() {
        if (this.mFileMessage != null) {
            Uri path = this.mFileMessage.getLocalPath();
            if (path != null) {
                boolean exists = FileUtils.isFileExistsWithUri(this, path);
                if (this.info == null) {
                    if (exists) {
                        this.mFileDownloadInfo.state = 1;
                    } else {
                        this.mFileDownloadInfo.state = 3;
                    }
                } else if (exists) {
                    if (this.info.isStop()) {
                        this.mFileDownloadInfo.state = 7;
                    }

                    if (this.info.isDownLoading()) {
                        if (RongIMClient.getInstance().isFileDownloading(this.mMessage.getMessageId())) {
                            this.mFileDownloadInfo.state = 2;
                        } else {
                            this.mFileDownloadInfo.state = 7;
                        }
                    }
                } else {
                    FileUtils.removeFile(this.pausedPath);
                    this.mFileDownloadInfo.state = 3;
                }
            }
        } else if (this.info != null) {
            if (this.info.isStop()) {
                this.mFileDownloadInfo.state = 7;
            }

            if (this.info.isDownLoading()) {
                if (RongIMClient.getInstance().isFileDownloading(this.mMessage.getMessageId())) {
                    this.mFileDownloadInfo.state = 2;
                } else {
                    this.mFileDownloadInfo.state = 7;
                }
            }
        } else if (this.mProgress > 0 && this.mProgress < 100) {
            this.mFileDownloadInfo.state = 2;
            this.mFileDownloadInfo.progress = this.mProgress;
        } else {
            this.mFileDownloadInfo.state = 0;
        }

        this.refreshDownloadState();
    }

    protected void refreshDownloadState() {
        long downloadedFileLength;
        switch (this.mFileDownloadInfo.state) {
            case 0:
                this.mFileButton.setText(this.getString(string.rc_ac_file_preview_begin_download));
                break;
            case 1:
                this.mFileButton.setText(this.getString(string.rc_ac_file_download_open_file_btn));
                break;
            case 2:
                if (this.supportResumeTransfer == FilePreviewActivity.SupportResumeStatus.SUPPORT) {
                    this.mDownloadProgressView.setVisibility(0);
                    this.mFileDownloadProgressBar.setProgress(this.mFileDownloadInfo.progress);
                    this.downloadedFileLength = (long) ((double) this.mFileMessage.getSize() * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                    this.mFileSizeView.setText(this.getString(string.rc_ac_file_download_progress_tv, new Object[]{FileTypeUtils.formatFileSize(this.downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileSize)}));
                    this.mDownloadProgressTextView.setVisibility(8);
                    this.mFileButton.setText(this.getString(string.rc_cancel));
                } else {
                    this.mFileButton.setVisibility(8);
                    this.mDownloadProgressView.setVisibility(0);
                    this.mFileDownloadProgressBar.setProgress(this.mFileDownloadInfo.progress);
                    downloadedFileLength = (long) ((double) this.mFileMessage.getSize() * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                    this.mDownloadProgressTextView.setText(this.getString(string.rc_ac_file_download_progress_tv, new Object[]{FileTypeUtils.formatFileSize(downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileSize)}));
                }
                break;
            case 3:
                this.mFileSizeView.setText(FileTypeUtils.formatFileSize(this.mFileSize));
                this.mFileButton.setText(this.getString(string.rc_ac_file_preview_begin_download));
                break;
            case 4:
                if (this.supportResumeTransfer == FilePreviewActivity.SupportResumeStatus.SUPPORT) {
                    this.mDownloadProgressView.setVisibility(0);
                    this.info = this.getFileInfo();
                    if (this.info != null) {
                        this.mFileDownloadInfo.progress = (int) (100L * this.info.getFinished() / this.info.getLength());
                    }

                    this.mFileDownloadProgressBar.setProgress(this.mFileDownloadInfo.progress);
                    downloadedFileLength = (long) ((double) this.mFileMessage.getSize() * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                    this.mFileSizeView.setText(this.getString(string.rc_ac_file_download_progress_pause, new Object[]{FileTypeUtils.formatFileSize(downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileSize)}));
                    this.mFileButton.setText(this.getString(string.rc_ac_file_preview_download_resume));
                } else {
                    this.mDownloadProgressView.setVisibility(8);
                    this.mFileButton.setVisibility(0);
                    this.mFileSizeView.setText(FileTypeUtils.formatFileSize(this.mFileSize));
                    this.mFileButton.setText(this.getString(string.rc_ac_file_preview_begin_download));
                }

                Toast toast = Toast.makeText(this, this.getString(string.rc_ac_file_preview_download_error), 0);
                if (this.mFileDownloadInfo.state != 5) {
                    toast.show();
                }

                this.mToasts.add(toast);
                break;
            case 5:
                this.mDownloadProgressView.setVisibility(8);
                this.mFileDownloadProgressBar.setProgress(0);
                this.mFileButton.setVisibility(0);
                this.mFileButton.setText(this.getString(string.rc_ac_file_preview_begin_download));
                this.mFileSizeView.setText(FileTypeUtils.formatFileSize(this.mFileSize));
                Toast.makeText(this, this.getString(string.rc_ac_file_preview_download_cancel), 0).show();
                break;
            case 6:
                this.mDownloadProgressView.setVisibility(8);
                this.mFileButton.setVisibility(0);
                this.mFileButton.setText(this.getString(string.rc_ac_file_download_open_file_btn));
                this.mFileSizeView.setText(FileTypeUtils.formatFileSize(this.mFileSize));
                Toast.makeText(this, this.getString(string.rc_ac_file_preview_downloaded) + this.mFileDownloadInfo.path, 0).show();
                break;
            case 7:
                this.mDownloadProgressView.setVisibility(0);
                if (this.info != null) {
                    this.mFileDownloadInfo.progress = (int) (100L * this.info.getFinished() / this.info.getLength());
                    this.downloadedFileLength = this.info.getFinished();
                } else {
                    this.downloadedFileLength = (long) ((double) this.mFileMessage.getSize() * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                }

                this.mFileDownloadProgressBar.setProgress(this.mFileDownloadInfo.progress);
                this.mFileSizeView.setText(this.getString(string.rc_ac_file_download_progress_pause, new Object[]{FileTypeUtils.formatFileSize(this.downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileSize)}));
                this.mFileButton.setText(this.getString(string.rc_ac_file_preview_download_resume));
        }

    }

    public void onEventMainThread(FileMessageEvent event) {
        if (this.mMessage.getMessageId() == event.getMessage().getMessageId()) {
            switch (event.getCallBackType()) {
                case 100:
                    if (this.mFileDownloadInfo.state != 5) {
                        if (event.getMessage() == null || event.getMessage().getContent() == null) {
                            return;
                        }

                        FileMessage fileMessage = (FileMessage) event.getMessage().getContent();
                        this.mFileMessage.setLocalPath(Uri.parse(fileMessage.getLocalPath().toString()));
                        this.mFileDownloadInfo.state = 6;
                        this.mFileDownloadInfo.path = fileMessage.getLocalPath().toString();
                        this.refreshDownloadState();
                    }
                    break;
                case 101:
                    if (this.mFileDownloadInfo.state != 5 && this.mFileDownloadInfo.state != 7) {
                        this.mFileDownloadInfo.state = 2;
                        this.mFileDownloadInfo.progress = event.getProgress();
                        this.refreshDownloadState();
                    }
                    break;
                case 102:
                    this.mFileDownloadInfo.state = 5;
                    this.refreshDownloadState();
                    break;
                case 103:
                    if (this.mFileDownloadInfo.state != 5) {
                        this.mFileDownloadInfo.state = 4;
                        this.refreshDownloadState();
                    }
            }
        }

    }

    public void onEventMainThread(RemoteMessageRecallEvent event) {
        if (this.mMessage != null) {
            int messageId = this.mMessage.getMessageId();
            if (messageId == event.getMessageId()) {
                (new Builder(this, 5)).setMessage(this.getString(string.rc_recall_success)).setPositiveButton(this.getString(string.rc_dialog_ok), new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FilePreviewActivity.this.finish();
                    }
                }).setCancelable(false).show();
            }

        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onRestart() {
        super.onRestart();
        this.pausedPath = FileUtils.getTempFilePath(this, this.mMessage.getMessageId());
        this.getFileDownloadInfoInSubThread();
    }

    private void getFileDownloadInfoInSubThread() {
        (new Thread(new Runnable() {
            public void run() {
                FilePreviewActivity.this.info = FilePreviewActivity.this.getFileInfo();
                FilePreviewActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        FilePreviewActivity.this.setViewStatusForResumeTransfer();
                        FilePreviewActivity.this.getFileDownloadInfoForResumeTransfer();
                    }
                });
            }
        })).start();
    }

    private FileInfo getFileInfo() {
        FileInfo savedFileInfo = null;

        try {
            String savedFileInfoString = FileUtils.getStringFromFile(this.pausedPath);
            if (!TextUtils.isEmpty(savedFileInfoString)) {
                savedFileInfo = this.getFileInfoFromJsonString(savedFileInfoString);
            }
        } catch (Exception var3) {
            RLog.e("FilePreviewActivity", "getFileInfo", var3);
        }

        return savedFileInfo;
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        RongContext.getInstance().getEventBus().unregister(this);

        try {
            Iterator var1 = this.mToasts.iterator();

            while (var1.hasNext()) {
                Toast toast = (Toast) var1.next();
                toast.cancel();
            }
        } catch (Exception var3) {
            RLog.e("FilePreviewActivity", "onDestroy", var3);
        }

        super.onDestroy();
    }

    public Message getMessage() {
        return this.mMessage;
    }

    private FileInfo getFileInfoFromJsonString(String jsonString) {
        FileInfo fileInfo = new FileInfo();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            fileInfo.setFileName(jsonObject.optString("filename"));
            fileInfo.setUrl(jsonObject.optString("url"));
            fileInfo.setLength(jsonObject.optLong("length"));
            fileInfo.setFinished(jsonObject.optLong("finish"));
            fileInfo.setStop(jsonObject.optBoolean("isStop", false));
            fileInfo.setDownLoading(jsonObject.optBoolean("isDownLoading", false));
        } catch (JSONException var4) {
            RLog.e("FilePreviewActivity", "getFileInfoFromJsonString", var4);
        }

        return fileInfo;
    }

    private static enum SupportResumeStatus {
        NOT_SET(-1),
        NOT_SUPPORT(0),
        SUPPORT(1);

        int value;

        private SupportResumeStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static FilePreviewActivity.SupportResumeStatus valueOf(int code) {
            FilePreviewActivity.SupportResumeStatus[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                FilePreviewActivity.SupportResumeStatus c = var1[var3];
                if (code == c.getValue()) {
                    return c;
                }
            }

            FilePreviewActivity.SupportResumeStatus c = NOT_SET;
            c.value = code;
            return c;
        }
    }

    public class FileDownloadInfo {
        public int state;
        public int progress;
        public String path;

        public FileDownloadInfo() {
        }
    }
}
