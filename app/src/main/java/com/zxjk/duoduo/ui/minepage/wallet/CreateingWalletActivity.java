package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GenerateMnemonicResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.CreateWLoadingView;
import com.zxjk.duoduo.utils.MD5Utils;

public class CreateingWalletActivity extends BaseActivity {

    private CreateWLoadingView clv;

    private TextView tvTips;

    private Button btnComplete;

    private GenerateMnemonicResponse response;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createing_wallet);

        String symbol = getIntent().getStringExtra("symbol");
        String pwd = getIntent().getStringExtra("pwd");

        TextView title = findViewById(R.id.tv_title);
        tvTips = findViewById(R.id.tvTips);
        clv = findViewById(R.id.clv);
        btnComplete = findViewById(R.id.btnComplete);

        title.setText(R.string.createwallet);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ServiceFactory.getInstance().getBaseService(Api.class)
                .generateMnemonic(symbol, MD5Utils.getMD5(pwd))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .doOnSubscribe(disposable -> clv.startAnim())
                .doOnTerminate(() -> clv.finish())
                .subscribe(response -> {
                    tvTips.setText(R.string.createwallet_tips3);
                    btnComplete.setVisibility(View.VISIBLE);
                    this.response = response;
                }, t -> {
                    tvTips.setText(R.string.createwallet_tips4);
                    handleApiError(t);
                });
    }

    public void accomplish(View view) {
        Intent intent = new Intent(this, WalletActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        intent = new Intent(this, CreateWalletSuccessActivity.class);
        intent.putExtra("response", response);
        startActivity(intent);
    }
}
