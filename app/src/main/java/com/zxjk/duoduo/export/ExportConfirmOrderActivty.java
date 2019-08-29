package com.zxjk.duoduo.export;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetThirdPartyPaymentOrderResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MD5Utils;

@SuppressLint("CheckResult")
public class ExportConfirmOrderActivty extends BaseActivity {

    private TextView tvMoney;
    private TextView tvOrderInfo;
    private TextView tvOrderReceiver;
    private TextView tvOrderNum;
    private GetThirdPartyPaymentOrderResponse orderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_confirm_order_activty);

        initView();

        initData();
    }

    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getThirdPartyPaymentOrder(getIntent().getStringExtra("data"))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(response -> {
                    this.orderInfo = response;
                    tvMoney.setText(response.getHk());
                    tvOrderInfo.setText(response.getGoodsName());
                    tvOrderNum.setText(response.getOrderNumber());
                    tvOrderReceiver.setText(response.getBusinessName());
                }, this::handleApiError);
    }

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.confirmpay);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        tvMoney = findViewById(R.id.tvMoney);
        tvOrderInfo = findViewById(R.id.tvOrderInfo);
        tvOrderReceiver = findViewById(R.id.tvOrderReceiver);
        tvOrderNum = findViewById(R.id.tvOrderNum);
    }

    public void pay(View view) {
        if (orderInfo == null) {
            ToastUtils.showShort(R.string.function_fail1);
            return;
        }
        new NewPayBoard(this).show(s -> ServiceFactory.getInstance().getBaseService(Api.class)
                .thirdPartyPayment(GsonUtils.toJson(orderInfo), MD5Utils.getMD5(s))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(response -> {
                    Intent result = new Intent("receiver.duoduo.pay");
                    if (response.code != Constant.CODE_SUCCESS) {
                        result.putExtra("result", response.msg);
                    } else {
                        result.putExtra("result", "success");
                    }
                    sendBroadcast(result);

                    Intent intent = new Intent(this, ExportPaySuccessActivity.class);
                    intent.putExtra("bussinessName", orderInfo.getBusinessName());
                    intent.putExtra("hk", orderInfo.getHk());
                    startActivity(intent);
                    finish();
                }, this::handleApiError));
    }

}
