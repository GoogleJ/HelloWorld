package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetLinkCoinOrdersOrderDetails;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.BuyCoinDialog;
import com.zxjk.duoduo.utils.AliPayUtils;
import com.zxjk.duoduo.utils.ClickUtils;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

@SuppressLint("CheckResult")
public class BuyCoinPaymentActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgSpinningRound;
    private TextView tvTotal;
    private TextView tvTime;
    private FrameLayout flSafetyGuarantee;
    private TextView tvModePayment;
    private ImageView imgQRCode;
    private TextView tvPayeeName;
    private TextView tvPayeeId;
    private TextView tvOtherOrderId;
    private TextView tvCoinAmount;
    private TextView tvPrice;
    private TextView tvOrderTime;
    private TextView tvCancelTheOrder;
    private LinearLayout llPayment;
    private TextView tvPaymentTime;
    private TextView tvOpenAlipay;
    private QuickPopup quickPopup;
    private TextView tvSubBranchId;
    private TextView tvPayee;

    private GetLinkCoinOrdersOrderDetails byBoinsResponse;
    private String otherOrderId;
    private String defaultRenegeNumber;
    private BuyCoinDialog buyCoinDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coin_payment);

        initView();

        initData();
    }

    private void initView() {
        imgSpinningRound = findViewById(R.id.img_spinning_round);
        tvTotal = findViewById(R.id.tv_total);
        tvTime = findViewById(R.id.tv_time);
        flSafetyGuarantee = findViewById(R.id.fl_safety_guarantee);
        tvModePayment = findViewById(R.id.tv_mode_payment);
        imgQRCode = findViewById(R.id.img_QR_code);
        tvPayeeName = findViewById(R.id.tv_payee_name);
        tvPayeeId = findViewById(R.id.tv_payee_id);
        tvOtherOrderId = findViewById(R.id.tv_other_order_id);
        tvCoinAmount = findViewById(R.id.tv_coin_amount);
        tvPrice = findViewById(R.id.tv_price);
        tvOrderTime = findViewById(R.id.tv_order_time);
        tvCancelTheOrder = findViewById(R.id.tv_cancel_the_order);
        llPayment = findViewById(R.id.ll_payment);
        tvPaymentTime = findViewById(R.id.tv_payment_time);
        tvOpenAlipay = findViewById(R.id.tv_open_alipay);
        tvSubBranchId = findViewById(R.id.tv_sub_branch_id);
        tvPayee = findViewById(R.id.tv_payee);

        otherOrderId = getIntent().getStringExtra("otherOrderId");
        defaultRenegeNumber = MMKVUtils.getInstance().decodeString("DefaultRenegeNumber");
        findViewById(R.id.rl_back).setOnClickListener(v -> onBackDialog());

        tvTotal.setOnClickListener(this);
        findViewById(R.id.tv_copy_payee_name).setOnClickListener(this);
        findViewById(R.id.tv_copy_payee_id).setOnClickListener(this);
        findViewById(R.id.tv_copy_other_order_id).setOnClickListener(this);
        findViewById(R.id.tv_copy_coin_amount).setOnClickListener(this);
        findViewById(R.id.tv_copy_price).setOnClickListener(this);
        findViewById(R.id.tv_copy_order_time).setOnClickListener(this);
    }

    private void initData() {
        startAnimation();
        linkCoinOrdersOrderDetails(otherOrderId);

        if ("0".equals(getIntent().getStringExtra("befrom"))) {
            if (System.currentTimeMillis() - MMKVUtils.getInstance().decodeLong("ANNOUNCEMENTS") >= 30 * 24 * 60 * 60 * 1000) {
                tryShowDialog();
            }
        }

        if (!ClickUtils.isFastDoubleClick(R.id.tv_open_alipay)) {
            tvOpenAlipay.setOnClickListener(v -> {
                Observable
                        .create((ObservableOnSubscribe<String>) emitter ->
                                Glide.with(this)
                                        .asBitmap()
                                        .load(byBoinsResponse.getAlipayUrl())
                                        .listener(new RequestListener<Bitmap>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                emitter.tryOnError(new RuntimeException());
                                                ToastUtils.showShort("网络异常,请稍后再试");
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                String s = QRCodeDecoder.syncDecodeQRCode(bitmap);
                                                emitter.onNext(s);
                                                return false;
                                            }
                                        })
                                        .submit(500, 500))
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .compose(bindUntilEvent(ActivityEvent.DESTROY))
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                        .timeout(3, TimeUnit.SECONDS)
                        .subscribe(s -> AliPayUtils.startAlipayClient(this, s), t -> {
                            initData();
                        });
                return;
            });
        }

        findViewById(R.id.tv_cancel_the_order).setOnClickListener(v ->
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
                                    .subscribe(s -> finish());
                            dialog.dismiss();
                        });
                    }
                }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager()));

        findViewById(R.id.ll_payment).setOnClickListener(v ->
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .payMoney(byBoinsResponse.getOtherOrderId())
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver())
                        .subscribe(s -> {
                            ToastUtils.showShort("付款成功");
                            finish();
                        }, this::handleApiError));

        findViewById(R.id.fl_safety_guarantee).setOnClickListener(v -> {
            if (quickPopup == null) {
                TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
                showAnimation.setDuration(250);
                TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
                dismissAnimation.setDuration(500);
                quickPopup = QuickPopupBuilder.with(this)
                        .contentView(R.layout.popup_safety_guarantee)
                        .config(new QuickPopupConfig()
                                .withShowAnimation(showAnimation)
                                .withDismissAnimation(dismissAnimation)
                                .withClick(R.id.tv_close, null, true))
                        .build();
            }
            quickPopup.showPopupWindow();
        });
    }

    private void startAnimation() {
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(5000);
        rotate.setRepeatCount(-1);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        imgSpinningRound.setAnimation(rotate);
    }

    private void linkCoinOrdersOrderDetails(String otherOrderId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .linkCoinOrdersOrderDetails(otherOrderId)
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(data -> {
                    this.byBoinsResponse = data;
                    tvTotal.setText("¥ " + data.getTotal());
                    long l1 = (Long.parseLong(data.getNow()) - Long.parseLong(data.getCreateTime())) / 1000;

                    long total = ((Integer.valueOf(data.getDisputeMinute()) * 60 - l1) <= 0 ? 0 : (Integer.valueOf(data.getDisputeMinute()) * 60 - l1));
                    Observable.interval(0, 1, TimeUnit.SECONDS)
                            .take(total)
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(bindToLifecycle())
                            .subscribe(l -> {
                                long minute = (total - l) / 60;
                                long second = ((total - l) % 60);
                                String time = minute + ":" + (second == 0 ? "00" : (second < 10 ? ("0" + second) : second));
                                tvTime.setText(time);
                                tvPaymentTime.setText(time);
                                if (total == 0 || l == total - 1) {
                                    finish();
                                }
                            }, t -> {
                            });

                    if (data.getPayType().equals("ALIPAY")) {
                        setDrawables(getResources().getDrawable(R.drawable.pay_treasure, null), tvModePayment, "支付宝");
                        tvPayeeName.setText(data.getAlipayName());
                        tvPayeeId.setText(data.getAlipayId());
                        tvOpenAlipay.setVisibility(View.VISIBLE);
                    } else if (data.getPayType().equals("WEIXIN")) {
                        setDrawables(getResources().getDrawable(R.drawable.wechat, null), tvModePayment, "微信");
                        tvPayeeName.setText(data.getWeixinName());
                        tvPayeeId.setText(data.getWeixinId());
                    } else {
                        findViewById(R.id.ll_sub_branch).setVisibility(View.VISIBLE);
                        imgQRCode.setVisibility(View.GONE);
                        tvSubBranchId.setText(data.getCardAddress());
                        setDrawables(getResources().getDrawable(R.drawable.bank_card, null), tvModePayment, "银行卡");
                        tvPayee.setText("银行账号");
                        tvPayeeName.setText(data.getCardUserName());
                        tvPayeeId.setText(data.getCardCode());
                    }

                    tvOtherOrderId.setText(data.getOtherOrderId());
                    tvCoinAmount.setText(data.getCoinAmount() + "\u0020" + data.getCoinSymbol());
                    tvPrice.setText(data.getPrice() + "\u0020" + data.getCurrency() + "/" + data.getCoinSymbol());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    if (!TextUtils.isEmpty(data.getCreateTime())) {
                        String sd = sdf.format(new Date(Long.parseLong(data.getCreateTime())));
                        tvOrderTime.setText(sd);
                    }

                    imgQRCode.setOnClickListener(v -> {
                        if (!"EBANK".equals(data.getPayType())) {
                            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
                            showAnimation.setDuration(350);
                            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
                            dismissAnimation.setDuration(500);
                            QuickPopup invitePop;
                            invitePop = QuickPopupBuilder.with(this)
                                    .contentView(R.layout.popup_qr)
                                    .config(new QuickPopupConfig()
                                            .withShowAnimation(showAnimation)
                                    )
                                    .show();
                            if ("WEIXIN".equals(data.getPayType())) {
                                GlideUtil.loadNormalImg(invitePop.findViewById(R.id.img_merchants_qr), data.getWeixinUrl());
                            } else if ("ALIPAY".equals(data.getPayType()))
                                GlideUtil.loadNormalImg(invitePop.findViewById(R.id.img_merchants_qr), data.getAlipayUrl());
                        }
                    });

                }, this::handleApiError);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_copy_sub_branch_id:
                copy(byBoinsResponse.getCardAddress());
                break;
            case R.id.tv_total:
                copy(byBoinsResponse.getTotal());
                break;
            case R.id.tv_copy_payee_name:
                if ("ALIPAY".equals(byBoinsResponse.getPayType())) {
                    copy(byBoinsResponse.getAlipayName());
                } else if ("WEIXIN".equals(byBoinsResponse.getPayType())) {
                    copy(byBoinsResponse.getWeixinName());
                } else {
                    copy(byBoinsResponse.getCardUserName());
                }
                break;
            case R.id.tv_copy_payee_id:
                if (byBoinsResponse.getPayType().equals("ALIPAY")) {
                    copy(byBoinsResponse.getAlipayId());
                } else if (byBoinsResponse.getPayType().equals("WEIXIN")) {
                    copy(byBoinsResponse.getWeixinId());
                } else {
                    setDrawables(getResources().getDrawable(R.drawable.bank_card, null), tvModePayment, "银行卡");
                    copy(byBoinsResponse.getCardCode());
                }
                copy(byBoinsResponse.getAlipayName());
                break;
            case R.id.tv_copy_other_order_id:
                copy(byBoinsResponse.getOtherOrderId());
                break;
            case R.id.tv_copy_coin_amount:
                copy(byBoinsResponse.getCoinAmount());
                break;
            case R.id.tv_copy_price:
                copy(byBoinsResponse.getPrice());
                break;
            case R.id.tv_copy_order_time:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String sd = sdf.format(new Date(Long.parseLong(byBoinsResponse.getCreateTime())));
                copy(sd);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onBackDialog();
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

    private void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", text);
        clipboard.setPrimaryClip(clip);
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
    }

    private void onBackDialog() {
        String text = getString(R.string.payment_reminder);
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary, null)), 12, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        NiceDialog.init().setLayoutId(R.layout.dialog_remove_order).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                TextView tv = holder.getView(R.id.tv_content);
                tv.setText(style);
                holder.setText(R.id.tv_title, R.string.confirm_to_leave_the_pay);
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_ok, v -> {
                    finish();
                });
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    private void tryShowDialog() {
        buyCoinDialog = new BuyCoinDialog(this,
                "注意事项",
                "1.类似海浪、比特币、以太坊、BTC、USDT等数字货币相关信息；\n2.确保付款卡所属人与平台实名信息一致，若不一致卖家有权不放币；\n3.未付款前请勿标记已完成付款，恶意操作系统将会冻结你的账户。\nHilamg客服ID：3937354",
                "确定",
                true)
                .setKey("ANNOUNCEMENTS")
                .setVisibilityHint(true);
        buyCoinDialog.show();
    }
}