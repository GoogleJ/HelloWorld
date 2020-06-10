//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.tools;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import io.rong.common.RLog;
import io.rong.common.RongWebView;
import io.rong.imkit.RongBaseActivity;
import io.rong.imkit.R.bool;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongBaseActivity.ActionBar;
import java.util.List;

public class RongWebviewActivity extends RongBaseActivity {
  private static final String TAG = "RongWebviewActivity";
  private String mPrevUrl;
  protected RongWebView mWebView;
  private ProgressBar mProgressBar;
  protected TextView mWebViewTitle;
  private RongWebviewActivity.OnTitleReceivedListener onTitleReceivedListener;

  public RongWebviewActivity() {
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(layout.rc_ac_webview);
    Intent intent = this.getIntent();
    this.mWebView = (RongWebView)this.findViewById(id.rc_webview);
    this.mProgressBar = (ProgressBar)this.findViewById(id.rc_web_progressbar);
    this.mWebViewTitle = (TextView)this.findViewById(id.rc_action_bar_title);
    this.mWebView.setVerticalScrollbarOverlay(true);
    this.mWebView.getSettings().setLoadWithOverviewMode(true);
    this.mWebView.getSettings().setUseWideViewPort(true);
    this.mWebView.getSettings().setBuiltInZoomControls(true);
    if (VERSION.SDK_INT > 11) {
      this.mWebView.getSettings().setDisplayZoomControls(false);
    }

    this.mWebView.getSettings().setSupportZoom(true);
    this.mWebView.setWebViewClient(new RongWebviewActivity.RongWebviewClient());
    this.mWebView.setWebChromeClient(new RongWebviewActivity.RongWebChromeClient());
    this.mWebView.setDownloadListener(new RongWebviewActivity.RongWebViewDownLoadListener());
    this.mWebView.getSettings().setDomStorageEnabled(true);
    this.mWebView.getSettings().setDefaultTextEncodingName("utf-8");
    String url = intent.getStringExtra("url");
    Uri data = intent.getData();
    if (url != null) {
      if (this.getResources().getBoolean(bool.rc_set_java_script_enabled)) {
        if (url.startsWith("file://")) {
          this.mWebView.getSettings().setJavaScriptEnabled(false);
        } else {
          this.mWebView.getSettings().setJavaScriptEnabled(true);
        }
      }

      this.mPrevUrl = url;
      this.mWebView.loadUrl(url);
      String title = intent.getStringExtra("title");
      if (this.mWebViewTitle != null && !TextUtils.isEmpty(title)) {
        this.mWebViewTitle.setText(title);
      }
    } else if (data != null) {
      this.mPrevUrl = data.toString();
      this.mWebView.loadUrl(data.toString());
    }

    this.onCreateActionbar(new ActionBar());
  }

  public void setOnTitleReceivedListener(RongWebviewActivity.OnTitleReceivedListener onTitleReceivedListener) {
    this.onTitleReceivedListener = onTitleReceivedListener;
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == 4 && this.mWebView.canGoBack()) {
      this.mWebView.goBack();
      return true;
    } else {
      return super.onKeyDown(keyCode, event);
    }
  }

  public boolean checkIntent(Context context, Intent intent) {
    PackageManager packageManager = context.getPackageManager();
    List<ResolveInfo> apps = packageManager.queryIntentActivities(intent, 0);
    return apps.size() > 0;
  }

  private class RongWebViewDownLoadListener implements DownloadListener {
    private RongWebViewDownLoadListener() {
    }

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
      Uri uri = Uri.parse(url);
      Intent intent = new Intent("android.intent.action.VIEW", uri);
      if (RongWebviewActivity.this.checkIntent(RongWebviewActivity.this, intent)) {
        RongWebviewActivity.this.startActivity(intent);
        if (uri.getScheme().equals("file") && uri.toString().endsWith(".txt")) {
          RongWebviewActivity.this.finish();
        }
      }

    }
  }

  private class RongWebChromeClient extends WebChromeClient {
    private RongWebChromeClient() {
    }

    public void onProgressChanged(WebView view, int newProgress) {
      if (newProgress == 100) {
        RongWebviewActivity.this.mProgressBar.setVisibility(8);
      } else {
        if (RongWebviewActivity.this.mProgressBar.getVisibility() == 8) {
          RongWebviewActivity.this.mProgressBar.setVisibility(0);
        }

        RongWebviewActivity.this.mProgressBar.setProgress(newProgress);
      }

      super.onProgressChanged(view, newProgress);
    }

    public void onReceivedTitle(WebView view, String title) {
      if (RongWebviewActivity.this.mWebViewTitle != null && TextUtils.isEmpty(RongWebviewActivity.this.mWebViewTitle.getText())) {
        RongWebviewActivity.this.mWebViewTitle.setText(title);
      }

      if (RongWebviewActivity.this.onTitleReceivedListener != null) {
        RongWebviewActivity.this.onTitleReceivedListener.onTitleReceived(title);
      }

    }

    public void onCloseWindow(WebView window) {
      super.onCloseWindow(window);
      if (!RongWebviewActivity.this.isFinishing()) {
        RongWebviewActivity.this.finish();
      }

    }
  }

  private class RongWebviewClient extends WebViewClient {
    private RongWebviewClient() {
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (RongWebviewActivity.this.mPrevUrl != null) {
        if (!RongWebviewActivity.this.mPrevUrl.equals(url)) {
          if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")) {
            Intent intent = new Intent("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            intent.setFlags(268435456);
            intent.setFlags(536870912);

            try {
              RongWebviewActivity.this.startActivity(intent);
            } catch (Exception var6) {
              RLog.e("RongWebviewActivity", "not apps install for this intent =" + var6.toString());
              RLog.e("RongWebviewActivity", "RongWebviewClient", var6);
            }

            return true;
          } else {
            RongWebviewActivity.this.mPrevUrl = url;
            RongWebviewActivity.this.mWebView.loadUrl(url);
            return true;
          }
        } else {
          return false;
        }
      } else {
        RongWebviewActivity.this.mPrevUrl = url;
        RongWebviewActivity.this.mWebView.loadUrl(url);
        return true;
      }
    }

    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
      Builder builder = new Builder(RongWebviewActivity.this);
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

  public interface OnTitleReceivedListener {
    void onTitleReceived(String var1);
  }
}
