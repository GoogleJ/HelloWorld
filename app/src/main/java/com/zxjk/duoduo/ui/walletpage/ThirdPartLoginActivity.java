package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
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

import androidx.core.app.ActivityOptionsCompat;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.AppFirstLogin;
import com.zxjk.duoduo.ui.CountrySelectActivity;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.SetUpPaymentPwdActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class ThirdPartLoginActivity extends BaseActivity {

    public static final String ACTION_THIRDPARTLOGINACCESS = "ThirdPartLoginAccess";
    public static final String ACTION_LOGINAUTHORIZATIONSWICH = "SwichLoginAccess";

    private String appId = "";
    private String randomStr = "";
    private String sign = "";
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
    private Disposable mdDisposable;
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
        if(Constant.currentUser.getId() == null){
            mImgBack.setVisibility(View.INVISIBLE);
        }

        ViewTreeObserver vto = mLlThirdPartContrary.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mLlThirdPartContrary.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mLlThirdPartContrary.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int width = mLlThirdPartContrary.getWidth();

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTvThirdCodeText.getLayoutParams();
                params.width = width;
                mTvThirdCodeText.setLayoutParams(params);
            }
        });
    }


    private void initData() {
        action = getIntent().getStringExtra("action");
        appId = getIntent().getStringExtra("appId");
        randomStr = getIntent().getStringExtra("randomStr");
        sign = getIntent().getStringExtra("sign");

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
                        mBtnToLogin.setBackground(getResources().getDrawable(R.drawable.shape_theme6));
                        mBtnToLogin.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else if (start == 0) {
                        mBtnToLogin.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
                        mBtnToLogin.setTextColor(getResources().getColor(R.color.business_card_duoduo_id));
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
                        mBtnToLogin.setBackground(getResources().getDrawable(R.drawable.shape_theme6));
                        mBtnToLogin.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else if (start == 0) {
                        mBtnToLogin.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
                        mBtnToLogin.setTextColor(getResources().getColor(R.color.business_card_duoduo_id));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mImgBack.setOnClickListener(v -> {
                finish();

        });
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
        if (TextUtils.isEmpty(mEtThirdPartPhone.getText())) {
            ToastUtils.showShort("请输入手机号");
            return;
        } else {
            if ("1".equals(isChinaPhone) && !RegexUtils.isMobileExact(phone)) {
                ToastUtils.showShort(R.string.edit_mobile_tip);
                return;
            } else {
                if (TextUtils.isEmpty(mEtVerificationCode.getText()) || mEtVerificationCode.getText().length() != 6) {
                    ToastUtils.showShort(R.string.please_enter_verification_code);
                    return;
                }
            }
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .appUserRegisterAndLogin(phone, mEtVerificationCode.getText().toString())
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
                    ToastUtils.showShort("验证码已发送至" + mTvThirdPartContrary.getText().toString() + " " + head + "****" + tail);


                    //点击后置为不可点击状态
                    mBtnThirdVerificationCode.setEnabled(false);
                    //从0开始发射11个数字为：0-10依次输出，延时0s执行，每1s发射一次。
                    mdDisposable = Flowable.intervalRange(0, 61, 0, 1, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext(l -> {

                                mBtnThirdVerificationCode.setTextColor(getResources().getColor(R.color.white));
                                mBtnThirdVerificationCode.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                                mBtnThirdVerificationCode.setBackground(getResources().getDrawable(R.drawable.shape_theme4));
                                mBtnThirdVerificationCode.setText("重新获取(" + (60 - l) + ")");
                            }).doOnComplete(() -> {
                                mBtnThirdVerificationCode.setEnabled(true);

                                mBtnThirdVerificationCode.setTextColor(getResources().getColor(R.color.business_card_duoduo_id));
                                mBtnThirdVerificationCode.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                                mBtnThirdVerificationCode.setBackground(getResources().getDrawable(R.drawable.shape_theme5));
                                mBtnThirdVerificationCode.setText("获取验证码");
                            })
                            .subscribe();

                }, this::handleApiError);
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
                        Intent intent = new Intent(ThirdPartLoginActivity.this, AppFirstLogin.class);
                        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ThirdPartLoginActivity.this
                                , mImgThirdLogin, "appicon");
                        intent.putExtra("setupPayPass", true);
                        startActivity(intent, activityOptionsCompat.toBundle());
                    } else {
                        Intent intent = new Intent(ThirdPartLoginActivity.this, SetUpPaymentPwdActivity.class);
                        intent.putExtra("firstLogin", true);
                        startActivity(intent);
                        finish();
                    }
                    return;
                }

                MMKVUtils.getInstance().enCode("isLogin", true);

                if (!MMKVUtils.getInstance().decodeBool("appFirstLogin")) {
                    MMKVUtils.getInstance().enCode("appFirstLogin", true);
                    Intent intent = new Intent(ThirdPartLoginActivity.this, AppFirstLogin.class);
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ThirdPartLoginActivity.this
                            , mImgThirdLogin, "appicon");
                    startActivity(intent, activityOptionsCompat.toBundle());
                } else {
                    switch (action) {
                        case ACTION_THIRDPARTLOGINACCESS:
                            Intent intent = new Intent(ThirdPartLoginActivity.this, LoginAuthorizationActivity.class);
                            intent.putExtra("action", ACTION_THIRDPARTLOGINACCESS);
                            intent.putExtra("appId", appId);
                            intent.putExtra("randomStr", randomStr);
                            intent.putExtra("sign", sign);
                            startActivity(intent);
                            finish();
                            break;
                        case ACTION_LOGINAUTHORIZATIONSWICH:
                            startActivity(new Intent(ThirdPartLoginActivity.this, HomeActivity.class));
                            finish();
                            break;
                    }
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
    protected void onDestroy() {
        super.onDestroy();
        if (mdDisposable != null) {
            mdDisposable.dispose();
        }
    }
}