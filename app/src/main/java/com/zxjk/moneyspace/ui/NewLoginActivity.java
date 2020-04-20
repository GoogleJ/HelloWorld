package com.zxjk.moneyspace.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.CountryEntity;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

public class NewLoginActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvChangeLanguage;
    private LinearLayout llContrary;
    private TextView tvContrary;
    private EditText etPhone;

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

        initView();

        initData();
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

        ivBack.setOnClickListener(v -> finish());
    }

    @SuppressLint("CheckResult")
    public void code(View view) {
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
                    Intent intent = new Intent(this, GetCodeActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }, this::handleApiError);
    }

    public void email(View view) {
        startActivity(new Intent(this, NewLoginActivity1.class));
        finish();
    }

    private void initView() {
        ivBack = findViewById(R.id.ivBack);
        tvChangeLanguage = findViewById(R.id.tvChangeLanguage);
        llContrary = findViewById(R.id.llContrary);
        tvContrary = findViewById(R.id.tvContrary);
        etPhone = findViewById(R.id.etPhone);
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

}
