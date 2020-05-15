package com.zxjk.duoduo.ui.webcast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.CreateLiveRequest;
import com.zxjk.duoduo.db.Cast;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.OssUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.qqtheme.framework.widget.WheelView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.Group;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class CreateWechatCastActivity extends BaseActivity {
    private QuickPopup timePicker;

    private int stepFlag = 1;
    private boolean isFlipping;

    private LinearLayout llRoot;

    private ViewFlipper vf;
    private ImageView ivStep;
    private TextView tvStep;

    private RelativeLayout rlStep1;
    private ImageView ivCrossStep1;
    private ImageView ivContent1;

    private FrameLayout flStep2;
    private EditText etStep2;
    private TextView tvCountStep2;

    private LinearLayout llStep3;
    private TextView tvStep3Time;

    private LinearLayout llStep4;
    private EditText etStep4;
    private TextView tvCountStep4;
    private RelativeLayout rlImgContainerStep4;
    private ImageView ivCrossStep4;
    private ImageView ivContent4;

    private TextView tvBottom;

    private String thumbUrl;
    private String title;
    private long startTimeStamp;
    private String detailUrl;
    private String chooseFlag;

    private ArrayList<String> dateList = new ArrayList<>(3);
    private ArrayList<String> timeList1 = new ArrayList<>();
    private ArrayList<String> timeList2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wechat_cast);

        setTrasnferStatusBar(true);

        initView();

        initData();
    }

    private void initData() {
        chooseFlag = getIntent().getStringExtra("chooseFlag");
        rlStep1.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (rlStep1.getWidth() > 0) {
                            ViewGroup.LayoutParams params = rlStep1.getLayoutParams();
                            params.height = (int) (rlStep1.getWidth() * 0.6f);
                            rlStep1.setLayoutParams(params);
                            rlStep1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });

        getPermisson(rlStep1, result -> {
            if (result) {
                showSelectPop();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        getPermisson(rlImgContainerStep4, result -> {
            if (result) {
                showSelectPop();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        etStep2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCountStep2.setText(String.valueOf(s.toString().length()));
            }
        });

        etStep4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCountStep4.setText(String.valueOf(s.toString().length()));
            }
        });
    }

    private void initView() {
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        llRoot = findViewById(R.id.llRoot);

        vf = findViewById(R.id.vf);
        ivStep = findViewById(R.id.ivStep);
        tvStep = findViewById(R.id.tvStep);

        rlStep1 = findViewById(R.id.rlStep1);
        ivCrossStep1 = findViewById(R.id.ivCrossStep1);
        ivContent1 = findViewById(R.id.ivContent1);

        flStep2 = findViewById(R.id.flStep2);
        etStep2 = findViewById(R.id.etStep2);
        tvCountStep2 = findViewById(R.id.tvCountStep2);

        llStep3 = findViewById(R.id.llStep3);
        tvStep3Time = findViewById(R.id.tvStep3Time);

        llStep4 = findViewById(R.id.llStep4);
        etStep4 = findViewById(R.id.etStep4);
        tvCountStep4 = findViewById(R.id.tvCountStep4);
        rlImgContainerStep4 = findViewById(R.id.rlImgContainerStep4);
        ivCrossStep4 = findViewById(R.id.ivCrossStep4);
        ivContent4 = findViewById(R.id.ivContent4);

        tvBottom = findViewById(R.id.tvBottom);
    }

    @SuppressLint("CheckResult")
    public void next(View view) {
        if (stepFlag == 4) {
            String detail = etStep4.getText().toString().trim();
            if (TextUtils.isEmpty(detail)) {
                ToastUtils.showShort(R.string.input_cast_detail);
                return;
            }
            if (TextUtils.isEmpty(detailUrl)) {
                ToastUtils.showShort(R.string.setup_cast_detailimg);
                return;
            }

            CreateLiveRequest request = new CreateLiveRequest();
            request.setGroupId(getIntent().getStringExtra("groupId"));
            request.setLiveDetails(detail);
            request.setLiveContentImg(detailUrl);
            request.setTopic(title);
            request.setLivePoster(thumbUrl);
            request.setStartTime(String.valueOf(startTimeStamp));

            Group groupInfo = RongUserInfoManager.getInstance().getGroupInfo(getIntent().getStringExtra("groupId"));
            if (groupInfo != null) {
                String groupInfoName = groupInfo.getName();
                if (!TextUtils.isEmpty(groupInfoName)) {
                    request.setGroupNikeName(groupInfoName.replace("おれは人间をやめるぞ！ジョジョ―――ッ!", ""));
                }
            }

            ServiceFactory.getInstance().getBaseService(Api.class)
                    .createLive(GsonUtils.toJson(request))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        Cast cast = new Cast();
                        cast.setRoomId(s);
                        cast.setType("1");
                        cast.setStartTimeStamp(startTimeStamp);
                        Application.daoSession.getCastDao().insertOrReplace(cast);

                        Intent intent = new Intent(this, WechatCastDetailActivity.class);
                        intent.putExtra("roomId", s);
                        intent.putExtra("fromCreate", true);
                        startActivity(intent);
                        finish();
                    }, this::handleApiError);
            return;
        }
        updateUIByStep(true);
    }

    public void selectTime(View view) {
        if (timePicker == null) {
            initTimeList();

            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
            showAnimation.setDuration(250);
            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
            dismissAnimation.setDuration(500);

            timePicker = QuickPopupBuilder.with(this)
                    .contentView(R.layout.popup_choosetime_cast)
                    .config(new QuickPopupConfig()
                            .withShowAnimation(showAnimation)
                            .withDismissAnimation(dismissAnimation)
                            .withClick(R.id.ivClose, null, true))
                    .build();

            WheelView wheel1 = timePicker.findViewById(R.id.wheel1);
            WheelView wheel2 = timePicker.findViewById(R.id.wheel2);
            WheelView wheel3 = timePicker.findViewById(R.id.wheel3);
            TextView tvBottom = timePicker.findViewById(R.id.tvBottom);

            tvBottom.setOnClickListener(v -> {
                String date = dateList.get(wheel1.getSelectedIndex());
                String time = timeList1.get(wheel2.getSelectedIndex()) + timeList2.get(wheel3.getSelectedIndex());
                String yearStr = TimeUtils.millis2String(TimeUtils.getNowMills(), "yyyy年");
                startTimeStamp = TimeUtils.string2Millis(yearStr + date + time, "yyyy年MM月dd日HH点mm分");
                tvStep3Time.setText(date + "  " + time);
                timePicker.dismiss();
            });

            initWheelView(wheel1);
            initWheelView(wheel2);
            initWheelView(wheel3);

            wheel1.setItems(dateList);
            wheel2.setItems(timeList1);
            wheel3.setItems(timeList2);

            try {
                wheel2.setSelectedIndex(Integer.parseInt(TimeUtils.millis2String(System.currentTimeMillis(), "HH")));
                int minute = Integer.parseInt(TimeUtils.millis2String(System.currentTimeMillis(), "mm"));
                int i = (int) Math.ceil(minute / 5f);
                if (i >= timeList2.size()) {
                    i = timeList2.size() - 1;
                }
                wheel3.setSelectedIndex(i);
            } catch (Exception e) {
            }
        }

        timePicker.showPopupWindow();
    }

    @SuppressLint("CheckResult")
    private void updateUIByStep(boolean isNext) {
        if (isFlipping) return;

        Fade fade = new Fade();
        ChangeBounds changeBounds = new ChangeBounds();
        TransitionSet set = new TransitionSet();
        set.addTransition(fade);
        set.addTransition(changeBounds);
        set.setDuration(300);
        set.setInterpolator(new OvershootInterpolator());
        changeBounds.excludeTarget(tvStep, true);
        TransitionManager.beginDelayedTransition(llRoot, set);

        if (isNext) {
            switch (stepFlag) {
                case 1:
                    if (TextUtils.isEmpty(thumbUrl)) {
                        ToastUtils.showShort(R.string.upload_cast_thumb);
                        return;
                    }
                    rlStep1.setVisibility(View.GONE);
                    flStep2.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(etStep2.getText().toString().trim())) {
                        etStep2.requestFocus();
                        KeyboardUtils.showSoftInput(this);
                    }
                    break;
                case 2:
                    String etTitleText = etStep2.getText().toString().trim();
                    if (TextUtils.isEmpty(etTitleText)) {
                        ToastUtils.showShort(R.string.setup_cast_title);
                        return;
                    }
                    title = etTitleText;
                    flStep2.setVisibility(View.GONE);
                    llStep3.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    if (startTimeStamp == 0) {
                        ToastUtils.showShort(R.string.setup_cast_time);
                        return;
                    }
                    llStep3.setVisibility(View.GONE);
                    llStep4.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(etStep4.getText().toString().trim())) {
                        etStep4.requestFocus();
                        KeyboardUtils.showSoftInput(this);
                    }
                    break;
            }
        } else {
            switch (stepFlag) {
                case 2:
                    rlStep1.setVisibility(View.VISIBLE);
                    flStep2.setVisibility(View.GONE);
                    break;
                case 3:
                    flStep2.setVisibility(View.VISIBLE);
                    llStep3.setVisibility(View.GONE);
                    break;
                case 4:
                    llStep3.setVisibility(View.VISIBLE);
                    llStep4.setVisibility(View.GONE);
                    break;
            }
        }

        if (isNext) {
            vf.showNext();
            stepFlag += 1;
        } else {
            vf.showPrevious();
            stepFlag -= 1;
        }

        isFlipping = true;
        Observable.timer(350, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(a -> isFlipping = false);

        if (stepFlag == 4) {
            tvBottom.setText(R.string.done1);
        } else {
            tvBottom.setText(R.string.next);
        }

        switch (stepFlag) {
            case 1:
                tvStep.setText(R.string.create_cast_step1);
                ivStep.setImageResource(R.drawable.ic_create_cast_step1);
                break;
            case 2:
                tvStep.setText(R.string.create_cast_step2);
                ivStep.setImageResource(R.drawable.ic_create_cast_step2);
                break;
            case 3:
                tvStep.setText(R.string.create_cast_step3);
                ivStep.setImageResource(R.drawable.ic_create_cast_step3);
                break;
            case 4:
                tvStep.setText(R.string.create_cast_step4);
                ivStep.setImageResource(R.drawable.ic_create_cast_step4);
                break;
        }
    }

    private void initTimeList() {
        for (int i = 0; i < 4; i++) {
            dateList.add(TimeUtils.millis2String(TimeUtils.getNowMills() + i * 86400000,
                    "MM月dd日"));
        }

        for (int i = 0; i < 24; i++) {
            String str;
            str = i + "点";
            if (i < 10) {
                str = "0" + str;
            }
            timeList1.add(str);
        }

        for (int i = 0; i < 59; i++) {
            if (i % 5 != 0) {
                continue;
            }
            if (i == 0) {
                timeList2.add("00分");
            } else if (i == 5) {
                timeList2.add("05分");
            } else {
                timeList2.add(i + "分");
            }
        }
    }

    private void initWheelView(WheelView wheel) {
        WheelView.DividerConfig dividerConfig = new WheelView.DividerConfig();
        dividerConfig.setColor(Color.parseColor("#DDDDDD"));
        dividerConfig.setThick(CommonUtils.dip2px(this, 1));

        wheel.setLineSpaceMultiplier(WheelView.LINE_SPACE_MULTIPLIER);
        wheel.setTextPadding(WheelView.TEXT_PADDING);
        wheel.setTextSize(17);
        wheel.setTypeface(Typeface.DEFAULT);
        wheel.setTextColor(Color.parseColor("#C1C2C6"), Color.BLACK);
        wheel.setOffset(WheelView.ITEM_OFF_SET);
        wheel.setCycleDisable(true);
        wheel.setUseWeight(true);
        wheel.setDividerConfig(dividerConfig);
    }

    private void showSelectPop() {
        KeyboardUtils.hideSoftInput(this);
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    if (stepFlag == 1) {
                        TakePicUtil.config(new TakePicUtil.Config().rectParm(23, 14));
                        TakePicUtil.takePicture(CreateWechatCastActivity.this);
                    } else {
                        TakePicUtil.takePicture(CreateWechatCastActivity.this, false);
                    }
                });
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    if (stepFlag == 1) {
                        TakePicUtil.config(new TakePicUtil.Config().rectParm(23, 14));
                        TakePicUtil.albumPhoto(CreateWechatCastActivity.this);
                    } else {
                        TakePicUtil.albumPhoto(CreateWechatCastActivity.this, false);
                    }
                });
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
            }
        }).setShowBottom(true)
                .setOutCancel(true)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }

    @SuppressLint("CheckResult")
    private void uploadImg() {
        zipFile(Collections.singletonList(corpFile.getPath()))
                .flatMap((Function<List<File>, ObservableSource<String>>) files -> Observable.create(e ->
                        OssUtils.uploadFile(files.get(0).getAbsolutePath(), new OssUtils.OssCallBack1() {
                            @Override
                            public void onSuccess(String url) {
                                e.onNext(url);
                                e.onComplete();
                            }

                            @Override
                            public void onFail() {
                                e.tryOnError(new Exception(getString(R.string.function_fail)));
                            }
                        }, null)))
                .flatMap((Function<String, ObservableSource<String>>) url -> Observable.create(e ->
                        Glide.with(this).downloadOnly().load(url)
                                .listener(new RequestListener<File>() {
                                    @Override
                                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                                        e.onNext(url);
                                        e.onComplete();
                                        return true;
                                    }

                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException ex, Object model, Target<File> target, boolean isFirstResource) {
                                        e.tryOnError(new Exception(getString(R.string.upload_fail)));
                                        return false;
                                    }
                                }).submit(ivContent1.getWidth(), ivContent1.getHeight())
                ))
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                .timeout(6, TimeUnit.SECONDS)
                .subscribe(url -> {
                    if (stepFlag == 1) {
                        GlideUtil.loadCornerImg(ivContent1, url, 10);
                        thumbUrl = url;
                        ivCrossStep1.setVisibility(View.GONE);
                        rlStep1.setBackground(null);
                    } else if (stepFlag == 4) {
                        GlideUtil.loadCornerImg(ivContent4, url, 10);
                        detailUrl = url;
                        ivCrossStep4.setVisibility(View.GONE);
                        rlImgContainerStep4.setBackground(null);
                    }
                }, t -> {
                    if (TextUtils.isEmpty(t.getMessage())) {
                        ToastUtils.showShort(R.string.function_fail);
                    } else if (t instanceof TimeoutException) {
                        ToastUtils.showShort(getString(R.string.upload_fail));
                    } else {
                        ToastUtils.showShort(t.getMessage());
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (corpFile == null) {
            return;
        }

        uploadImg();
    }

    @Override
    public void onBackPressed() {
        if (stepFlag >= 2) {
            updateUIByStep(false);
            return;
        }
        super.onBackPressed();
    }
}
