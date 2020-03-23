package com.zxjk.duoduo.ui.webcast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.KeyboardUtils;
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
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.OssUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.picker.TimePicker;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class CreateCastActivity extends BaseActivity {

    private int stepFlag = 1;
    private boolean isFlipping;

    private LinearLayout llRoot;

    private ViewFlipper vf;
    private ImageView ivStep;
    private TextView tvStep;

    private RelativeLayout rlStep1;
    private ImageView ivCrossStep1;
    private ImageView ivContent1;
    private ImageView ivDelete;

    private FrameLayout flStep2;
    private EditText etStep2;
    private TextView tvCountStep2;

    private LinearLayout llStep3;
    private TextView tvStep3Date;
    private TextView tvStep3Time;

    private LinearLayout llStep4;
    private EditText etStep4;
    private TextView tvCountStep4;
    private RelativeLayout rlImgContainerStep4;
    private ImageView ivCrossStep4;
    private ImageView ivContent4;
    private ImageView ivDelete2;

    private TextView tvBottom;

    private String thumbUrl;
    private String title;
    private String date;
    private String time;
    private String detail;
    private String detailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cast);

        setTrasnferStatusBar(true);

        initView();

        initData();
    }

    private void initData() {
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
        ivDelete = findViewById(R.id.ivDelete);

        flStep2 = findViewById(R.id.flStep2);
        etStep2 = findViewById(R.id.etStep2);
        tvCountStep2 = findViewById(R.id.tvCountStep2);

        llStep3 = findViewById(R.id.llStep3);
        tvStep3Date = findViewById(R.id.tvStep3Date);
        tvStep3Time = findViewById(R.id.tvStep3Time);

        llStep4 = findViewById(R.id.llStep4);
        etStep4 = findViewById(R.id.etStep4);
        tvCountStep4 = findViewById(R.id.tvCountStep4);
        rlImgContainerStep4 = findViewById(R.id.rlImgContainerStep4);
        ivCrossStep4 = findViewById(R.id.ivCrossStep4);
        ivContent4 = findViewById(R.id.ivContent4);
        ivDelete2 = findViewById(R.id.ivDelete2);

        tvBottom = findViewById(R.id.tvBottom);
    }

    public void next(View view) {
        if (stepFlag == 4) {
            if (TextUtils.isEmpty(detail)) {
                ToastUtils.showShort(R.string.input_cast_detail);
                return;
            }
            if (TextUtils.isEmpty(detailUrl)) {
                //todo 详情img是否为必选项?
//                ToastUtils.showShort(R.string.setup_cast_detailimg);
//                return;
            }
            //todo 调用接口创建直播
            return;
        }
        updateUIByStep(true);
    }

    public void selectTime(View view) {
        TimePicker timePicker = new TimePicker(this, DateTimePicker.HOUR_24);
        String[] split = TimeUtils.millis2String(TimeUtils.getNowMills() + 1800000, "HH:mm").split(":");
        timePicker.setRangeStart(Integer.parseInt(split[0]) + 1, Integer.parseInt(split[1]));
        timePicker.setRangeEnd(23, 59);
        initPicker(timePicker);
        timePicker.setTitleText(getString(R.string.choose_time));

        timePicker.setOnTimePickListener((hour, minute) -> {
            String time = getString(R.string.x_colon_x, hour, minute);
            tvStep3Time.setText(time);
            this.time = time;
        });

        timePicker.show();
    }

    public void selectDate(View view) {
        DatePicker datePicker = new DatePicker(this, DateTimePicker.MONTH_DAY);
        String[] nowString = TimeUtils.getNowString(new SimpleDateFormat("MM-dd")).split("-");
        datePicker.setRangeStart(Integer.parseInt(nowString[0]),
                Integer.parseInt(nowString[1]));
        datePicker.setRangeEnd(Integer.parseInt(nowString[0]), Integer.parseInt(nowString[1]) + 3);
        initPicker(datePicker);
        datePicker.setTitleText(getString(R.string.choose_date));

        datePicker.setOnDatePickListener((DatePicker.OnMonthDayPickListener) (month, day) -> {
            String date = getString(R.string.x_month_x_day, month, day);
            tvStep3Date.setText(date);
            this.date = date;
        });

        datePicker.show();
    }

    public void clearImg(View view) {
        if (stepFlag == 1) {
            ivDelete.setVisibility(View.INVISIBLE);
            ivCrossStep1.setVisibility(View.VISIBLE);
            ivContent1.setImageBitmap(null);
            thumbUrl = "";
            rlStep1.setBackgroundResource(R.drawable.shape_create_cast_step1);
        } else if (stepFlag == 4) {
            ivDelete2.setVisibility(View.INVISIBLE);
            ivCrossStep4.setVisibility(View.VISIBLE);
            ivContent4.setImageBitmap(null);
            detailUrl = "";
            rlImgContainerStep4.setBackgroundResource(R.drawable.shape_create_cast_step1);
        }
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
                    if (TextUtils.isEmpty(date)) {
                        ToastUtils.showShort(R.string.setup_cast_date);
                        return;
                    }
                    if (TextUtils.isEmpty(time)) {
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

    private void showSelectPop() {
        KeyboardUtils.hideSoftInput(this);
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    if (stepFlag == 1) {
                        TakePicUtil.config(new TakePicUtil.Config().rectParm(23, 14));
                        TakePicUtil.takePicture(CreateCastActivity.this);
                    } else {
                        TakePicUtil.takePicture(CreateCastActivity.this, false);
                    }
                });
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    if (stepFlag == 1) {
                        TakePicUtil.config(new TakePicUtil.Config().rectParm(23, 14));
                        TakePicUtil.albumPhoto(CreateCastActivity.this);
                    } else {
                        TakePicUtil.albumPhoto(CreateCastActivity.this, false);
                    }
                });
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
            }
        }).setShowBottom(true)
                .setOutCancel(true)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }

    private void initPicker(DateTimePicker picker) {
        picker.setLabel("", "", "", "", "");
        picker.setTitleTextSize(17);
        picker.setTitleTextColor(ContextCompat.getColor(this, R.color.textcolor1));
        picker.setCancelTextColor(ContextCompat.getColor(this, R.color.textcolor3));
        picker.setSubmitTextColor(ContextCompat.getColor(this, R.color.colorTheme));
        picker.setTopLineColor(Color.parseColor("#E5E5E5"));
        picker.setDividerVisible(false);
        picker.setTextColor(Color.parseColor("#000000"), Color.parseColor("#bababa"));
        picker.setTextSize(18);
        View foot = new View(this);
        foot.setBackgroundColor(Color.parseColor("#ffffff"));
        picker.setFooterView(foot);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(this, 24));
        foot.setLayoutParams(layoutParams);
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
                        ivDelete.setVisibility(View.VISIBLE);
                    } else if (stepFlag == 4) {
                        GlideUtil.loadCornerImg(ivContent4, url, 10);
                        detailUrl = url;
                        ivCrossStep4.setVisibility(View.GONE);
                        rlImgContainerStep4.setBackground(null);
                        ivDelete2.setVisibility(View.VISIBLE);
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
