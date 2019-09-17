package com.zxjk.duoduo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.CountryEntity;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.service.RegisterBlockWalletService;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.PayPsdInputView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class NewLoginActivity extends BaseActivity {

    private MSGReceiver receiver;

    private ImageView ivBack;
    private TextView tvChangeLanguage;
    private TextView tvTips1;
    private TextView tvTips2;

    private PayPsdInputView ppivVerify;

    private LinearLayout llPhone;
    private LinearLayout llContrary;
    private TextView tvContrary;
    private EditText etPhone;

    private Button btnConfirm;

    /**
     * 0：输入手机号
     * 1：输入验证码
     */
    private int state = 0;

    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setFullScreen(this);
        setContentView(R.layout.activity_new_login);

        getPermisson(g -> {
            if (g) {
                receiver = new MSGReceiver();
                registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
            }
        }, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS);

        initView();

        initData();
    }

    private void changeState() {
        state = ((state == 0) ? 1 : 0);

        if (state == 0) {
            tvTips1.setText(R.string.welcome);
            tvTips2.setVisibility(View.INVISIBLE);
            ivBack.setVisibility(View.INVISIBLE);
            ppivVerify.setVisibility(View.GONE);
            llPhone.setVisibility(View.VISIBLE);
            tvChangeLanguage.setVisibility(View.VISIBLE);
            btnConfirm.setText(R.string.next);
            return;
        }

        tvTips1.setText(R.string.login_verify);
        tvTips2.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ppivVerify.setVisibility(View.VISIBLE);
        llPhone.setVisibility(View.GONE);
        tvChangeLanguage.setVisibility(View.INVISIBLE);
        btnConfirm.setText(R.string.login);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        tvChangeLanguage.setOnClickListener(v -> startActivity(new Intent(this, ChangeLanguageActivity.class)));

        llContrary.setOnClickListener(v -> startActivityForResult(new Intent(this, CountrySelectActivity.class), 200));

        ivBack.setOnClickListener(v -> changeState());

        btnConfirm.setOnClickListener(v -> {
            if (state == 0) {
                phone = etPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone) || !RegexUtils.isMobileExact(phone)) {
                    ToastUtils.showShort(R.string.edit_mobile_tip);
                    return;
                }

                ServiceFactory.getInstance().getBaseService(Api.class)
                        .getCode(tvContrary.getText().toString().substring(1) + "-" + phone, "0")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(s -> {
                            String head = phone.substring(0, 3);
                            String tail = phone.substring(phone.length() - 4);
                            tvTips2.setText("验证码已发送至" + tvContrary.getText().toString() + " " + head + "****" + tail);
                            changeState();
                        }, this::handleApiError);
                return;
            }

            if (TextUtils.isEmpty(ppivVerify.getPasswordString()) || ppivVerify.getPasswordString().length() != 6) {
                ToastUtils.showShort(R.string.please_enter_verification_code);
                return;
            }

            ServiceFactory.getInstance().getBaseService(Api.class)
                    .appUserRegisterAndLogin(phone, ppivVerify.getPasswordString())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(l -> {
                        startService(new Intent(this, RegisterBlockWalletService.class));

                        Constant.token = l.getToken();
                        Constant.userId = l.getId();
                        Constant.currentUser = l;
                        Constant.authentication = l.getIsAuthentication();

                        if (l.getIsFirstLogin().equals(Constant.FLAG_FIRSTLOGIN)) {
                            startActivity(new Intent(NewLoginActivity.this, EditPersonalInformationFragment.class));
                        } else {
                            connect(l.getRongToken());
                        }
                    }, this::handleApiError);
        });
    }

    private void initView() {
        ivBack = findViewById(R.id.ivBack);
        tvChangeLanguage = findViewById(R.id.tvChangeLanguage);
        tvTips1 = findViewById(R.id.tvTips1);
        tvTips2 = findViewById(R.id.tvTips2);

        ppivVerify = findViewById(R.id.ppivVerify);

        llPhone = findViewById(R.id.llPhone);
        llContrary = findViewById(R.id.llContrary);
        tvContrary = findViewById(R.id.tvContrary);
        etPhone = findViewById(R.id.etPhone);

        btnConfirm = findViewById(R.id.btnConfirm);
    }

    @SuppressLint("CheckResult")
    private void connect(String token) {
        Observable.timer(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(c -> CommonUtils.initDialog(this).show());

        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                CommonUtils.destoryDialog();
            }

            @Override
            public void onSuccess(String userid) {
                MMKVUtils.getInstance().enCode("isLogin", true);
                MMKVUtils.getInstance().enCode("date1", TimeUtils.getNowMills());
                MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                MMKVUtils.getInstance().enCode("token", Constant.currentUser.getToken());
                MMKVUtils.getInstance().enCode("userId", Constant.currentUser.getId());

                CommonUtils.destoryDialog();
                UserInfo userInfo = new UserInfo(userid, Constant.currentUser.getNick(), Uri.parse(Constant.currentUser.getHeadPortrait()));
                RongIM.getInstance().setCurrentUserInfo(userInfo);
                startActivity(new Intent(NewLoginActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                ToastUtils.showShort(R.string.connect_failed);
                CommonUtils.destoryDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (state == 1) {
            changeState();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            CountryEntity countryEntity = (CountryEntity) data.getSerializableExtra("country");
            tvContrary.setText("+" + (countryEntity != null ? countryEntity.countryCode : "86"));
            if (countryEntity != null) {
                Constant.HEAD_LOCATION = countryEntity.countryCode;
            }
        }
    }

    class MSGReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() == null) return;

            Object[] object = (Object[]) intent.getExtras().get("pdus");

            if (object == null) return;

            for (Object pdus : object) {
                byte[] pdusMsg = (byte[]) pdus;
                SmsMessage sms = SmsMessage.createFromPdu(pdusMsg);
                String content = sms.getMessageBody();//短信内容
                if (content.contains("MoChat")) ppivVerify.setText(parseSms(content));
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) unregisterReceiver(receiver);

        super.onDestroy();
    }

    private String parseSms(String msg) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(msg);
        return m.replaceAll("").trim().substring(0, 6);
    }
}
