package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GenerateMnemonicResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.CreateWLoadingView;
import com.zxjk.moneyspace.utils.AesUtil;

import static com.zxjk.moneyspace.Constant.ACTION_BROADCAST1;

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
        findViewById(R.id.rl_back).setVisibility(View.INVISIBLE);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .generateMnemonic(symbol, AesUtil.getInstance().encrypt(pwd))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .doOnSubscribe(disposable -> clv.startAnim())
                .doOnTerminate(() -> clv.finish())
                .subscribe(response -> {
                    tvTips.setText(R.string.createwallet_tips3);
                    btnComplete.setVisibility(View.VISIBLE);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_BROADCAST1));
                    this.response = response;
                }, t -> {
                    finish();
                    handleApiError(t);
                });
    }

    public void accomplish(View view) {
        Intent intent = new Intent(this, CreateWalletSuccessActivity.class);
        intent.putExtra("response", response);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
