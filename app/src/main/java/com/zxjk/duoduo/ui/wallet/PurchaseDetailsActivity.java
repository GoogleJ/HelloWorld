package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.ByBoinsResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.MMKVUtils;
import com.zxjk.duoduo.utils.Sha256;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

@SuppressLint("CheckResult")
public class PurchaseDetailsActivity extends BaseActivity {
    private ByBoinsResponse byBoinsResponse;

    private TextView tvTheOrderNumber;
    private TextView tvPaymentStatus;
    private TextView tvCancelTheOrder;
    private TextView tvTotal;
    private TextView tvPrice;
    private TextView tvNumber;
    private TextView tvOrderTime;
    private TextView tvPaymentType;
    private TextView tvBusinessName;
    private TextView tvOtherUserId;
    private LinearLayout llOtherUserId;
    private TextView tvRemark;
    private TextView tvPayment;
    private LinearLayout llCancelTheOrder;
    private TextView tv1;
    private ScrollView scrollView;
    private RelativeLayout rlTitleBar;

    private String sign;
    private String timestamp;
    private String defaultRenegeNumber;
    private Boolean isJump = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_details);

        setLightStatusBar(true);
        BarUtils.setStatusBarColor(this, Color.parseColor("#0083BF"));
        initView();
        rlTitleBar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        initData();
    }

    private void initView() {
        tvTheOrderNumber = findViewById(R.id.tv_the_order_number);
        tvPaymentStatus = findViewById(R.id.tv_payment_status);
        tvCancelTheOrder = findViewById(R.id.tv_cancel_the_order);
        llOtherUserId = findViewById(R.id.ll_other_user_id);
        tvTotal = findViewById(R.id.tv_total);
        tvPrice = findViewById(R.id.tv_price);
        tvNumber = findViewById(R.id.tv_number);
        tvOrderTime = findViewById(R.id.tv_order_time);
        tvPaymentType = findViewById(R.id.tv_payment_type);
        tvBusinessName = findViewById(R.id.tv_business_name);
        tvOtherUserId = findViewById(R.id.tv_other_user_id);
        tvRemark = findViewById(R.id.tv_remark);
        tvPayment = findViewById(R.id.tv_payment);
        llCancelTheOrder = findViewById(R.id.ll_cancel_the_order);
        tv1 = findViewById(R.id.tv1);
        scrollView = findViewById(R.id.scrollview);
        rlTitleBar = findViewById(R.id.rl_title_bar);

        findViewById(R.id.rl_back).setOnClickListener(v ->
                finish()
        );

    }

    private void initData() {
        if (isJump) {
            byBoinsResponse = (ByBoinsResponse) getIntent().getExtras().getSerializable("ByBoinsResponse");
        }

        defaultRenegeNumber = MMKVUtils.getInstance().decodeString("DefaultRenegeNumber");

        if (!TextUtils.isEmpty(byBoinsResponse.getState())) {
            if (byBoinsResponse.getState().equals("2")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting_coin, null), tvPaymentStatus, getString(R.string.waiting_to_put_money2));
                tvPayment.setVisibility(View.INVISIBLE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.waiting_to_put_money));

                long l1 = (System.currentTimeMillis() - Long.parseLong(byBoinsResponse.getPayTime())) / 1000;

                long total = (900 - l1) <= 0 ? 0 : (900 - l1);
                Observable.interval(0, 1, TimeUnit.SECONDS)
                        .take(total)
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(bindToLifecycle())
                        .subscribe(l -> {
                            long minute = (total - l) / 60;
                            long second = (total - l) % 60;
                            tvCancelTheOrder.setText(minute + ":" + (second == 0 ? "00" : (second < 10 ? ("0" + (second - 1)) : second)));
                            if (total == 0 || l == total - 1) {
                                orderInfo();
                            }
                        }, t -> {
                        });

            } else if (byBoinsResponse.getState().equals("6")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                tvPayment.setVisibility(View.INVISIBLE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.no_payment));
                tvCancelTheOrder.setText(R.string.already_timeout);
            } else if (byBoinsResponse.getState().equals("4")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                tvPayment.setVisibility(View.INVISIBLE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.buyer_cancel));
                tvCancelTheOrder.setVisibility(View.GONE);
            } else if (byBoinsResponse.getState().equals("5")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                tvPayment.setVisibility(View.INVISIBLE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.seller_to_cancel));
                tvCancelTheOrder.setVisibility(View.GONE);
            } else if (byBoinsResponse.getState().equals("1") || byBoinsResponse.getState().equals("7")) {
                if (byBoinsResponse.getAppeal().equals("0") && byBoinsResponse.getAppealTime().equals("0")) {
                    if (byBoinsResponse.getState().equals("1")) {
                        setDrawables(getResources().getDrawable(R.drawable.for_the_payment, null), tvPaymentStatus, getString(R.string.waiting_for_payment));
                    } else {
                        setDrawables(getResources().getDrawable(R.drawable.ic_timeout_coin, null), tvPaymentStatus, getString(R.string.coin_timeout2));
                        tvPayment.setText(R.string.the_complaint);
                        llCancelTheOrder.setVisibility(View.GONE);
                    }
                } else if (byBoinsResponse.getAppeal().equals("1")) {
                    setDrawables(getResources().getDrawable(R.drawable.ic_the_complaint, null), tvPaymentStatus, getString(R.string.in_the_complaint));
                    tvPayment.setVisibility(View.INVISIBLE);
                    llCancelTheOrder.setVisibility(View.GONE);
                }
            } else if (byBoinsResponse.getState().equals("3")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, getString(R.string.complete_the_transaction));
                tvPayment.setVisibility(View.INVISIBLE);
                llCancelTheOrder.setVisibility(View.GONE);
            }
        }

        tvTheOrderNumber.setText(getString(R.string.order_number, byBoinsResponse.getTransId()));
        tvTotal.setText(byBoinsResponse.getTotal());
        tvPrice.setText(byBoinsResponse.getPrice() + "\u0020" + byBoinsResponse.getCurrencyType() + "/" + byBoinsResponse.getCurrency());
        tvNumber.setText(byBoinsResponse.getAmount() + "\u0020" + byBoinsResponse.getCurrency());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (!TextUtils.isEmpty(byBoinsResponse.getCreateTime())) {
            String sd = sdf.format(new Date(Long.parseLong(byBoinsResponse.getCreateTime())));
            tvOrderTime.setText(sd);
        }

        if (byBoinsResponse.getPaymentType().equals("1")) {
            setDrawables(getResources().getDrawable(R.drawable.bank_card2, null), tvPaymentType, getString(R.string.bank_card_payment));
        } else if (byBoinsResponse.getPaymentType().equals("2")) {
            setDrawables(getResources().getDrawable(R.drawable.pay_treasure, null), tvPaymentType, getString(R.string.alipay_pay));
        } else {
            setDrawables(getResources().getDrawable(R.drawable.wechat, null), tvPaymentType, getString(R.string.wechat_pay));
        }

        tvBusinessName.setText(byBoinsResponse.getBusinessName());
        tvOtherUserId.setText(byBoinsResponse.getOtherUserId());
        tvRemark.setText(byBoinsResponse.getRemark());

        tvPayment.setOnClickListener(v -> {
            Intent intent;
            if (tvPayment.getText().equals(getString(R.string.the_complaint))) {
                intent = new Intent(this, TheAppealActivity.class);
            } else {
                intent = new Intent(this, OrderDetailsActivity.class);
            }
            intent.putExtra("ByBoinsResponse", byBoinsResponse);
            startActivity(intent);
            finish();
        });

        tvTheOrderNumber.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clip = ClipData.newPlainText("simple text", byBoinsResponse.getTransId());

            clipboard.setPrimaryClip(clip);
            ToastUtils.showShort(R.string.duplicated_to_clipboard);
        });

        tvCancelTheOrder.setOnClickListener(v -> {
            if (tvCancelTheOrder.getText().equals(getString(R.string.cancel_the_order))) {
                onBackDialog();
            }
        });

        llOtherUserId.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessDetailsActivity.class);
            intent.putExtra("ByBoinsResponse", byBoinsResponse);
            startActivity(intent);
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

    private void onBackDialog() {
        NiceDialog.init().setLayoutId(R.layout.dialog_remove_order).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_content, getString(R.string.remove_order_remind, defaultRenegeNumber, defaultRenegeNumber));
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_ok, v -> {
                    long timeStampSec = System.currentTimeMillis() / 1000;
                    timestamp = String.format("%010d", timeStampSec);

                    String secret = "nonce=" + timestamp + "&trans_id=" + byBoinsResponse.getTransId() + "&type=" + "2" + "&user_id=" + Constant.USERID + Constant.SECRET;
                    sign = Sha256.getSHA256(secret);

                    ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                            .removeOrder(byBoinsResponse.getTransId(), "2", timestamp, Constant.USERID)
                            .compose(RxSchedulers.otc())
                            .compose(RxSchedulers.ioObserver())
                            .compose(bindToLifecycle())
                            .subscribe(s -> finish(), PurchaseDetailsActivity.this::handleApiError);

                    dialog.dismiss();
                });
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    private void orderInfo() {
        long timeStampSec = System.currentTimeMillis() / 1000;
        timestamp = String.format("%010d", timeStampSec);
        String secret = "nonce=" + timestamp +
                "&trans_id=" + byBoinsResponse.getTransId() +
                "&user_id=" + Constant.USERID + Constant.SECRET;
        sign = Sha256.getSHA256(secret);

        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                .orderInfo(timestamp,
                        byBoinsResponse.getTransId(),
                        Constant.USERID,
                        byBoinsResponse.getPaymentType(),
                        byBoinsResponse.getCreateTime())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .subscribe(s -> {
                    getBoinsResponse(s);
                    isJump = false;
                    initData();
                }, PurchaseDetailsActivity.this::handleApiError);
    }

    private void getBoinsResponse(ByBoinsResponse byBoinsResponse) {
        this.byBoinsResponse = byBoinsResponse;
    }

}