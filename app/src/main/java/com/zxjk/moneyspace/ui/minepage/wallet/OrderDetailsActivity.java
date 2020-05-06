package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
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
import com.zxjk.moneyspace.bean.response.GetOrderInfoById;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.PayPsdInputView;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;
import com.zxjk.moneyspace.utils.MD5Utils;
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
public class OrderDetailsActivity extends BaseActivity {

    private GetOrderInfoById byBoinsResponse;
    private TextView tvTotal;
    private TextView tvTheOrderNumber;
    private TextView tvCancelTheOrder;
    private TextView tvPaymentType;
    private TextView tvPrice;
    private TextView tvNumber;
    private TextView tvBankName;
    private LinearLayout llBankName;
    private TextView tvBranchBankName;
    private LinearLayout llBranchBankName;
    private TextView tvRemark1;
    private TextView tvRemark2;
    private TextView tvPayment;
    private TextView tvPaymentType2;
    private NestedScrollView scrollView;
    private LinearLayout llTitleBar;
    private QuickPopup byCoinsOrAmount;
    private PayPsdInputView editText;
    private LinearLayout tvNick;
    private String customerIdentity;

    private String sign;
    private String timestamp;
    private Boolean payment = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        setTrasnferStatusBar(true);

        initView();

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
        llTitleBar = findViewById(R.id.ll_title_bar);
        tvNick = findViewById(R.id.tv_nick);
        findViewById(R.id.rl_back).setOnClickListener(v -> {
            onBackDialog();
        });
    }

    private void initData() {
        byBoinsResponse = (GetOrderInfoById) getIntent().getSerializableExtra("GetOrderInfoById");
        customerIdentity = getIntent().getStringExtra("customerIdentity");

        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(currentTime);
        timestamp = dataOne(formatter.format(date));

        long l1 = (System.currentTimeMillis() - Long.parseLong(byBoinsResponse.getCreateTime())) / 1000;
        long total = (900 - l1) <= 0 ? 0 : (900 - l1);
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(total)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(l -> {
                    long minute = (total - l) / 60;
                    long second = (total - l) % 60;
                    tvCancelTheOrder.setText(minute + ":" + (second == 60 ? "00" : ((second < 10 ? ("0" + second) : second))));
                    if (total == 0 || l == total - 1) {
                        ToastUtils.showShort(getString(R.string.order_a_timeout));
                        tvCancelTheOrder.setText(R.string.already_timeout);
                        tvPayment.getBackground().setAlpha(180);
                        payment = false;
                    }
                }, t -> {
                });

        tvTotal.setText(byBoinsResponse.getMoney());
        tvTheOrderNumber.setText(getString(R.string.order_number, byBoinsResponse.getBothOrderId()));

        if (byBoinsResponse.getPayType().equals("3")) {
            tvPaymentType.setText(R.string.bank_card_payment);
            setDrawables(getResources().getDrawable(R.drawable.bank_card2, null), null, tvPaymentType);

            tvPaymentType2.setText(R.string.payee_card_number);
            llBankName.setVisibility(View.VISIBLE);
            tvBankName.setText(byBoinsResponse.getOpenBank());
            tvPrice.setText(byBoinsResponse.getRealName());
            tvBankName.setText(byBoinsResponse.getOpenBank());
            tvNumber.setText(byBoinsResponse.getPayNumber());
        } else if (byBoinsResponse.getPayType().equals("2")) {
            tvPaymentType.setText(R.string.alipay_pay);
            setDrawables(getResources().getDrawable(R.drawable.pay_treasure, null), getResources().getDrawable(R.drawable.ic_qr_code_small, null), tvPaymentType);
            tvNumber.setText(byBoinsResponse.getZhifubaoNumber());
            tvPrice.setText(byBoinsResponse.getRealName());
        } else {
            tvPaymentType.setText(R.string.wechat_pay);
            setDrawables(getResources().getDrawable(R.drawable.wechat, null), getResources().getDrawable(R.drawable.ic_qr_code_small, null), tvPaymentType);

            tvNick.setVisibility(View.GONE);
            llBankName.setVisibility(View.GONE);
            tvNumber.setText(byBoinsResponse.getWechatNick());
            tvPrice.setText(byBoinsResponse.getRealName());
        }



        tvPaymentType.setOnClickListener(v -> {

            if (!byBoinsResponse.getPayType().equals("3")) {
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
                GlideUtil.loadNormalImg(invitePop.findViewById(R.id.img_merchants_qr), byBoinsResponse.getReceiptPicture());
            }
        });

        tvTheOrderNumber.setOnClickListener(v -> copyText(byBoinsResponse.getBothOrderId()));

        tvPrice.setOnClickListener(v -> copyText(byBoinsResponse.getNick()));

        tvNumber.setOnClickListener(v -> copyText(tvNumber.getText().toString()));

        tvBankName.setOnClickListener(v -> copyText(byBoinsResponse.getOpenBank()));

//        tvBranchBankName.setOnClickListener(v -> copyText(byBoinsResponse.getSubBranch()));

        tvPayment.setOnClickListener(v -> popupPayView());

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
                holder.setOnClickListener(R.id.tv_ok, v -> finish());
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    @Override
    public void onBackPressed() {
        onBackDialog();
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

        editText.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {

            }

            @Override
            public void onEqual(String psd) {

            }

            @Override
            public void inputFinished(String inputPsd) {
                if (payment) {
                    if(!TextUtils.isEmpty(customerIdentity)){
                        byBoinsResponse.setIsSystems(customerIdentity);
                    }
                    if (byBoinsResponse.getIsSystems().equals("0")) {
                        String secret = "bothOrderId=" + byBoinsResponse.getBothOrderId() +
                                "&nonce=" + timestamp +
                                "&payPwd=" + MD5Utils.getMD5(inputPsd) + Constant.SECRET;
                        sign = Sha256.getSHA256(secret);

                        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                                .userConfirmPay(byBoinsResponse.getBothOrderId(),
                                        timestamp,
                                        MD5Utils.getMD5(inputPsd))
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.otc())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(OrderDetailsActivity.this)))
                                .subscribe(s -> {
                                    ToastUtils.showShort("付款成功");
                                    byCoinsOrAmount.dismiss();
                                    finish();
                                }, throwable -> handleApiError(throwable));
                    } else {
                        String secret = "bothOrderId=" + byBoinsResponse.getBothOrderId() +
                                "&nonce=" + timestamp +
                                "&payPwd=" + MD5Utils.getMD5(inputPsd) + Constant.SECRET;
                        sign = Sha256.getSHA256(secret);

                        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                                .acceptorConfirmPay(byBoinsResponse.getBothOrderId(),
                                        timestamp,
                                        MD5Utils.getMD5(inputPsd))
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.otc())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(OrderDetailsActivity.this)))
                                .subscribe(s -> {
                                    ToastUtils.showShort("付款成功");
                                    byCoinsOrAmount.dismiss();
                                    finish();
                                }, throwable -> handleApiError(throwable));
                    }
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


}
