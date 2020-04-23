package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.request.SignTransactionRequest;
import com.zxjk.moneyspace.bean.response.GetBalanceInfoResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.msgpage.QrCodeActivity;
import com.zxjk.moneyspace.ui.widget.NewPayBoard;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MD5Utils;
import com.zxjk.moneyspace.utils.MoneyValueFilter;

public class DownCoinActivity extends BaseActivity {

    private GetBalanceInfoResponse.BalanceListBean data;

    private EditText etBlockAddress;
    private EditText etCount;
    private TextView tvGasPrice;
    private TextView tvGasPrice1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_coin);

        data = getIntent().getParcelableExtra("data");
        initView();
    }

    private void initView() {
        TextView title = findViewById(R.id.tv_title);
        title.setText(getString(R.string.xx_trade, data.getCurrencyName()));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        etBlockAddress = findViewById(R.id.etBlockAddress);
        etCount = findViewById(R.id.etCount);
        tvGasPrice = findViewById(R.id.tvGasPrice);
        tvGasPrice1 = findViewById(R.id.tvGasPrice1);
        etCount.setHint(R.string.tibi_num_tip);
        etCount.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(Integer.parseInt(data.getDecimals()))});
        tvGasPrice.setVisibility(View.VISIBLE);
        tvGasPrice.setText(R.string.trade_commission);
        tvGasPrice1.setText(data.getRate() + data.getCoin());
    }

    public void scan(View view) {
        Intent intent = new Intent(this, QrCodeActivity.class);
        intent.putExtra("actionType", QrCodeActivity.ACTION_IMPORT_WALLET);
        startActivityForResult(intent, 1);
    }

    @SuppressLint("CheckResult")
    public void confirm(View view) {
        String blockAddress = etBlockAddress.getText().toString().trim();
        if (TextUtils.isEmpty(blockAddress)) {
            ToastUtils.showShort(R.string.input_wallet_address);
            return;
        }

        String count = etCount.getText().toString().trim();
        if (TextUtils.isEmpty(count)) {

            ToastUtils.showShort(R.string.input_down_count);
            return;
        }

        if (Double.parseDouble(count) <= 0) {
            ToastUtils.showShort(R.string.down_count_less_zero);
            return;
        }

        if (blockAddress.equals(data.getBalanceAddress())) {
            ToastUtils.showShort(R.string.address_same);
            return;
        }

        String limit = data.getCurrencyLimit();
        if (!TextUtils.isEmpty(limit)) {
            double limitNum = Double.parseDouble(limit);
            if (Double.parseDouble(count) < limitNum) {
                ToastUtils.showShort(getString(R.string.current_downcoin_nums_cant_less_than, limitNum));
                return;
            }
        }

        new NewPayBoard(this)
                .show(pwd -> {
                    if (data.getParentSymbol().equals("ETH")) {
                        SignTransactionRequest request = new SignTransactionRequest();
                        request.setBalance(count);
                        request.setTokenName(data.getCoin());
                        request.setToAddress(blockAddress);
                        request.setFromAddress(data.getBalanceAddress());
                        request.setSerialType("1");
                        request.setTransType("1");
                        request.setRate(data.getRate());

                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .signTransaction(GsonUtils.toJson(request), MD5Utils.getMD5(pwd))
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.normalTrans())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                                .subscribe(s -> {
                                    Intent intent = new Intent(this, UpDownCoinResultActivity.class);
                                    intent.putExtra("type", getString(R.string.tibi));
                                    intent.putExtra("logo", data.getCoin());
                                    startActivity(intent);
                                    finish();
                                }, this::handleApiError);
                    } else {
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1 && data != null) {
            etBlockAddress.setText(data.getStringExtra("result"));
        }
    }
}
