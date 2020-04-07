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
import com.zxjk.moneyspace.bean.response.ByBoinsResponse;
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
    private ByBoinsResponse byBoinsResponse;

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
        etSocialSlogan=findViewById(R.id.etSocialSlogan);
        ivAddImages = findViewById(R.id.iv_add_images);
        tvSubmit = findViewById(R.id.tv_submit);
        ivDeleteImage = findViewById(R.id.iv_delete_image);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    @SuppressLint("CheckResult")
    private void initData() {
        byBoinsResponse = (ByBoinsResponse) getIntent().getSerializableExtra("ByBoinsResponse");
        ivAddImages.setOnClickListener(v -> TakePicUtil.albumPhoto(TheAppealActivity.this));
        ivDeleteImage.setOnClickListener(v -> {
            ivAddImages.setImageResource(R.drawable.ic_add_images);
            ivDeleteImage.setVisibility(View.GONE);
            imageAddress = "";
        });

        tvSubmit.setOnClickListener(v -> {
            if(TextUtils.isEmpty(etSocialSlogan.getText())){
                ToastUtils.showShort(R.string.appeal_message);
                return;
            }
            if (TextUtils.isEmpty(imageAddress)) {
                ToastUtils.showShort(R.string.set_add_image);
                return;
            }

            long timeStampSec = System.currentTimeMillis() / 1000;
            timestamp = String.format("%010d", timeStampSec);

            String secret = "img=" + imageAddress +
                    "&nonce=" + timestamp +
                    "&phone=" + Constant.currentUser.getMobile() +
                    "&reason=" + etSocialSlogan.getText() +
                    "&trans_id=" + byBoinsResponse.getTransId() +
                    "&user_id=" + Constant.USERID + Constant.SECRET;
            sign = Sha256.getSHA256(secret);
            ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                    .orderAppeal(imageAddress, timestamp, Constant.currentUser.getMobile(), String.valueOf(etSocialSlogan.getText()),byBoinsResponse.getTransId(),Constant.USERID)
                    .compose(bindToLifecycle())
                    .flatMap(paymentDoneResponse -> {
                        String secret1 = "nonce=" + timestamp +
                                "&trans_id=" + byBoinsResponse.getTransId() +
                                "&user_id=" + Constant.USERID + Constant.SECRET;
                        sign = Sha256.getSHA256(secret1);
                        return ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                                .orderInfo(timestamp, byBoinsResponse.getTransId(), Constant.USERID, byBoinsResponse.getPaymentType(), byBoinsResponse.getCreateTime());
                    })
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(s -> {
                        Intent intent = new Intent(this, PurchaseDetailsActivity.class);
                        intent.putExtra("ByBoinsResponse", s);
                        startActivity(intent);
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