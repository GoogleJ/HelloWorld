package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.security.rp.RPSDK;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxException;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


@SuppressLint("CheckResult")
public class SettingActivity extends BaseActivity {

    private TextView tv_perfection;
    private ImageView iv_authentication;
    private TextView tv_authentication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        isAuthentication();
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
        tv_title.setText("设置");
        RelativeLayout rl_back = findViewById(R.id.rl_back);

        tv_authentication = findViewById(R.id.tv_authentication);
        iv_authentication = findViewById(R.id.iv_authentication);
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

        //新消息通知
        findViewById(R.id.rl_newMessage).setOnClickListener(v -> {
            Intent mItent = new Intent();
            mItent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            mItent.setData(Uri.fromParts("package", getPackageName(), null));
            mItent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mItent);
        });

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
                startActivity(new Intent(SettingActivity.this, BillingMessageActivity.class));
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
                                tv_authentication.setText("已认证");

                                iv_authentication.setVisibility(View.VISIBLE);
                                break;
                            case "2":
                                tv_authentication.setText("认证审核中");

                                iv_authentication.setVisibility(View.GONE);
                                break;
                            case "1":
                                tv_authentication.setText("认证未通过");

                                iv_authentication.setVisibility(View.GONE);
                                break;
                            default:
                                tv_authentication.setText("未认证");

                                iv_authentication.setVisibility(View.GONE);
                                break;
                        }
                    }, this::handleApiError);
        }
    }
}
