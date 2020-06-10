//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.activity;

import android.annotation.TargetApi;
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

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongBaseActivity;
import io.rong.imkit.RongIM;
import io.rong.imkit.tools.RongWebviewActivity;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.utils.FileTypeUtils;
import io.rong.imlib.IRongCallback.IDownloadMediaFileCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.OperationCallback;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.FileInfo;

public class WebFilePreviewActivity extends RongBaseActivity implements OnClickListener {
    private static final String TAG = "WebFilePreviewActivity";
    private static final String PATH = "webfile";
    public static final int NOT_DOWNLOAD = 0;
    public static final int DOWNLOADED = 1;
    public static final int DOWNLOADING = 2;
    public static final int DELETED = 3;
    public static final int DOWNLOAD_ERROR = 4;
    public static final int DOWNLOAD_CANCEL = 5;
    public static final int DOWNLOAD_SUCCESS = 6;
    public static final int DOWNLOAD_PAUSE = 7;
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
    private File mAttachFile;
    protected WebFilePreviewActivity.FileDownloadInfo mFileDownloadInfo;
    private FrameLayout mContentContainer;
    private WebFilePreviewActivity.SupportResumeStatus supportResumeTransfer;
    private FileInfo mFileInfo;
    private String pausedPath;
    private long downloadedFileLength;

    public WebFilePreviewActivity() {
        this.supportResumeTransfer = WebFilePreviewActivity.SupportResumeStatus.NOT_SET;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(layout.rc_ac_file_download);
        this.initView();
        this.initData();
    }

    public void setContentView(int resId) {
        this.mContentContainer.removeAllViews();
        this.mContentContainer.addView(LayoutInflater.from(this).inflate(resId, (ViewGroup) null));
    }

    private void initView() {
        this.mContentContainer = (FrameLayout) this.findViewById(id.rc_ac_ll_content_container);
        View view = LayoutInflater.from(this).inflate(layout.rc_ac_file_preview_content, (ViewGroup) null);
        this.mContentContainer.addView(view);
        this.mFileTypeImage = (ImageView) this.findViewById(id.rc_ac_iv_file_type_image);
        this.mFileNameView = (TextView) this.findViewById(id.rc_ac_tv_file_name);
        this.mFileSizeView = (TextView) this.findViewById(id.rc_ac_tv_file_size);
        this.mFileButton = (Button) this.findViewById(id.rc_ac_btn_download_button);
        this.mDownloadProgressView = (LinearLayout) this.findViewById(id.rc_ac_ll_progress_view);
        this.mCancel = this.findViewById(id.rc_btn_cancel);
        this.mDownloadProgressTextView = (TextView) this.findViewById(id.rc_ac_tv_download_progress);
        this.mFileDownloadProgressBar = (ProgressBar) this.findViewById(id.rc_ac_pb_download_progress);
        TextView title = (TextView) this.findViewById(id.rc_action_bar_title);
        title.setText(string.rc_ac_file_download_preview);
        this.mCancel.setVisibility(8);
        this.onCreateActionbar(new ActionBar());
    }

    private void initData() {
        Intent intent = this.getIntent();
        if (intent != null) {
            this.mFileDownloadInfo = new WebFilePreviewActivity.FileDownloadInfo();
            this.mFileDownloadInfo.url = intent.getStringExtra("fileUrl");
            this.mFileDownloadInfo.fileName = intent.getStringExtra("fileName");
            this.mFileDownloadInfo.size = Long.valueOf(intent.getStringExtra("fileSize"));
            this.mFileDownloadInfo.uid = RongUtils.md5(this.mFileDownloadInfo.fileName + this.mFileDownloadInfo.size);
            this.mFileDownloadInfo.path = FileUtils.getCachePath(this, "webfile");
            this.mFileTypeImage.setImageResource(FileTypeUtils.fileTypeImageId(this.mFileDownloadInfo.fileName));
            this.mFileNameView.setText(this.mFileDownloadInfo.fileName);
            this.mFileSizeView.setText(FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size));
            this.mFileButton.setOnClickListener(this);
            this.mCancel.setOnClickListener(this);
            this.mAttachFile = new File(this.mFileDownloadInfo.path, this.mFileDownloadInfo.fileName);
            if (this.mAttachFile.exists()) {
                this.mFileButton.setText(this.getString(string.rc_ac_file_download_open_file_btn));
            }

            RongIM.getInstance().supportResumeBrokenTransfer(this.mFileDownloadInfo.url, new ResultCallback<Boolean>() {
                public void onSuccess(Boolean aBoolean) {
                    WebFilePreviewActivity.this.supportResumeTransfer = aBoolean ? WebFilePreviewActivity.SupportResumeStatus.SUPPORT : WebFilePreviewActivity.SupportResumeStatus.NOT_SUPPORT;
                    WebFilePreviewActivity.this.getFileDownloadInfo();
                }

                public void onError(ErrorCode e) {
                    WebFilePreviewActivity.this.mFileDownloadInfo.state = 4;
                    WebFilePreviewActivity.this.refreshDownloadState();
                }
            });
        }
    }

    private void getFileDownloadInfo() {
        this.pausedPath = FileUtils.getTempFilePath(this, this.mFileDownloadInfo.uid);
        this.mFileInfo = this.getFileInfo(this.pausedPath);
        if (!this.mAttachFile.exists()) {
            if (this.mFileInfo != null) {
                FileUtils.removeFile(this.pausedPath);
            }

            this.mFileDownloadInfo.state = 0;
        } else if (this.mFileInfo == null) {
            this.mFileDownloadInfo.state = 1;
        } else {
            if (this.mFileInfo.isStop()) {
                this.mFileDownloadInfo.state = 7;
            }

            if (this.mFileInfo.isDownLoading()) {
                if (RongIMClient.getInstance().isFileDownloading(this.mFileDownloadInfo.uid)) {
                    this.mFileDownloadInfo.state = 2;
                } else {
                    this.mFileDownloadInfo.state = 7;
                }
            }
        }

        this.refreshDownloadState();
    }

    protected void refreshDownloadState() {
        switch (this.mFileDownloadInfo.state) {
            case 0:
                this.mFileButton.setText(this.getString(string.rc_ac_file_preview_begin_download));
                break;
            case 1:
                this.mFileButton.setText(this.getString(string.rc_ac_file_download_open_file_btn));
                break;
            case 2:
                this.mDownloadProgressView.setVisibility(0);
                this.mFileDownloadProgressBar.setProgress(this.mFileDownloadInfo.progress);
                this.downloadedFileLength = (long) ((double) this.mFileDownloadInfo.size * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                this.mFileSizeView.setText(this.getString(string.rc_ac_file_download_progress_tv, new Object[]{FileTypeUtils.formatFileSize(this.downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size)}));
                if (this.supportResumeTransfer == WebFilePreviewActivity.SupportResumeStatus.SUPPORT) {
                    this.mDownloadProgressTextView.setVisibility(8);
                    this.mFileButton.setText(this.getString(string.rc_cancel));
                } else {
                    this.mFileButton.setVisibility(8);
                }
                break;
            case 3:
                this.mFileSizeView.setText(FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size));
                this.mFileButton.setText(this.getString(string.rc_ac_file_preview_begin_download));
                break;
            case 4:
                if (this.supportResumeTransfer == WebFilePreviewActivity.SupportResumeStatus.SUPPORT) {
                    this.mDownloadProgressView.setVisibility(0);
                    this.mFileInfo = this.getFileInfo(this.pausedPath);
                    if (this.mFileInfo != null) {
                        this.mFileDownloadInfo.progress = (int) (100L * this.mFileInfo.getFinished() / this.mFileInfo.getLength());
                    }

                    this.mFileDownloadProgressBar.setProgress(this.mFileDownloadInfo.progress);
                    long downloadedFileLength = (long) ((double) this.mFileDownloadInfo.size * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                    this.mFileSizeView.setText(this.getString(string.rc_ac_file_download_progress_pause, new Object[]{FileTypeUtils.formatFileSize(downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size)}));
                    this.mFileButton.setText(this.getString(string.rc_ac_file_preview_download_resume));
                } else {
                    this.mDownloadProgressView.setVisibility(8);
                    this.mFileButton.setVisibility(0);
                    this.mFileSizeView.setText(FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size));
                    this.mFileButton.setText(this.getString(string.rc_ac_file_preview_begin_download));
                }

                Toast.makeText(this, this.getString(string.rc_ac_file_preview_download_error), 0).show();
                break;
            case 5:
                this.mDownloadProgressView.setVisibility(8);
                this.mFileDownloadProgressBar.setProgress(0);
                this.mFileButton.setVisibility(0);
                this.mFileButton.setText(this.getString(string.rc_ac_file_preview_begin_download));
                this.mFileSizeView.setText(FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size));
                Toast.makeText(this, this.getString(string.rc_ac_file_preview_download_cancel), 0).show();
                break;
            case 6:
                this.mDownloadProgressView.setVisibility(8);
                this.mFileButton.setVisibility(0);
                this.mFileButton.setText(this.getString(string.rc_ac_file_download_open_file_btn));
                this.mFileSizeView.setText(FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size));
                Toast.makeText(this, this.getString(string.rc_ac_file_preview_downloaded) + this.mFileDownloadInfo.path, 0).show();
                break;
            case 7:
                this.mDownloadProgressView.setVisibility(0);
                if (this.mFileInfo != null) {
                    this.mFileDownloadInfo.progress = (int) (100L * this.mFileInfo.getFinished() / this.mFileInfo.getLength());
                    this.downloadedFileLength = this.mFileInfo.getFinished();
                } else {
                    this.downloadedFileLength = (long) ((double) this.mFileDownloadInfo.size * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                }

                this.mFileDownloadProgressBar.setProgress(this.mFileDownloadInfo.progress);
                this.mFileSizeView.setText(this.getString(string.rc_ac_file_download_progress_pause, new Object[]{FileTypeUtils.formatFileSize(this.downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size)}));
                this.mFileButton.setText(this.getString(string.rc_ac_file_preview_download_resume));
        }

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
                    if (this.mAttachFile != null) {
                        this.openFile(this.mFileDownloadInfo.fileName, this.mAttachFile.getAbsolutePath());
                    }
                    break;
                case 2:
                    this.mFileDownloadInfo.state = 7;
                    RongIMClient.getInstance().pauseDownloadMediaFile(this.mFileDownloadInfo.uid, (OperationCallback) null);
                    this.mFileButton.setText(this.getResources().getString(string.rc_ac_file_preview_download_resume));
                    this.mFileInfo = this.getFileInfo(this.pausedPath);
                    if (this.mFileInfo != null) {
                        this.downloadedFileLength = this.mFileInfo.getFinished();
                    } else {
                        this.downloadedFileLength = (long) ((double) this.mFileDownloadInfo.size * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                    }

                    this.mFileSizeView.setText(this.getString(string.rc_ac_file_download_progress_pause, new Object[]{FileTypeUtils.formatFileSize(this.downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size)}));
                    break;
                case 7:
                    if (RongIM.getInstance().getCurrentConnectionStatus() == ConnectionStatus.NETWORK_UNAVAILABLE) {
                        Toast.makeText(this, this.getString(string.rc_notice_network_unavailable), 0).show();
                        return;
                    }

                    if (this.supportResumeTransfer == WebFilePreviewActivity.SupportResumeStatus.SUPPORT) {
                        this.mFileDownloadInfo.state = 2;
                        this.downloadFile();
                        if (this.mFileDownloadInfo.state != 4 && this.mFileDownloadInfo.state != 5) {
                            this.mFileButton.setText(this.getResources().getString(string.rc_cancel));
                        }
                    }
            }
        }

    }

    private void startToDownload() {
        if (RongIM.getInstance().getCurrentConnectionStatus() == ConnectionStatus.NETWORK_UNAVAILABLE) {
            Toast.makeText(this, this.getString(string.rc_notice_network_unavailable), 0).show();
        } else {
            if (this.supportResumeTransfer == WebFilePreviewActivity.SupportResumeStatus.NOT_SET) {
                RongIM.getInstance().supportResumeBrokenTransfer(this.mFileDownloadInfo.url, new ResultCallback<Boolean>() {
                    public void onSuccess(Boolean aBoolean) {
                        if (WebFilePreviewActivity.this.mFileDownloadInfo.state == 0 || WebFilePreviewActivity.this.mFileDownloadInfo.state == 3 || WebFilePreviewActivity.this.mFileDownloadInfo.state == 4 || WebFilePreviewActivity.this.mFileDownloadInfo.state == 5) {
                            WebFilePreviewActivity.this.supportResumeTransfer = WebFilePreviewActivity.SupportResumeStatus.valueOf(aBoolean ? 1 : 0);
                            WebFilePreviewActivity.this.downloadFile();
                        }

                    }

                    public void onError(ErrorCode e) {
                        WebFilePreviewActivity.this.mFileDownloadInfo.state = 4;
                        WebFilePreviewActivity.this.refreshDownloadState();
                    }
                });
            } else if (this.mFileDownloadInfo.state == 0 || this.mFileDownloadInfo.state == 4 || this.mFileDownloadInfo.state == 3 || this.mFileDownloadInfo.state == 5) {
                this.downloadFile();
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
            if (this.supportResumeTransfer == WebFilePreviewActivity.SupportResumeStatus.SUPPORT) {
                this.mFileButton.setText(this.getResources().getString(string.rc_cancel));
                this.mCancel.setVisibility(8);
                this.mDownloadProgressView.setVisibility(0);
                this.mDownloadProgressTextView.setVisibility(8);
                this.mFileInfo = this.getFileInfo(this.pausedPath);
                if (this.mFileInfo != null) {
                    this.downloadedFileLength = this.mFileInfo.getFinished();
                } else {
                    this.downloadedFileLength = (long) ((double) this.mFileDownloadInfo.size * ((double) this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
                }

                this.mFileSizeView.setText(this.getString(string.rc_ac_file_download_progress_tv, new Object[]{FileTypeUtils.formatFileSize(this.downloadedFileLength), FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size)}));
            } else {
                this.mFileButton.setVisibility(8);
                this.mDownloadProgressView.setVisibility(0);
                this.mDownloadProgressTextView.setText(this.getString(string.rc_ac_file_download_progress_tv, new Object[]{FileTypeUtils.formatFileSize(0L), FileTypeUtils.formatFileSize(this.mFileDownloadInfo.size)}));
            }

            RongIM.getInstance().downloadMediaFile(this.mFileDownloadInfo.uid, this.mFileDownloadInfo.url, this.mFileDownloadInfo.fileName, this.mFileDownloadInfo.path, new IDownloadMediaFileCallback() {
                public void onFileNameChanged(String newFileName) {
                    WebFilePreviewActivity.this.mFileDownloadInfo.fileName = newFileName;
                }

                public void onSuccess() {
                    WebFilePreviewActivity.this.mFileDownloadInfo.state = 6;

                    try {
                        WebFilePreviewActivity.this.mAttachFile = new File(WebFilePreviewActivity.this.mFileDownloadInfo.path, WebFilePreviewActivity.this.mFileDownloadInfo.fileName);
                    } catch (Exception var2) {
                        RLog.e("WebFilePreviewActivity", "downloadFile" + var2);
                    }

                    WebFilePreviewActivity.this.refreshDownloadState();
                }

                public void onProgress(int progress) {
                    if (WebFilePreviewActivity.this.mFileDownloadInfo.state != 5 && WebFilePreviewActivity.this.mFileDownloadInfo.state != 7) {
                        WebFilePreviewActivity.this.mFileDownloadInfo.progress = progress;
                        WebFilePreviewActivity.this.mFileDownloadInfo.state = 2;
                        WebFilePreviewActivity.this.refreshDownloadState();
                    }

                }

                public void onError(ErrorCode code) {
                    if (WebFilePreviewActivity.this.mFileDownloadInfo.state != 5) {
                        WebFilePreviewActivity.this.mFileDownloadInfo.state = 4;
                        WebFilePreviewActivity.this.refreshDownloadState();
                    }

                }

                public void onCanceled() {
                    WebFilePreviewActivity.this.mFileDownloadInfo.state = 5;
                    WebFilePreviewActivity.this.refreshDownloadState();
                }
            });
        }
    }

    private FileInfo getFileInfo(String path) {
        FileInfo savedFileInfo = null;

        try {
            String savedFileInfoString = FileUtils.getStringFromFile(path);
            if (!TextUtils.isEmpty(savedFileInfoString)) {
                savedFileInfo = this.getFileInfoFromJsonString(savedFileInfoString);
            }
        } catch (Exception var4) {
            RLog.e("WebFilePreviewActivity", "getFileInfo", var4);
        }

        return savedFileInfo;
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
            RLog.e("WebFilePreviewActivity", "getFileInfoFromJsonString", var4);
        }

        return fileInfo;
    }

    public void openFile(String fileName, String fileSavePath) {
        if (!this.openInsidePreview(fileName, fileSavePath)) {
            Intent intent = FileTypeUtils.getOpenFileIntent(fileName, fileSavePath);

            try {
                if (intent != null) {
                    intent.addFlags(1);
                    this.startActivity(intent);
                } else {
                    Toast.makeText(this, this.getString(string.rc_ac_file_preview_can_not_open_file), 0).show();
                }
            } catch (Exception var5) {
                Toast.makeText(this, this.getString(string.rc_ac_file_preview_can_not_open_file), 0).show();
            }
        }

    }

    protected boolean openInsidePreview(String fileName, String fileSavePath) {
        Uri downloaded_apk;
        if (fileSavePath.endsWith(".txt")) {
            Intent webIntent = new Intent(this, RongWebviewActivity.class);
            webIntent.setPackage(this.getPackageName());
            if (VERSION.SDK_INT >= 24) {
                downloaded_apk = FileProvider.getUriForFile(this, this.getPackageName() + this.getString(string.rc_authorities_fileprovider), new File(fileSavePath));
                webIntent.putExtra("url", downloaded_apk.toString());
            } else {
                webIntent.putExtra("url", "file://" + fileSavePath);
            }

            webIntent.putExtra("title", fileName);
            this.startActivity(webIntent);
            return true;
        } else if (fileSavePath.endsWith(".apk")) {
            File file = new File(fileSavePath);
            if (!file.exists()) {
                Toast.makeText(this, this.getString(string.rc_file_not_exist), 0).show();
                return false;
            } else {
                if (VERSION.SDK_INT >= 24) {
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

                return true;
            }
        } else {
            return false;
        }
    }

    protected void onRestart() {
        super.onRestart();
        this.getFileDownloadInfo();
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

        public static WebFilePreviewActivity.SupportResumeStatus valueOf(int code) {
            WebFilePreviewActivity.SupportResumeStatus[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                WebFilePreviewActivity.SupportResumeStatus c = var1[var3];
                if (code == c.getValue()) {
                    return c;
                }
            }

            WebFilePreviewActivity.SupportResumeStatus c = NOT_SET;
            c.value = code;
            return c;
        }
    }

    private class FileDownloadInfo {
        int state;
        int progress;
        String path;
        String fileName;
        String url;
        String uid;
        long size;

        private FileDownloadInfo() {
        }
    }
}
