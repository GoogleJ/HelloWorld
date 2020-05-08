package com.zxjk.moneyspace.ui.findpage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.msgpage.ShareGroupQRActivity;
import com.zxjk.moneyspace.ui.widget.ProgressView;
import com.zxjk.moneyspace.utils.ShareUtil;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class NewsDetailActivity extends BaseActivity {

    private FrameLayout fl_webview;
    private ProgressView pb_webview;

    private String currentUrl;

    private WebSettings webSettings;
    private WebView mWebView;

    private TextView tv_title;
    private String title;

    private QuickPopup invitePop;
    private ValueAnimator pbAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        ServiceFactory.getInstance().getBaseService(Api.class);

        currentUrl = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");

        initView();

        initAnimtor();

        initWebView();
    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("CheckResult")
    private void initView() {
        fl_webview = findViewById(R.id.fl_webview);
        pb_webview = findViewById(R.id.pb_webview);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        findViewById(R.id.rl_end).setVisibility(View.VISIBLE);
        ImageView imageView = findViewById(R.id.iv_end);
        imageView.setImageResource(R.drawable.ic_share_action_right);
        imageView.setOnClickListener(v -> {
            if (invitePop == null) {
                TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
                showAnimation.setDuration(350);
                TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
                dismissAnimation.setDuration(500);
                invitePop = QuickPopupBuilder.with(this)
                        .contentView(R.layout.popup_news_card)
                        .config(new QuickPopupConfig()
                                .withShowAnimation(showAnimation)
                                .withDismissAnimation(dismissAnimation)
                                .withClick(R.id.tv1, view -> shareTo(1), true)
                                .withClick(R.id.tv2, view -> shareTo(2), true)
                                .withClick(R.id.tv3, view -> shareTo(3), true)
                                .withClick(R.id.tv4, view -> shareTo(4), true)
                                .withClick(R.id.tv5, view -> {
                                    ToastUtils.showShort(R.string.duplicated_to_clipboard);
                                    ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                                    if (cm != null) {
                                        cm.setPrimaryClip(ClipData.newPlainText("text", getIntent().getStringExtra("url")));
                                    }
                                }, true)

                        )
                        .show();
            } else {
                invitePop.showPopupWindow();
            }
        });
    }

    private void shareTo(int plantform) {
        currentUrl = currentUrl + "&share=share";
        UMWeb link = new UMWeb(currentUrl);
        link.setTitle("MoneySpace");
        link.setDescription(title);

        SHARE_MEDIA platform = null;
        switch (plantform) {
            case 1:
                platform = SHARE_MEDIA.WEIXIN;
                savePointInfo();
                break;
            case 2:
                platform = SHARE_MEDIA.WEIXIN_CIRCLE;
                savePointInfo();
                break;
            case 3:
                platform = SHARE_MEDIA.QQ;
                savePointInfo();
                break;
            case 4:
                RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                    @Override
                    public void onSuccess(List<Conversation> conversations) {
                        Intent intent = new Intent(NewsDetailActivity.this, ShareGroupQRActivity.class);
                        intent.putParcelableArrayListExtra("data", (ArrayList<Conversation>) conversations);
                        intent.putExtra("fromShareNews", true);
                        intent.putExtra("url", getIntent().getStringExtra("url"));
                        intent.putExtra("title", getIntent().getStringExtra("title"));
                        intent.putExtra("icon", getIntent().getStringExtra("icon"));
                        intent.putExtra("article", getIntent().getStringExtra("article"));
                        startActivity(intent);
                        savePointInfo();
                        finish();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }
                });
                break;
        }
        new ShareAction(this)
                .setPlatform(platform)
                .withMedia(link)
                .setCallback(new ShareUtil.ShareListener())
                .share();
    }

    @SuppressLint("CheckResult")
    private void savePointInfo() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .savePointInfo("4")
                .compose(RxSchedulers.ioObserver())
                .subscribe(s -> {
                }, t -> {
                });
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

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
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
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (pb_webview.getVisibility() == View.GONE) {
                    pb_webview.setProgress(0);
                    pb_webview.setVisibility(View.VISIBLE);
                    pbAnim.start();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                view.loadUrl(url);

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                ToastUtils.showShort(R.string.loadurl_fail);
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                ToastUtils.showShort(R.string.loadurl_fail);
                super.onReceivedSslError(view, handler, error);
            }
        });

        mWebView.loadUrl(currentUrl);
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
