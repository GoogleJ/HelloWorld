package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

    private static String FIRST_CLICK = "first_click";
    private String data;
    private String version;
    private SharedPreferences settings ;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.blockWallet);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());


        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
         version = packInfo.versionName;
        settings = getSharedPreferences(FIRST_CLICK, 0);

        data = settings.getString("firstdot", "0");

        if(version.equals("1.9.1") && data.equals("1")){
            findViewById(R.id.first).setVisibility(View.GONE);
        }else if(!version.equals("1.9.1")){
            findViewById(R.id.first).setVisibility(View.GONE);
        }
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
        String url = "http://hilamg-recharge.ztoken.cn/?obj=" + data;

        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", getString(R.string.payPhone));
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    public void func4(View view) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getOpenPurchaseStatus()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(d -> {
                    if(d.equals("1")){
                        if(data.equals("0")){
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("firstdot","1");
                            editor.commit();
                            findViewById(R.id.first).setVisibility(View.GONE);
                        }
                        startActivity(new Intent(this,OneKeyBuyCoinActivity.class));
                    }else {
                        findViewById(R.id.first).setVisibility(View.GONE);
                        ToastUtils.showShort(R.string.developing);
                    }
                }, this::handleApiError);
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
