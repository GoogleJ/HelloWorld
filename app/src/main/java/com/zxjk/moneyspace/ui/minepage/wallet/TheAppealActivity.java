package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetOrderInfoById;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;
import com.zxjk.moneyspace.utils.OssUtils;
import com.zxjk.moneyspace.utils.Sha256;
import com.zxjk.moneyspace.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;

public class TheAppealActivity extends BaseActivity {
    private GetOrderInfoById getOrderInfoById;

    private EditText etSocialSlogan;
    private ImageView ivAddImages;
    private TextView tvSubmit;
    private ImageView ivDeleteImage;

    private String imageAddress;
    private String sign;
    private String timestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_complaint);

        initView();

        initData();
    }

    private void initView() {
        etSocialSlogan = findViewById(R.id.etSocialSlogan);
        ivAddImages = findViewById(R.id.iv_add_images);
        tvSubmit = findViewById(R.id.tv_submit);
        ivDeleteImage = findViewById(R.id.iv_delete_image);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    @SuppressLint("CheckResult")
    private void initData() {
        getOrderInfoById = (GetOrderInfoById) getIntent().getSerializableExtra("GetOrderInfoById");
        ivAddImages.setOnClickListener(v -> TakePicUtil.albumPhoto(TheAppealActivity.this));
        ivDeleteImage.setOnClickListener(v -> {
            ivAddImages.setImageResource(R.drawable.ic_add_images);
            ivDeleteImage.setVisibility(View.GONE);
            imageAddress = "";
        });

        tvSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etSocialSlogan.getText())) {
                ToastUtils.showShort(R.string.appeal_message);
                return;
            }
            if (TextUtils.isEmpty(imageAddress)) {
                ToastUtils.showShort(R.string.set_add_image);
                return;
            }

            long timeStampSec = System.currentTimeMillis() / 1000;
            timestamp = String.format("%010d", timeStampSec);

            String secret = "appealReason=" + etSocialSlogan.getText().toString() +
                    "&bothOrderId=" + getOrderInfoById.getBothOrderId() +
                    "&nonce=" + timestamp +
                    "&picture=" + imageAddress +
                    "&plaintiffId=" + Constant.userId + Constant.SECRET;
            sign = Sha256.getSHA256(secret);
            ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                    .addAppeal(etSocialSlogan.getText().toString(),
                            getOrderInfoById.getBothOrderId(),
                            timestamp,
                            imageAddress,
                            Constant.userId)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.otc())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        ToastUtils.showShort(getString(R.string.toast13));
                        finish();
                    }, this::handleApiError);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (corpFile != null) {
            zipFile(Collections.singletonList(corpFile.getPath()), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    imageAddress = url;
                    GlideUtil.loadCornerImg(ivAddImages, url, 3);
                });
            });
            ivDeleteImage.setVisibility(View.VISIBLE);
        }
    }
}