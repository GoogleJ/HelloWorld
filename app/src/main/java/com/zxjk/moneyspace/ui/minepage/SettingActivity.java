package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.security.rp.RPSDK;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxException;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.NewLoginActivity;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MMKVUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;


@SuppressLint("CheckResult")
public class SettingActivity extends BaseActivity {

    private TextView tv_perfection;
    private ImageView iv_authentication;
    private TextView tv_authentication;
    private Switch swGlobalMute;
    private Switch swGlobalVibrate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        isAuthentication();

        setupRemind();

        if (MMKVUtils.getInstance().decodeBool("bottom_vibrate")) {
            swGlobalVibrate.setChecked(true);
        } else {
            swGlobalVibrate.setChecked(false);
        }
        swGlobalVibrate.setOnClickListener(v -> {
            swGlobalVibrate.setEnabled(false);
            if (swGlobalVibrate.isChecked()) {
                MMKVUtils.getInstance().enCode("bottom_vibrate", true);
            } else {
                MMKVUtils.getInstance().enCode("bottom_vibrate", false);
            }
            swGlobalVibrate.setEnabled(true);
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        tv_authentication.setText(CommonUtils.getAuthenticate(Constant.currentUser.getIsAuthentication()));
        iv_authentication.setVisibility(Constant.currentUser.getIsAuthentication().equals("0") ? View.VISIBLE : View.GONE);

        if (SPUtils.getInstance().getBoolean(Constant.currentUser.getId(), false)) {
            tv_perfection.setText(getString(R.string.complete_payinfo));
        }
    }

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.setting);
        RelativeLayout rl_back = findViewById(R.id.rl_back);

        tv_authentication = findViewById(R.id.tv_authentication);
        iv_authentication = findViewById(R.id.iv_authentication);
        swGlobalMute = findViewById(R.id.swGlobalMute);
        swGlobalVibrate = findViewById(R.id.swGlobalVibrate);
        boolean hasCompletePay = SPUtils.getInstance().getBoolean(Constant.currentUser.getId(), false);
        tv_perfection = findViewById(R.id.tv_perfection);
        tv_perfection.setText(hasCompletePay ? R.string.complete_payinfo : R.string.uncomplete_payinfo);

        //返回
        rl_back.setOnClickListener(v -> finish());

        //账号
        findViewById(R.id.rl_account_number).setOnClickListener(v ->
                startActivity(new Intent(SettingActivity.this, AccountActivity.class)));

        //隐私
        findViewById(R.id.rl_privicy).setOnClickListener(v ->
                startActivity(new Intent(SettingActivity.this, PrivicyActivity.class)));

        //实名认证
        findViewById(R.id.rl_realNameAuthentication).setOnClickListener(v -> {
            if (Constant.currentUser.getIsAuthentication().equals("2")) {
                ToastUtils.showShort(R.string.verifying_pleasewait);
            } else if (Constant.currentUser.getIsAuthentication().equals("0")) {
                ToastUtils.showShort(R.string.authen_true);
            } else {
                Api api = ServiceFactory.getInstance().getBaseService(Api.class);
                api.getAuthToken()
                        .compose(bindUntilEvent(ActivityEvent.DESTROY))
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .flatMap(s -> Observable.create(emitter ->
                                RPSDK.start(s, SettingActivity.this, (audit, s1) -> {
                                    if (audit == RPSDK.AUDIT.AUDIT_PASS || audit == RPSDK.AUDIT.AUDIT_FAIL) {
                                        emitter.onNext(true);
                                    } else {
                                        emitter.onError(new RxException.ParamsException("认证失败,请稍后尝试", 100));
                                    }
                                })))
                        .observeOn(Schedulers.io())
                        .flatMap(b -> api.initAuthData())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s -> {
                            Constant.currentUser.setIsAuthentication("0");
                            tv_authentication.setText(CommonUtils.getAuthenticate(Constant.currentUser.getIsAuthentication()));
                        }, this::handleApiError);
            }
        });
        //收款信息
        findViewById(R.id.rl_collectionInformation).setOnClickListener(v -> {
            String isAuthentication = Constant.currentUser.getIsAuthentication();
            if ("0".equals(isAuthentication)) {
                startActivity(new Intent(SettingActivity.this, com.zxjk.moneyspace.ui.minepage.BillingMessageActivity.class));
            } else if ("2".equals(isAuthentication)) {
                ToastUtils.showShort(R.string.waitAuthentication);
            } else {
                ToastUtils.showShort(R.string.notAuthentication);
            }
        });
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
                                MMKVUtils.getInstance().enCode("isLogin", false);
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

    private void isAuthentication() {
        if (!Constant.currentUser.getIsAuthentication().equals("0")) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getCustomerAuth()
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        Constant.currentUser.setIsAuthentication(s);
                        MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                        switch (s) {
                            case "0":
                                tv_authentication.setText(R.string.authen_true);

                                iv_authentication.setVisibility(View.VISIBLE);
                                break;
                            case "2":
                                tv_authentication.setText(R.string.authening);

                                iv_authentication.setVisibility(View.GONE);
                                break;
                            case "1":
                                tv_authentication.setText(R.string.authenfail);

                                iv_authentication.setVisibility(View.GONE);
                                break;
                            default:
                                tv_authentication.setText(R.string.authen_false);

                                iv_authentication.setVisibility(View.GONE);
                                break;
                        }
                    }, this::handleApiError);
        }
    }
}
