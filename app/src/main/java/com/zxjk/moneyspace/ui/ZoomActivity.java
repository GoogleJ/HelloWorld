package com.zxjk.moneyspace.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.request.EditCommunityRequest;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.PinchImageView;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;
import com.zxjk.moneyspace.utils.OssUtils;
import com.zxjk.moneyspace.utils.SaveImageUtil;
import com.zxjk.moneyspace.utils.TakePicUtil;

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

        BarUtils.setStatusBarVisibility(this, false);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setNavigationBarColor(0x00000000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        setContentView(R.layout.activity_zoom);
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
            pic.setOnClickListener(v -> finishAfterTransition());
        }
    }

    public void back(View view) {
        finishAfterTransition();
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

}
