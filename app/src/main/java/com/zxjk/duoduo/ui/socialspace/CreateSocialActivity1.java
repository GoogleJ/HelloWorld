package com.zxjk.duoduo.ui.socialspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.Utils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class CreateSocialActivity1 extends BaseActivity {

    private WebSettings webSettings;
    private WebView mWebView;
    private FrameLayout fl_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_social1);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.create_social);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        initWebView();
    }

    private void initWebView() {
        fl_webview = findViewById(R.id.fl_webview);
        mWebView = new WebView(Utils.getApp().getApplicationContext());

        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(layoutParams);
        fl_webview.addView(mWebView, 0);

        webSettings = mWebView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);

        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

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

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                view.loadUrl(url);

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.loadUrl("www.baidu.com");
    }

    public void nextStep(View view) {
        startActivity(new Intent(this, CreateSocialActivity2.class));
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            fl_webview.removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
