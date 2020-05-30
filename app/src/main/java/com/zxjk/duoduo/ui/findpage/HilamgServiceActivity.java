package com.zxjk.duoduo.ui.findpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import com.zxjk.duoduo.ui.minepage.CooperateActivity;
import com.zxjk.duoduo.ui.minepage.RewardMotActivity;
import com.zxjk.duoduo.ui.wallet.OneKeyBuyCoinActivity;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.CommonUtils;

public class HilamgServiceActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hilamg_service);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv1:
                startActivity(new Intent(this, RewardMotActivity.class));
                break;
            case R.id.tv2:
                break;
            case R.id.tv3:
                payPhone();
                break;
            case R.id.tv4:
                buyCoin();
                break;
            case R.id.tv5:
                mall();
                break;
            case R.id.tv6:
                startActivity(new Intent(this, CooperateActivity.class));
                break;
            case R.id.tv7:
                break;
            case R.id.tv8:
                break;
            case R.id.tv9:
                ToastUtils.showShort(getString(R.string.toast1));
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void buyCoin() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getOpenPurchaseStatus()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(d -> {
                    if (d.equals("1")) {
                        startActivity(new Intent(this, OneKeyBuyCoinActivity.class));
                    } else {
                        findViewById(R.id.first).setVisibility(View.GONE);
                        ToastUtils.showShort(R.string.developing);
                    }
                }, this::handleApiError);
    }

    private void payPhone() {
        String data = AesUtil.getInstance().encrypt(GsonUtils.toJson(new PayPhoneRequest(Constant.token, Constant.userId)));
        String url = "http://hilamg-recharge.ztoken.cn/?obj=" + data;
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", getString(R.string.payPhone));
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    private void mall() {
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
}
