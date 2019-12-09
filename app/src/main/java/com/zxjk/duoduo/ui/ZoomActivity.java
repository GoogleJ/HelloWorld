package com.zxjk.duoduo.ui;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.PinchImageView;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.SaveImageUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ScreenUtils.setFullScreen(this);
        setContentView(R.layout.activity_zoom);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        setLightStatusBar(false);
        getWindow().setStatusBarColor(Color.BLACK);
        BarUtils.setNavBarVisibility(this, false);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
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
                            .withClick(R.id.tv1, v -> handlePopwindow(1), true)
                            .withClick(R.id.tv2, v -> handlePopwindow(2), true)
                            .withClick(R.id.tv4, null, true))
                    .show();
            save2Phone(show.findViewById(R.id.tv3), show);
        }
    }

    private void save2Phone(View view, QuickPopup show) {
//        getPermisson(view, g -> {
//            show.dismiss(true);
//            //保存到手机
//            if (bitmap == null) {
//                return;
//            }
//
//            ivRecipetImg.buildDrawingCache();
//
//            SaveImageUtil.get().savePic(ivRecipetImg.getDrawingCache(), success -> {
//                if (success) {
//                    ToastUtils.showShort(R.string.savesucceed);
//                    return;
//                }
//                ToastUtils.showShort(R.string.savefailed);
//            });
//        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }


//    @Override
//    public void finishAfterTransition() {
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        super.finishAfterTransition();
//    }
}
