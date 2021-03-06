package com.zxjk.duoduo.ui.externalfunc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.CountryEntity;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.CountrySelectActivity;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.SetUpPaymentPwdActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class ThirdPartLoginActivity extends BaseActivity {
    public static final String ACTION_THIRDPARTLOGINACCESS = "ThirdPartLoginAccess";
    public static final String ACTION_AUTHORIZEDLOGIN = "authorizedlogin";
    public static final String ACTION_LOGINAUTHORIZATIONSWICH = "SwichLoginAccess";
    public static final String ACTION_PAY = "Pay";

    private String action = "";
    private LinearLayout mLlThirdPartContrary;
    private TextView mTvThirdPartContrary;
    private Button mBtnThirdVerificationCode;
    private EditText mEtThirdPartPhone;
    private EditText mEtVerificationCode;
    private ImageView mImgThirdLogin;
    private TextView mTvThirdCodeText;
    private Button mBtnToLogin;
    private ImageView mImgBack;

    private String phone;
    /**
     * 0：国外
     * 1：国内
     */
    private String isChinaPhone;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdpart_login);

        initView();

        initData();
    }

    private void initView() {
        mLlThirdPartContrary = findViewById(R.id.ll_third_part_contrary);
        mTvThirdPartContrary = findViewById(R.id.tv_third_part_contrary);
        mBtnThirdVerificationCode = findViewById(R.id.btn_third_verification_code);
        mEtThirdPartPhone = findViewById(R.id.et_third_part_Phone);
        mEtVerificationCode = findViewById(R.id.et_third_verification_code);
        mImgThirdLogin = findViewById(R.id.img_third_login);
        mTvThirdCodeText = findViewById(R.id.tv_third_code_text);
        mBtnToLogin = findViewById(R.id.btn_to_login);
        mImgBack = findViewById(R.id.img_back);

        ViewTreeObserver vto = mLlThirdPartContrary.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mLlThirdPartContrary.getWidth();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTvThirdCodeText.getLayoutParams();
                params.width = width;
                mTvThirdCodeText.setLayoutParams(params);

                mLlThirdPartContrary.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initData() {
        action = getIntent().getStringExtra("action");

        if (TextUtils.isEmpty(action)) {
            ToastUtils.showShort(R.string.wrong_param_data);
            finish();
        }

        mLlThirdPartContrary.setOnClickListener(v ->
                startActivityForResult(new Intent(this, CountrySelectActivity.class),
                        200));

        mEtVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mEtThirdPartPhone.getText().toString().equals("")) {
                    if (count > 0) {
                        mBtnToLogin.setBackground(ContextCompat.getDrawable(ThirdPartLoginActivity.this, R.drawable.shape_theme6));
                        mBtnToLogin.setTextColor(ContextCompat.getColor(ThirdPartLoginActivity.this, R.color.colorPrimary));
                    } else if (start == 0) {
                        mBtnToLogin.setBackground(ContextCompat.getDrawable(ThirdPartLoginActivity.this, R.drawable.setbar_bg));
                        mBtnToLogin.setTextColor(ContextCompat.getColor(ThirdPartLoginActivity.this, R.color.business_card_duoduo_id));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtThirdPartPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mEtVerificationCode.getText().toString().equals("")) {
                    if (count > 0) {
                        mBtnToLogin.setBackground(ContextCompat.getDrawable(ThirdPartLoginActivity.this, R.drawable.shape_theme6));
                        mBtnToLogin.setTextColor(ContextCompat.getColor(ThirdPartLoginActivity.this, R.color.colorPrimary));
                    } else if (start == 0) {
                        mBtnToLogin.setBackground(ContextCompat.getDrawable(ThirdPartLoginActivity.this, R.drawable.setbar_bg));
                        mBtnToLogin.setTextColor(ContextCompat.getColor(ThirdPartLoginActivity.this, R.color.business_card_duoduo_id));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mImgBack.setOnClickListener(v -> finish());
    }

    @SuppressLint("CheckResult")
    public void getThirdVerificationCode(View view) {
        String phoneText = mEtThirdPartPhone.getText().toString().trim();
        if ("86".equals(mTvThirdPartContrary.getText().toString().substring(1))) {
            isChinaPhone = "1";
            phone = phoneText;
        } else {
            isChinaPhone = "0";
            phone = mTvThirdPartContrary.getText().toString().substring(1) + phoneText;
        }

        if (TextUtils.isEmpty(phone) || "1".equals(isChinaPhone) && !RegexUtils.isMobileExact(phone)) {
            ToastUtils.showShort(R.string.edit_mobile_tip);
            return;
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCode(phone, isChinaPhone)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                .doOnNext(s -> {
                    mBtnThirdVerificationCode.setEnabled(false);
                    String head = phone.substring(0, 3);
                    String tail = phone.substring(phone.length() - 4);
                    ToastUtils.showShort(getString(R.string.verCodeSendTo) + mTvThirdPartContrary.getText().toString() + " " + head + "****" + tail);
                })
                .flatMap((Function<String, ObservableSource<Long>>) s ->
                        Observable.intervalRange(0, 61, 0, 1, TimeUnit.SECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    mBtnThirdVerificationCode.setEnabled(true);
                    mBtnThirdVerificationCode.setTextColor(ContextCompat.getColor(this, R.color.business_card_duoduo_id));
                    mBtnThirdVerificationCode.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                    mBtnThirdVerificationCode.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_theme5));
                    mBtnThirdVerificationCode.setText(getString(R.string.getVerGode));
                })
                .subscribe(l -> {
                    if (l == 0) {
                        mBtnThirdVerificationCode.setTextColor(ContextCompat.getColor(this, R.color.white));
                        mBtnThirdVerificationCode.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                        mBtnThirdVerificationCode.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_theme4));
                    }

                    mBtnThirdVerificationCode.setText(getString(R.string.reGet) + "(" + (60 - l) + ")");
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    public void login(View view) {
        String phoneText = mEtThirdPartPhone.getText().toString().trim();
        if ("86".equals(mTvThirdPartContrary.getText().toString().substring(1))) {
            isChinaPhone = "1";
            phone = phoneText;
        } else {
            isChinaPhone = "0";
            phone = mTvThirdPartContrary.getText().toString().substring(1) + phoneText;
        }

        if (TextUtils.isEmpty(phone) || "1".equals(isChinaPhone) && !RegexUtils.isMobileExact(phone)) {
            ToastUtils.showShort(R.string.edit_mobile_tip);
            return;
        }

        String code = mEtVerificationCode.getText().toString().trim();

        if (TextUtils.isEmpty(code) || code.length() != 6) {
            ToastUtils.showShort(R.string.please_enter_verification_code);
            return;
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .appUserRegisterAndLogin(phone, mEtVerificationCode.getText().toString(), "", "")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(l -> {
                    Constant.token = l.getToken();
                    Constant.userId = l.getId();
                    Constant.currentUser = l;
                    Constant.authentication = l.getIsAuthentication();
                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                    MMKVUtils.getInstance().enCode("token", Constant.currentUser.getToken());
                    MMKVUtils.getInstance().enCode("userId", Constant.currentUser.getId());

                    if ("0".equals(Constant.currentUser.getIsFirstLogin())) {
                        Intent intent = new Intent(ThirdPartLoginActivity.this, SetUpPaymentPwdActivity.class);
                        intent.putExtra("firstLogin", true);
                        intent.putExtra("action", action);
                        switch (action) {
                            case ACTION_THIRDPARTLOGINACCESS:
                                if (TextUtils.isEmpty(getIntent().getStringExtra("appId"))) {
                                    ToastUtils.showShort(R.string.wrong_param_data);
                                    finish();
                                    return;
                                }
                                intent.putExtra("action", ACTION_THIRDPARTLOGINACCESS);
                                intent.putExtra("appId", getIntent().getStringExtra("appId"));
                                break;
                            case ACTION_PAY:
                                ToastUtils.showShort(R.string.havent_register_goto_hilamg);
                                finish();
                                return;
                        }
                        startActivity(intent);
                    } else {
                        switch (action) {
                            case ACTION_THIRDPARTLOGINACCESS:
                                if (TextUtils.isEmpty(getIntent().getStringExtra("appId"))) {
                                    ToastUtils.showShort(R.string.wrong_param_data);
                                    finish();
                                    return;
                                }
                                Intent intent = new Intent(ThirdPartLoginActivity.this, LoginAuthorizationActivity.class);
                                intent.putExtra("action", ACTION_THIRDPARTLOGINACCESS);
                                intent.putExtra("appId", getIntent().getStringExtra("appId"));
                                startActivity(intent);
                                break;
                            case ACTION_LOGINAUTHORIZATIONSWICH:
                                startActivity(new Intent(ThirdPartLoginActivity.this, HomeActivity.class));
                                break;
                            case ACTION_PAY:
                                intent = new Intent(ThirdPartLoginActivity.this, PayConfirmActivity.class);
                                intent.putExtra("orderId", getIntent().getStringExtra("orderId"));
                                startActivity(intent);
                                break;
                        }
                    }

                    finish();
                }, this::handleApiError);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            CountryEntity countryEntity = (CountryEntity) data.getSerializableExtra("country");
            mTvThirdPartContrary.setText(getString(R.string.country_phone, (countryEntity != null ? countryEntity.countryCode : "86")));
        }
    }

}