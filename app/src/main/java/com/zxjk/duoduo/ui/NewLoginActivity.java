package com.zxjk.duoduo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionListenerAdapter;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.CountryEntity;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.PayPsdInputView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.LanguageUtil;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

import static androidx.transition.TransitionSet.ORDERING_SEQUENTIAL;

public class NewLoginActivity extends BaseActivity {

    private MSGReceiver receiver;

    private ImageView ivIcon;
    private LinearLayout llRoot;
    private ImageView ivBack;
    private TextView tvChangeLanguage;
    private ViewFlipper vf;
    private TextView tvTips;
    private PayPsdInputView ppivVerify;
    private LinearLayout llPhone;
    private LinearLayout llContrary;
    private TextView tvContrary;
    private EditText etPhone;
    private Button btnConfirm;
    private TextView mNewLoginText;

    private TransitionSet anim;
    private boolean isAniming;

    /**
     * 0：输入手机号
     * 1：输入验证码
     */
    private int state = 0;

    private String phone;

    /**
     * 0：国外
     * 1：国内
     */
    private String isChinaPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        BarUtils.setStatusBarVisibility(this, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        setContentView(R.layout.activity_new_login);

        getPermisson(g -> {
            if (g) {
                receiver = new MSGReceiver();
                registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
            }
        }, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS);

        initAnim();

        initView();

        initData();
    }

    private void changeState() {
        state = ((state == 0) ? 1 : 0);

        TransitionManager.beginDelayedTransition(llRoot, anim);

        if (state == 0) {
            vf.showNext();
            tvTips.setVisibility(View.GONE);
            ivBack.setVisibility(View.INVISIBLE);
            mNewLoginText.setVisibility(View.VISIBLE);
            tvChangeLanguage.setVisibility(View.VISIBLE);
            ppivVerify.setVisibility(View.GONE);
            llPhone.setVisibility(View.VISIBLE);
            btnConfirm.setText(R.string.next);
            ppivVerify.cleanPsd();
            return;
        }

        vf.showPrevious();
        tvTips.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvChangeLanguage.setVisibility(View.INVISIBLE);
        mNewLoginText.setVisibility(View.GONE);
        ppivVerify.setVisibility(View.VISIBLE);
        llPhone.setVisibility(View.GONE);
        btnConfirm.setText(R.string.login);
    }

    private void initAnim() {
        Fade fadeIn = new Fade(Fade.IN);
        fadeIn.excludeTarget(R.id.llPhone, true);
        fadeIn.excludeTarget(R.id.ppivVerify, true);
        Fade fadeOut = new Fade(Fade.OUT);
        fadeOut.excludeTarget(R.id.llPhone, true);
        fadeOut.excludeTarget(R.id.ppivVerify, true);

        Slide slideIn = new Slide(Gravity.END);
        slideIn.excludeTarget(R.id.llPhone, true);
        slideIn.excludeTarget(R.id.tvTips, true);
        slideIn.excludeTarget(R.id.ivBack, true);
        slideIn.excludeTarget(R.id.tvChangeLanguage, true);
        slideIn.excludeTarget(R.id.tv_new_login_text, true);

        Slide slideOut = new Slide(Gravity.START);
        slideOut.excludeTarget(R.id.ppivVerify, true);
        slideOut.excludeTarget(R.id.tvTips, true);
        slideOut.excludeTarget(R.id.ivBack, true);
        slideOut.excludeTarget(R.id.tvChangeLanguage, true);
        slideOut.excludeTarget(R.id.tv_new_login_text, true);

        TransitionSet set = new TransitionSet();
        set.addTransition(fadeIn);
        set.addTransition(fadeOut);
        set.addTransition(slideIn);
        set.addTransition(slideOut);

        anim = new TransitionSet();
        anim.setDuration(600);
        anim.setOrdering(ORDERING_SEQUENTIAL);
        anim.setInterpolator(new OvershootInterpolator());
        anim.addTransition(new ChangeBounds());
        anim.addTransition(set);

        anim.excludeTarget(R.id.vf, true);
        anim.addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(@NonNull Transition transition) {
                isAniming = true;
            }

            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                isAniming = false;
                KeyboardUtils.hideSoftInput(NewLoginActivity.this);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void initData() {
        tvChangeLanguage.setOnClickListener(v -> {
            if ("english".equals(LanguageUtil.getInstance(this).getCurrentLanguage())) {
                LanguageUtil.getInstance(this).changeLanguage(LanguageUtil.CHINESE);
            } else {
                LanguageUtil.getInstance(this).changeLanguage(LanguageUtil.ENGLISH);
            }
            finish();
            startActivity(new Intent(this, NewLoginActivity.class));
        });

        llContrary.setOnClickListener(v -> startActivityForResult(new Intent(this, CountrySelectActivity.class), 200));

        ivBack.setOnClickListener(v -> {
            if (!isAniming) {
                changeState();
            }
        });

        btnConfirm.setOnClickListener(v -> {

            if (isAniming) return;

            if (state == 0) {
                getCode();
            } else {
                doLogin();
            }

        });

        ppivVerify.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {
            }

            @Override
            public void onEqual(String psd) {
            }

            @Override
            public void inputFinished(String inputPsd) {
                doLogin();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void getCode() {
        String phoneText = etPhone.getText().toString().trim();
        if ("86".equals(tvContrary.getText().toString().substring(1))) {
            isChinaPhone = "1";
            phone = phoneText;
        } else {
            isChinaPhone = "0";
            phone = tvContrary.getText().toString().substring(1) + phoneText;
        }
        if (TextUtils.isEmpty(phone) || ("1".equals(isChinaPhone) && !RegexUtils.isMobileExact(phone))) {
            ToastUtils.showShort(R.string.edit_mobile_tip);
            return;
        }
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCode(phone, isChinaPhone)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(o -> {
                    String head = phone.substring(0, 3);
                    String tail = phone.substring(phone.length() - 4);
                    tvTips.setText("验证码已发送至" + tvContrary.getText().toString() + " " + head + "****" + tail);
                    changeState();
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    private void doLogin() {
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
                    MMKVUtils.getInstance().decodeString("SocialListOrder");
                    Constant.token = l.getToken();
                    Constant.userId = l.getId();
                    Constant.currentUser = l;
                    Constant.authentication = l.getIsAuthentication();

                    connect(l.getRongToken(), l.getIsFirstLogin().equals(Constant.FLAG_FIRSTLOGIN));
                }, this::handleApiError);
    }

    private void initView() {
        ivIcon = findViewById(R.id.ivIcon);
        llRoot = findViewById(R.id.llRoot);
        ivBack = findViewById(R.id.ivBack);
        tvChangeLanguage = findViewById(R.id.tvChangeLanguage);
        tvTips = findViewById(R.id.tvTips);
        vf = findViewById(R.id.vf);
        mNewLoginText = findViewById(R.id.tv_new_login_text);
        ppivVerify = findViewById(R.id.ppivVerify);

        llPhone = findViewById(R.id.llPhone);
        llContrary = findViewById(R.id.llContrary);
        tvContrary = findViewById(R.id.tvContrary);
        etPhone = findViewById(R.id.etPhone);

        btnConfirm = findViewById(R.id.btnConfirm);
    }

    @SuppressLint("CheckResult")
    private void connect(String token, boolean equals) {
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
                CommonUtils.destoryDialog();

                MMKVUtils.getInstance().enCode("date1", TimeUtils.getNowMills());
                MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                MMKVUtils.getInstance().enCode("token", Constant.currentUser.getToken());
                MMKVUtils.getInstance().enCode("userId", Constant.currentUser.getId());
                UserInfo userInfo = new UserInfo(userid, Constant.currentUser.getNick(), Uri.parse(Constant.currentUser.getHeadPortrait()));
                RongIM.getInstance().setCurrentUserInfo(userInfo);

                if (equals) {
                    if (!MMKVUtils.getInstance().decodeBool("appFirstLogin")) {
                        MMKVUtils.getInstance().enCode("appFirstLogin", true);
                        Intent intent = new Intent(NewLoginActivity.this, AppFirstLogin.class);
                        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(NewLoginActivity.this
                                , ivIcon, "appicon");
                        intent.putExtra("setupPayPass", true);
                        startActivity(intent, activityOptionsCompat.toBundle());
                    } else {
                        Intent intent = new Intent(NewLoginActivity.this, SetUpPaymentPwdActivity.class);
                        intent.putExtra("firstLogin", true);
                        startActivity(intent);
                        finish();
                    }
                    return;
                }

                MMKVUtils.getInstance().enCode("isLogin", true);

                if (!MMKVUtils.getInstance().decodeBool("appFirstLogin")) {
                    MMKVUtils.getInstance().enCode("appFirstLogin", true);
                    Intent intent = new Intent(NewLoginActivity.this, AppFirstLogin.class);
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(NewLoginActivity.this
                            , ivIcon, "appicon");
                    startActivity(intent, activityOptionsCompat.toBundle());
                } else {
                    startActivity(new Intent(NewLoginActivity.this, HomeActivity.class));
                    finish();
                }

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
                if (content.contains("Hilamg")) ppivVerify.setText(parseSms(content));
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
