package com.zxjk.duoduo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.EditCommunityRequest;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.PinchImageView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.OssUtils;
import com.zxjk.duoduo.utils.SaveImageUtil;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class ZoomActivity extends BaseActivity {
    @BindView(R.id.pic)
    PinchImageView pic;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;

    //1:logo 2:bg
    private int type;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ScreenUtils.setFullScreen(this);
        setContentView(R.layout.activity_zoom);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        setLightStatusBar(false);
        getWindow().setStatusBarColor(Color.BLACK);
        BarUtils.setNavBarColor(this, Color.BLACK);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        type = getIntent().getIntExtra("type", 1);
        String imageUrl = getIntent().getStringExtra("image");
        GlideUtil.loadNormalImg(pic, imageUrl);

        if (getIntent().getBooleanExtra("fromSocialHomePage", false)) {
            llTitle.setVisibility(View.VISIBLE);
        } else {
            pic.setOnClickListener(v -> finish());
        }
    }

    public void back(View view) {
        finish();
    }

    public void func(View view) {
        if (TextUtils.isEmpty(getIntent().getStringExtra("id"))) {
            //cant edit
            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
            showAnimation.setDuration(250);
            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
            dismissAnimation.setDuration(500);
            QuickPopup show = QuickPopupBuilder.with(this)
                    .contentView(R.layout.popup_socialbg_edit1)
                    .config(new QuickPopupConfig()
                            .withShowAnimation(showAnimation)
                            .withDismissAnimation(dismissAnimation)
                            .withClick(R.id.tv4, null, true))
                    .show();
            save2Phone(show.findViewById(R.id.tv1), show);
        } else {
            //can edit
            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
            showAnimation.setDuration(250);
            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
            dismissAnimation.setDuration(500);
            QuickPopup show = QuickPopupBuilder.with(this)
                    .contentView(R.layout.popup_socialbg_edit2)
                    .config(new QuickPopupConfig()
                            .withShowAnimation(showAnimation)
                            .withDismissAnimation(dismissAnimation)
                            .withClick(R.id.tv4, null, true))
                    .show();

            save2Phone(show.findViewById(R.id.tv3), show);

            getPermisson(show.findViewById(R.id.tv1), result -> {
                if (result) {
                    show.dismiss(true);
                    if (type == 2) {
                        TakePicUtil.config(new TakePicUtil.Config().rectParm(100, 75));
                    }
                    TakePicUtil.takePicture(this);
                }
            }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            getPermisson(show.findViewById(R.id.tv2), result -> {
                if (result) {
                    show.dismiss(true);
                    if (type == 2) {
                        TakePicUtil.config(new TakePicUtil.Config().rectParm(100, 75));
                    }
                    TakePicUtil.albumPhoto(this);
                }
            }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (corpFile != null) {
            zipFile(Collections.singletonList(corpFile.getPath()), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    EditCommunityRequest request = new EditCommunityRequest();
                    request.setGroupId(getIntent().getStringExtra("id"));
                    if (type == 1) {
                        request.setLogo(url);
                    } else {
                        request.setBgi(url);
                    }
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .editCommunity(GsonUtils.toJson(request))
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                            .compose(RxSchedulers.normalTrans())
                            .subscribe(r -> {
                                this.url = url;
                                ToastUtils.showShort(R.string.successfully_modified);
                                GlideUtil.loadNormalImg(pic, url);
                            }, this::handleApiError);
                });
            });
        }
    }

    @Override
    public void finish() {
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent();
            intent.putExtra("url", url);
            setResult(1, intent);
        }
        super.finish();
    }

    private void save2Phone(View view, QuickPopup show) {
        getPermisson(view, g -> {
            show.dismiss(true);
            //保存到手机
            pic.buildDrawingCache();

            SaveImageUtil.get().savePic(pic.getDrawingCache(), success -> {
                if (success) {
                    ToastUtils.showShort(R.string.savesucceed);
                    return;
                }
                ToastUtils.showShort(R.string.savefailed);
            });
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }


//    @Override
//    public void finishAfterTransition() {
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        super.finishAfterTransition();
//    }
}
