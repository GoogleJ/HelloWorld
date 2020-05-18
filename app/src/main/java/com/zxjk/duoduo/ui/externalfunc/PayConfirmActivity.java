package com.zxjk.duoduo.ui.externalfunc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.ThirdPartyPaymentOrderResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MD5Utils;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class PayConfirmActivity extends BaseActivity {

    private String orderId;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirm);

        orderId = getIntent().getStringExtra("orderId");

        if (TextUtils.isEmpty(orderId)) {
            ToastUtils.showShort(R.string.wrong_param_data);
            finish();
            return;
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getThirdPartyPaymentOrder(orderId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(r -> {
                    TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
                    showAnimation.setDuration(250);
                    TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
                    dismissAnimation.setDuration(500);

                    QuickPopup popView = QuickPopupBuilder.with(this)
                            .contentView(R.layout.pop_third_pay)
                            .config(new QuickPopupConfig()
                                    .withClick(R.id.btnPay, v -> doPay(r), false)
                                    .backgroundColor(Color.parseColor("#80000000"))
                                    .gravity(Gravity.BOTTOM)
                                    .withShowAnimation(showAnimation)
                                    .withDismissAnimation(dismissAnimation))
                            .build();

                    ImageView ivIcon1 = popView.findViewById(R.id.ivIcon1);
                    ImageView ivIcon2 = popView.findViewById(R.id.ivIcon2);
                    GlideUtil.loadCircleImg(ivIcon1, r.getSymbolLogo());
                    GlideUtil.loadCircleImg(ivIcon2, r.getSymbolLogo());

                    TextView tvSymbol = popView.findViewById(R.id.tvSymbol);
                    TextView tvMoneyCNY = popView.findViewById(R.id.tvMoneyCNY);
                    TextView tvOrderInfo = popView.findViewById(R.id.tvOrderInfo);
                    TextView tvReceiver = popView.findViewById(R.id.tvReceiver);
                    TextView tvMoney = popView.findViewById(R.id.tvMoney);
                    TextView tvPayment = popView.findViewById(R.id.tvPayment);
                    Button btnPay = popView.findViewById(R.id.btnPay);

                    tvSymbol.setText(r.getSymbol());
                    tvMoneyCNY.setText("≈" + r.getCnyAmount() + " CNY");
                    tvOrderInfo.setText(r.getOrderNumber());
                    tvReceiver.setText(r.getBusinessName());
                    tvMoney.setText(r.getAmount());
                    if (r.getPay() == 0) {
                        tvPayment.setText(getString(R.string.current_left, r.getSymbol(), r.getBalance()));
                        btnPay.setEnabled(false);
                        btnPay.setClickable(false);
                        btnPay.setAlpha(0.6f);
                    } else {
                        tvPayment.setText(getString(R.string.pay_left, r.getSymbol(), r.getBalance()));
                    }
                }, this::handleApiError);

    }

    private void doPay(ThirdPartyPaymentOrderResponse r) {
        new NewPayBoard(this).show(pwd ->
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .thirdPartyPayment(orderId, MD5Utils.getMD5(pwd))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            Intent intent = new Intent(this, ThirdPartPayResultActivity.class);
                            intent.putExtra("data", r);
                            startActivity(intent);
                            finish();
                        }, this::handleApiError)
        );
    }

    @Override
    public void onBackPressed() {
    }
}
