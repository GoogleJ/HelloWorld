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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetLinkCoinOrdersOrderDetails;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.MD5Utils;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

@SuppressLint("CheckResult")
public class PurchaseDetailsActivity extends BaseActivity {
    private GetLinkCoinOrdersOrderDetails byBoinsResponse;

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
    private TextView tvTitle;
    private CardView cardView1;
    private TextView tvCurrency;
    private ImageView imgAccountType;
    private TextView tvAccountNumber;
    private LinearLayout llSell;
    private TextView tvDispute;
    private TextView tvPayCoin;
    private TextView tv2;
    private TextView tvAppealRemark;
    private LinearLayout llAppealRemark;

    private String sign;
    private String timestamp;
    private String defaultRenegeNumber;
    private String otherOrderId;


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
        tvTitle = findViewById(R.id.tv_title);
        cardView1 = findViewById(R.id.card_view1);
        imgAccountType = findViewById(R.id.img_account_type);
        tvAccountNumber = findViewById(R.id.tv_account_number);
        tvCurrency = findViewById(R.id.tv_currency);
        llSell = findViewById(R.id.ll_sell);
        tvDispute = findViewById(R.id.tv_dispute);
        tvPayCoin = findViewById(R.id.tv_pay_coin);
        tv2 = findViewById(R.id.tv2);
        tvAppealRemark = findViewById(R.id.tv_appeal_remark);
        llAppealRemark = findViewById(R.id.ll_appeal_remark);

        findViewById(R.id.rl_back).setOnClickListener(v ->
                finish()
        );

    }

    private void initData() {
        otherOrderId = getIntent().getStringExtra("otherOrderId");

        linkCoinOrdersOrderDetails(otherOrderId);

        defaultRenegeNumber = MMKVUtils.getInstance().decodeString("DefaultRenegeNumber");

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

            ClipData clip = ClipData.newPlainText("simple text", byBoinsResponse.getOtherOrderId());

            clipboard.setPrimaryClip(clip);
            ToastUtils.showShort(R.string.duplicated_to_clipboard);
        });

        tvCancelTheOrder.setOnClickListener(v -> {
            onBackDialog();
        });
    }

    private void linkCoinOrdersOrderDetails(String otherOrderId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .linkCoinOrdersOrderDetails(otherOrderId)
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(data -> {
                    this.byBoinsResponse = data;
                    if ("ALIPAY".equals(data.getPayType())) {
                        imgAccountType.setBackground(getResources().getDrawable(R.drawable.pay_treasure, null));
                        tvAccountNumber.setText(data.getAlipayId());
                        setDrawables(getResources().getDrawable(R.drawable.pay_treasure, null), tvPaymentType, getString(R.string.alipay_pay));
                    } else if ("WEIXIN".equals(data.getPayType())) {
                        imgAccountType.setBackground(getResources().getDrawable(R.drawable.wechat, null));
                        tvAccountNumber.setText(data.getWeixinId());
                        setDrawables(getResources().getDrawable(R.drawable.wechat, null), tvPaymentType, getString(R.string.wechat_pay));
                    } else {
                        imgAccountType.setBackground(getResources().getDrawable(R.drawable.bank_card2, null));
                        tvAccountNumber.setText(data.getCardCode());
                        setDrawables(getResources().getDrawable(R.drawable.bank_card2, null), tvPaymentType, getString(R.string.bank_card_payment));
                    }

//                    tvRemark.setText(byBoinsResponse.getRemark());

                    tvTheOrderNumber.setText(getString(R.string.order_number, data.getOtherOrderId()));

                    tvTotal.setText(data.getTotal());

                    tvCurrency.setText(data.getCurrency());

                    tvPrice.setText(data.getPrice() + "\u0020" + data.getCurrency() + "/" + data.getCoinSymbol());

                    tvNumber.setText(data.getCoinAmount() + "\u0020" + data.getCoinSymbol());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    if (!TextUtils.isEmpty(data.getCreateTime())) {
                        String sd = sdf.format(new Date(Long.parseLong(data.getCreateTime())));
                        tvOrderTime.setText(sd);
                    }

                    if ("BUY".equals(data.getType())) {
                        tvTitle.setText(R.string.purchase_details);
                        cardView1.setVisibility(View.VISIBLE);
                        if ("UNFINISHED".equals(data.getOrderStatus())) {//待付款状态
                            llCancelTheOrder.setVisibility(View.GONE);
                        } else if ("PAYED".equals(data.getOrderStatus())) {//待卖家放币状态
                            findViewById(R.id.ll2).setVisibility(View.GONE);
                            setDrawables(getResources().getDrawable(R.drawable.ic_waiting_coin, null), tvPaymentStatus, getString(R.string.waiting_to_put_money2));
                            setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.waiting_to_put_money));
                            tv2.setVisibility(View.VISIBLE);

                            long l1 = (System.currentTimeMillis() - Long.parseLong(data.getPayMoneyTime())) / 1000;

                            long total = (900 - l1) <= 0 ? 0 : (900 - l1);
                            Observable.interval(0, 1, TimeUnit.SECONDS)
                                    .take(total)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .compose(bindToLifecycle())
                                    .subscribe(l -> {
                                        long minute = (total - l) / 60;
                                        long second = ((total - l) % 60) - 1;
                                        tv2.setText(minute + ":" + (second == 0 ? "00" : (second < 10 ? ("0" + second) : second)));
                                        if (total == 0 || l == total - 1) {
                                            linkCoinOrdersOrderDetails(otherOrderId);
                                        }
                                    }, t -> {
                                    });
                        } else if ("DISPUTE".equals(data.getOrderStatus())) {
                            findViewById(R.id.ll2).setVisibility(View.GONE);
                        }

                        if ("ALIPAY".equals(data.getPayType())) {
                            tvBusinessName.setText(data.getAlipayName());
                            tvOtherUserId.setText(data.getAlipayId());
                        } else if ("WEIXIN".equals(data.getPayType())) {
                            tvBusinessName.setText(data.getWeixinName());
                            tvOtherUserId.setText(data.getWeixinId());
                        } else {
                            tvBusinessName.setText(data.getCardUserName());
                            tvOtherUserId.setText(data.getCardCode());
                        }

                        if (1 == data.getShowDispute()) {
                            tvCancelTheOrder.setVisibility(View.GONE);
                            tvPayment.setText("申诉");
                            setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, "商家未放币，已超时");
                            tvPayment.setVisibility(View.VISIBLE);
                            llCancelTheOrder.setVisibility(View.GONE);
                            findViewById(R.id.ll2).setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvTitle.setText("出售详情");
                        llSell.setVisibility(View.VISIBLE);
                        tvDispute.setOnClickListener(v -> {
                            if (1 == data.getShowDispute()) {
                                Intent intent = new Intent(this, TheAppealActivity.class);
                                intent.putExtra("ByBoinsResponse", data);
                                startActivity(intent);
                            }
                        });

                        tvPayCoin.setOnClickListener(v -> {
                            NiceDialog.init().setLayoutId(R.layout.dialog_remove_order).setConvertListener(new ViewConvertListener() {
                                @Override
                                protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                                    holder.setText(R.id.tv_title, "确认放行?");
                                    holder.setText(R.id.tv_content, "请务必登录网上银行或第三方支付账号\n" +
                                            "确认收到该笔款项");
                                    holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                                    holder.setOnClickListener(R.id.tv_ok, v -> {
                                        if ("PAYED".equals(data.getOrderStatus())) {
                                            new NewPayBoard(PurchaseDetailsActivity.this)
                                                    .show(pwd -> payCoin(MD5Utils.getMD5(pwd)));
                                        }
                                        dialog.dismiss();
                                    });
                                }
                            }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());

                        });

                        if ("UNFINISHED".equals(data.getOrderStatus())) {//待付款状态
                            tvPayCoin.setTextColor(Color.parseColor("#909399"));
                            tvPayCoin.setBackground(getResources().getDrawable(R.drawable.shape_border_f2f3f6_5, null));
                            tvPayCoin.getBackground().setAlpha(100);
                            tv1.setText("预计15分钟内可收到买家付款");
                        } else if ("PAYED".equals(data.getOrderStatus())) {//待放币状态
                            setDrawables(getResources().getDrawable(R.drawable.ic_waiting_coin, null), tvPaymentStatus, "待放币");
                            tv1.setText("请查收对方付款 ");
                            tvDispute.setBackground(getResources().getDrawable(R.drawable.shape_border_f2f3f6_5, null));
                            tvDispute.setTextColor(getResources().getColor(R.color.black, null));
                            tvPayCoin.setBackground(getResources().getDrawable(R.drawable.shape_4182f9_5, null));
                            tvPayCoin.setTextColor(getResources().getColor(R.color.white, null));
                        } else if ("DISPUTE".equals(data.getOrderStatus())) {
                            llAppealRemark.setVisibility(View.VISIBLE);
                            tvAppealRemark.setText(data.getAppealRemark());
                            setDrawables(getResources().getDrawable(R.drawable.ic_the_complaint, null), tvPaymentStatus, getString(R.string.in_the_complaint));
                            tv1.setText("订单申诉中");
                        } else if ("TIMEOUT".equals(data.getOrderStatus())) {
                            tv1.setText("买家未付款");
                        }
                        if (0 == data.getShowDispute()) {
                            tvDispute.getBackground().setAlpha(100);
                            tvDispute.setTextColor(Color.parseColor("#909399"));
                        }
                    }

                    if ("UNFINISHED".equals(data.getOrderStatus())) {
                        setDrawables(getResources().getDrawable(R.drawable.for_the_payment, null), tvPaymentStatus, getString(R.string.waiting_for_payment));
                    } else if ("TIMEOUT".equals(data.getOrderStatus())) {
                        setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                        tvPayment.setVisibility(View.INVISIBLE);
                        setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.no_payment));
                        tv2.setVisibility(View.VISIBLE);
                        tv2.setText(R.string.already_timeout);
                        findViewById(R.id.ll2).setVisibility(View.GONE);
                        findViewById(R.id.ll1).setVisibility(View.GONE);
                    } else if ("CANCELED".equals(data.getOrderStatus())) {
                        setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                        tvPayment.setVisibility(View.INVISIBLE);
                        setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.buyer_cancel));
                        tv2.setVisibility(View.GONE);
                        findViewById(R.id.ll2).setVisibility(View.GONE);
                        findViewById(R.id.ll1).setVisibility(View.GONE);
                    } else if ("REFUND".equals(data.getOrderStatus())) {
                        setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                        tvPayment.setVisibility(View.INVISIBLE);
                        setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.seller_to_cancel));
                        tv2.setVisibility(View.GONE);
                        findViewById(R.id.ll2).setVisibility(View.GONE);
                        findViewById(R.id.ll1).setVisibility(View.GONE);
                    } else if ("FORCECANCEL".equals(data.getOrderStatus())) {
                        setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                        tvPayment.setVisibility(View.INVISIBLE);
                        setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, "强制取消");
                        tv2.setVisibility(View.GONE);
                        findViewById(R.id.ll2).setVisibility(View.GONE);
                        findViewById(R.id.ll1).setVisibility(View.GONE);
                    } else if ("FINISHED".equals(data.getOrderStatus()) || "FORCEFINISHED".equals(data.getOrderStatus())) {
                        setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, getString(R.string.complete_the_transaction));
                        tvPayment.setVisibility(View.INVISIBLE);
                        tv1.setText("订单已完成");
                        findViewById(R.id.ll2).setVisibility(View.GONE);
                        findViewById(R.id.ll1).setVisibility(View.GONE);
                    } else if ("DISPUTE".equals(data.getOrderStatus())) {
                        setDrawables(getResources().getDrawable(R.drawable.ic_the_complaint, null), tvPaymentStatus, getString(R.string.in_the_complaint));
                        tvPayment.setVisibility(View.GONE);
                        llCancelTheOrder.setVisibility(View.GONE);
                        findViewById(R.id.ll2).setVisibility(View.GONE);
                        findViewById(R.id.ll1).setVisibility(View.GONE);
                    }
                }, this::handleApiError);
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
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .ordersCancel(byBoinsResponse.getOtherOrderId())
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.normalTrans())
                            .compose(RxSchedulers.ioObserver())
                            .subscribe(s -> {
                                finish();
                            });
                    dialog.dismiss();
                });
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }


    private void payCoin(String pwd) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .payCoin(byBoinsResponse.getOtherOrderId(), pwd)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .subscribe(s -> {
                    ToastUtils.showShort("放币成功");
                }, this::handleApiError);
    }
}