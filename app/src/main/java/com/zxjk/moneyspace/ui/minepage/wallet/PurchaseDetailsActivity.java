package com.zxjk.moneyspace.ui.minepage.wallet;

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
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.ConfirmSellOrBuy;
import com.zxjk.moneyspace.bean.response.GetOrderInfoById;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.PayPsdInputView;
import com.zxjk.moneyspace.utils.MD5Utils;
import com.zxjk.moneyspace.utils.MMKVUtils;
import com.zxjk.moneyspace.utils.Sha256;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

@SuppressLint("CheckResult")
public class PurchaseDetailsActivity extends BaseActivity {
    private GetOrderInfoById getOrderInfoById;

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
    private LinearLayout llUserConfirmDeposit;
    private TextView tvBuyer1;
    private TextView tvBuyer2;
    private TextView tvAppeal;
    private TextView tvDispatchRelease;

    private String sign;
    private String timestamp;
    private String defaultRenegeNumber;
    private Boolean isJump = true;
    private String orderId;
    private ConfirmSellOrBuy confirmUserSellResponse;
    private String count;
    private QuickPopup byCoinsOrAmount;
    private PayPsdInputView editText;
    private String customerIdentity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_details);
        setTrasnferStatusBar(true);

        initView();

        initData();

    }

    private void initView() {
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
        llUserConfirmDeposit = findViewById(R.id.ll_user_confirm_deposit);
        tvBuyer1 = findViewById(R.id.tv_buyer1);
        tvBuyer2 = findViewById(R.id.tv_buyer2);
        tvAppeal = findViewById(R.id.tv_appeal);
        tvDispatchRelease = findViewById(R.id.tv_dispatch_release);

        findViewById(R.id.rl_back).setOnClickListener(v ->
                finish()
        );
    }

    private void initData() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(currentTime);
        timestamp = dataOne(formatter.format(date));

        if (isJump) {
            getOrderInfoById = (GetOrderInfoById) getIntent().getExtras().getSerializable("GetOrderInfoById");
            orderId = getIntent().getStringExtra("orderId");
            confirmUserSellResponse = (ConfirmSellOrBuy) getIntent().getExtras().getSerializable("ConfirmUserSellResponse");
            count = getIntent().getStringExtra("count");
            customerIdentity = getIntent().getStringExtra("customerIdentity");
        }

        if (!TextUtils.isEmpty(count)) {
            if (count.equals("0")) {
                tvPayment.setVisibility(View.VISIBLE);
            } else {
                llUserConfirmDeposit.setVisibility(View.VISIBLE);
            }
        }

        defaultRenegeNumber = MMKVUtils.getInstance().decodeString("DefaultRenegeNumber");

        if (TextUtils.isEmpty(getOrderInfoById.getBothOrderId())) {
            if (getOrderInfoById.getStatus().equals("7")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_accelerate, null), tvPaymentStatus, getString(R.string.complete_the_transaction));
                tvPayment.setVisibility(View.INVISIBLE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, "订单已完成");
                tvCancelTheOrder.setVisibility(View.GONE);
                tvBuyer1.setText("买家昵称");
                tvBuyer2.setText("买家实名");
                tvRemark.setText(getOrderInfoById.getRealName());
            } else if (getOrderInfoById.getStatus().equals("1")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                tvPayment.setVisibility(View.INVISIBLE);
                tv1.setVisibility(View.GONE);
                tvCancelTheOrder.setVisibility(View.GONE);
                tvBuyer1.setText("限量");
                tvBusinessName.setText(getOrderInfoById.getMinNum() + "-" + getOrderInfoById.getMaxNum());
                findViewById(R.id.ll_nick).setVisibility(View.GONE);
                if (getOrderInfoById.getBuyOrSell().equals("0")) {
                    tvBuyer1.setText("卖家昵称");
                    tvBuyer2.setText("卖家账号");
                } else {
                    tvBuyer1.setText("买家昵称");
                    tvBuyer2.setText("买家账号");
                }
                llUserConfirmDeposit.setVisibility(View.GONE);
            } else if (getOrderInfoById.getStatus().equals("9")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_wait, null), tvPaymentStatus, "交易中");
                tvPayment.setVisibility(View.INVISIBLE);

                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, "待付款");
            }
        } else {
            if (getOrderInfoById.getStatus().equals("3")) {

                tvRemark.setText(getOrderInfoById.getSellDuoduoId());

                if (getOrderInfoById.getBuyOrSell().equals("0")) {
                    tvBuyer1.setText("卖家昵称");
                    tvBuyer2.setText("卖家账号");
                    tvBusinessName.setText(getOrderInfoById.getSellNick());
                    setDrawables(getResources().getDrawable(R.drawable.ic_wait, null), tvPaymentStatus, getString(R.string.waiting_to_put_money2));
                    tvPayment.setVisibility(View.INVISIBLE);
                    setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.waiting_to_put_money));
                    tvPayment.setVisibility(View.GONE);
                    llUserConfirmDeposit.setVisibility(View.GONE);
                } else {
                    tvBuyer1.setText("买家昵称");
                    tvBuyer2.setText("买家实名");
                    tvBusinessName.setText(getOrderInfoById.getBuyNick());
                    setDrawables(getResources().getDrawable(R.drawable.ic_wait, null), tvPaymentStatus, "请放行");
                    tvPayment.setVisibility(View.INVISIBLE);
                    setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, "请查收对方付款");
                    llUserConfirmDeposit.setVisibility(View.VISIBLE);
                    tvDispatchRelease.setBackground(getResources().getDrawable(R.drawable.shape_4182f9_5, null));
                    tvDispatchRelease.setTextColor(Color.parseColor("#E7C39B"));
                    tvAppeal.setBackground(getResources().getDrawable(R.drawable.shape_appeal, null));
                    tvAppeal.setTextColor(Color.parseColor("#272E3F"));
                    tvDispatchRelease.setAlpha(1);
                    tvAppeal.setAlpha(1);
                }
                long l1 = (System.currentTimeMillis() - Long.parseLong(getOrderInfoById.getCreateTime())) / 1000;
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
                                setDrawables(getResources().getDrawable(R.drawable.ic_wait, null), tvPaymentStatus, "待卖家放币，已超时");
                                llCancelTheOrder.setVisibility(View.GONE);
                                tvPayment.setText(getString(R.string.the_complaint));
                            }
                        }, t -> {
                        });

            } else if (getOrderInfoById.getStatus().equals("0")) {
                if (count.equals("0")) {
                    setDrawables(getResources().getDrawable(R.drawable.ic_wait, null), tvPaymentStatus, "待买家付款");
                    tv1.setText(getString(R.string.fifteen_minutes));
                    tvCancelTheOrder.setText("取消订单");
                    tvCancelTheOrder.setVisibility(View.VISIBLE);
                    llUserConfirmDeposit.setVisibility(View.GONE);
                } else {
                    tvCancelTheOrder.setVisibility(View.GONE);
                    llUserConfirmDeposit.setVisibility(View.VISIBLE);
                    long l1 = (System.currentTimeMillis() - Long.parseLong(getOrderInfoById.getCreateTime())) / 1000;
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
                                    TextView tvAppeal = findViewById(R.id.tv_appeal);
                                    tvAppeal.setBackgroundColor(getResources().getColor(R.color.color_yellow_red, null));
                                    tvAppeal.setTextColor(getResources().getColor(R.color.black, null));
                                    TextView tvDispatchRelease = findViewById(R.id.tv_dispatch_release);
                                    tvDispatchRelease.setBackgroundColor(getResources().getColor(R.color.black, null));
                                    tvDispatchRelease.setTextColor(getResources().getColor(R.color.color_yellow_red, null));
                                    tvDispatchRelease.setAlpha(1);
                                    tvAppeal.setAlpha(1);
                                }
                            }, t -> {
                            });
                    setDrawables(getResources().getDrawable(R.drawable.ic_wait, null), tvPaymentStatus, "待付款");
                    if (getOrderInfoById.getBuyOrSell().equals("0")) {
                        tvBuyer1.setText("卖家昵称");
                        tvBuyer2.setText("卖家账号");
                        tvBusinessName.setText(getOrderInfoById.getRealName());
                    } else {
                        tvBuyer1.setText("买家昵称");
                        tvBuyer2.setText("买家实名");
                        tvBusinessName.setText(getOrderInfoById.getBuyNick());
                    }
                }
            } else if (getOrderInfoById.getStatus().equals("1")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                tvPayment.setVisibility(View.GONE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.no_payment));
                tvCancelTheOrder.setVisibility(View.VISIBLE);
                tvCancelTheOrder.setText(getString(R.string.buyer_cancel));
                llUserConfirmDeposit.setVisibility(View.GONE);
                if (getOrderInfoById.getBuyOrSell().equals("0")) {
                    tvBuyer1.setText("卖家昵称");
                    tvBuyer2.setText("卖家账号");
                    tvBusinessName.setText(getOrderInfoById.getSellNick());
                    tvRemark.setText(getOrderInfoById.getRealName());
                } else {
                    tvBuyer1.setText("买家昵称");
                    tvBuyer2.setText("买家实名");
                    tvBusinessName.setText(getOrderInfoById.getBuyNick());
                    tvRemark.setText(getOrderInfoById.getRealName());
                }
            } else if (getOrderInfoById.getStatus().equals("2")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_cancel_coin, null), tvPaymentStatus, getString(R.string.has_been_cancelled));
                tvPayment.setVisibility(View.GONE);
                setDrawables(getResources().getDrawable(R.drawable.ic_waiting, null), tv1, getString(R.string.no_payment));
                tvCancelTheOrder.setVisibility(View.VISIBLE);
                tvCancelTheOrder.setText("超时系统取消");
            } else if (getOrderInfoById.getStatus().equals("4")) {
                TextView tvAppeal = findViewById(R.id.tv_appeal);
                tvAppeal.setBackgroundColor(getResources().getColor(R.color.color_yellow_red, null));
                tvAppeal.setTextColor(getResources().getColor(R.color.black, null));
                tvAppeal.setAlpha(1);
                if (getOrderInfoById.getIsSystems().equals("0")) {
                    setDrawables(getResources().getDrawable(R.drawable.ic_wait, null), tvPaymentStatus, getString(R.string.coin_timeout2));
                    tvPayment.setText(R.string.the_complaint);
                    tvPayment.setVisibility(View.VISIBLE);
                    llCancelTheOrder.setVisibility(View.GONE);
                    llUserConfirmDeposit.setVisibility(View.GONE);
                } else {
                    setDrawables(getResources().getDrawable(R.drawable.ic_wait, null), tvPaymentStatus, "未放币");
                    tvPayment.setVisibility(View.GONE);
                    llCancelTheOrder.setVisibility(View.GONE);
                    llUserConfirmDeposit.setVisibility(View.VISIBLE);
                }

            } else if (getOrderInfoById.getStatus().equals("5")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_accelerate, null), tvPaymentStatus, "申诉完成");
                tvPayment.setVisibility(View.GONE);
                llCancelTheOrder.setVisibility(View.GONE);
            } else if (getOrderInfoById.getStatus().equals("6")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_wait, null), tvPaymentStatus, getString(R.string.in_the_complaint));
                tvPayment.setVisibility(View.GONE);
                llCancelTheOrder.setVisibility(View.GONE);
            } else if (getOrderInfoById.getStatus().equals("7")) {
                setDrawables(getResources().getDrawable(R.drawable.ic_accelerate, null), tvPaymentStatus, getString(R.string.complete_the_transaction));
                tvPayment.setVisibility(View.GONE);
                tv1.setVisibility(View.GONE);
                tvCancelTheOrder.setText("订单已完成");
                tvCancelTheOrder.setVisibility(View.GONE);
                llUserConfirmDeposit.setVisibility(View.GONE);
            }
        }

        tvTheOrderNumber.setText(getString(R.string.order_number, orderId));
        tvTotal.setText(getOrderInfoById.getMoney());
        tvPrice.setText(getOrderInfoById.getPrice() + "\u0020" + "CNY" + "/" + getOrderInfoById.getCurrency());
        tvNumber.setText(getOrderInfoById.getNumber() + "\u0020" + getOrderInfoById.getCurrency());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!TextUtils.isEmpty(getOrderInfoById.getCreateTime())) {
            String sd = sdf.format(new Date(Long.parseLong(getOrderInfoById.getCreateTime())));
            tvOrderTime.setText(sd);
        }


        if (!TextUtils.isEmpty(getOrderInfoById.getIsSystems())) {
            if (getOrderInfoById.getIsSystems().equals("1")) {
                findViewById(R.id.img_acceptor).setVisibility(View.VISIBLE);
            }
        }
        if (!getOrderInfoById.getStatus().equals("1")) {
            if (getOrderInfoById.getBuyOrSell().equals("0")) {
                tvBuyer1.setText("卖家昵称");
                tvBuyer2.setText("卖家账号");
                tvBusinessName.setText(getOrderInfoById.getSellNick());
                tvRemark.setText(getOrderInfoById.getSellDuoduoId());
            } else {
                tvBuyer1.setText("买家昵称");
                tvBuyer2.setText("买家实名");
                tvBusinessName.setText(getOrderInfoById.getBuyNick());
                tvRemark.setText(getOrderInfoById.getRealName());
            }
        }

        if (getOrderInfoById.getPayType().equals("1")) {
            setDrawables(getResources().getDrawable(R.drawable.ic_otc_wechat, null), tvPaymentType, getString(R.string.wechat_pay));
//            tvRemark.setText(getOrderInfoById.getWechatNick());
        } else if (getOrderInfoById.getPayType().equals("2")) {
            setDrawables(getResources().getDrawable(R.drawable.ic_otc_ali_pay, null), tvPaymentType, getString(R.string.pay_treasure));
//            tvRemark.setText(getOrderInfoById.getZhifubaoNumber());
        } else {
            setDrawables(getResources().getDrawable(R.drawable.ic_otc_bank_card, null), tvPaymentType, getString(R.string.bank_card));
//            tvRemark.setText(getOrderInfoById.getPayNumber());
        }

//        tvBusinessName.setText(getOrderInfoById.getSellNick());

        tvPayment.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderDetailsActivity.class);

            intent.putExtra("GetOrderInfoById", getOrderInfoById);
            intent.putExtra("customerIdentity", customerIdentity);
            startActivity(intent);
            finish();
        });

        tvTheOrderNumber.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clip = ClipData.newPlainText("simple text", getOrderInfoById.getBothOrderId());

            clipboard.setPrimaryClip(clip);
            ToastUtils.showShort(R.string.duplicated_to_clipboard);
        });

        tvCancelTheOrder.setOnClickListener(v -> {
            if (tvCancelTheOrder.getText().equals(getString(R.string.cancel_the_order))) {
                onBackDialog();
            }
        });

        tvAppeal.setOnClickListener(v -> {
            Intent intent;
            if (getOrderInfoById.getBuyOrSell().equals("1") && getOrderInfoById.getStatus().equals("3") ||getOrderInfoById.getStatus().equals("4")) {

                intent = new Intent(this, TheAppealActivity.class);
                intent.putExtra("GetOrderInfoById", getOrderInfoById);
                startActivity(intent);

                finish();
            }
        });

        tvDispatchRelease.setOnClickListener(v -> {
            if (getOrderInfoById.getStatus().equals("3")) {
                popupPayView();
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

    private void onBackDialog() {
        NiceDialog.init().setLayoutId(R.layout.dialog_remove_order).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_content, getString(R.string.remove_order_remind, Constant.defaultRenegeNumber, Constant.defaultRenegeNumber));
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_ok, v -> {

                    String secret = "bothOrderId=" + getOrderInfoById.getBothOrderId() +
                            "&nonce=" + timestamp + Constant.SECRET;
                    sign = Sha256.getSHA256(secret);

                    ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                            .userCancelledBuy(getOrderInfoById.getBothOrderId(), timestamp)
                            .compose(RxSchedulers.otc())
                            .compose(RxSchedulers.ioObserver())
                            .compose(bindToLifecycle())
                            .subscribe(data -> {

                                ToastUtils.showShort(data);

                            }, PurchaseDetailsActivity.this::handleApiError);
                    finish();
                    dialog.dismiss();
                });
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    public String dataOne(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }


//    private void orderInfo() {
//        long timeStampSec = System.currentTimeMillis() / 1000;
//        timestamp = String.format("%010d", timeStampSec);
//        String secret = "nonce=" + timestamp +
//                "&trans_id=" + getOrderInfoById.getTransId() +
//                "&user_id=" + Constant.USERID + Constant.SECRET;
//        sign = Sha256.getSHA256(secret);
//
//        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
//                .getOrderInfoById(timestamp,
//                        getOrderInfoById.getTransId(),
//                        Constant.USERID,
//                        getOrderInfoById.getPaymentType(),
//                        getOrderInfoById.getCreateTime())
//                .compose(bindToLifecycle())
//                .compose(RxSchedulers.normalTrans())
//                .compose(RxSchedulers.ioObserver())
//                .subscribe(s -> {
//                    getBoinsResponse(s);
//                    isJump = false;
//                    initData();
//                }, PurchaseDetailsActivity.this::handleApiError);
//    }

    private void getBoinsResponse(GetOrderInfoById byBoinsResponse) {
        this.getOrderInfoById = byBoinsResponse;
    }

    private void popupPayView() {

        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(350);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);
        byCoinsOrAmount = QuickPopupBuilder.with(this)
                .contentView(R.layout.popup_pay_view)
                .config(new QuickPopupConfig()
                        .withShowAnimation(showAnimation)
                        .withDismissAnimation(dismissAnimation)
                ).show();
        editText = byCoinsOrAmount.findViewById(R.id.m_set_payment_pwd_edit);
        byCoinsOrAmount.setAutoShowInputMethod(editText, true);
        showSoftInputFromWindow(editText);
        editText.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {

            }

            @Override
            public void onEqual(String psd) {

            }

            @Override
            public void inputFinished(String inputPsd) {
                if (getOrderInfoById.getIsSystems().equals("0")) {
                    String secret = "bothOrderId=" + getOrderInfoById.getBothOrderId() +
                            "&nonce=" + timestamp +
                            "&payPwd=" + MD5Utils.getMD5(inputPsd) + Constant.SECRET;
                    sign = Sha256.getSHA256(secret);

                    ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                            .userConfirmDeposit(getOrderInfoById.getBothOrderId(), timestamp, MD5Utils.getMD5(inputPsd))
                            .compose(RxSchedulers.otc())
                            .compose(RxSchedulers.ioObserver())
                            .compose(bindToLifecycle())
                            .subscribe(data -> {
                                ToastUtils.showShort("放行成功");
                                finish();
                            }, PurchaseDetailsActivity.this::handleApiError);
                } else {
                    String secret = "bothOrderId=" + getOrderInfoById.getBothOrderId() +
                            "&nonce=" + timestamp +
                            "&payPwd=" + MD5Utils.getMD5(inputPsd) + Constant.SECRET;
                    sign = Sha256.getSHA256(secret);

                    ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                            .acceptorConfirmDeposit(getOrderInfoById.getBothOrderId(), timestamp, MD5Utils.getMD5(inputPsd))
                            .compose(RxSchedulers.otc())
                            .compose(RxSchedulers.ioObserver())
                            .compose(bindToLifecycle())
                            .subscribe(data -> {
                                ToastUtils.showShort("放行成功");
                                finish();
                            }, PurchaseDetailsActivity.this::handleApiError);
                }

            }
        });


        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editText, 0);
                           }
                       },
                150);
        byCoinsOrAmount.findViewById(R.id.tv_exit).setOnClickListener(v -> {

            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            byCoinsOrAmount.dismiss();
        });
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        });
    }

    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editText, 0);
                           }
                       },
                100);
    }

}