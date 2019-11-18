package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.OssUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;

public class CreateSocialActivity4 extends BaseActivity {

    private FrameLayout flTop;
    private TextView tvTitle;
    private ImageView ivBackground;

    private String bgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);
        setContentView(R.layout.activity_create_social4);

        initView();

    }

    private void initView() {
        flTop = findViewById(R.id.flTop);
        tvTitle = findViewById(R.id.tvTitle);
        ivBackground = findViewById(R.id.ivBackground);

        ViewGroup.LayoutParams layoutParams = flTop.getLayoutParams();
        layoutParams.height = (int) (ScreenUtils.getScreenWidth() * 0.75);
        flTop.setLayoutParams(layoutParams);

        Glide.with(this).load(R.drawable.bg_default_social).into(ivBackground);

        tvTitle.setText(R.string.modify);
    }

    public void back(View view) {
        finish();
    }

    public void camera(View view) {
        KeyboardUtils.hideSoftInput(this);
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                //拍照
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.config(new TakePicUtil.Config().rectParm(100, 75));
                    TakePicUtil.takePicture(CreateSocialActivity4.this);
                });
                //相册选择
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.config(new TakePicUtil.Config().rectParm(100, 75));
                    TakePicUtil.albumPhoto(CreateSocialActivity4.this);
                });
                //取消
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
            }
        }).setShowBottom(true)
                .setOutCancel(true)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (corpFile != null) {
            zipFile(Collections.singletonList(corpFile.getPath()), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    bgUrl = url;
                    GlideUtil.loadNormalImg(ivBackground, url);
                });
            });
        }
    }
}
