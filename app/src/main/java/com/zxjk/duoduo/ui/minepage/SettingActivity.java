package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.NewLoginActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

@SuppressLint("CheckResult")
public class SettingActivity extends BaseActivity {
    private Switch swGlobalMute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();

        setupRemind();

    }

    private void setupRemind() {
        RongIM.getInstance().getNotificationQuietHours(new RongIMClient.GetNotificationQuietHoursCallback() {
            @Override
            public void onSuccess(String s, int i) {
                if (i != 0) {
                    swGlobalMute.setChecked(false);
                } else {
                    swGlobalMute.setChecked(true);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });

        swGlobalMute.setOnClickListener(v -> {
            swGlobalMute.setEnabled(false);
            if (!swGlobalMute.isChecked()) {
                RongIM.getInstance().setNotificationQuietHours("00:00:00", 1439, new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        swGlobalMute.setEnabled(true);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        ToastUtils.showShort(R.string.function_fail);
                        swGlobalMute.setEnabled(true);
                        swGlobalMute.setChecked(!swGlobalMute.isChecked());
                    }
                });
            } else {
                RongIM.getInstance().removeNotificationQuietHours(new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        swGlobalMute.setEnabled(true);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        ToastUtils.showShort(R.string.function_fail);
                        swGlobalMute.setEnabled(true);
                        swGlobalMute.setChecked(!swGlobalMute.isChecked());
                    }
                });
            }
        });
    }

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.setting);
        RelativeLayout rl_back = findViewById(R.id.rl_back);

        swGlobalMute = findViewById(R.id.swGlobalMute);

        //返回
        rl_back.setOnClickListener(v -> finish());

        //账号
        findViewById(R.id.rl_account_number).setOnClickListener(v ->
                startActivity(new Intent(SettingActivity.this, AccountActivity.class)));

        //隐私
        findViewById(R.id.rl_privicy).setOnClickListener(v ->
                startActivity(new Intent(SettingActivity.this, PrivicyActivity.class)));

        //帮助中心
        findViewById(R.id.rl_helpCenter).setOnClickListener(v ->
                startActivity(new Intent(SettingActivity.this, HelpActivity.class)));
        //意见反馈
        findViewById(R.id.rl_feedback).setOnClickListener(v ->
                startActivity(new Intent(SettingActivity.this, FeedbackActivity.class)));
        //关于多多
        findViewById(R.id.rl_aboutDuoDuo).setOnClickListener(v ->
                startActivity(new Intent(SettingActivity.this, AboutActivity.class)));
        //语言切换
        findViewById(R.id.rl_languageSwitch).setOnClickListener(v -> {
        });
    }

    public void unLogin(View view) {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @SuppressLint("CheckResult")
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, R.string.hinttext);
                holder.setText(R.id.tv_content, R.string.your_will_unlogin);
                holder.setText(R.id.tv_cancel, R.string.cancel);
                holder.setText(R.id.tv_notarize, R.string.queding);
                holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v12 -> {
                    dialog.dismiss();
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .loginOut()
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(SettingActivity.this)))
                            .compose(RxSchedulers.normalTrans())
                            .subscribe(s -> {
                                RongIM.getInstance().logout();
                                Constant.clear();
                                ToastUtils.showShort(R.string.login_out);
                                Intent intent = new Intent(SettingActivity.this, NewLoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }, SettingActivity.this::handleApiError);
                });
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    public void paySetting(View view) {
        startActivity(new Intent(this, PaySettingActivity.class));
    }

}
