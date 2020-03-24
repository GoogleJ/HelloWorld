package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.blankj.utilcode.util.ScreenUtils;
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
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.Sha256;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

import static tyrantgit.explosionfield.Utils.dp2Px;

@SuppressLint("CheckResult")
public class OrderDetailsActivity extends BaseActivity {

    private ByBoinsResponse byBoinsResponse;
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
        tvBranchBankName = findViewById(R.id.tv_branch_bank_name);
        llBranchBankName = findViewById(R.id.ll_branch_bank_name);
        tvRemark1 = findViewById(R.id.tv_remark1);
        tvRemark2 = findViewById(R.id.tv_remark2);
        tvPayment = findViewById(R.id.tv_payment);
        tvPaymentType2 = findViewById(R.id.tv_payment_type2);
        scrollView = findViewById(R.id.scrollview);
        llTitleBar = findViewById(R.id.ll_title_bar);
        findViewById(R.id.rl_back).setOnClickListener(v -> {
                onBackDialog();
        });
    }

    private void initData() {
        byBoinsResponse = (ByBoinsResponse) getIntent().getSerializableExtra("ByBoinsResponse");


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

        tvTotal.setText(byBoinsResponse.getTotal());
        tvTheOrderNumber.setText(getString(R.string.order_number,byBoinsResponse.getTransId()));

        if (byBoinsResponse.getPaymentType().equals("1")) {
            tvPaymentType.setText(R.string.bank_card_payment);
            setDrawables(getResources().getDrawable(R.drawable.bank_card2, null), null, tvPaymentType);
            tvNumber.setText(byBoinsResponse.getCardNumber());
            tvPaymentType2.setText(R.string.payee_card_number);
            llBankName.setVisibility(View.VISIBLE);
            llBranchBankName.setVisibility(View.VISIBLE);
            tvBankName.setText(byBoinsResponse.getDepositBank());
            tvBranchBankName.setText(byBoinsResponse.getSubBranch());
            tvRemark1.setText(R.string.merchants_remark);
        } else if (byBoinsResponse.getPaymentType().equals("2")) {
            tvPaymentType.setText(R.string.alipay_pay);
            setDrawables(getResources().getDrawable(R.drawable.pay_treasure, null), getResources().getDrawable(R.drawable.ic_qr_code_small, null), tvPaymentType);
            tvNumber.setText(byBoinsResponse.getAlipayAccount());
        } else {
            tvPaymentType.setText(R.string.wechat_pay);
            setDrawables(getResources().getDrawable(R.drawable.wechat, null), getResources().getDrawable(R.drawable.ic_qr_code_small, null), tvPaymentType);
            tvNumber.setText(byBoinsResponse.getWeChatAccount());
        }

        tvPrice.setText(byBoinsResponse.getName());
        tvRemark2.setText(byBoinsResponse.getRemark());

        tvPaymentType.setOnClickListener(v -> {

            if (!byBoinsResponse.getPaymentType().equals("1")) {
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
                GlideUtil.loadNormalImg(invitePop.findViewById(R.id.img_merchants_qr), byBoinsResponse.getCollectionImg());
            }
        });

        tvTheOrderNumber.setOnClickListener(v -> copyText(byBoinsResponse.getTransId()));

        tvPrice.setOnClickListener(v -> copyText(byBoinsResponse.getName()));

        tvNumber.setOnClickListener(v -> copyText(tvNumber.getText().toString()));

        tvBankName.setOnClickListener(v -> copyText(byBoinsResponse.getDepositBank()));

        tvBranchBankName.setOnClickListener(v -> copyText(byBoinsResponse.getSubBranch()));

        tvPayment.setOnClickListener(v -> {
            if(payment){
                long timeStampSec = System.currentTimeMillis() / 1000;
                timestamp = String.format("%010d", timeStampSec);

                String secret = "collection_id=" + byBoinsResponse.getCollectionId() +
                        "&nonce=" + timestamp +
                        "&trans_id=" + byBoinsResponse.getTransId() +
                        "&user_id=" + Constant.USERID + Constant.SECRET;
                Log.i("tag", "initData: " + secret);
                sign = Sha256.getSHA256(secret);
                ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                        .paymentDone(byBoinsResponse.getCollectionId(),
                                timestamp,
                                byBoinsResponse.getTransId(),
                                Constant.USERID)
                        .compose(bindToLifecycle())
                        .flatMap(paymentDoneResponse -> {
                            String secret1 = "nonce=" + timestamp +
                                    "&trans_id=" + byBoinsResponse.getTransId() +
                                    "&user_id=" + Constant.USERID + Constant.SECRET;
                            sign = Sha256.getSHA256(secret1);
                            return ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                                    .orderInfo(timestamp, byBoinsResponse.getTransId(), Constant.USERID, byBoinsResponse.getPaymentType(), byBoinsResponse.getCreateTime());
                        })
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(OrderDetailsActivity.this)))
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(s -> {
                            Intent intent = new Intent(OrderDetailsActivity.this, PurchaseDetailsActivity.class);
                            intent.putExtra("ByBoinsResponse", s);
                            startActivity(intent);
                            finish();
                        });
            }
        });


        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (nestedScrollView, i, i1, i2, i3) -> {

            int height = dp2Px(45);

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
