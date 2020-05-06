package com.zxjk.moneyspace.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.SuccessActivity2;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.wallet.BalanceDetailActivity;
import com.zxjk.moneyspace.ui.widget.NewPayBoard;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MD5Utils;
import com.zxjk.moneyspace.utils.MoneyValueFilter;

//人民币提现
public class CNYDownActivity extends BaseActivity {

    private TextView tvBank;
    private TextView tvCenterMoney;
    private EditText et;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnydown);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.tixian);

        tvBank = findViewById(R.id.tvBank);
        tvCenterMoney = findViewById(R.id.tvCenterMoney);

        et = findViewById(R.id.et);

        et.setFilters((new InputFilter[]{new MoneyValueFilter().setDigits(2)}));

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCnyWithdrawRate()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .subscribe(s -> {
                    tvCenterMoney.setVisibility(View.VISIBLE);
                    tvCenterMoney.setText(getString(R.string.centermoneyis, s));
                }, t -> {
                });

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCustomerBankInfo()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(r -> tvBank.setText(r.getBank() + "  (" + r.getBankNum().substring(r.getBankNum().length() - 4) + ")"), this::handleApiError);
    }

    public void done(View view) {
        if (TextUtils.isEmpty(et.getText().toString().trim())) {
            ToastUtils.showShort(R.string.input_skin);
            return;
        }

        new NewPayBoard(this)
                .show(result ->
                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .cnyWithdraw(et.getText().toString().trim(), MD5Utils.getMD5(result))
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.normalTrans())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                                .subscribe(s -> {
                                    Intent intent = new Intent(this, BalanceDetailActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    intent = new Intent(this, SuccessActivity2.class);
                                    startActivity(intent);
                                }, this::handleApiError)
                );

    }

}
