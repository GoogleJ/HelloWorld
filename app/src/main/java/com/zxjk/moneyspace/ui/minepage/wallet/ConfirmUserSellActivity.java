package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.ConfirmUserSellResponse;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


@SuppressLint("CheckResult")
public class ConfirmUserSellActivity extends BaseActivity {
    private TextView tvTheOrderNumber;
    private TextView tvPaymentStatus;
    private TextView tvCancelTheOrder;
    private TextView tvTotal;
    private TextView tvPrice;
    private TextView tvNumber;
    private TextView tvOrderTime;
    private TextView tvPaymentType;
    private TextView tvBusinessName;
    private TextView tvRemark;
    private TextView tvPayment;
    private LinearLayout llCancelTheOrder;
    private TextView tv1;
    private NestedScrollView scrollView;
    private LinearLayout llTitleBar;

    private String sign;
    private String timestamp;
    private String defaultRenegeNumber;
    private Boolean isJump = true;
    private String orderId;
    private ConfirmUserSellResponse confirmUserSellResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_details);
        setTrasnferStatusBar(true);
    }

    private void initView(){
        tvTheOrderNumber = findViewById(R.id.tv_the_order_number);
        tvPaymentStatus = findViewById(R.id.tv_payment_status);
        tvCancelTheOrder = findViewById(R.id.tv_cancel_the_order);
        tvTotal = findViewById(R.id.tv_total);
        tvPrice = findViewById(R.id.tv_price);
        tvNumber = findViewById(R.id.tv_number);
        tvOrderTime = findViewById(R.id.tv_order_time);
        tvPaymentType = findViewById(R.id.tv_payment_type);
        tvBusinessName = findViewById(R.id.tv_business_name);
        tvRemark = findViewById(R.id.tv_remark);
        tvPayment = findViewById(R.id.tv_payment);
        llCancelTheOrder = findViewById(R.id.ll_cancel_the_order);
        tv1 = findViewById(R.id.tv1);
        scrollView = findViewById(R.id.scrollview);
        llTitleBar = findViewById(R.id.ll_title_bar);

        findViewById(R.id.rl_back).setOnClickListener(v ->
                finish()
        );
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (nestedScrollView, i, i1, i2, i3) -> {

            int height = CommonUtils.dip2px(this, 45);

            if (i1 <= height) {
                float scale = (float) i1 / height;
                float alpha = scale * 255;
                llTitleBar.setBackgroundColor(Color.argb((int) alpha, 0, 0, 0));
            } else {
                llTitleBar.setBackgroundResource(R.color.colorPrimary);
            }

            if (i1 <= 0) {
                llTitleBar.setBackgroundColor(Color.argb(0, 79, 139, 242));
            } else if (i1 > 0 && i1 < height) {
                float scale = (float) i1 / height;
                float alpha = (255 * scale);
                llTitleBar.setBackgroundColor(Color.argb((int) alpha, 79, 139, 242));
            } else {
                llTitleBar.setBackgroundColor(Color.argb(255, 79, 139, 242));
            }

        });
    }

    private void initData(){
        confirmUserSellResponse = (ConfirmUserSellResponse) getIntent().getExtras().getSerializable("ConfirmUserSellResponse");

        if (TextUtils.isEmpty(confirmUserSellResponse.getBothOrderId())) {
            if (confirmUserSellResponse.getStatus().equals("7")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting_coin, null), tvPaymentStatus, getString(R.string.complete_the_transaction));
                tvPayment.setVisibility(View.INVISIBLE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, "订单已完成");
            } else if (confirmUserSellResponse.getStatus().equals("1")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                tvPayment.setVisibility(View.INVISIBLE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, "订单已完成");
            } else if (confirmUserSellResponse.getStatus().equals("9")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting_coin, null), tvPaymentStatus, "交易中");
                tvPayment.setVisibility(View.INVISIBLE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, "订单已完成");
            }
        } else {
            if (confirmUserSellResponse.getStatus().equals("3")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting_coin, null), tvPaymentStatus, getString(R.string.waiting_to_put_money2));
                tvPayment.setVisibility(View.INVISIBLE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.waiting_to_put_money));

                long l1 = (System.currentTimeMillis() - Long.parseLong(confirmUserSellResponse.getCreateTime())) / 1000;
                long total = ((900 - l1) <= 0 ? 0 : (900 - l1)) + 15;
                Observable.interval(0, 1, TimeUnit.SECONDS)
                        .take(total + 10)
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(bindToLifecycle())
                        .subscribe(l -> {
                            long minute = (total - l) / 60;
                            long second = (total - l) % 60;
                            tvCancelTheOrder.setText(minute + ":" + (second == 60 ? "00" : ((second < 10 ? ("0" + (second - 1)) : second))));
                            if (total == 0 || l == total - 1) {
//                                    orderInfo();
                            }
                        }, t -> {
                        });

            } else if (confirmUserSellResponse.getStatus().equals("0")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.for_the_payment));
                tvPayment.setVisibility(View.INVISIBLE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.no_payment));
                tvCancelTheOrder.setText("待买家付款");
            } else if (confirmUserSellResponse.getStatus().equals("1")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                tvPayment.setVisibility(View.GONE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.no_payment));
                tvCancelTheOrder.setVisibility(View.VISIBLE);
                tvCancelTheOrder.setText(getString(R.string.buyer_cancel));
            } else if (confirmUserSellResponse.getStatus().equals("2")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                tvPayment.setVisibility(View.GONE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.no_payment));
                tvCancelTheOrder.setVisibility(View.VISIBLE);
                tvCancelTheOrder.setText("超时系统取消");
            } else if (confirmUserSellResponse.getStatus().equals("4")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_timeout_coin, null), tvPaymentStatus, getString(R.string.coin_timeout2));
                tvPayment.setText(R.string.the_complaint);
                llCancelTheOrder.setVisibility(View.GONE);
            } else if (confirmUserSellResponse.getStatus().equals("5")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_the_complaint, null), tvPaymentStatus, "申诉完成");
                tvPayment.setVisibility(View.GONE);
                llCancelTheOrder.setVisibility(View.GONE);
            } else if (confirmUserSellResponse.getStatus().equals("6")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_the_complaint, null), tvPaymentStatus, getString(R.string.in_the_complaint));
                tvPayment.setVisibility(View.GONE);
                llCancelTheOrder.setVisibility(View.GONE);
            } else if (confirmUserSellResponse.getStatus().equals("7")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, getString(R.string.complete_the_transaction));
                tvPayment.setVisibility(View.GONE);
                llCancelTheOrder.setVisibility(View.GONE);
            }
        }

        tvTheOrderNumber.setText(getString(R.string.order_number, orderId));
        tvTotal.setText(confirmUserSellResponse.getMoney());
//        tvPrice.setText(confirmUserSellResponse.getPrice() + "\u0020" + "CNY" + "/" + confirmUserSellResponse.getCurrency());
//        tvNumber.setText(confirmUserSellResponse.getNumber() + "\u0020" + confirmUserSellResponse.getCurrency());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (!TextUtils.isEmpty(confirmUserSellResponse.getCreateTime())) {
            String sd = sdf.format(new Date(Long.parseLong(confirmUserSellResponse.getCreateTime())));
            tvOrderTime.setText(sd);
        }

        if (confirmUserSellResponse.getPayType().equals("1")) {
            tvPaymentType.setText("微信支付");
//            tvRemark.setText(confirmUserSellResponse.getWechatNick());
        } else if (confirmUserSellResponse.getPayType().equals("2")) {
//            tvRemark.setText(confirmUserSellResponse.getZhifubaoNumber());
            tvPaymentType.setText("支付宝");
        } else {
//            tvRemark.setText(confirmUserSellResponse.getPayNumber());
            tvPaymentType.setText("银行卡");
        }

//                Drawable drawable = resource;
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
//                        .getMinimumHeight());// 设置边界
//                tvPaymentType.setCompoundDrawables(drawable, null, null, null);
//                tvPaymentType.setCompoundDrawablePadding(8);



        tvBusinessName.setText(confirmUserSellResponse.getSellNick());

        tvPayment.setOnClickListener(v -> {
            Intent intent;
            if (tvPayment.getText().equals(getString(R.string.the_complaint))) {
                intent = new Intent(this, TheAppealActivity.class);
            } else {
                intent = new Intent(this, OrderDetailsActivity.class);
            }
//            intent.putExtra("ByBoinsResponse", getOrderInfoById);
            startActivity(intent);
            finish();
        });

        tvTheOrderNumber.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

//            ClipData clip = ClipData.newPlainText("simple text", getOrderInfoById.getTransId());

//            clipboard.setPrimaryClip(clip);
            ToastUtils.showShort(R.string.duplicated_to_clipboard);
        });

        tvCancelTheOrder.setOnClickListener(v -> {
            if (tvCancelTheOrder.getText().equals(getString(R.string.cancel_the_order))) {
//                onBackDialog();
            }
        });
    }


    private void setDrawables(Drawable drawable, TextView textView, String text) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());// 设置边界
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setCompoundDrawablePadding(8);
    }

}
