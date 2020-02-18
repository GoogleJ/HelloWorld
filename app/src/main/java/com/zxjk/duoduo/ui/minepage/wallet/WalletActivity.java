package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.PayPhoneRequest;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.AesUtil;
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

    @SuppressLint("CheckResult")
    public void mall(View view) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getShoppingUrl("1")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(url -> {
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title", "商城");
                    intent.putExtra("type", "mall");
                    startActivity(intent);
                }, this::handleApiError);
    }

    public void func1(View view) {
        ToastUtils.showShort(R.string.developing);
    }

    public void func2(View view) {
        ToastUtils.showShort(R.string.developing);
    }

    public void func3(View view) {
        String data = AesUtil.getInstance().encrypt(GsonUtils.toJson(new PayPhoneRequest(Constant.token, Constant.userId)));
        String url = "http://tellus-admin.huijin.fun/?obj=" + data;

        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", getString(R.string.payPhone));
        startActivity(intent);
    }

    public void func4(View view) {
        ToastUtils.showShort(R.string.developing);
    }

    public void func5(View view) {
        ToastUtils.showShort(R.string.developing);
    }

    public void func6(View view) {
        ToastUtils.showShort(R.string.developing);
    }

    public void func7(View view) {
        ToastUtils.showShort(R.string.developing);
    }
}
