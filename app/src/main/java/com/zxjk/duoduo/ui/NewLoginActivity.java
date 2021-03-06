package com.zxjk.duoduo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.CountryEntity;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.PayPsdInputView;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //剪切板处理过后的result
    private String resultUri;
    //剪切板中读取到的邀请人id
    private String inviteId = "";
    //剪切板中读取到的群id
    private String groupId = "";

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

        if (getIntent().getBooleanExtra("attachAD", false)) {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url", Constant.URL_628ACTIVITY);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
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
//            if ("english".equals(LanguageUtil.getInstance(this).getCurrentLanguage())) {
//                LanguageUtil.getInstance(this).changeLanguage(LanguageUtil.CHINESE);
//            } else {
//                LanguageUtil.getInstance(this).changeLanguage(LanguageUtil.ENGLISH);
//            }
//            finish();
//            startActivity(new Intent(this, NewLoginActivity.class));
            ToastUtils.showShort(R.string.developing);
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
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                .subscribe(o -> {
                    String head = phoneText.substring(0, 3);
                    String tail = phoneText.substring(phoneText.length() - 4);
                    tvTips.setText(getString(R.string.send_sms_to, tvContrary.getText().toString() + " " + head + "****" + tail));
                    changeState();
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    private void doLogin() {
        if (TextUtils.isEmpty(ppivVerify.getPasswordString()) || ppivVerify.getPasswordString().length() != 6) {
            ToastUtils.showShort(R.string.please_enter_verification_code);
            return;
        }

        parseClipbord();

        ServiceFactory.getInstance().getBaseService(Api.class)
                .appUserRegisterAndLogin(phone, ppivVerify.getPasswordString(), inviteId, groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(l -> {
                    Constant.token = l.getToken();
                    Constant.userId = l.getId();
                    Constant.currentUser = l;
                    Constant.authentication = l.getIsAuthentication();

                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                    MMKVUtils.getInstance().enCode("token", Constant.currentUser.getToken());
                    MMKVUtils.getInstance().enCode("userId", Constant.currentUser.getId());

                    if (!MMKVUtils.getInstance().decodeBool("appFirstLogin")) {
                        //first open app,enter AppFirstLoginActivity
                        MMKVUtils.getInstance().enCode("appFirstLogin", true);
                        Intent intent = new Intent(NewLoginActivity.this, AppFirstLogin.class);
                        ActivityOptionsCompat aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(NewLoginActivity.this,
                                ivIcon, "appicon");
                        intent.putExtra("resultUri", resultUri);
                        intent.putExtra("setupPayPass", "0".equals(Constant.currentUser.getIsFirstLogin()));
                        startActivity(intent, aoc.toBundle());
                    } else {
                        Intent intent;
                        if ("0".equals(Constant.currentUser.getIsFirstLogin())) {
                            //user first register app
                            intent = new Intent(NewLoginActivity.this, SetUpPaymentPwdActivity.class);
                            intent.putExtra("firstLogin", true);
                        } else {
                            //old user login
                            intent = new Intent(NewLoginActivity.this, HomeActivity.class);
                        }
                        intent.putExtra("resultUri", resultUri);
                        startActivity(intent);
                        finish();
                    }
                }, this::handleApiError);
    }

    private void parseClipbord() {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager != null) {
                ClipData clipData = clipboardManager.getPrimaryClip();
                if (null != clipData && clipData.getItemCount() > 0) {
                    ClipData.Item item = clipData.getItemAt(0);
                    if (null != item && !TextUtils.isEmpty(item.getText())) {
                        String copyContent = item.getText().toString();
                        if (copyContent.contains(Constant.APP_SHARE_URL)) {
                            resultUri = copyContent.substring(0, copyContent.indexOf("?") + 1);

                            if (resultUri.equals(Constant.APP_SHARE_URL)) {
                                String userIdJiequ = copyContent.substring(copyContent.indexOf("?") + 1);
                                String result = userIdJiequ;
                                if (userIdJiequ.contains("&")) {
                                    String[] split = userIdJiequ.split("&");
                                    result = split[0];
                                }

                                userIdJiequ = result;

                                resultUri += AesUtil.getInstance().decrypt(userIdJiequ);

                                Uri uri = Uri.parse(resultUri);
                                inviteId = uri.getQueryParameter("id");
                                groupId = uri.getQueryParameter("groupId");
                                String type = uri.getQueryParameter("type");

                                if (TextUtils.isEmpty(type)) {
                                    resultUri = "hilamg://web/?action=addFriend&id=" + inviteId;
                                } else if (type.equals("1")) {
                                    resultUri = "hilamg://web/?action=joinCommunity&id=" + inviteId + "&groupId=" + groupId;
                                } else if (type.equals("0")) {
                                    resultUri = "hilamg://web/?action=joinGroup&id=" + inviteId + "&groupId=" + groupId;
                                }
                            }
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ""));
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
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
            tvContrary.setText(getString(R.string.country_phone, (countryEntity != null ? countryEntity.countryCode : "86")));
            if (countryEntity != null) {
                Constant.HEAD_LOCATION = countryEntity.countryCode;
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

    class MSGReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() == null) return;

            try {
                Object[] object = (Object[]) intent.getExtras().get("pdus");

                if (object == null) return;

                for (Object pdus : object) {
                    byte[] pdusMsg = (byte[]) pdus;
                    SmsMessage sms = SmsMessage.createFromPdu(pdusMsg);
                    String content = sms.getMessageBody();
                    if (content.contains("Hilamg")) {
                        ppivVerify.setText(parseSms(content));
                        return;
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
