package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.FindHailangResponse;
import com.zxjk.moneyspace.bean.response.GetCustomerIdentity;
import com.zxjk.moneyspace.bean.response.GetOTCSymbolInfo;
import com.zxjk.moneyspace.bean.response.GetOrderInfoById;
import com.zxjk.moneyspace.bean.response.UserBuyResponse;
import com.zxjk.moneyspace.bean.response.UserSellResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseFragment;
import com.zxjk.moneyspace.ui.minepage.BillingMessageActivity;
import com.zxjk.moneyspace.ui.widget.PayPsdInputView;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MD5Utils;
import com.zxjk.moneyspace.utils.Sha256;

import org.jsoup.internal.StringUtil;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

@SuppressLint("CheckResult")
public class BuyCoinViewPagerFragment extends BaseFragment implements View.OnClickListener {

    private static final String KEY = "title";
    private int decimalDigits = 4;// 小数的位数
    private int integerDigits = 10;// 整数的位数
    private int decimalDigits2 = 2;// 小数的位数
    private int integerDigits2 = 10;// 整数的位数
    private String paymentType;
    private String minimum;//最小金额
    private String maximums;
    private TextView tvUnitPrice;
    private TextView tvBuyPatterns;
    private TextView tvPurchaseAmount;
    private EditText etPurchaseAmount;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<FindHailangResponse, BaseViewHolder> adapter;
    private LinearLayout llPrice;
    private LinearLayout llLimit;
    private LinearLayout llReceiveOrPay;
    private TextView tvBuyCoin;
    private TextView tvMaximum;
    private EditText etMinimum;
    private EditText etMaximum;
    private PayPsdInputView editText;
    private EditText etUnitPrice;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private TextView tv1;

    private GetOrderInfoById confirmSellOrBuy;
    private TextView tvCurrency;

    private List<String> paytype = new ArrayList();

    private UserSellResponse userSell;
    private String defaultRenegeNumber;
    private String buyPatterns = "CNY";
    private String logo;
    private String sign;
    private String timestamp;
    private Double amount;//购买数量:当前购买类型为CNY时,需要用（输入金额/单价）来获得购买数量,当前购买类型为数量时,直接获取输入框内容
    private Double total;//实付金额:当前类型为CNY时,直接获取输入框内容,当前购买类型为数量时, (输入数量*单价)获得实付金额

    private List<Integer> prices = new ArrayList<>();

    private QuickPopup byCoinsOrAmount;
    private QuickPopup buyCoinType;
    private RecyclerView rcPayType;
    private LinearLayout llPayType;
    private BaseQuickAdapter<GetOTCSymbolInfo.PayInfoListBean, BaseViewHolder> payTypeAdapter;

    private String preferentialPrice;
    private TextView tvBillingMessage;


    private NumberFormat nf;
    private String amountScale;
    private String customerIdentity;
    private GetCustomerIdentity getCustomerIdentity;
    private String currency;
    private String price;
    private String rate;
    private String balance;
    private int count;

    private UserBuyResponse userBuy;
    private List<GetOTCSymbolInfo.PayInfoListBean> payInfoList = new ArrayList<>();

    public static BuyCoinViewPagerFragment newInstance(String currency, String price, String rate, String balance, GetCustomerIdentity customerIdentity, int count, List<GetOTCSymbolInfo.PayInfoListBean> payInfoList) {

        BuyCoinViewPagerFragment fragment = new BuyCoinViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("currency", currency);
        bundle.putString("price", price);
        bundle.putString("rate", rate);
        bundle.putString("balance", balance);
        bundle.putSerializable("getCustomerIdentity", customerIdentity);
        bundle.putInt("count", count);
        bundle.putSerializable("payInfoList", (Serializable) payInfoList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_buy_coin_view_pager, container, false);
        currency = getArguments().getString("currency");
        price = getArguments().getString("price");
        rate = getArguments().getString("rate");
        balance = getArguments().getString("balance");
        getCustomerIdentity = (GetCustomerIdentity) getArguments().getSerializable("getCustomerIdentity");
        customerIdentity = getCustomerIdentity.getIdentity();
        count = getArguments().getInt("count");
        payInfoList = (List<GetOTCSymbolInfo.PayInfoListBean>) getArguments().getSerializable("payInfoList");

        initView();

        setSign();

        initData();

        return rootView;
    }

    private void initView() {
        llPrice = rootView.findViewById(R.id.ll_price);
        llLimit = rootView.findViewById(R.id.ll_limit);
        llReceiveOrPay = rootView.findViewById(R.id.ll_receive_or_pay);
        rootView.findViewById(R.id.ll_buy_coin).setOnClickListener(this::onClick);
        tvUnitPrice = rootView.findViewById(R.id.tv_unit_price);
        tvBuyPatterns = rootView.findViewById(R.id.tv_buy_patterns);
        tvPurchaseAmount = rootView.findViewById(R.id.tv_purchase_amount);
        etPurchaseAmount = rootView.findViewById(R.id.et_purchase_amount);
        tvBuyCoin = rootView.findViewById(R.id.tv_buy_coin);
        tvMaximum = rootView.findViewById(R.id.tv_maximum);
        etMinimum = rootView.findViewById(R.id.et_minimum);
        etMaximum = rootView.findViewById(R.id.et_maximum);
        etUnitPrice = rootView.findViewById(R.id.et_unit_price);
        rootView.findViewById(R.id.tv_bank_card).setOnClickListener(this::onClick);
        rootView.findViewById(R.id.tv_ali_pay).setOnClickListener(this::onClick);
        rootView.findViewById(R.id.tv_WeChat).setOnClickListener(this::onClick);
        img1 = rootView.findViewById(R.id.img1);
        img2 = rootView.findViewById(R.id.img2);
        img3 = rootView.findViewById(R.id.img3);
        rcPayType = rootView.findViewById(R.id.rc_pay_type);
        llPayType = rootView.findViewById(R.id.ll_pay_type);

        tv1 = rootView.findViewById(R.id.tv1);
        tvCurrency = rootView.findViewById(R.id.tv_currency);
        tvBillingMessage = rootView.findViewById(R.id.tv_billing_message);

        tvMaximum.setOnClickListener(v -> etPurchaseAmount.setText(balance));
        tvBuyPatterns.setText(currency);
        tvCurrency.setText("CNY/" + currency);
        if (customerIdentity.equals("1")) {
            //承兑商
            if (count == 0) {
                tvPurchaseAmount.setText(R.string.amount2);
                etPurchaseAmount.setHint(R.string.purchase_amount_hint);
                tvBuyCoin.setText(R.string.buy_order);
                tvMaximum.setVisibility(View.GONE);
                rcPayType.setVisibility(View.GONE);
                llPayType.setVisibility(View.VISIBLE);
                tv1.setText(getString(R.string.maximum_purchase_quantity) + getCustomerIdentity.getMaxBuyNum() + currency);
            } else {
                tvPurchaseAmount.setText(R.string.sell_amout);
                etPurchaseAmount.setHint(R.string.hint4);
                tvBuyCoin.setText(R.string.sell_order2);
                rcPayType.setVisibility(View.VISIBLE);
                llPayType.setVisibility(View.GONE);
                tv1.setText(getString(R.string.maximum_quantity_sold) + getCustomerIdentity.getMaxSaleNum() + currency);
            }
        } else if (customerIdentity.equals("0")) {
            //普通用户
            llPrice.setVisibility(View.GONE);
            llLimit.setVisibility(View.GONE);
            if (count == 0) {
                llReceiveOrPay.setVisibility(View.GONE);
                tvMaximum.setVisibility(View.GONE);
                tvPurchaseAmount.setText(R.string.amount2);
                etPurchaseAmount.setHint(R.string.purchase_amount_hint);
                rcPayType.setVisibility(View.GONE);
                llPayType.setVisibility(View.VISIBLE);
            } else {
                tvPurchaseAmount.setText(R.string.sell_amout);
                etPurchaseAmount.setHint(R.string.hint4);
                tvBuyCoin.setText(R.string.sell);
                rcPayType.setVisibility(View.VISIBLE);
                llPayType.setVisibility(View.GONE);
            }
        }
    }

    private void initData() {

        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(currentTime);
        timestamp = dataOne(formatter.format(date));

        tvUnitPrice.setText(getString(R.string.the_unit_price, price, "CNY", currency));

        etUnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //EdtiText输入整数和小数位数限制
                String s = editable.toString();

                if (s.contains(".")) {
                    if (integerDigits2 > 0) {
                        etUnitPrice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(integerDigits2 + decimalDigits2 + 1)});
                    }
                    if (s.length() - 1 - s.indexOf(".") > decimalDigits2) {
                        s = s.substring(0,
                                s.indexOf(".") + decimalDigits2 + 1);
                        editable.replace(0, editable.length(), s.trim());
                    }
                } else {
                    if (integerDigits2 > 0) {
                        etUnitPrice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(integerDigits2 + 1)});
                        if (s.length() > integerDigits2) {
                            s = s.substring(0, integerDigits2);
                            editable.replace(0, editable.length(), s.trim());
                        }
                    }
                }
                if (s.trim().equals(".")) {
                    s = "0" + s;
                    editable.replace(0, editable.length(), s.trim());
                }
                if (s.startsWith("0")
                        && s.trim().length() > 1) {
                    if (!s.substring(1, 2).equals(".")) {
                        editable.replace(0, editable.length(), "0");
                    }
                }
            }
        });
        etPurchaseAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //EdtiText输入整数和小数位数限制
                String s = editable.toString();

                if (s.contains(".")) {
                    if (integerDigits > 0) {
                        etPurchaseAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(integerDigits + decimalDigits + 1)});
                    }
                    if (s.length() - 1 - s.indexOf(".") > decimalDigits) {
                        s = s.substring(0,
                                s.indexOf(".") + decimalDigits + 1);
                        editable.replace(0, editable.length(), s.trim());
                    }
                } else {
                    if (integerDigits > 0) {
                        etPurchaseAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(integerDigits + 1)});
                        if (s.length() > integerDigits) {
                            s = s.substring(0, integerDigits);
                            editable.replace(0, editable.length(), s.trim());
                        }
                    }
                }
                if (s.trim().equals(".")) {
                    s = "0" + s;
                    editable.replace(0, editable.length(), s.trim());
                }
                if (s.startsWith("0")
                        && s.trim().length() > 1) {
                    if (!s.substring(1, 2).equals(".")) {
                        editable.replace(0, editable.length(), "0");
                    }
                }
            }
        });

        payTypeAdapter = new BaseQuickAdapter<GetOTCSymbolInfo.PayInfoListBean, BaseViewHolder>(R.layout.rc_item_paytype) {

            @Override
            protected void convert(BaseViewHolder helper, GetOTCSymbolInfo.PayInfoListBean item) {
                TextView textView = helper.getView(R.id.tv_bank_card);
                if (item.getPayType().equals("1")) {
                    setDrawables(getResources().getDrawable(R.drawable.ic_otc_wechat, null), textView, getString(R.string.wechat));
                } else if (item.getPayType().equals("2")) {
                    setDrawables(getResources().getDrawable(R.drawable.ic_otc_ali_pay, null), textView, getString(R.string.alipay_pay));
                } else if (item.getPayType().equals("3")){
                    setDrawables(getResources().getDrawable(R.drawable.ic_otc_bank_card, null), textView, getString(R.string.bank_card));
                }
            }
        };
        payTypeAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.findViewById(R.id.img1).getVisibility() == View.GONE) {
                view.findViewById(R.id.img1).setVisibility(View.VISIBLE);
                paytype.add(payInfoList.get(position).getPayType());
            } else if (view.findViewById(R.id.img1).getVisibility() == View.VISIBLE) {
                view.findViewById(R.id.img1).setVisibility(View.GONE);
                paytype.remove(payInfoList.get(position).getPayType());
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rcPayType.setLayoutManager(layoutManager);
        rcPayType.setAdapter(payTypeAdapter);
        payTypeAdapter.setNewData(payInfoList);
        if (payInfoList.size() == 0) {
            tvBillingMessage.setVisibility(View.VISIBLE);
            tvBillingMessage.setOnClickListener(v -> {
                String isAuthentication = Constant.currentUser.getIsAuthentication();
                if ("2".equals(isAuthentication)) {
                    ToastUtils.showShort(R.string.waitAuthentication);
                } else {
                    startActivity(new Intent(getActivity(), BillingMessageActivity.class));
                }
            });
        }
    }

    private void setSign() {
        long timeStampSec = System.currentTimeMillis() / 1000;
        timestamp = String.format("%010d", timeStampSec);

        String secret = "currency=" + currency + "&nonce=" + timestamp + Constant.SECRET;
        sign = Sha256.getSHA256(secret);
    }

    //支付信息popup
    private void byCoinsOrAmount(int TermOfPayment) {
        if (buyCoinType != null) {
            buyCoinType.dismiss();
        }

        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(350);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);
        byCoinsOrAmount = QuickPopupBuilder.with(getActivity())
                .contentView(R.layout.popup_buy_coins_or_amount)
                .config(new QuickPopupConfig()
                        .withShowAnimation(showAnimation)
                        .withDismissAnimation(dismissAnimation)
                        .withClick(R.id.bt_to_buy, v ->
                                {
                                    if (customerIdentity.equals("0") && count == 0) {
                                        String secret = "nonce=" + timestamp +
                                                "&number=" + etPurchaseAmount.getText().toString() +
                                                "&payType=" + paymentType +
                                                "&sellOrderId=" + confirmSellOrBuy.getSellOrderId() + Constant.SECRET;

                                        sign = Sha256.getSHA256(secret);
                                        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                                                .userConfirmBuy(confirmSellOrBuy.getSellOrderId(),
                                                        paymentType,
                                                        timestamp,
                                                        etPurchaseAmount.getText().toString())
                                                .compose(bindToLifecycle())
                                                .compose(RxSchedulers.otc())
                                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                                                .subscribe(s -> {
                                                    Intent intent = new Intent(getContext(), PurchaseDetailsActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    s.setBuyOrSell("0");
                                                    bundle.putSerializable("GetOrderInfoById", s);
                                                    bundle.putString("count", String.valueOf(count));
                                                    bundle.putString("orderId", s.getBothOrderId());
                                                    bundle.putString("customerIdentity", customerIdentity);
                                                    intent.putExtras(bundle);
                                                    getActivity().startActivity(intent);
                                                }, this::handleApiError);
                                    } else {
                                        popupPayView();
                                    }
                                }
                                , true)
                        .withClick(R.id.img_exit, null, true)
                ).show();

        TextView tvTitle = byCoinsOrAmount.findViewById(R.id.tv_title);
        if (count != 0) {
            tvTitle.setText(R.string.confirm_to_sell);
        }
        TextView tvTermOfPayment = byCoinsOrAmount.findViewById(R.id.tv_terms_of_payment);
        if (TermOfPayment == 3) {
            setDrawable(getResources().getDrawable(R.drawable.bank_card, null), tvTermOfPayment, getString(R.string.bank_card));
            paymentType = "3";
        } else if (TermOfPayment == 2) {
            setDrawable(getResources().getDrawable(R.drawable.pay_treasure, null), tvTermOfPayment, getString(R.string.pay_treasure));
            paymentType = "2";
        } else {
            setDrawable(getResources().getDrawable(R.drawable.wechat, null), tvTermOfPayment, getString(R.string.wechat_pay));
            paymentType = "1";
        }
        TextView tvPrice = byCoinsOrAmount.findViewById(R.id.tv_price);
        tvPrice.setText(price + " CNY/" + currency);
        TextView tvAmount = byCoinsOrAmount.findViewById(R.id.tv_amount);
        tvAmount.setText(etPurchaseAmount.getText());
        TextView tvTotal = byCoinsOrAmount.findViewById(R.id.tv_total);
        tvTotal.setText(String.valueOf(Float.parseFloat(price) * Float.parseFloat(etPurchaseAmount.getText().toString())));
    }

    //    按金额购买和币种购买的接口调用
    private void toBuyCoin(int paymentType) {
        String secret = "currency=" + currency +
                "&nonce=" + timestamp +
                "&number=" + etPurchaseAmount.getText().toString() +
                "&payType=" + paymentType + Constant.SECRET;
        sign = Sha256.getSHA256(secret);
        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                .userBuy(timestamp,
                        etPurchaseAmount.getText().toString(),
                        currency,
                        String.valueOf(paymentType))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                .subscribe(s -> {
                    confirmSellOrBuy = s;
                    byCoinsOrAmount(paymentType);
                }, this::handleApiError);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_bank_card:
                if (img1.getVisibility() == View.GONE) {
                    img1.setVisibility(View.VISIBLE);
                    paytype.add("3");
                } else {
                    img1.setVisibility(View.GONE);
                    paytype.remove("3");
                }
                break;
            case R.id.tv_ali_pay:
                if (img2.getVisibility() == View.GONE) {
                    img2.setVisibility(View.VISIBLE);
                    paytype.add("2");
                } else {
                    img2.setVisibility(View.GONE);
                    paytype.remove("2");
                }
                break;
            case R.id.tv_WeChat:
                if (img3.getVisibility() == View.GONE) {
                    img3.setVisibility(View.VISIBLE);
                    paytype.add("1");
                } else {
                    img3.setVisibility(View.GONE);
                    paytype.remove("1");
                }
                break;
            case R.id.ll_buy_coin:
                if (customerIdentity.equals("1")) {
                    if (TextUtils.isEmpty(etPurchaseAmount.getText())) {
                        ToastUtils.showShort(getString(R.string.toast1));
                        showSoftInputFromWindow(etPurchaseAmount);
                        return;
                    } else {
                        if (!TextUtils.isEmpty(etMinimum.getText().toString())
                                && Float.valueOf(etMinimum.getText().toString()) > Float.valueOf(etPurchaseAmount.getText().toString())) {
                            ToastUtils.showShort(getString(R.string.toast2));
                            etMinimum.setText("");
                            showSoftInputFromWindow(etMinimum);
                            return;
                        } else if (!TextUtils.isEmpty(etMaximum.getText().toString())
                                && Float.valueOf(etMaximum.getText().toString()) > Float.valueOf(etPurchaseAmount.getText().toString())) {
                            ToastUtils.showShort(getString(R.string.toast3));
                            etMaximum.setText(etPurchaseAmount.getText());
                            return;
                        } else if (!TextUtils.isEmpty(etMinimum.getText().toString())
                                && !TextUtils.isEmpty(etMaximum.getText().toString())
                                && Float.valueOf(etMinimum.getText().toString()) > Float.valueOf(etMaximum.getText().toString())) {
                            ToastUtils.showShort(getString(R.string.toast4));
                            etMinimum.setText("1");
                            etMaximum.setText(etPurchaseAmount.getText());
                            return;
                        }
                    }

                    if (TextUtils.isEmpty(etUnitPrice.getText())) {
                        ToastUtils.showShort(getString(R.string.hint));
                        showSoftInputFromWindow(etUnitPrice);
                        return;
                    } else if (Float.valueOf(etUnitPrice.getText().toString()) < Float.valueOf(price)) {
                        ToastUtils.showShort(getString(R.string.toast5, price));
                        showSoftInputFromWindow(etUnitPrice);
                        return;
                    }

                    if (TextUtils.isEmpty(etMinimum.getText())) {
                        ToastUtils.showShort(getString(R.string.toast6));
                        showSoftInputFromWindow(etMinimum);
                        return;
                    }
                    if (TextUtils.isEmpty(etMaximum.getText())) {
                        ToastUtils.showShort(getString(R.string.toast7));
                        showSoftInputFromWindow(etMaximum);
                        return;
                    }
                    if (paytype.isEmpty()) {
                        if (count == 0) {
                            ToastUtils.showShort(getString(R.string.toast8));
                            return;
                        } else {
                            ToastUtils.showShort(getString(R.string.toast9));
                            return;
                        }
                    }

                    if (payInfoList.isEmpty()) {
                        ToastUtils.showShort(getString(R.string.toast10));
                        return;
                    }
                }


                if (customerIdentity.equals("1")) {
                    //承兑商
                    popupPayView();

                } else if (customerIdentity.equals("0")) {
                    TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
                    showAnimation.setDuration(350);
                    TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
                    dismissAnimation.setDuration(500);
                    if (count == 0) {
                        if (etPurchaseAmount.getText().length() == 0) {
                            ToastUtils.showShort(R.string.purchase_amount_hint);
                        } else {
                            buyCoinType = QuickPopupBuilder.with(getActivity())
                                    .contentView(R.layout.popup_buycoin)
                                    .config(new QuickPopupConfig()
                                            .withShowAnimation(showAnimation)
                                            .withDismissAnimation(dismissAnimation)
                                            .withClick(R.id.tv1, v1 -> {
                                                toBuyCoin(1);
                                            }, true)
                                            .withClick(R.id.tv2, v2 -> {
                                                toBuyCoin(2);
                                            }, true)
                                            .withClick(R.id.tv3, v3 -> {
                                                toBuyCoin(3);
                                            }, true)
                                            .withClick(R.id.img_exit, null, true)
                                    ).show();
                            buyCoinType.findViewById(R.id.ll_pay).setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (TextUtils.isEmpty(etPurchaseAmount.getText())) {
                            ToastUtils.showShort(getString(R.string.toast1));
                            showSoftInputFromWindow(etPurchaseAmount);
                            return;
                        }
                        paymentType = "";
                        paymentType = StringUtil.join(paytype, ",");
                        if (TextUtils.isEmpty(paymentType)) {
                            ToastUtils.showShort(getString(R.string.toast11));
                            return;
                        }

                        String secret = "currency=" + currency +
                                "&nonce=" + timestamp +
                                "&number=" + etPurchaseAmount.getText().toString() +
                                "&payType=" + paymentType + Constant.SECRET;
                        sign = Sha256.getSHA256(secret);
                        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                                .userSell(timestamp,
                                        etPurchaseAmount.getText().toString(),
                                        currency,
                                        paymentType)
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.otc())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                                .subscribe(s -> {
                                            this.userSell = s;
                                            byCoinsOrAmount(Integer.valueOf(paymentType));
                                        },
                                        this::handleApiError);
                    }
                }
                break;
        }
    }

    private void popupPayView() {
        if (byCoinsOrAmount != null) {
            byCoinsOrAmount.dismiss();
        }
        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(350);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);
        byCoinsOrAmount = QuickPopupBuilder.with(getActivity())
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
                if (customerIdentity.equals("1")) {
                    acceptorDeal(inputPsd);
                } else {
                    toSellCoin(inputPsd);
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

            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            byCoinsOrAmount.dismiss();
        });
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        });
    }


    //获取点击后对应支付信息
//    private void getPayment(int position) {
//        price = findHailangResponse.get(position).getOtc_active().getPrice();//单价
//
//        DecimalFormat df4 = new DecimalFormat("###.00");
//
//        if (buyPatterns.equals("CNY")) {
//            amount = Double.parseDouble(nf.format(Float.parseFloat(String.valueOf(etPurchaseAmount.getText())) / Float.parseFloat(findHailangResponse.get(position).getOtc_active().getPrice())));
//            total = Double.parseDouble(String.valueOf(etPurchaseAmount.getText()));
//        } else {
//            amount = Double.parseDouble(nf.format(Float.parseFloat(String.valueOf(etPurchaseAmount.getText()))));
//            total = Double.parseDouble(df4.format(Double.parseDouble(String.valueOf(etPurchaseAmount.getText())) * Double.parseDouble(findHailangResponse.get(position).getOtc_active().getPrice())));
//        }
//    }

    private void setDrawables(Drawable drawable, TextView textView, String text) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());// 设置边界
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
        textView.setCompoundDrawables(null, drawable, null, null);
        textView.setCompoundDrawablePadding(8);
    }

    private void setDrawable(Drawable drawable, TextView textView, String text) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());// 设置边界
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setCompoundDrawablePadding(8);
    }

    private void acceptorDeal(String pwd) {

        paymentType = StringUtil.join(paytype, ",");

        minimum = TextUtils.isEmpty(etMinimum.getText().toString()) ? minimum = "0" : etMinimum.getText().toString();
        maximums = TextUtils.isEmpty(etMaximum.getText().toString()) ? etPurchaseAmount.getText().toString() : etMaximum.getText().toString();

        String secret = "currency=" + currency +
                "&maxNum=" + maximums +
                "&minNum=" + minimum +
                "&nonce=" + timestamp +
                "&number=" + etPurchaseAmount.getText().toString() +
                "&payPwd=" + MD5Utils.getMD5(pwd) +
                "&payType=" + paymentType +
                "&price=" + etUnitPrice.getText().toString() + Constant.SECRET;
        sign = Sha256.getSHA256(secret);

        if (count == 0) {
            ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                    .acceptorBuy(currency,
                            etMaximum.getText().toString(),
                            etMinimum.getText().toString(),
                            timestamp,
                            etPurchaseAmount.getText().toString(),
                            MD5Utils.getMD5(pwd),
                            paymentType,
                            etUnitPrice.getText().toString())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                    .subscribe(d ->
                            {
                                Intent intent = new Intent(getContext(), AcceptorOrderActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("currency", currency);
                                bundle.putString("number", etPurchaseAmount.getText().toString());
                                bundle.putString("price", etUnitPrice.getText().toString());
                                bundle.putString("maxNum", maximums);
                                bundle.putString("minNum", minimum);
                                bundle.putString("orderId", d);
                                bundle.putString("count", "0");
                                bundle.putString("payType", paymentType);
                                intent.putExtras(bundle);
                                getActivity().startActivity(intent);
                                byCoinsOrAmount.dismiss();
                            },
                            this::handleApiError);
        } else {
            secret = "currency=" + currency +
                    "&maxNum=" + maximums +
                    "&minNum=" + minimum +
                    "&nonce=" + timestamp +
                    "&number=" + etPurchaseAmount.getText().toString() +
                    "&payPwd=" + MD5Utils.getMD5(pwd) +
                    "&payType=" + paymentType +
                    "&price=" + etUnitPrice.getText().toString() +
                    "&rate=" + rate + Constant.SECRET;
            sign = Sha256.getSHA256(secret);
            ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                    .acceptorSell(currency, etMaximum.getText().toString(), etMinimum.getText().toString(), timestamp,
                            etPurchaseAmount.getText().toString(), MD5Utils.getMD5(pwd), paymentType, etUnitPrice.getText().toString(), rate)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                    .subscribe(d ->
                    {
                        Intent intent = new Intent(getContext(), AcceptorOrderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("currency", currency);
                        bundle.putString("number", etPurchaseAmount.getText().toString());
                        bundle.putString("price", etUnitPrice.getText().toString());
                        bundle.putString("maxNum", maximums);
                        bundle.putString("minNum", minimum);
                        bundle.putString("orderId", d);
                        bundle.putString("count", "1");
                        bundle.putString("payType", paymentType);
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        byCoinsOrAmount.dismiss();
                    }, this::handleApiError);
        }
    }

    private void toSellCoin(String pwd) {

        if (byCoinsOrAmount != null) {
            byCoinsOrAmount.dismiss();
        }
        String secret = "buyOrderId=" + userSell.getBuyOrderId() +
                "&nonce=" + timestamp +
                "&number=" + etPurchaseAmount.getText().toString() +
                "&payPwd=" + MD5Utils.getMD5(pwd) +
                "&payType=" + paymentType + Constant.SECRET;
        sign = Sha256.getSHA256(secret);
        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                .confirmUserSell(userSell.getBuyOrderId(),
                        paymentType,
                        timestamp,
                        etPurchaseAmount.getText().toString(),
                        MD5Utils.getMD5(pwd))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                .subscribe(s -> {
                    ToastUtils.showShort(getString(R.string.toast12));

                }, this::handleApiError);
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