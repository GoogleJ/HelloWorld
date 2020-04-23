package com.zxjk.moneyspace.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.request.AddBankInfoRequest;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

public class BindCNYCardActivity extends BaseActivity {

    private boolean fromBalance;
    private boolean fromBalance1;

    private EditText etBank;
    private EditText etBankCard;
    private EditText etBankAddress;
    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_cnycard);

        fromBalance = getIntent().getBooleanExtra("fromBalance", false);
        fromBalance1 = getIntent().getBooleanExtra("fromBalance1", false);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.bindbank);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        etBank = findViewById(R.id.etBank);
        etBankCard = findViewById(R.id.etBankCard);
        etBankAddress = findViewById(R.id.etBankAddress);
        etName = findViewById(R.id.etName);

    }

    @SuppressLint("CheckResult")
    public void bind(View view) {
        if (TextUtils.isEmpty(etBank.getText().toString().trim())) {
            ToastUtils.showShort(R.string.info_empty);
            return;
        }
        if (TextUtils.isEmpty(etBankCard.getText().toString().trim())) {
            ToastUtils.showShort(R.string.info_empty);
            return;
        }
        if (TextUtils.isEmpty(etBankAddress.getText().toString().trim())) {
            ToastUtils.showShort(R.string.info_empty);
            return;
        }
        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            ToastUtils.showShort(R.string.info_empty);
            return;
        }

        AddBankInfoRequest request = new AddBankInfoRequest();
        request.setBank(etBank.getText().toString().trim());
        request.setBankNum(etBankCard.getText().toString().trim());
        request.setSubbranch(etBankAddress.getText().toString().trim());
        request.setCardholderName(etName.getText().toString().trim());

        ServiceFactory.getInstance().getBaseService(Api.class)
                .addCustomerBankInfo(GsonUtils.toJson(request))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    if (fromBalance) {
                        startActivity(new Intent(this, CNYUpActivity.class));
                    }
                    if (fromBalance1) {
                        startActivity(new Intent(this, CNYDownActivity.class));
                    }
                    finish();
                }, this::handleApiError);
    }
}
