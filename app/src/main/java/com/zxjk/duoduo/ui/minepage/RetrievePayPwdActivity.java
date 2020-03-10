package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

public class RetrievePayPwdActivity extends BaseActivity implements View.OnClickListener {
    private ImageView idCardImage;
    private ImageView phoneCodeImage;
    private LinearLayout idCardLayout;
    private ConstraintLayout phoneCodeLayout;
    private EditText idCardEdit;
    private TextView phone;
    private TextView textGetCode;
    private EditText verifiedCodeEdit;
    private TextView commitBtn;
    private boolean isTrue = true;

    //短信验证
    private String messagfeCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_payment_pwd);
        initView();
    }

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.find_pay_password));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        idCardImage = findViewById(R.id.id_card_image);
        phoneCodeImage = findViewById(R.id.phone_code_image);
        idCardLayout = findViewById(R.id.id_card_layout);
        phoneCodeLayout = findViewById(R.id.phone_code_layout);
        idCardEdit = findViewById(R.id.id_card_edit);
        phone = findViewById(R.id.phone);
        textGetCode = findViewById(R.id.text_get_code);
        verifiedCodeEdit = findViewById(R.id.verified_code_edit);
        commitBtn = findViewById(R.id.commit_btn);
        commitBtn.setOnClickListener(this);
        textGetCode.setOnClickListener(this);
        phone.setText(Constant.currentUser.getMobile());

    }

    @SuppressLint("CheckResult")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_btn:
                if (isTrue) {
                    if (TextUtils.isEmpty(idCardEdit.getText().toString().trim())) {
                        ToastUtils.showShort(getString(R.string.please_input_id_card));
                        return;
                    }

                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .verifyPaperworkNumber(idCardEdit.getText().toString())
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.normalTrans())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                            .subscribe(s -> {
                                idCardLayout.setVisibility(View.GONE);
                                phoneCodeLayout.setVisibility(View.VISIBLE);
                                phoneCodeImage.setImageResource(R.drawable.icon_phone_code);
                                isTrue = false;
                            }, this::handleApiError);
                } else {
                    if (TextUtils.isEmpty(verifiedCodeEdit.getText().toString().trim())) {
                        ToastUtils.showShort(getString(R.string.please_enter_verification_code));
                        return;
                    }

                    if (6 != verifiedCodeEdit.getText().toString().trim().length()) {
                        ToastUtils.showShort(getString(R.string.edit_code_tip));
                        return;
                    }

                    Intent intent = new Intent(this, SettingPayPwdActivity.class);
                    intent.putExtra("idCardEdit", idCardEdit.getText().toString());
                    intent.putExtra("verifiedCodeEdit", verifiedCodeEdit.getText().toString().trim());
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.text_get_code:
                registerCode(phone.getText().toString());
                break;
            default:
        }
    }

    @SuppressLint("CheckResult")
    public void registerCode(String phone) {
        countDownTimer.start();
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCode(phone, "1")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    ToastUtils.showShort(getString(R.string.code_label));
                    messagfeCode = s;
                }, t -> {
                    handleApiError(t);
                    countDownTimer.onFinish();
                    countDownTimer.cancel();
                });
    }

    private CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            long time = millisUntilFinished / 1000;
            textGetCode.setText(time + getString(R.string.tiem_new_code));
            textGetCode.setClickable(false);
        }

        @Override
        public void onFinish() {
            textGetCode.setText(getString(R.string.regain_verification_code));
            textGetCode.setClickable(true);
        }
    };
}
