package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.walletpage.BlockWalletActivity;
import com.zxjk.duoduo.ui.walletpage.ExchangeActivity;
import com.zxjk.duoduo.utils.CommonUtils;

public class WalletActivity extends BaseActivity {

    private TextView tvMoney1;
    private TextView tvMoney2;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.blockWallet);
        tvMoney1 = findViewById(R.id.tvMoney1);
        tvMoney2 = findViewById(R.id.tvMoney2);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getBalanceHk(Constant.currentUser.getWalletAddress())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(c -> {
                    tvMoney1.setText(c.getBalanceMoToken());
                    tvMoney2.setText(c.getBalanceMot());
                }, this::handleApiError);

    }

    public void exchange(View view) {
        startActivity(new Intent(this, ExchangeActivity.class));
    }

    public void balanceLeft(View view) {
        startActivity(new Intent(this, BalanceLeftActivity.class));
    }

    public void blockWallet(View view) {
//        startActivity(new Intent(this, BlockWalletActivity.class));
        startActivity(new Intent(this, NewBlockWalletActivity.class));
    }
}
