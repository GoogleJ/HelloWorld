package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;

@SuppressLint("JavascriptInterface")
public class OnlineServiceActivity extends BaseActivity {
    WebView webView;
    String url;
    TextView tv_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_service);
        webView = findViewById(R.id.web_view);
        tv_title = findViewById(R.id.tv_title);
        url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            tv_title.setText(R.string.keFu);
        } else {
            tv_title.setText(R.string.shengming);
        }
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        initSetting();
    }

    private void initSetting() {
        webView.loadUrl(url == null ? Constant.currentUser.getOnlineService() : url);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
    }


}
