package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.zxjk.duoduo.BuildConfig;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetAppVersionResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

import java.io.File;
import java.io.IOException;

@SuppressLint("SetTextI18n")
public class AboutActivity extends BaseActivity {
    private long updateProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTrasnferStatusBar(true);
        initView();
    }

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_versionName = findViewById(R.id.tv_versionName);
        tv_versionName.setText(CommonUtils.getVersionName(this));
        tv_title.setTextColor(ContextCompat.getColor(this, R.color.white));
        tv_title.setText(getString(R.string.about_duo_duo));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    @SuppressLint("CheckResult")
    public void checkUpdate(View view) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getAppVersion()
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .compose(RxSchedulers.ioObserver())
                .subscribe(response -> {
                    if (response.code != Constant.CODE_SUCCESS) {
                        return;
                    }
                    GetAppVersionResponse data = response.data;
                    String appVersionName = AppUtils.getAppVersionName();

                    File file = new File(Utils.getApp().getCacheDir(), data.getVersion() + ".apk");
                    if (file.exists()) {
                        file.delete();
                    }

                    if (!appVersionName.equals(data.getVersion())) {
                        NiceDialog.init().setLayoutId(R.layout.dialog_update).setConvertListener(new ViewConvertListener() {
                            @Override
                            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                                TextView tv = holder.getView(R.id.tv);
                                tv.setText(Html.fromHtml(data.getUpdateContent()));
                                ((TextView) holder.getView(R.id.tv)).setMovementMethod(new ScrollingMovementMethod());
                                TextView tvUpdate = holder.getView(R.id.tvUpdate);
                                FrameLayout flUpgrade = holder.getView(R.id.flUpgrade);

                                downloadOrInstall(tvUpdate, flUpgrade, data);
                            }
                        }).setDimAmount(0.5f).setOutCancel("0".equals(response.data.getIsEnforcement())).show(getSupportFragmentManager());
                    } else {
                        ToastUtils.showShort(R.string.newestVersion);
                    }
                }, t -> {
                });
    }

    private void downloadOrInstall(TextView tvUpdate, FrameLayout flUpgrade, GetAppVersionResponse data) {
        flUpgrade.setOnClickListener(v -> {
            //后台下载APK并更新
            flUpgrade.setClickable(false);
            ServiceFactory.downloadFile(data.getVersion(), data.getUpdateAddress(), new ServiceFactory.DownloadListener() {
                @Override
                public void onStart(long max) {
                    updateProgress = max;
                    ToastUtils.showShort(R.string.update_start);
                }

                @Override
                public void onProgress(long progress) {
                    runOnUiThread(() -> tvUpdate.setText((int) ((float) progress / updateProgress * 100) + "%"));
                }

                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        flUpgrade.setClickable(true);
                        tvUpdate.setText(R.string.dianjianzhuang);
                        flUpgrade.setOnClickListener(v1 -> {
                            File file = new File(Utils.getApp().getCacheDir(), data.getVersion() + ".apk");// 设置路径
                            String[] command = {"chmod", "777", file.getPath()};
                            ProcessBuilder builder = new ProcessBuilder(command);
                            try {
                                builder.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Intent intent = installIntent(file.getPath());
                            if (intent != null) {
                                startActivity(intent);
                            }
                        });
                        flUpgrade.performClick();
                    });
                }

                @Override
                public void onFailure() {
                    runOnUiThread(() -> {
                        File file = new File(Utils.getApp().getCacheDir(), data.getVersion() + ".apk");
                        if (file.exists()) {
                            file.delete();
                        }
                        downloadOrInstall(tvUpdate, flUpgrade, data);
                        flUpgrade.setClickable(true);
                        tvUpdate.setText(R.string.dianjichongshi);
                    });
                    ToastUtils.showShort(R.string.update_failure);
                }
            });
        });
    }

    private Intent installIntent(String path) {
        try {
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".FileProvider", file),
                        "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
