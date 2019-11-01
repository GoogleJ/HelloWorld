package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.walletpage.ExchangeActivity;
import com.zxjk.duoduo.utils.CommonUtils;

public class WalletActivity extends BaseActivity {

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.blockWallet);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    public void balanceLeft(View view) {
        startActivity(new Intent(this, BalanceLeftActivity.class));
    }

    @SuppressLint("CheckResult")
    public void blockWallet(View view) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .isExistWalletInfo()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    if (s.equals("0")) {
                        startActivity(new Intent(this, BlockWalletEmptyActivity.class));
                    } else {
                        Intent intent = new Intent(this, NewBlockWalletActivity.class);
                        startActivity(intent);
                    }
                }, this::handleApiError);
    }
}
