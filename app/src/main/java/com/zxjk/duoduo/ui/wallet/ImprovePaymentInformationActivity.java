package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.umeng.commonsdk.debug.I;
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
    }

    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .improvePaymentInformation()
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(data -> {
                    if(1 == data.getALIPAY()){
                        tvAliPay.setText("已完善");
                    }else {
                        llAliPay.setOnClickListener(v -> {
                            Intent intent = new Intent(this,ReceiptTypeActivity.class);
                            intent.putExtra("type","ALIPAY");
                            startActivity(intent);
                        });
                        tvAliPay.setText("未完善");
                    }
                    if(1 == data.getWEIXIN()){
                        tvWechat.setText("已完善");
                    }else {
                        llWechat.setOnClickListener(v -> {
                            Intent intent = new Intent(this,ReceiptTypeActivity.class);
                            intent.putExtra("type","WEIXIN");
                            startActivity(intent);
                        });
                        tvWechat.setText("未完善");
                    }
                    if(1 == data.getEBANK()){
                        tvBankPay.setText("已完善");
                    }else {
                        llBankPay.setOnClickListener(v -> {
                            Intent intent = new Intent(this,ReceiptTypeActivity.class);
                            intent.putExtra("type","EBANK");
                            startActivity(intent);
                        });
                        tvBankPay.setText("未完善");
                    }
                    if(1 == data.getMOBILE()){
                        tvPhone.setText("已完善");
                    }else {
                        llPhone.setOnClickListener(v -> {
                            Intent intent = new Intent(this,ReceiptTypeActivity.class);
                            intent.putExtra("type","MOBILE");
                            startActivity(intent);
                        });
                        tvPhone.setText("未完善");
                    }
                }, this::handleApiError);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
