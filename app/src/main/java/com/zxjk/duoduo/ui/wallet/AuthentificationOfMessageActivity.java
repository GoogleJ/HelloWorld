package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.security.rp.RPSDK;
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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class AuthentificationOfMessageActivity extends BaseActivity {
    private LinearLayout llWechat;
    private TextView tvWechat;
    private LinearLayout llAliPay;
    private TextView tvAliPay;
    private LinearLayout llBankPay;
    private TextView tvBankPay;
    private TextView tvRealNameAuthentication;
    private TextView tvNickName;
    private TextView tvPhoneNumber;
    private String payType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification_of_message);

        initView();

        initData();
    }

    private void initView() {
        llWechat = findViewById(R.id.ll_we_chat_payment);
        llAliPay = findViewById(R.id.ll_ali_pay_payment);
        llBankPay = findViewById(R.id.ll_bank_payment);
        tvWechat = findViewById(R.id.tv_we_chat_type);
        tvAliPay = findViewById(R.id.tv_ali_pay_type);
        tvBankPay = findViewById(R.id.tv_bank_type);
        tvNickName = findViewById(R.id.tv_nick_name);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvRealNameAuthentication = findViewById(R.id.tv_realNameAuthentication);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("信息配置");
        findViewById(R.id.rl_back).setOnClickListener(v -> {
            finish();
        });
    }

    private void initData() {
        if (Constant.currentUser.getIsAuthentication().equals("0")) {
            tvRealNameAuthentication.setText("重新认证");
        }
        improvePaymentInformation();
    }

    private void improvePaymentInformation() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getConfig()
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(data -> {
                    Intent intent = new Intent(this, ReceiptTypeActivity.class);
                    if (1 == data.getALIPAY()) {
                        tvAliPay.setText("已完善");
                    } else {
                        tvAliPay.setText("未完善");
                    }
                    if (1 == data.getWEIXIN()) {
                        tvWechat.setText("已完善");
                    } else {
                        tvWechat.setText("未完善");
                    }
                    if (1 == data.getEBANK()) {
                        tvBankPay.setText("已完善");
                    } else {
                        tvBankPay.setText("未完善");
                    }

                    tvPhoneNumber.setText(data.getMobile());
                    tvNickName.setText(data.getUserName());

                    llBankPay.setOnClickListener(v -> {
                        if (1 == data.getMOBILE()) {
                            intent.putExtra("type", "EBANK");
                            intent.putExtra("paymentinformation", data.getEBANK());
                            startActivityForResult(intent, 1);
                        } else {
                            ToastUtils.showShort("请先添加手机号信息");
                        }
                    });
                    llWechat.setOnClickListener(v -> {
                        if (1 == data.getMOBILE()) {
                            intent.putExtra("type", "WEIXIN");
                            intent.putExtra("paymentinformation", data.getWEIXIN());
                            startActivityForResult(intent, 1);
                        } else {
                            ToastUtils.showShort("请先添加手机号信息");
                        }
                    });
                    llAliPay.setOnClickListener(v -> {
                        if (1 == data.getMOBILE()) {
                            intent.putExtra("type", "ALIPAY");
                            intent.putExtra("paymentinformation", data.getALIPAY());
                            startActivityForResult(intent, 1);
                        } else {
                            ToastUtils.showShort("请先添加手机号信息");
                        }
                    });
                    findViewById(R.id.ll_nick_name).setOnClickListener(v -> {
                        intent.putExtra("type", "MOBILE");
                        startActivityForResult(intent, 1);
                    });
                    findViewById(R.id.ll_phone_number).setOnClickListener(v -> {
                        intent.putExtra("type", "MOBILE");
                        startActivityForResult(intent, 1);
                    });
                }, this::handleApiError);
    }


    public void realNameAuthentication(View view) {
        if (Constant.currentUser.getIsAuthentication().equals("2")) {
            ToastUtils.showShort(R.string.verifying_pleasewait);
        }else {
            Api api = ServiceFactory.getInstance().getBaseService(Api.class);
            api.getAuthToken()
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .flatMap(s -> Observable.create(emitter ->
                            RPSDK.start(s, AuthentificationOfMessageActivity.this, (audit, s1) -> {
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
                        tvRealNameAuthentication.setText("重新认证");
                    }, this::handleApiError);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1000 && data != null) {

            payType = data.getStringExtra("pay");
            if (!TextUtils.isEmpty(payType)) {
                switch (payType) {
                    case "WEIXIN":
                        tvWechat.setText("已完善");
                        break;
                    case "ALIPAY":
                        tvAliPay.setText("已完善");
                        break;
                    case "EBANK":
                        tvBankPay.setText("已完善");
                        break;
                    default:
                        break;
                }
                improvePaymentInformation();
            }
        }
    }
}
