package com.zxjk.duoduo.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.Utils;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.base.WebActivityToLogin;
import com.zxjk.duoduo.ui.widget.ProgressView;

public class WebActivity extends BaseActivity implements WebActivityToLogin {
    String currentUrl;
    private FrameLayout fl_webview;
    private ProgressView pb_webview;
    private WebSettings webSettings;
    private WebView mWebView;
    private ValueAnimator pbAnim;
    private boolean showAnim = true;

    private BroadcastReceiver downloadCompleteReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
                    if (TextUtils.isEmpty(type)) {
                        type = "*/*";
                    }
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                    if (uri != null) {
                        Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
                        handlerIntent.setDataAndType(uri, type);
                        handlerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        handlerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(handlerIntent);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web111);

        if (getIntent().getBooleanExtra("fromSocialApp", false)) {
            findViewById(R.id.llTop).setVisibility(View.VISIBLE);
            TextView title = findViewById(R.id.tvTitle);
            title.setText(getIntent().getStringExtra("appName"));
            showAnim = false;
        }

        currentUrl = getIntent().getStringExtra("url");

        ((Application) getApplication()).getWebDataUtils().setWebActivityToLogin(this);

        initView();

        if (showAnim) {
            initAnimtor();
        }

        initWebView();
    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("CheckResult")
    private void initView() {
        fl_webview = findViewById(R.id.fl_webview);
        pb_webview = findViewById(R.id.pb_webview);
    }

    private void initAnimtor() {
        pbAnim = ValueAnimator.ofFloat(0f, 70f);
        pbAnim.setDuration(3000);
        pbAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        pbAnim.addUpdateListener(animation -> pb_webview.setProgress((Float) animation.getAnimatedValue()));
        pbAnim.start();
    }

    private void initWebView() {
        mWebView = new WebView(Utils.getApp().getApplicationContext());

        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(layoutParams);
        fl_webview.addView(mWebView, 0);

        webSettings = mWebView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);

//        webSettings.setMixedContentMode(WebSettings.);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webSettings.setUseWideViewPort(true);
        //允许js代码
        webSettings.setJavaScriptEnabled(true);
        //允许SessionStorage/LocalStorage存储
        webSettings.setDomStorageEnabled(true);
        webSettings.setBlockNetworkImage(false);

        //缩放操作
        webSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(true); //隐藏原生的缩放控件

        webSettings.setAllowFileAccess(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    try {
                        pbAnim.cancel();
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(pb_webview.getProgress(), 100f);
                        valueAnimator.setDuration((long) (1500 * (1 - pb_webview.getProgress() / 100f)));
                        valueAnimator.addUpdateListener(animation -> pb_webview.setProgress((Float) animation.getAnimatedValue()));
                        valueAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                pb_webview.setVisibility(View.GONE);
                            }
                        });
                        valueAnimator.start();
                    } catch (Exception e) {
                    }
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try {
                    if (pb_webview.getVisibility() == View.GONE) {
                        pb_webview.setProgress(0);
                        pb_webview.setVisibility(View.VISIBLE);
                        pbAnim.start();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = Uri.parse(request.getUrl().toString());
                if (!TextUtils.isEmpty(uri.getScheme()) && !uri.getScheme().startsWith("http")) {
                    if (uri.getScheme().equals("hilamg")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (!TextUtils.isEmpty(uri.getScheme()) && !uri.getScheme().startsWith("http")) {
                    if (uri.getScheme().equals("hilamg")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            }
        });

        mWebView.loadUrl(currentUrl);

        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // 设置通知栏的描述
//          request.setDescription("This is description");
            // 允许在计费流量下下载
            request.setAllowedOverMetered(false);
            // 允许漫游时下载
            request.setAllowedOverRoaming(true);
            // 允许下载的网路类型
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(downloadCompleteReceive, intentFilter);
        });
    }

    @Override
    protected void onDestroy() {
        ((Application) getApplication()).getWebDataUtils().setWebActivityToLogin(null);
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            fl_webview.removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void webToLogin(String token) {
        mWebView.clearCache(true);
        mWebView.loadUrl(currentUrl + "?token=" + token);
    }
}
