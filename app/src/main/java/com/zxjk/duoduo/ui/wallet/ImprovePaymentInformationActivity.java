package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;

@SuppressLint("CheckResult")
public class ImprovePaymentInformationActivity extends BaseActivity {
    private LinearLayout llWechat;
    private TextView tvWechat;
    private LinearLayout llAliPay;
    private TextView tvAliPay;
    private LinearLayout llBankPay;
    private TextView tvBankPay;
    private LinearLayout llPhone;
    private TextView tvPhone;

    private String payType;
    private String wechat = "WEIXIN";
    private String alipay = "ALIPAY";
    private String bank = "EBANK";
    private String mobile = "MOBILE ";
    private String buyType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_improve_payment_information);

        initView();

        initData();

    }

    private void initView() {
        llWechat = findViewById(R.id.ll_we_chat_payment);
        llAliPay = findViewById(R.id.ll_ali_pay_payment);
        llBankPay = findViewById(R.id.ll_bank_payment);
        llPhone = findViewById(R.id.ll_phone_payment);
        tvWechat = findViewById(R.id.tv_we_chat_type);
        tvAliPay = findViewById(R.id.tv_ali_pay_type);
        tvBankPay = findViewById(R.id.tv_bank_type);
        tvPhone = findViewById(R.id.tv_phone_type);

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.collection_information);
        findViewById(R.id.rl_back).setOnClickListener(v -> {
            finish();
        });
    }

    private void initData() {
        buyType = getIntent().getStringExtra("buyType");
        if (!TextUtils.isEmpty(getIntent().getStringExtra("buyType")) && "BUY".equals(buyType)) {
            llWechat.setVisibility(View.GONE);
            llAliPay.setVisibility(View.GONE);
            llBankPay.setVisibility(View.GONE);
        }
        ServiceFactory.getInstance().getBaseService(Api.class)
                .improvePaymentInformation()
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
                    if (1 == data.getMOBILE()) {
                        tvPhone.setText("已完善");
                    } else {
                        tvPhone.setText("未完善");
                    }
                    llPhone.setOnClickListener(v -> {
                        intent.putExtra("type", "MOBILE");
                        intent.putExtra("paymentinformation", data.getMOBILE());
                        startActivityForResult(intent, 1);
                    });
                    llBankPay.setOnClickListener(v -> {
                        intent.putExtra("type", "EBANK");
                        intent.putExtra("paymentinformation", data.getEBANK());
                        startActivityForResult(intent, 1);
                    });
                    llWechat.setOnClickListener(v -> {
                        intent.putExtra("type", "WEIXIN");
                        intent.putExtra("paymentinformation", data.getWEIXIN());
                        startActivityForResult(intent, 1);
                    });
                    llAliPay.setOnClickListener(v -> {
                        intent.putExtra("type", "ALIPAY");
                        intent.putExtra("paymentinformation", data.getALIPAY());
                        startActivityForResult(intent, 1);
                    });
                }, this::handleApiError);
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
                    case "MOBILE":
                        tvPhone.setText("已完善");
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
