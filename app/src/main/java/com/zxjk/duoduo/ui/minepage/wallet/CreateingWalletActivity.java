package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GenerateMnemonicResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.CreateWLoadingView;
import com.zxjk.duoduo.utils.MD5Utils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class CreateingWalletActivity extends BaseActivity {

    private CreateWLoadingView clv;

    private TextView tvTips;

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

        title.setText(R.string.createwallet);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ServiceFactory.getInstance().getBaseService(Api.class)
                .generateMnemonic(symbol, MD5Utils.getMD5(pwd))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .doOnSubscribe(disposable -> clv.startAnim())
                .flatMap((Function<GenerateMnemonicResponse, ObservableSource<?>>) response -> {
                    this.response = response;
                    tvTips.setText(R.string.createwallet_tips3);
                    return Observable.timer(1, TimeUnit.SECONDS);
                })
                .subscribe(s -> {
//                    Intent intent = new Intent(this, CreateWalletSuccessActivity.class);
//                    intent.putExtra("response", response);
//                    startActivity(intent);
                    clv.finish();
                }, t -> {
                    tvTips.setText(R.string.createwallet_tips4);
                    handleApiError(t);
                });
    }
}
