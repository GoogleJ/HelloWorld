package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.SaveCommunityRequest;
import com.zxjk.duoduo.bean.response.GetPaymentListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.OssUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;

public class CreateSocialActivity4 extends BaseActivity {

    private FrameLayout flTop;
    private TextView tvTitle;
    private ImageView ivBackground;
    private ImageView ivHead;
    private EditText etName;
    private EditText etSlogan;
    private TextView tvCount;
    private TextView tvLogo;

    //标识 是否为背景选择
    private boolean isBgFlag;
    //社群背景图
    private String bgUrl = "https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/4FA1FF20-DBD9-4E55-AB90-DC80D3969B59.jpg";
    //社群头像
    private String headUrl = "https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnableTouchHideKeyBoard(false);
        setTrasnferStatusBar(true);
        BarUtils.setStatusBarLightMode(this, true);
        setContentView(R.layout.activity_create_social4);

        initView();
    }

    private void initView() {
        flTop = findViewById(R.id.flTop);
        tvTitle = findViewById(R.id.tvTitle);
        ivBackground = findViewById(R.id.ivBackground);
        ivHead = findViewById(R.id.ivHead);
        etName = findViewById(R.id.etName);
        etSlogan = findViewById(R.id.etSlogan);
        tvCount = findViewById(R.id.tvCount);
        tvLogo = findViewById(R.id.tvLogo);

        ViewGroup.LayoutParams layoutParams = flTop.getLayoutParams();
        layoutParams.height = (int) (ScreenUtils.getScreenWidth() * 0.75);
        flTop.setLayoutParams(layoutParams);

        Glide.with(this).load(R.drawable.bg_default_social).into(ivBackground);
        Glide.with(this).load(io.rong.imkit.R.drawable.rc_default_portrait).into(ivHead);

        tvTitle.setText(R.string.modify);

        etSlogan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCount.setText(s.length() + "/1000");
            }
        });
    }

    @SuppressLint("CheckResult")
    public void createSocial(View view) {
        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort(R.string.input_socialname1);
            return;
        }

        SaveCommunityRequest request = new SaveCommunityRequest();

        GetPaymentListBean payBean = getIntent().getParcelableExtra("pay");
        String payMoney = getIntent().getStringExtra("payMoney");
        if (payBean != null) {
            request.setPay("1");
            request.setPaySymbol(payBean.getSymbol());
            request.setPayFee(payMoney);
        }

        request.setLogo(headUrl);
        request.setBgi(bgUrl);
        request.setName(name);
        request.setIntroduction(etSlogan.getText().toString().trim());

        ServiceFactory.getInstance().getBaseService(Api.class)
                .saveCommuntiy(GsonUtils.toJson(request))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(r -> {
                    ToastUtils.showShort(R.string.create_social_success);
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    intent = new Intent(this, SocialHomeActivity.class);
                    startActivity(intent);
                }, this::handleApiError);
    }

    public void back(View view) {
        finish();
    }

    public void camera(View view) {
        isBgFlag = true;
        KeyboardUtils.hideSoftInput(this);
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.config(new TakePicUtil.Config().rectParm(100, 75));
                    TakePicUtil.takePicture(CreateSocialActivity4.this);
                });
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.config(new TakePicUtil.Config().rectParm(100, 75));
                    TakePicUtil.albumPhoto(CreateSocialActivity4.this);
                });
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
                    if (isBgFlag) {
                        bgUrl = url;
                        GlideUtil.loadNormalImg(ivBackground, url);
                    } else {
                        tvLogo.setVisibility(View.INVISIBLE);
                        headUrl = url;
                        GlideUtil.loadCircleImg(ivHead, url);
                    }
                });
            });
        }
    }

    private RectF rectHead;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (rectHead == null) {
                int i = CommonUtils.dip2px(this, 88);
                int[] location = new int[2];
                ivHead.getLocationOnScreen(location);
                rectHead = new RectF(location[0], location[1], location[0] + i, location[1] + i);
            }
            if (rectHead.contains(event.getRawX(), event.getRawY())) {
                isBgFlag = false;
                KeyboardUtils.hideSoftInput(this);
                NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.tv_photograph, v -> {
                            dialog.dismiss();
                            TakePicUtil.takePicture(CreateSocialActivity4.this);
                        });
                        holder.setOnClickListener(R.id.tv_photo_select, v -> {
                            dialog.dismiss();
                            TakePicUtil.albumPhoto(CreateSocialActivity4.this);
                        });
                        holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                    }
                }).setShowBottom(true)
                        .setOutCancel(true)
                        .setDimAmount(0.5f)
                        .show(getSupportFragmentManager());
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
