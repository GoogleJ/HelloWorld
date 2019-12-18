package com.zxjk.duoduo.ui.socialspace;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CommunityInfoResponse;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.scanuri.BaseUri;
import com.zxjk.duoduo.ui.msgpage.GroupQRActivity;
import com.zxjk.duoduo.ui.msgpage.ShareGroupQRActivity;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.SaveImageUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;


public class SocialQRCodeActivity extends BaseActivity {

    private int minimumHeightForVisibleOverlappingContent = 0;
    private int totalScrollRange = 0;
    private int statusbarHeight = 0;

    private CommunityInfoResponse data;


    private AppBarLayout app_bar;
    private CollapsingToolbarLayout collapsingLayout;
    private ImageView ivBg;
    private Toolbar toolbar;
    private ImageView ivToolBarStart;
    private ImageView ivHead;
    private TextView tvTitle;
    private TextView tvSocialId;
    private TextView tvSocialName;
    private ImageView imgqrcode;


    private BaseUri uri = new BaseUri("action4");
    private String uri2Code;
    private Bitmap bitmap2;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnableTouchHideKeyBoard(false);
        setTrasnferStatusBar(true);
        BarUtils.setStatusBarLightMode(this, true);
        setContentView(R.layout.activity_social_qrcode);

        initView();

        onAppBarScroll();

        setSocialBackgroundHeight();

        setSupportActionBar(toolbar);

        setToolBarMarginTop();

        data = getIntent().getParcelableExtra("data");


//        new Thread() {
//            public void run() {
//                    try {
//                        bitmap2 = Glide.with(SocialQRCodeActivity.this)
//                                .asBitmap()
//                                .load(data.getLogo())
//                                .submit(100, 100).get();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//        }.start();


        //QRcode
        tvTitle.setText(R.string.qrcodename);
        tvSocialName.setText(data.getName());

        tvSocialId.setText("社群号:" + data.getCode());
        GlideUtil.loadNormalImg(ivBg, data.getBgi());
        GlideUtil.loadNormalImg(ivHead, data.getLogo());


        uri.data = new QRCodeData();
        ((QRCodeData) uri.data).inviterId = data.getCode();
        ((QRCodeData) uri.data).groupName = data.getName();
        ((QRCodeData) uri.data).groupId = data.getGroupId();
        uri2Code = new Gson().toJson(uri);

        getCodeBitmap();
    }


    private void setToolBarMarginTop() {
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
        statusbarHeight = BarUtils.getStatusBarHeight();
        layoutParams1.topMargin = statusbarHeight;
        toolbar.setLayoutParams(layoutParams1);
    }


    private void setSocialBackgroundHeight() {
        ViewGroup.LayoutParams layoutParams = app_bar.getLayoutParams();
        layoutParams.height = (int) (ScreenUtils.getScreenWidth() * 0.75);
        app_bar.setLayoutParams(layoutParams);
        ivBg.setImageResource(R.drawable.bg_default_social);
    }


    private void onAppBarScroll() {
        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int absOffset = Math.abs(verticalOffset);
            if (minimumHeightForVisibleOverlappingContent <= 0) {
                minimumHeightForVisibleOverlappingContent = app_bar.getMinimumHeightForVisibleOverlappingContent();
            }
            if (totalScrollRange <= 0) {
                totalScrollRange = app_bar.getTotalScrollRange();
                collapsingLayout.setScrimVisibleHeightTrigger((int) (totalScrollRange * 0.5));
            }

            if (absOffset <= minimumHeightForVisibleOverlappingContent) {
                if (ivToolBarStart.getVisibility() == View.GONE) {
                    ivToolBarStart.setVisibility(View.VISIBLE);
                    ivToolBarStart.setImageResource(R.drawable.ic_social_back);
                }
            } else if (absOffset < totalScrollRange) {
                if (ivToolBarStart.getVisibility() == View.VISIBLE) {
                    ivToolBarStart.setVisibility(View.GONE);
                }
                if (tvTitle.getVisibility() == View.VISIBLE) {
                    tvTitle.setVisibility(View.INVISIBLE);
                }
            } else if (absOffset == totalScrollRange) {
                if (tvTitle.getVisibility() == View.INVISIBLE) {
                    tvTitle.setVisibility(View.VISIBLE);
                }
                ivToolBarStart.setVisibility(View.VISIBLE);
                ivToolBarStart.setImageResource(R.drawable.ico_back);
            }
        });
    }



    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        app_bar = findViewById(R.id.app_bar);
        collapsingLayout = findViewById(R.id.collapsingLayout);
        ivBg = findViewById(R.id.ivBg);
        toolbar = findViewById(R.id.toolbar);
        ivToolBarStart = findViewById(R.id.ivToolBarStart);
        tvTitle = findViewById(R.id.tvTitle);
        tvSocialId = findViewById(R.id.tvSocialId);
        tvSocialName = findViewById(R.id.tvSocialName);
        ivHead = findViewById(R.id.ivHead);
        imgqrcode = findViewById(R.id.img_qrcode);
    }

    public void back(View view) {
        finish();
    }


    //保存二维码
    public void save(View view) {
        Log.i("save", "点击了: ");
        getPermisson(findViewById(R.id.cardSave), g -> {
            //保存到手机
            if (bitmap == null) {
                return;
            }

            imgqrcode.buildDrawingCache();

            SaveImageUtil.get().savePic(imgqrcode.getDrawingCache(), success -> {
                if (success) {
                    ToastUtils.showShort(R.string.savesucceed);
                    return;
                }
                ToastUtils.showShort(R.string.savefailed);
            });
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    //分享二维码
    public void share(View view) {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                imgqrcode.buildDrawingCache();
                Constant.shareGroupQR = imgqrcode.getDrawingCache();
                Intent intent = new Intent(SocialQRCodeActivity.this, ShareGroupQRActivity.class);
                intent.putParcelableArrayListExtra("data", (ArrayList<Conversation>) conversations);
                startActivity(intent);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }



    private Bitmap bitmap;

    //bitmap二维码
    @SuppressLint("CheckResult")
    private void getCodeBitmap() {
        Observable.create((ObservableOnSubscribe<Bitmap>) e -> {
            bitmap2 = Glide.with(SocialQRCodeActivity.this)
                    .asBitmap()
                    .load(data.getLogo())
                    .submit(100, 100).get();
            bitmap = QRCodeEncoder.syncEncodeQRCode(uri2Code, UIUtil.dip2px(this, 250), Color.BLACK ,bitmap2);
            e.onNext(bitmap);
        })
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(b -> imgqrcode.setImageBitmap(b));
    }





    public static class QRCodeData {
        public String groupId;
        public String inviterId;
        public String groupName;
    }


}
