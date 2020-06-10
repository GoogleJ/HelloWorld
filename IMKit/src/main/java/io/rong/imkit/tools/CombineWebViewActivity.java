//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.tools;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.common.RongWebView;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongBaseActivity;
import io.rong.imkit.RongContext;
import io.rong.imkit.model.Event.RemoteMessageRecallEvent;
import io.rong.imkit.utilities.RongUtils;

public class CombineWebViewActivity extends RongBaseActivity {
    private static final String TAG = CombineWebViewActivity.class.getSimpleName();
    private static final int VIDEO_WIDTH = 300;
    private static final int VIDEO_HEIGHT = 600;
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_MEDIA = "media";
    protected RongWebView mWebView;
    protected TextView mWebViewTitle;
    private ProgressBar mProgress;
    private ImageView mImageView;
    private TextView mTextView;
    private String mType;
    private int mMessageId;

    public CombineWebViewActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.rc_combine_webview);
        this.initUI();
        this.initData();
        RongContext.getInstance().getEventBus().register(this);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initUI() {
        this.mWebView = (RongWebView) this.findViewById(id.rc_webview);
        this.mWebViewTitle = (TextView) this.findViewById(id.rc_action_bar_title);
        this.mProgress = (ProgressBar) this.findViewById(id.rc_web_progress);
        this.mImageView = (ImageView) this.findViewById(id.rc_web_download_failed);
        this.mTextView = (TextView) this.findViewById(id.rc_web_download_text);
        this.mWebView.setVerticalScrollbarOverlay(true);
        this.mWebView.setWebViewClient(new CombineWebViewActivity.CombineWebViewClient());
        this.mWebView.setWebChromeClient(new CombineWebViewActivity.CombineWebChromeClient());
        if (VERSION.SDK_INT >= 17) {
            this.mWebView.addJavascriptInterface(new CombineWebViewActivity.JsInterface(), "interface");
        }

        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.getSettings().setLoadWithOverviewMode(true);
        this.mWebView.getSettings().setUseWideViewPort(true);
        this.mWebView.getSettings().setBuiltInZoomControls(true);
        this.mWebView.getSettings().setSupportZoom(true);
        this.mWebView.getSettings().setDomStorageEnabled(true);
        this.mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        this.mWebView.getSettings().setDisplayZoomControls(false);
        if (VERSION.SDK_INT >= 17) {
            this.mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }

        if (VERSION.SDK_INT >= 21) {
            this.mWebView.getSettings().setMixedContentMode(0);
        }

    }

    private void initData() {
        Intent intent = this.getIntent();
        if (intent != null) {
            this.mMessageId = intent.getIntExtra("messageId", -1);
            String uri = intent.getStringExtra("uri");
            this.mType = intent.getStringExtra("type");
            String title = intent.getStringExtra("title");
            this.mWebView.loadUrl(uri);
            if (this.mWebViewTitle != null && !TextUtils.isEmpty(title)) {
                this.mWebViewTitle.setText(title);
            }

            this.onCreateActionbar(new ActionBar());
        }
    }

    private void openFile(JSONObject jsonObj) {
        Intent intent = new Intent("io.rong.imkit.intent.action.openwebfile");
        intent.setPackage(this.getPackageName());
        intent.putExtra("fileUrl", jsonObj.optString("fileUrl"));
        intent.putExtra("fileName", jsonObj.optString("fileName"));
        intent.putExtra("fileSize", jsonObj.optString("fileSize"));
        this.startActivity(intent);
    }

    private void openCombine(JSONObject jsonObj) {
        String type = "media";
        String uri = jsonObj.optString("fileUrl");
        String filePath = FileUtils.getCachePath(RongContext.getInstance()) + File.separator + "combine" + File.separator + RongUtils.md5(uri) + ".html";
        if ((new File(filePath)).exists()) {
            uri = Uri.parse("file://" + filePath).toString();
            type = "local";
        }

        Intent intent = new Intent("io.rong.imkit.intent.action.combinewebview");
        intent.setPackage(this.getPackageName());
        intent.addFlags(268435456);
        intent.putExtra("uri", uri);
        intent.putExtra("type", type);
        intent.putExtra("title", jsonObj.optString("title"));
        this.startActivity(intent);
    }

    private void openLink(JSONObject jsonObj) {
        String link = jsonObj.optString("link");
        Intent intent = new Intent("io.rong.imkit.intent.action.webview");
        intent.setPackage(this.getPackageName());
        intent.putExtra("url", link);
        this.startActivity(intent);
    }

    private void openPhone(JSONObject jsonObj) {
        String phoneNumber = jsonObj.optString("phoneNum");
        Intent intent = new Intent("android.intent.action.DIAL");
        intent.setData(Uri.parse("tel:" + phoneNumber));
        this.startActivity(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && this.mWebView.canGoBack()) {
            this.mWebView.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void onEventMainThread(RemoteMessageRecallEvent event) {
        if (this.mMessageId != -1 && this.mMessageId == event.getMessageId()) {
            (new Builder(this, 5)).setMessage(this.getString(string.rc_recall_success)).setPositiveButton(this.getString(string.rc_dialog_ok), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    CombineWebViewActivity.this.finish();
                }
            }).setCancelable(false).show();
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.mWebView != null) {
            this.mWebView.destroy();
        }

        RongContext.getInstance().getEventBus().unregister(this);
    }

    private static class DownloadTask extends AsyncTask<String, Void, Void> {
        private DownloadTask() {
        }

        protected void onPreExecute() {
        }

        protected Void doInBackground(String... params) {
            OutputStream out = null;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(15000);
                InputStream in = urlConnection.getInputStream();
                File file = new File(params[1]);
                if (!file.exists()) {
                    File dir = new File(file.getParent());
                    dir.mkdirs();
                    boolean isCreateNewFile = file.createNewFile();
                    RLog.d(CombineWebViewActivity.TAG, "DownloadTask isCreateNewFile" + isCreateNewFile);
                }

                out = new FileOutputStream(file);
                byte[] buffer = new byte[10240];

                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                in.close();
            } catch (IOException var17) {
                RLog.e(CombineWebViewActivity.TAG, "DownloadTask", var17);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException var16) {
                        RLog.e(CombineWebViewActivity.TAG, "DownloadTask", var16);
                    }
                }

            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {
        }
    }

    private class JsInterface {
        private JsInterface() {
        }

        @JavascriptInterface
        public void sendInfoToAndroid(String uri) {
            try {
                JSONObject jsonObj = new JSONObject(uri);
                String type = jsonObj.optString("type");
                RLog.d(CombineWebViewActivity.TAG, "sendInfoToAndroid type:" + type);
                if ("RC:FileMsg".equals(type)) {
                    CombineWebViewActivity.this.openFile(jsonObj);
                } else if ("RC:LBSMsg".equals(type)) {
                } else if ("RC:CombineMsg".equals(type)) {
                    CombineWebViewActivity.this.openCombine(jsonObj);
                } else if ("link".equals(type)) {
                    CombineWebViewActivity.this.openLink(jsonObj);
                } else if ("phone".equals(type)) {
                    CombineWebViewActivity.this.openPhone(jsonObj);
                }
            } catch (Exception var4) {
                RLog.e(CombineWebViewActivity.TAG, "sendInfoToAndroid", var4);
            }

        }
    }

    private class CombineWebChromeClient extends WebChromeClient {
        private CombineWebChromeClient() {
        }

        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                CombineWebViewActivity.this.mProgress.setVisibility(8);
                CombineWebViewActivity.this.mImageView.setVisibility(8);
                CombineWebViewActivity.this.mTextView.setVisibility(8);
                CombineWebViewActivity.this.mWebView.setVisibility(0);
            } else {
                if (CombineWebViewActivity.this.mProgress.getVisibility() == 8) {
                    CombineWebViewActivity.this.mProgress.setVisibility(0);
                }

                if (CombineWebViewActivity.this.mTextView.getVisibility() == 8) {
                    CombineWebViewActivity.this.mTextView.setText(CombineWebViewActivity.this.getString(string.rc_combine_webview_loading));
                    CombineWebViewActivity.this.mTextView.setVisibility(0);
                }

                if (CombineWebViewActivity.this.mWebView.getVisibility() == 0) {
                    CombineWebViewActivity.this.mWebView.setVisibility(8);
                }
            }

            super.onProgressChanged(view, newProgress);
        }

        public void onReceivedTitle(WebView view, String title) {
            if (CombineWebViewActivity.this.mWebViewTitle != null && TextUtils.isEmpty(CombineWebViewActivity.this.mWebViewTitle.getText())) {
                CombineWebViewActivity.this.mWebViewTitle.setText(title);
            }

        }

        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
            if (!CombineWebViewActivity.this.isFinishing()) {
                CombineWebViewActivity.this.finish();
            }

        }

        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(300, 600, Config.ALPHA_8);
        }
    }

    private class CombineWebViewClient extends WebViewClient {
        private CombineWebViewClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            RLog.d(CombineWebViewActivity.TAG, "onPageStarted url:" + url);
            if ("media".equals(CombineWebViewActivity.this.mType) && url != null && (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("ftp"))) {
                String filePath = FileUtils.getCachePath(RongContext.getInstance()) + File.separator + "combine" + File.separator + RongUtils.md5(url) + ".html";
                if (!(new File(filePath)).exists()) {
                    (new CombineWebViewActivity.DownloadTask()).execute(new String[]{url, filePath});
                }
            }

        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            RLog.d(CombineWebViewActivity.TAG, "onReceivedError errorCode:" + errorCode);
            CombineWebViewActivity.this.mProgress.setVisibility(8);
            CombineWebViewActivity.this.mImageView.setVisibility(0);
            CombineWebViewActivity.this.mTextView.setVisibility(0);
            CombineWebViewActivity.this.mWebView.setVisibility(8);
            CombineWebViewActivity.this.mTextView.setText(CombineWebViewActivity.this.getString(string.rc_combine_webview_download_failed));
        }

        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            Builder builder = new Builder(CombineWebViewActivity.this);
            builder.setMessage(string.rc_notification_error_ssl_cert_invalid);
            builder.setPositiveButton(string.rc_dialog_ok, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton(string.rc_dialog_cancel, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
