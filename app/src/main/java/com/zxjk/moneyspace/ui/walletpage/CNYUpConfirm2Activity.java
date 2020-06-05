package com.zxjk.moneyspace.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
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
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.SuccessActivity1;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.wallet.BalanceDetailActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;
import com.zxjk.moneyspace.utils.OssUtils;
import com.zxjk.moneyspace.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class CNYUpConfirm2Activity extends BaseActivity {

    private String money;

    private EditText et;
    private ImageView ivSign;
    private TextView tvCount;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnyup_confirm2);

        money = getIntent().getStringExtra("money");

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.uploadSign);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        et = findViewById(R.id.et);
        ivSign = findViewById(R.id.ivSign);
        tvCount = findViewById(R.id.tvCount);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCount.setText(s.toString().length() + "");
            }
        });
    }

    @SuppressLint("CheckResult")
    public void done(View view) {
        if (TextUtils.isEmpty(url)) {
            ToastUtils.showShort(R.string.nosign);
            return;
        }

        if (TextUtils.isEmpty(et.getText().toString().trim())) {
            ToastUtils.showShort(R.string.input_empty);
            return;
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .cnyRechargeConfirm(money, et.getText().toString().trim(), url)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
//                    Intent intent = new Intent(this, BalanceDetailActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);

                    Intent intent = new Intent(this, SuccessActivity1.class);
                    startActivity(intent);
                    finish();
                }, this::handleApiError);
    }

    public void upload(View view) {
        KeyboardUtils.hideSoftInput(this);
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.takePicture(CNYUpConfirm2Activity.this, false);

                });
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.albumPhoto(CNYUpConfirm2Activity.this, false);
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
                                }).submit(ivSign.getWidth(), ivSign.getHeight())
                ))
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                .timeout(6, TimeUnit.SECONDS)
                .subscribe(url -> {
                    GlideUtil.loadCornerImg(ivSign, url, 10);
                    this.url = url;
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

}

