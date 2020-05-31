package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetPaymentListBean;
import com.zxjk.duoduo.bean.response.GetSymbolPrice;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MD5Utils;
import com.zxjk.duoduo.utils.MoneyValueFilter;

import java.util.ArrayList;

public class PayAliActivity extends BaseActivity {

    private String qrdata;

    private ImageView ivCoinIcon;
    private TextView tvCoin;
    private EditText etMoney;

    private GetSymbolPrice result;
    private ArrayList<GetSymbolPrice> list = new ArrayList<>();

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_ali);
        BarUtils.setStatusBarColor(this, Color.parseColor("#272E3F"));
        FrameLayout flTop = findViewById(R.id.flTop);
        BarUtils.addMarginTopEqualStatusBarHeight(flTop);

        qrdata = getIntent().getStringExtra("qrdata");

        ivCoinIcon = findViewById(R.id.ivCoinIcon);
        tvCoin = findViewById(R.id.tvCoin);
        etMoney = findViewById(R.id.etMoney);

        etMoney.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(2)});

        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Api api = ServiceFactory.getInstance().getBaseService(Api.class);

        api.getSymbolPrice()
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(bindToLifecycle())
                .subscribe(l -> {
                    list.addAll(l);
                    result = list.get(0);
                    GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
                    tvCoin.setText(result.getSymbol());
                }, t -> {
                    handleApiError(t);
                    finish();
                });

    }

    public void pay(View view) {
        if (TextUtils.isEmpty(etMoney.getText().toString().trim())) {
            ToastUtils.showShort(R.string.input_empty);
            return;
        }

        new NewPayBoard(this)
                .show(pwd -> ServiceFactory.getInstance().getBaseService(Api.class)
                        .addSubmitOrder(MD5Utils.getMD5(pwd), qrdata, etMoney.getText().toString().trim(), result.getSymbol())
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            ToastUtils.showShort(R.string.paysuccess);
                            finish();
                        }, this::handleApiError)
                );
    }

    public void chooseCoin(View view) {
        Intent intent = new Intent(this, ChooseCoinActivity.class);
        intent.putExtra("data", list);
        startActivityForResult(intent, 1);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == 1 && resultCode == 1) {
            result = data.getParcelableExtra("result");
            GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
            tvCoin.setText(result.getSymbol());
            etMoney.setHint(getString(R.string.transLeft)  + result.getSymbol());
        }
    }

}
