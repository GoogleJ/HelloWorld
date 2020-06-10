package com.zxjk.duoduo.ui.cast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.CastDao;
import com.zxjk.duoduo.bean.response.GetChatRoomInfoResponse;
import com.zxjk.duoduo.db.Cast;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.OssUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import org.greenrobot.greendao.query.DeleteQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.qqtheme.framework.widget.WheelView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class ModifyWechatCastActivity extends BaseActivity {
    private GetChatRoomInfoResponse info;

    private FrameLayout flThumbContainer;
    private ImageView ivThumb;

    private EditText etTitle;
    private TextView tvCountTitle;

    private TextView tvTime;

    private EditText etDetail;
    private TextView tvCountDetail;

    private SubsamplingScaleImageView ivDetail;

    private QuickPopup timePicker;
    private ArrayList<String> dateList = new ArrayList<>(3);
    private ArrayList<String> timeList1 = new ArrayList<>();
    private ArrayList<String> timeList2 = new ArrayList<>();

    private int imgFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_wechat_cast);

        info = getIntent().getParcelableExtra("info");

        flThumbContainer = findViewById(R.id.flThumbContainer);
        ivThumb = findViewById(R.id.ivThumb);
        ivDetail = findViewById(R.id.ivDetail);
        etTitle = findViewById(R.id.etTitle);
        tvCountTitle = findViewById(R.id.tvCountTitle);
        tvTime = findViewById(R.id.tvTime);
        etDetail = findViewById(R.id.etDetail);
        tvCountDetail = findViewById(R.id.tvCountDetail);

        initData();
    }

    public void back(View view) {
        finish();
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
                info.setStartTime(String.valueOf(TimeUtils.string2Millis(yearStr + date + time, "yyyy年MM月dd日HH点mm分")));
                tvTime.setText(date + "  " + time);
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

    public void deleteCast(View view) {
        MuteRemoveDialog dialog = new MuteRemoveDialog(this, getString(R.string.cancel), getString(R.string.queding),
                getString(R.string.hinttext), getString(R.string.confirm_delete_cast));
        dialog.setOnCommitListener(() -> ServiceFactory.getInstance().getBaseService(Api.class)
                .delLive(info.getRoomId())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .doOnNext(s -> {
                    DeleteQuery<Cast> deleteQuery = Application.daoSession.queryBuilder(Cast.class)
                            .where(CastDao.Properties.RoomId.eq(info.getRoomId())).buildDelete();
                    deleteQuery.executeDeleteWithoutDetachingEntities();
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    Intent intent1 = new Intent(this, HomeActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                }, this::handleApiError));
        dialog.show();
    }

    @SuppressLint("CheckResult")
    public void save(View view) {
        info.setTopic(etTitle.getText().toString());
        info.setLiveDetails(etDetail.getText().toString());
        ServiceFactory.getInstance().getBaseService(Api.class)
                .modifyLive(GsonUtils.toJson(info),info.getLiveType())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    Cast cast = new Cast();
                    cast.setStartTimeStamp(Long.parseLong(info.getStartTime()));
                    cast.setRoomId(info.getRoomId());
                    cast.setType("1");
                    Application.daoSession.getCastDao().insertOrReplace(cast);

                    Intent intent = new Intent();
                    intent.putExtra("info", info);
                    this.setResult(1, intent);
                    finish();
                }, this::handleApiError);
    }

    private void initData() {
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCountTitle.setText(String.valueOf(s.toString().length()));
            }
        });

        etDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCountDetail.setText(String.valueOf(s.toString().length()));
            }
        });

        etTitle.setText(info.getTopic());
        etDetail.setText(info.getLiveDetails());
        tvTime.setText(TimeUtils.millis2String(Long.parseLong(info.getStartTime()), "MM月dd日  HH点mm分"));

        flThumbContainer.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (flThumbContainer.getWidth() > 0) {
                            ViewGroup.LayoutParams params = flThumbContainer.getLayoutParams();
                            params.height = (int) (flThumbContainer.getWidth() * 0.6f);
                            flThumbContainer.setLayoutParams(params);
                            flThumbContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });

        GlideUtil.loadNormalImg(ivThumb, info.getLivePoster());

        loadLongImg();

        getPermisson(ivThumb, result -> {
            if (result) {
                imgFlag = 1;
                showSelectPop();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        getPermisson(ivDetail, result -> {
            if (result) {
                imgFlag = 2;
                showSelectPop();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void loadLongImg() {
        Glide.with(this).download(new GlideUrl(info.getLiveContentImg()))
                .into(new CustomViewTarget<SubsamplingScaleImageView, File>(ivDetail) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    }

                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        ivDetail.setImage(ImageSource.uri(Uri.fromFile(resource)));
                    }

                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {
                    }
                });
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
                    if (imgFlag == 1) {
                        TakePicUtil.config(new TakePicUtil.Config().rectParm(23, 14));
                        TakePicUtil.takePicture(ModifyWechatCastActivity.this);
                    } else {
                        TakePicUtil.takePicture(ModifyWechatCastActivity.this, false);
                    }
                });
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    if (imgFlag == 1) {
                        TakePicUtil.config(new TakePicUtil.Config().rectParm(23, 14));
                        TakePicUtil.albumPhoto(ModifyWechatCastActivity.this);
                    } else {
                        TakePicUtil.albumPhoto(ModifyWechatCastActivity.this, false);
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
    private void uploadImg1() {
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
                                }).submit(ivThumb.getWidth(), ivThumb.getHeight())
                ))
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                .timeout(6, TimeUnit.SECONDS)
                .subscribe(url -> {
                    GlideUtil.loadNormalImg(ivThumb, url);
                    info.setLivePoster(url);
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

    @SuppressLint("CheckResult")
    private void uploadImg2() {
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
                .flatMap((Function<String, ObservableSource<File>>) url -> Observable.create(e ->
                        Glide.with(this).download(new GlideUrl(url))
                                .into(new CustomViewTarget<SubsamplingScaleImageView, File>(ivDetail) {
                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        e.tryOnError(new Exception(getString(R.string.upload_fail)));
                                    }

                                    @Override
                                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                        info.setLiveContentImg(url);
                                        e.onNext(resource);
                                        e.onComplete();
                                    }

                                    @Override
                                    protected void onResourceCleared(@Nullable Drawable placeholder) {
                                    }
                                })
                ))
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                .timeout(6, TimeUnit.SECONDS)
                .subscribe(resource -> {
                    ivDetail.setImage(ImageSource.uri(Uri.fromFile(resource)));
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
        if (imgFlag == 1) {
            uploadImg1();
        } else {
            uploadImg2();
        }
    }

}
