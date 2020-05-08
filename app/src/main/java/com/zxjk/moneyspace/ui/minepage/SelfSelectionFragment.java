package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetBuyList;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseFragment;
import com.zxjk.moneyspace.ui.minepage.wallet.PurchaseDetailsActivity;
import com.zxjk.moneyspace.ui.widget.NewsLoadMoreView;
import com.zxjk.moneyspace.ui.widget.PayPsdInputView;
import com.zxjk.moneyspace.utils.MD5Utils;
import com.zxjk.moneyspace.utils.Sha256;

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

public class SelfSelectionFragment extends BaseFragment {


    private RecyclerView recyclerView;
    private RecyclerView rcPayType;
    private BaseQuickAdapter<GetBuyList, BaseViewHolder> adapter;
    private BaseQuickAdapter<PayType, BaseViewHolder> adapterPay;
    private SwipeRefreshLayout swipeRefreshLayout;
    private QuickPopup byCoinSelfSelectionPopup;
    private QuickPopup showPayTypePopup;
    private QuickPopup confirmPopup;
    private QuickPopup pwdPopup;
    private TranslateAnimation showAnimation;
    private TranslateAnimation dismissAnimation;
    private List<GetBuyList> getBuyList = new ArrayList<>();
    private PayPsdInputView editText;
    private View inflate;

    private int count;
    private int page = 0;
    private int numsPerPage = 10;
    private String timestamp;
    private String sign;
    private String currency;
    private String money;
    private List<PayType> payTypeList = new ArrayList<>();
    private String number1;
    private String number2;
    private String customerIdentity;
    private int decimalDigits2 = 4;// 小数的位数
    private int integerDigits2 = 10;// 整数的位数

    public static SelfSelectionFragment newInstance(String currency, int count, String customerIdentity) {

        SelfSelectionFragment fragment = new SelfSelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("count", count);
        bundle.putString("currency", currency);
        bundle.putString("customerIdentity", customerIdentity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_self_selection, container, false);

        count = getArguments().getInt("count");
        currency = getArguments().getString("currency");
        customerIdentity = getArguments().getString("customerIdentity");

        initView();

        initData();

        return rootView;
    }

    private void initView() {
        recyclerView = rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
    }

    private void initData() {
        showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(350);
        dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);

        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(currentTime);
        timestamp = dataOne(formatter.format(date));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#4585F5"));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 0;
            if (count == 0) {
                getBuyList();
            } else {
                getSellList();
            }
        });
        if (count == 0) {
            getBuyList();
        } else {
            getSellList();
        }
        adapter = new BaseQuickAdapter<GetBuyList, BaseViewHolder>(R.layout.item_self_selection) {
            @Override
            protected void convert(BaseViewHolder helper, GetBuyList item) {
                helper.setText(R.id.tv_nick, item.getNick()).
                        setText(R.id.tv_un_sale_num, getString(R.string.the_number2) + item.getUnBoughtNum() + currency).
                        setText(R.id.tv_limit, getString(R.string.limit2) + item.getMinNum() + "-" + item.getMaxNum()).
                        setText(R.id.tv_price, item.getPrice());
                TextView tvBuyCoin = helper.getView(R.id.tv_buy_coin);
                TextView tvUnSaleNum = helper.getView(R.id.tv_un_sale_num);
                if (count == 0) {
                    tvUnSaleNum.setText(getString(R.string.the_number2) + item.getUnSaleNum() + currency);
                } else {
                    tvBuyCoin.setBackground(getResources().getDrawable(R.drawable.shape_self_select, null));
                    tvBuyCoin.setTextColor(getResources().getColor(R.color.black, null));
                    tvUnSaleNum.setText(getString(R.string.the_number2) + item.getUnBoughtNum() + currency);
                    tvBuyCoin.setText(R.string.sell);
                }
                String getPayType = item.getPayType();
                String[] strArr = getPayType.split(",");
                payTypeList.clear();
                for (int i = 0; i < strArr.length; i++) {
                    PayType p = new PayType();
                    p.setPayType(strArr[i]);
                    payTypeList.add(p);
                }
                tvBuyCoin.setOnClickListener(v -> {
                    showSelfSelectionPopup(item);
                });


                BaseQuickAdapter<PayType, BaseViewHolder> adapter = new BaseQuickAdapter<PayType, BaseViewHolder>(R.layout.item_rc_paytype) {
                    @Override
                    protected void convert(BaseViewHolder helper, PayType item) {
                        ImageView img = helper.getView(R.id.img_pay_type);

                        if (item.getPayType().equals("1")) {
                            img.setBackground(getResources().getDrawable(R.drawable.ic_selfselection_wechat, null));
                        } else if (item.getPayType().equals("2")) {
                            img.setBackground(getResources().getDrawable(R.drawable.ic_selfselection_alipay, null));
                        } else {
                            img.setBackground(getResources().getDrawable(R.drawable.ic_selfselection_bank, null));
                        }
                    }
                };

                RecyclerView rc = helper.getView(R.id.recycler_pay_type);
                LinearLayoutManager layout = new LinearLayoutManager(getActivity());
                layout.setOrientation(LinearLayoutManager.HORIZONTAL);//设置为横向排列
                rc.setLayoutManager(layout);
                rc.setAdapter(adapter);

                adapter.setNewData(payTypeList);
            }
        };


        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.setLoadMoreView(new NewsLoadMoreView());
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(() -> {
            if (count == 0) {
                getBuyList();
            } else {
                getSellList();
            }
        }, recyclerView);

        inflate = LayoutInflater.from(getActivity()).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = inflate.findViewById(R.id.tv);
        ImageView iv = inflate.findViewById(R.id.iv);
        iv.setImageResource(R.drawable.ic_empty_orders);
        tv.setText(getString(R.string.no_data));

    }

    @SuppressLint("CheckResult")
    private void getBuyList() {
        String secret = "currency=" + currency +
                "&nonce=" + timestamp +
                "&pageNoStr=" + page +
                "&pageSizeStr=" + numsPerPage + Constant.SECRET;
        sign = Sha256.getSHA256(secret);
        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                .getBuyList(currency,
                        timestamp,
                        String.valueOf(page),
                        String.valueOf(numsPerPage))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .doOnTerminate(() -> (swipeRefreshLayout).setRefreshing(false))
                .subscribe(s -> {
                    page += 1;
                    getBuyList.addAll(s);
                    if (page == 1) {
                        adapter.setNewData(s);
                        adapter.disableLoadMoreIfNotFullPage();
                    } else {
                        adapter.addData(s);
                        if (s.size() >= numsPerPage) {
                            adapter.loadMoreComplete();
                        } else {
                            adapter.loadMoreEnd(false);
                        }
                    }
                    if(s.size() == 0){
                        adapter.setEmptyView(inflate);
                    }
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    private void getSellList() {
        String secret = "currency=" + currency +
                "&nonce=" + timestamp +
                "&pageNoStr=" + page +
                "&pageSizeStr=" + numsPerPage + Constant.SECRET;
        sign = Sha256.getSHA256(secret);
        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                .getSellList(currency,
                        timestamp,
                        String.valueOf(page),
                        String.valueOf(numsPerPage))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .doOnTerminate(() -> (swipeRefreshLayout).setRefreshing(false))
                .subscribe(s -> {
                    page += 1;
                    getBuyList.addAll(s);
                    if (page == 1) {
                        adapter.setNewData(s);
                        adapter.disableLoadMoreIfNotFullPage();
                    } else {
                        adapter.addData(s);
                        if (s.size() >= numsPerPage) {
                            adapter.loadMoreComplete();
                        } else {
                            adapter.loadMoreEnd(false);
                        }
                    }
                    if(s.size() == 0){
                        adapter.setEmptyView(inflate);
                    }
                }, this::handleApiError);
    }

    private void showSelfSelectionPopup(GetBuyList getBuyList) {
        TextView tvCurrency;
        TextView tvPrice;
        EditText etSellCount;
        TextView tvCurrency2;
        TextView tvCountAll;
        TextView tvLimit;
        TextView tvUnSaleNum;
        TextView tvMoney;
        TextView tvBuyCoin;
        byCoinSelfSelectionPopup = QuickPopupBuilder.with(getActivity())
                .contentView(R.layout.popup_buy_coins_or_self_selection)
                .config(new QuickPopupConfig()
                        .withShowAnimation(showAnimation)
                        .withDismissAnimation(dismissAnimation)
                        .withClick(R.id.img_exit, null, true)
                ).show();
        tvCurrency = byCoinSelfSelectionPopup.findViewById(R.id.tv_currency);
        tvPrice = byCoinSelfSelectionPopup.findViewById(R.id.tv_price);
        etSellCount = byCoinSelfSelectionPopup.findViewById(R.id.et_sell_count);
        tvCurrency2 = byCoinSelfSelectionPopup.findViewById(R.id.tv_currency2);
        tvCountAll = byCoinSelfSelectionPopup.findViewById(R.id.tv_count_all);
        tvLimit = byCoinSelfSelectionPopup.findViewById(R.id.tv_limit);
        tvUnSaleNum = byCoinSelfSelectionPopup.findViewById(R.id.tv_un_sale_num);
        tvBuyCoin = byCoinSelfSelectionPopup.findViewById(R.id.tv_buy_coin);
        tvMoney = byCoinSelfSelectionPopup.findViewById(R.id.tv_money);
        ImageView imgExit = byCoinSelfSelectionPopup.findViewById(R.id.img_exit);

        if (count == 0) {
            tvCountAll.setOnClickListener(v -> etSellCount.setText(getBuyList.getUnSaleNum()));
        } else {
            tvCountAll.setOnClickListener(v -> etSellCount.setText(getBuyList.getUnBoughtNum()));
        }
        tvCurrency2.setText(currency);
        tvLimit.setText(getString(R.string.limit) + getBuyList.getMinNum() + "-" + getBuyList.getMaxNum());
        tvCurrency.setText(getString(R.string.sell)+currency);
        tvPrice.setText(getBuyList.getPrice());
        tvUnSaleNum.setText("0.0000" + currency);

        tvBuyCoin.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etSellCount.getText().toString())) {
                ToastUtils.showShort(getString(R.string.please_enter_quantity));
                return;
            }
            if (Float.valueOf(etSellCount.getText().toString()) < Float.valueOf(getBuyList.getMinNum()) || Float.valueOf(etSellCount.getText().toString()) > Float.valueOf(getBuyList.getMaxNum())) {
                ToastUtils.showShort(getString(R.string.please_evaluate_within_the_limit));
                return;
            }
            number2 = number1;
            showPayPopup(getBuyList);
            InputMethodManager imm = (InputMethodManager) etSellCount.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(etSellCount.getWindowToken(), 0);
        });

        imgExit.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) etSellCount.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(etSellCount.getWindowToken(), 0);
            byCoinSelfSelectionPopup.dismiss();
        });

        etSellCount.addTextChangedListener(new TextWatcher() {
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
                        etSellCount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(integerDigits2 + decimalDigits2 + 1)});
                    }
                    if (s.length() - 1 - s.indexOf(".") > decimalDigits2) {
                        s = s.substring(0,
                                s.indexOf(".") + decimalDigits2 + 1);
                        editable.replace(0, editable.length(), s.trim());
                    }
                } else {
                    if (integerDigits2 > 0) {
                        etSellCount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(integerDigits2 + 1)});
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
                number1 = editable.toString();
                if (!TextUtils.isEmpty(number1)) {
                    tvUnSaleNum.setText(number1 + currency);
                    money = String.valueOf(Float.valueOf(getBuyList.getPrice()) * Float.valueOf(number1));
                    tvMoney.setText("¥ " + money);
                } else {
                    tvUnSaleNum.setText("0.0000" + currency);
                    tvMoney.setText("¥ 0.00");
                }
            }
        });
    }


    private void showPayPopup(GetBuyList getBuyList) {

        if (byCoinSelfSelectionPopup != null) {
            byCoinSelfSelectionPopup.dismiss();
        }

        String getPayType = getBuyList.getPayType();
        String[] strArr = getPayType.split(",");
        payTypeList.clear();
        for (int i = 0; i < strArr.length; i++) {
            PayType p = new PayType();
            p.setPayType(strArr[i]);
            payTypeList.add(p);
        }

        adapterPay = new BaseQuickAdapter<PayType, BaseViewHolder>(R.layout.item_pay_popup) {
            @Override
            protected void convert(BaseViewHolder helper, PayType item) {

                TextView tv = helper.getView(R.id.tv1);
                Drawable drawable;
                if (item.getPayType().equals("3")) {
                    tv.setText(R.string.bank_card);
                    drawable = getResources().getDrawable(R.drawable.ic_selfselection_bank, null);
                } else if (item.getPayType().equals("2")) {
                    tv.setText(R.string.pay_treasure);
                    drawable = getResources().getDrawable(R.drawable.ic_selfselection_alipay, null);
                } else {
                    tv.setText(R.string.wechat_pay);
                    drawable = getResources().getDrawable(R.drawable.ic_selfselection_wechat, null);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                        .getMinimumHeight());// 设置边界

                tv.setCompoundDrawables(drawable, null, null, null);
                tv.setCompoundDrawablePadding(8);
            }
        };

        adapterPay.setOnItemClickListener((adapter, view, position) -> {
            confirmPopup(getBuyList, (PayType) adapter.getData().get(position));
        });

        showPayTypePopup = QuickPopupBuilder.with(getActivity())
                .contentView(R.layout.popup_buycoin)
                .config(new QuickPopupConfig()
                                .withShowAnimation(showAnimation)
                                .withDismissAnimation(dismissAnimation)
                                .withClick(R.id.bt_to_buy, v -> {
//                                    popupPayView();
                                }, true)
                                .withClick(R.id.img_exit, null, true)
                ).show();

        rcPayType = showPayTypePopup.findViewById(R.id.rc_pay);
        rcPayType.setVisibility(View.VISIBLE);
        rcPayType.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcPayType.setAdapter(adapterPay);
        adapterPay.setNewData(payTypeList);
    }

    private void confirmPopup(GetBuyList getBuyList, PayType payType) {
        if (showPayTypePopup != null) {
            showPayTypePopup.dismiss();
        }
        confirmPopup = QuickPopupBuilder.with(getActivity())
                .contentView(R.layout.popup_buy_coins_or_amount)
                .config(new QuickPopupConfig()
                        .withShowAnimation(showAnimation)
                        .withDismissAnimation(dismissAnimation)
                        .withClick(R.id.bt_to_buy, v -> popupPayView(getBuyList, payType), true)
                        .withClick(R.id.img_exit, null, true)
                ).show();
        TextView tvTermOfPayment = confirmPopup.findViewById(R.id.tv_terms_of_payment);
        if (payType.getPayType().equals("3")) {
            setDrawables(getResources().getDrawable(R.drawable.ic_selfselection_bank, null), tvTermOfPayment, getString(R.string.bank_card));
        } else if (payType.getPayType().equals("2")) {
            setDrawables(getResources().getDrawable(R.drawable.ic_selfselection_alipay, null), tvTermOfPayment, getString(R.string.pay_treasure));
        } else {
            setDrawables(getResources().getDrawable(R.drawable.ic_selfselection_wechat, null), tvTermOfPayment, getString(R.string.wechat_pay));
        }
        TextView tvPrice = confirmPopup.findViewById(R.id.tv_price);
        tvPrice.setText(getBuyList.getPrice() + "\u0020" + "CNY" + "/" + getBuyList.getCurrency());
        TextView tvAmount = confirmPopup.findViewById(R.id.tv_amount);
        TextView tvTitle = confirmPopup.findViewById(R.id.tv_title);
        TextView btToBuy = confirmPopup.findViewById(R.id.bt_to_buy);
        if (count == 0) {
            tvTitle.setText(R.string.confirm_the_purchase);
            btToBuy.setText(R.string.confirm_the_purchase);
        } else {
            btToBuy.setText(R.string.confirm_to_sell);
            tvTitle.setText(R.string.confirm_to_sell);
        }
        tvAmount.setText(number2);
        TextView tvTotal = confirmPopup.findViewById(R.id.tv_total);
        tvTotal.setText("¥ " + money);

    }


    private void popupPayView(GetBuyList getBuyList, PayType payType) {
        if (confirmPopup != null) {
            confirmPopup.dismiss();
        }
        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(350);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);
        pwdPopup = QuickPopupBuilder.with(getActivity())
                .contentView(R.layout.popup_pay_view)
                .config(new QuickPopupConfig()
                        .withShowAnimation(showAnimation)
                        .withDismissAnimation(dismissAnimation)
                ).show();
        editText = pwdPopup.findViewById(R.id.m_set_payment_pwd_edit);
        pwdPopup.setAutoShowInputMethod(editText, true);

        editText.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {

            }

            @Override
            public void onEqual(String psd) {

            }

            @Override
            public void inputFinished(String inputPsd) {
                if (count == 1) {
                    confirmUserSell(getBuyList, payType, inputPsd);
                    pwdPopup.dismiss();
                } else {
                    userConfirmBuy(getBuyList, payType, inputPsd);
                    pwdPopup.dismiss();
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
        pwdPopup.findViewById(R.id.tv_exit).setOnClickListener(v -> {

            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            pwdPopup.dismiss();
        });
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        });
    }


    @SuppressLint("CheckResult")
    private void confirmUserSell(GetBuyList getBuyList, PayType payType, String pwd) {
        String secret = "buyOrderId=" + getBuyList.getBuyOrderId() +
                "&nonce=" + timestamp +
                "&number=" + number2 +
                "&payPwd=" + MD5Utils.getMD5(pwd) +
                "&payType=" + payType.getPayType() + Constant.SECRET;
        sign = Sha256.getSHA256(secret);
        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                .confirmUserSell(getBuyList.getBuyOrderId(),
                        payType.getPayType(),
                        timestamp,
                        number2,
                        MD5Utils.getMD5(pwd))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .doOnTerminate(() -> (swipeRefreshLayout).setRefreshing(false))
                .subscribe(s -> {
                    s.setBuyOrSell("1");
                    Intent intent = new Intent(getContext(), PurchaseDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("GetOrderInfoById", s);
                    bundle.putString("count", String.valueOf(count));
                    bundle.putString("orderId", s.getBothOrderId());
                    bundle.putString("customerIdentity", customerIdentity);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    private void userConfirmBuy(GetBuyList getBuyList, PayType payType, String pwd) {
        String secret = "nonce=" + timestamp +
                "&number=" + number2 +
                "&payType=" + payType.getPayType() +
                "&sellOrderId=" + getBuyList.getSellOrderId() + Constant.SECRET;
        sign = Sha256.getSHA256(secret);
        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                .userConfirmBuy(getBuyList.getSellOrderId(),
                        payType.getPayType(),
                        timestamp,
                        number2)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .doOnTerminate(() -> (swipeRefreshLayout).setRefreshing(false))
                .subscribe(s -> {
                    s.setBuyOrSell("0");
                    Intent intent = new Intent(getContext(), PurchaseDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("GetOrderInfoById", s);
                    bundle.putString("count", String.valueOf(count));
                    bundle.putString("orderId", s.getBothOrderId());
                    bundle.putString("customerIdentity", customerIdentity);
                    intent.putExtras(bundle);
                    startActivity(intent);
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

    private void setDrawables(Drawable drawable, TextView textView, String text) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());// 设置边界
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setCompoundDrawablePadding(8);
    }

    public class PayType {
        private String payType;

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 0;
        initData();
    }
}
