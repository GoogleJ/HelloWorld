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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

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
    private TextView tvPayment;
    private ScrollView scrollView;
    private RelativeLayout rlTitleBar;
    private TextView tvTitle;
    private CardView cardView1;
    private TextView tvCurrency;
    private TextView tvAppealRemark;
    private LinearLayout llAppealRemark;
    private TextView tvPaymentStatus2;
    private TextView tvVendorName;
    private TextView tvMerchantAccount;
    private TextView tvSubBranchId;

    private String otherOrderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_details);

        initView();

        initData();
    }

    private void initView() {
        tvVendorName = findViewById(R.id.tv_vendor_name);
        tvMerchantAccount = findViewById(R.id.tv_merchant_account);
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
        tvPayment = findViewById(R.id.tv_payment);
        scrollView = findViewById(R.id.scrollview);
        rlTitleBar = findViewById(R.id.rl_title_bar);
        tvTitle = findViewById(R.id.tv_title);
        cardView1 = findViewById(R.id.card_view1);
        tvCurrency = findViewById(R.id.tv_currency);
        tvPaymentStatus2 = findViewById(R.id.tv_payment_status2);
        llAppealRemark = findViewById(R.id.ll_appeal_remark);
        tvAppealRemark = findViewById(R.id.tv_appeal_remark);
        tvSubBranchId = findViewById(R.id.tv_sub_branch_id);

        findViewById(R.id.rl_back).setOnClickListener(v ->
                finish()
        );

    }

    private void initData() {
        otherOrderId = getIntent().getStringExtra("otherOrderId");

        linkCoinOrdersOrderDetails(otherOrderId);

        findViewById(R.id.tv_cancel_the_order).setOnClickListener(v -> {
            Intent intent;
            if ("BUY".equals(byBoinsResponse.getType()) && 1 == byBoinsResponse.getShowDispute()
                    || "SELL".equals(byBoinsResponse.getType()) && "PAYED".equals(byBoinsResponse.getOrderStatus()) && 1 == byBoinsResponse.getShowDispute()) {
                intent = new Intent(this, TheAppealActivity.class);
                intent.putExtra("ByBoinsResponse", byBoinsResponse);
                startActivity(intent);
                finish();
            }
        });
        tvTheOrderNumber.setOnClickListener(v -> {
            copy(byBoinsResponse.getOtherOrderId());
        });

        findViewById(R.id.tv_copy_business_name).setOnClickListener(v -> {
            copy(byBoinsResponse.getSellerNickName());
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
                        setDrawables(getResources().getDrawable(R.drawable.pay_treasure, null), tvPaymentType, getString(R.string.alipay_pay));
                    } else if ("WEIXIN".equals(data.getPayType())) {
                        setDrawables(getResources().getDrawable(R.drawable.wechat, null), tvPaymentType, getString(R.string.wechat_pay));
                    } else {
                        setDrawables(getResources().getDrawable(R.drawable.bank_card, null), tvPaymentType, getString(R.string.bank_card_payment));
                    }

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
                        if ("PAYED".equals(data.getOrderStatus())) {//待卖家放币状态
                            setDrawables(getResources().getDrawable(R.drawable.ic_waiting_coin, null), tvPaymentStatus, "待商家放币");
                            tvPayment.setVisibility(View.VISIBLE);
                            tvPayment.setTextColor(Color.parseColor("#909399"));
                            findViewById(R.id.img_payment).setVisibility(View.VISIBLE);
                            findViewById(R.id.ll_payment).setBackground(getResources().getDrawable(R.drawable.shape_f2f3f6_5, null));
                            tvPaymentStatus2.setText(getString(R.string.waiting_to_put_money));
                            findViewById(R.id.ll2).setVisibility(View.VISIBLE);

                            long l1 = (Long.parseLong(data.getNow()) - Long.parseLong(data.getPayMoneyTime())) / 1000;
                            long total = ((Integer.valueOf(data.getDisputeMinute())*60 - l1) <= 0 ? 0 : (Integer.valueOf(data.getDisputeMinute())*60 - l1));
                            Observable.interval(0, 1, TimeUnit.SECONDS)
                                    .take(total)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .compose(bindToLifecycle())
                                    .subscribe(l -> {
                                        long minute = (total - l) / 60;
                                        long second = ((total - l) % 60);
                                        String time = minute + ":" + (second == 0 ? "00" : (second < 10 ? ("0" + second) : second));
                                        tvPayment.setText(time + "可申诉");
                                        if (total == 0 || l == total - 1) {
                                            finish();
                                        }
                                    }, t -> {
                                    });
                        } else if ("DISPUTE".equals(data.getOrderStatus())) {
                            if (1 == data.getAppealType()) {
                                tvPaymentStatus2.setText(getString(R.string.in_the_complaint));
                            } else {
                                tvPaymentStatus2.setText("商家申诉中");
                            }
                            findViewById(R.id.ll_payment).setVisibility(View.GONE);
                            setDrawables(getResources().getDrawable(R.drawable.ic_the_complaint, null), tvPaymentStatus, "申诉中");
                            llAppealRemark.setVisibility(View.VISIBLE);
                            tvAppealRemark.setText(data.getAppealRemark());
                        } else if ("TIMEOUT".equals(data.getOrderStatus())) {
                            setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, "超时自动取消订单");
                            tvPaymentStatus2.setText("已取消");
                        } else if ("REFUND".equals(data.getOrderStatus())) {
                            setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, "商家已退款，订单取消");
                            tvPaymentStatus2.setText("已取消");
                        } else if ("CANCELED".equals(data.getOrderStatus())) {
                            setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, "手动取消订单");
                            tvPaymentStatus2.setText("已取消");
                        } else if ("FINISHED".equals(data.getOrderStatus())) {
                            setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, "已放币");
                            tvPaymentStatus2.setText("已完成");
                        } else if ("FORCEFINISHED".equals(data.getOrderStatus())) {
                            if (1 == data.getAppealType()) {
                                setDrawables(getResources().getDrawable(R.drawable.ic_the_complaint, null), tvPaymentStatus, "申诉成功，已放币");
                                tvPaymentStatus2.setText("已完成");
                            } else {
                                setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, "商家申诉失败，已放币");
                                tvPaymentStatus2.setText("已完成");
                            }
                        } else if ("FORCECANCEL".equals(data.getOrderStatus())) {
                            if (1 == data.getAppealType()) {
                                setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, "申诉失败，取消订单");
                                tvPaymentStatus2.setText("已取消");
                            } else {
                                setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, "商家申诉成功，订单取消");
                                tvPaymentStatus2.setText("已取消");
                            }
                        }

                        if ("ALIPAY".equals(data.getPayType())) {
                            tvBusinessName.setText(data.getAlipayName());
                            tvOtherUserId.setText(data.getAlipayId());
                        } else if ("WEIXIN".equals(data.getPayType())) {
                            tvBusinessName.setText(data.getWeixinName());
                            tvOtherUserId.setText(data.getWeixinId());
                        } else {
                            findViewById(R.id.ll_sub_branch).setVisibility(View.VISIBLE);
                            tvMerchantAccount.setText("银行卡号");
                            tvBusinessName.setText(data.getCardUserName());
                            tvOtherUserId.setText(data.getCardCode());
                            tvSubBranchId.setText(data.getCardAddress());
                        }

                        if (1 == data.getShowDispute()) {
                            findViewById(R.id.ll_payment).setVisibility(View.GONE);
                            findViewById(R.id.tv_cancel_the_order).setBackground(getResources().getDrawable(R.drawable.shape_4182f9_5, null));
                            setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, "商家未放币，已超时");
                            tvCancelTheOrder.setVisibility(View.VISIBLE);
                            tvCancelTheOrder.setTextColor(Color.parseColor("#FFFFFF"));
                            findViewById(R.id.ll2).setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvTitle.setText("出售详情");
                        tvBusinessName.setText(data.getSellerNickName());
                        tvVendorName.setText("买家昵称");
                        findViewById(R.id.ll_other_user_id).setVisibility(View.GONE);
                        tvPayment.setOnClickListener(v -> {
                            if ("PAYED".equals(data.getOrderStatus()) || "DISPUTE".equals(data.getOrderStatus())) {
                                NiceDialog.init().setLayoutId(R.layout.dialog_remove_order)
                                        .setConvertListener(new ViewConvertListener() {
                                            @Override
                                            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                                                holder.setText(R.id.tv_title, "确认放行?");
                                                holder.setText(R.id.tv_content, "请务必登录网上银行或第三方支付账号确认收到该笔款项");
                                                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                                                holder.setOnClickListener(R.id.tv_ok, v -> {
                                                    dialog.dismiss();
                                                    new NewPayBoard(PurchaseDetailsActivity.this)
                                                            .show(pwd -> payCoin(MD5Utils.getMD5(pwd)));
                                                });
                                            }
                                        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
                            }
                        });

                        if ("UNFINISHED".equals(data.getOrderStatus())) {//待付款状态
                            tvPaymentStatus2.setText("待付款");
                            tvVendorName.setText("收款姓名");
                            tvMerchantAccount.setText("收款人账号");
                            findViewById(R.id.tv_copy_business_name).setVisibility(View.VISIBLE);
                            setDrawables(getResources().getDrawable(R.drawable.ic_waiting_coin, null), tvPaymentStatus, "请等待对方付款");
                            findViewById(R.id.ll2).setVisibility(View.VISIBLE);
                            tvPayment.setText("放行" + data.getCoinSymbol());
                            tvPayment.setTextColor(Color.parseColor("#909399"));
                            findViewById(R.id.ll_payment).setBackground(getResources().getDrawable(R.drawable.shape_f2f3f6_5, null));
                            tvCancelTheOrder.setBackground(getResources().getDrawable(R.drawable.shape_f2f3f6_5, null));
                            tvCancelTheOrder.setVisibility(View.VISIBLE);
                            tvCancelTheOrder.setTextColor(Color.parseColor("#909399"));
                            long l1 = (Long.parseLong(data.getNow()) - Long.parseLong(data.getCreateTime())) / 1000;
                            long total = ((Integer.valueOf(data.getDisputeMinute())*60 - l1) <= 0 ? 0 : (Integer.valueOf(data.getDisputeMinute())*60 - l1));
                            Observable.interval(0, 1, TimeUnit.SECONDS)
                                    .take(total)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .compose(bindToLifecycle())
                                    .subscribe(l -> {
                                        long minute = (total - l) / 60;
                                        long second = ((total - l) % 60);
                                        String time = minute + ":" + (second == 0 ? "00" : (second < 10 ? ("0" + second) : second));
                                        tvPaymentStatus.setText("请等待对方付款\u0020" + time);
                                        if (total == 0 || l == total - 1) {
                                            finish();
                                        }
                                    }, t -> {
                                    });
                        } else if ("PAYED".equals(data.getOrderStatus())) {//待放币状态
                            findViewById(R.id.tv_copy_business_name).setVisibility(View.VISIBLE);
                            setDrawables(getResources().getDrawable(R.drawable.ic_waiting_coin, null), tvPaymentStatus, "请放币，买家已完成付款");
                            findViewById(R.id.tv1).setVisibility(View.VISIBLE);
                            findViewById(R.id.ll2).setVisibility(View.VISIBLE);
                            tvPaymentStatus2.setText("待放币");
                            tvPayment.setText("放行" + data.getCoinSymbol());
                            tvPayment.setTextColor(Color.parseColor("#FFFFFF"));
                            findViewById(R.id.ll_payment).setBackground(getResources().getDrawable(R.drawable.shape_4182f9_5, null));
                            tvCancelTheOrder.setBackground(getResources().getDrawable(R.drawable.shape_border_f2f3f6_5, null));
                            tvCancelTheOrder.setVisibility(View.VISIBLE);
                            tvCancelTheOrder.setTextColor(Color.parseColor("#272E3F"));
                        } else if ("DISPUTE".equals(data.getOrderStatus())) {
                            findViewById(R.id.ll2).setVisibility(View.VISIBLE);
                            llAppealRemark.setVisibility(View.VISIBLE);
                            tvAppealRemark.setText(data.getAppealRemark());
                            tvPaymentStatus2.setText(getString(R.string.in_the_complaint));
                            setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, "申诉中");
                            tvPayment.setText("放行" + data.getCoinSymbol());
                            tvPayment.setTextColor(Color.parseColor("#FFFFFF"));
                            findViewById(R.id.ll_payment).setBackground(getResources().getDrawable(R.drawable.shape_4182f9_5, null));
                        } else if ("TIMEOUT".equals(data.getOrderStatus())) {
                            setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, "超时自动取消订单");
                            tvPaymentStatus2.setText("已取消");
                        } else if ("FORCEFINISHED".equals(data.getOrderStatus())) {
                            setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, "申诉失败系统自动放币");
                            tvPaymentStatus2.setText("已完成");
                            llAppealRemark.setVisibility(View.VISIBLE);
                            tvAppealRemark.setText(data.getAppealRemark());
                        } else if ("FINISHED".equals(data.getOrderStatus())) {
                            if (1 == data.getAutoPayCoin()) {
                                setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, "超时自动放币");
                                tvPaymentStatus2.setText("已完成");
                                findViewById(R.id.ll2).setVisibility(View.VISIBLE);
                                tvCancelTheOrder.setBackground(getResources().getDrawable(R.drawable.shape_4182f9_5, null));
                                tvCancelTheOrder.setVisibility(View.VISIBLE);
                                tvCancelTheOrder.setTextColor(Color.parseColor("#FFFFFF"));
                                findViewById(R.id.ll_payment).setVisibility(View.GONE);
                            } else {
                                setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, "已放币");
                                tvPaymentStatus2.setText("已完成");
                            }
                        } else if ("FORCECANCEL".equals(data.getOrderStatus())) {
                            if (1 == data.getAppealType()) {
                                setDrawables(getResources().getDrawable(R.drawable.ic_complete_coin, null), tvPaymentStatus, "申诉成功订单取消");
                                tvPaymentStatus2.setText("已取消");
                                llAppealRemark.setVisibility(View.VISIBLE);
                                tvAppealRemark.setText(data.getAppealRemark());
                            }
                        }
                    }
                }, this::handleApiError);
    }

    private void setDrawables(Drawable drawable, TextView textView, String text) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                    .getMinimumHeight());// 设置边界
        }
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setCompoundDrawablePadding(8);
    }

    private void payCoin(String pwd) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .payCoin(byBoinsResponse.getOtherOrderId(), pwd)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .subscribe(s -> {
                    ToastUtils.showShort("放币成功");
                    finish();
                }, this::handleApiError);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        linkCoinOrdersOrderDetails(otherOrderId);
    }


    private void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("simple text", text);

        clipboard.setPrimaryClip(clip);
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
    }
}