package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
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
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

@SuppressLint("CheckResult")
public class OrderDetailsActivity extends BaseActivity {

    private GetLinkCoinOrdersOrderDetails byBoinsResponse;
    private TextView tvTotal;
    private TextView tvTheOrderNumber;
    private TextView tvCancelTheOrder;
    private TextView tvPaymentType;
    private TextView tvPrice;
    private TextView tvNumber;
    private TextView tvBankName;
    private LinearLayout llBankName;
    private TextView tvPayment;
    private TextView tvPaymentType2;
    private ScrollView scrollView;
    private RelativeLayout rlTitleBar;
    private LinearLayout llPaymentType;

    private String sign;
    private String timestamp;
    private Boolean payment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        setLightStatusBar(true);
        BarUtils.setStatusBarColor(this, Color.parseColor("#0083BF"));
        initView();
        rlTitleBar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        initData();
    }

    private void initView() {
        tvTotal = findViewById(R.id.tv_total);
        tvTheOrderNumber = findViewById(R.id.tv_the_order_number);
        tvCancelTheOrder = findViewById(R.id.tv_cancel_the_order);
        tvPaymentType = findViewById(R.id.tv_payment_type);
        tvPrice = findViewById(R.id.tv_price);
        tvNumber = findViewById(R.id.tv_number);
        tvBankName = findViewById(R.id.tv_bank_name);
        llBankName = findViewById(R.id.ll_bank_name);

        tvPayment = findViewById(R.id.tv_payment);
        tvPaymentType2 = findViewById(R.id.tv_payment_type2);
        scrollView = findViewById(R.id.scrollview);
        rlTitleBar = findViewById(R.id.rl_title_bar);
        llPaymentType = findViewById(R.id.ll_payment_type);

        findViewById(R.id.rl_back).setOnClickListener(v -> onBackDialog());
    }

    private void initData() {
        byBoinsResponse = (GetLinkCoinOrdersOrderDetails) getIntent().getSerializableExtra("ByBoinsResponse");

        long l1 = (System.currentTimeMillis() - Long.parseLong(byBoinsResponse.getCreateTime())) / 1000;
        long total = (900 - l1) <= 0 ? 0 : (900 - l1);
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(total)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(l -> {
                    long minute = (total - l) / 60;
                    long second = (total - l) % 60;
                    tvCancelTheOrder.setText(minute + ":" + (second == 0 ? "00" : (second < 10 ? ("0" + second) : second)));
                    if (total == 0 || l == total - 1) {
                        ToastUtils.showShort(getString(R.string.order_a_timeout));
                        tvCancelTheOrder.setText(R.string.already_timeout);
                        tvPayment.setText(R.string.already_timeout);
                        tvPayment.getBackground().setAlpha(180);
                        payment = false;
                    }
                }, t -> {
                });

        tvTotal.setText(byBoinsResponse.getTotal());
        tvTheOrderNumber.setText(getString(R.string.order_number, byBoinsResponse.getOtherOrderId()));

        if ("ALIPAY".equals(byBoinsResponse.getPayType())) {
            tvPaymentType.setText(R.string.alipay_pay);
            setDrawables(getResources().getDrawable(R.drawable.pay_treasure, null), null, tvPaymentType);
            tvPrice.setText(byBoinsResponse.getAlipayName());
            tvNumber.setText(byBoinsResponse.getAlipayId());
        } else if ("WEIXIN".equals(byBoinsResponse.getPayType())) {
            tvPaymentType.setText(R.string.wechat_pay);
            setDrawables(getResources().getDrawable(R.drawable.wechat, null), null, tvPaymentType)
            ;
            tvPrice.setText(byBoinsResponse.getWeixinName());
            tvNumber.setText(byBoinsResponse.getWeixinId());
        } else {
            tvPaymentType.setText(R.string.bank_card_payment);
            tvPrice.setText(byBoinsResponse.getCardUserName());
            setDrawables(getResources().getDrawable(R.drawable.bank_card2, null), null, tvPaymentType);
            tvNumber.setText(byBoinsResponse.getCardCode());
            tvPaymentType2.setText(R.string.payee_card_number);
            llBankName.setVisibility(View.VISIBLE);
            tvBankName.setText(byBoinsResponse.getCardBank());

        }


        llPaymentType.setOnClickListener(v -> {

            if (!"EBANK".equals(byBoinsResponse.getPayType())) {
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
                if ("WEIXIN".equals(byBoinsResponse.getPayType())) {
                    GlideUtil.loadNormalImg(invitePop.findViewById(R.id.img_merchants_qr), byBoinsResponse.getWeixinUrl());
                } else if ("ALIPAY".equals(byBoinsResponse.getPayType()))
                    GlideUtil.loadNormalImg(invitePop.findViewById(R.id.img_merchants_qr), byBoinsResponse.getAlipayUrl());
            }
        });

        tvTheOrderNumber.setOnClickListener(v -> copyText(byBoinsResponse.getOtherOrderId()));

        tvPrice.setOnClickListener(v -> copyText(tvPrice.getText().toString()));

        tvNumber.setOnClickListener(v -> copyText(tvNumber.getText().toString()));

        tvBankName.setOnClickListener(v -> copyText(tvBankName.getText().toString()));

        tvPayment.setOnClickListener(v -> {
            if (payment) {
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .payMoney(byBoinsResponse.getOtherOrderId())
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver())
                        .subscribe(s -> {
                            ToastUtils.showShort("付款成功");
                            finish();
                        }, this::handleApiError);
            }
        });
    }

    private void setDrawables(Drawable drawable, Drawable drawable2, TextView textView) {
        Drawable drawables = drawable;
        drawables.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());// 设置边界
        if (drawable2 != null) {
            drawable2.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                    .getMinimumHeight());
        }
        textView.setCompoundDrawables(drawables, null, drawable2 != null ? drawable2 : null, null);
        textView.setCompoundDrawablePadding(8);
    }

    private void copyText(String text) {
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

    @Override
    public void onBackPressed() {
        onBackDialog();
    }
}
