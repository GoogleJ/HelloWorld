package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FindHailangResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.Sha256;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

@SuppressLint("CheckResult")
public class BuyCoinViewPagerFragment extends BaseFragment {

    private static final String KEY = "title";
    private int decimalDigits = 2;// 小数的位数
    private int integerDigits = 10;// 整数的位数
    private String paymentType;
    private String minimum;//最小金额
    private TextView tvBuyCoinSwitch;
    private LinearLayout llBuyCoin;
    private TextView tvUnitPrice;
    private TextView tvLimit;
    private TextView tvBuyPatterns;
    private TextView tvPurchaseAmount;
    private EditText etPurchaseAmount;
    private TextView buyCoinPrompt;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<FindHailangResponse, BaseViewHolder> adapter;

    private String defaultRenegeNumber;
    private String buyPatterns = "CNY";
    private String currency;
    private String logo;
    private String sign;
    private String timestamp;
    private String price;
    private Double amount;//购买数量:当前购买类型为CNY时,需要用（输入金额/单价）来获得购买数量,当前购买类型为数量时,直接获取输入框内容
    private Double total;//实付金额:当前类型为CNY时,直接获取输入框内容,当前购买类型为数量时, (输入数量*单价)获得实付金额

    private ArrayList<FindHailangResponse> findHailangResponse = new ArrayList<>();
    private List<Integer> prices = new ArrayList<>();

    private QuickPopup byCoinsOrAmount;
    private QuickPopup buyCoinType;

    private int lowestPrice = -1;
    private String preferentialPrice;


    private NumberFormat nf;
    private String amountScale;


    public static BuyCoinViewPagerFragment newInstance(String str, String logo, String defaultRenegeNumber, String amountScale) {
        BuyCoinViewPagerFragment fragment = new BuyCoinViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, str);
        bundle.putString("logo", logo);
        bundle.putString("DefaultRenegeNumber", defaultRenegeNumber);
        bundle.putString("AmountScale", amountScale);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_buy_coin_view_pager, container, false);
        currency = getArguments().getString(KEY);
        logo = getArguments().getString("logo");
        amountScale = getArguments().getString("AmountScale");
        defaultRenegeNumber = getArguments().getString("DefaultRenegeNumber");


        initView();

        setSign();

        initData();

        return rootView;
    }

    private void initView() {
        tvBuyCoinSwitch = rootView.findViewById(R.id.tv_buy_coin_switch);
        llBuyCoin = rootView.findViewById(R.id.ll_buy_coin);
        tvUnitPrice = rootView.findViewById(R.id.tv_unit_price);
        tvLimit = rootView.findViewById(R.id.tv_limit);
        tvBuyPatterns = rootView.findViewById(R.id.tv_buy_patterns);
        tvPurchaseAmount = rootView.findViewById(R.id.tv_purchase_amount);
        etPurchaseAmount = rootView.findViewById(R.id.et_purchase_amount);
        buyCoinPrompt = rootView.findViewById(R.id.buy_coin_prompt);
    }

    private void initData() {
        nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(Integer.parseInt(amountScale));

        buyCoinPrompt.setText(getString(R.string.buy_coin_prompt, defaultRenegeNumber));
        tvBuyCoinSwitch.setOnClickListener(v -> {
            if (tvBuyPatterns.getText().equals("CNY")) {
                tvBuyPatterns.setText(currency);
                buyPatterns = currency;
                decimalDigits = Integer.parseInt(amountScale);
                tvPurchaseAmount.setText(R.string.purchase_quantity);
                etPurchaseAmount.setHint(R.string.purchase_amount_hint);
                tvBuyCoinSwitch.setText(R.string.buying_coin);
            } else {
                tvBuyPatterns.setText("CNY");
                buyPatterns = "CNY";
                decimalDigits = 2;
                tvPurchaseAmount.setText(R.string.purchase_coin);
                etPurchaseAmount.setHint(R.string.purchase_coin_hint);
                tvBuyCoinSwitch.setText(R.string.buying_patterns);
            }
            etPurchaseAmount.setText("");
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
                //最大限度金额

                DecimalFormat df = new DecimalFormat("#.00");
                Double a1 = (Double.parseDouble(findHailangResponse.get(0).getOtc_active().getAmount()) - Double.parseDouble(findHailangResponse.get(0).getOtc_active().getFreezed())) * Double.parseDouble(findHailangResponse.get(0).getOtc_active().getPrice());
                Double str = Double.parseDouble(df.format(a1));
                if (!TextUtils.isEmpty(etPurchaseAmount.getText())) {
                    if (buyPatterns.equals("CNY")) {
                        if (editable.length() != 0) {
                            if (Double.parseDouble(String.valueOf(editable)) > str) {
                                etPurchaseAmount.setText(String.valueOf(a1));
                                ToastUtils.showShort(getString(R.string.buy_coins_prompt, String.valueOf(str)));
                            }
                        }
                    } else {
                        //(最大金额/当前价格)获得最多可购买币的数量
                        Double amount = Double.parseDouble(nf.format(Double.parseDouble(findHailangResponse.get(0).getOtc_active().getAmount()) - Double.parseDouble(findHailangResponse.get(0).getOtc_active().getFreezed())).replace(",",""));

                        if (Double.parseDouble(String.valueOf(editable)) > amount) {
                            ToastUtils.showShort(getString(R.string.buy_amount_prompt, amount.toString(), currency));
                            etPurchaseAmount.setText(String.valueOf(amount));
                        }
                    }
                }
            }
        });

        for (int i = 0; i < findHailangResponse.size() - 1; i++) {
            if (!TextUtils.isEmpty(preferentialPrice)) {
                if (Float.parseFloat(preferentialPrice) > Float.parseFloat(findHailangResponse.get(i + 1).getOtc_active().getPrice())) {
                    preferentialPrice = findHailangResponse.get(i + 1).getOtc_active().getPrice();
                    prices.add(findHailangResponse.get(i + 1).getPayment_type());
                }
            } else {
                if (Float.parseFloat(findHailangResponse.get(i).getOtc_active().getPrice()) < Float.parseFloat(findHailangResponse.get(i + 1).getOtc_active().getPrice())) {
                    preferentialPrice = findHailangResponse.get(i).getOtc_active().getPrice();
                    prices.add(findHailangResponse.get(i).getPayment_type());
                } else {
                    preferentialPrice = findHailangResponse.get(i + 1).getOtc_active().getPrice();
                    prices.add(findHailangResponse.get(i + 1).getPayment_type());
                }
            }
        }

        adapter = new BaseQuickAdapter<FindHailangResponse, BaseViewHolder>(R.layout.item_pay_popup) {
            @Override
            protected void convert(BaseViewHolder helper, FindHailangResponse item) {
                TextView tv = helper.getView(R.id.tv1);
                Drawable drawable;
                if (item.getPayment_type() == 1) {
                    drawable = getResources().getDrawable(R.drawable.bank_card, null);
                } else if (item.getPayment_type() == 2) {
                    drawable = getResources().getDrawable(R.drawable.pay_treasure, null);
                } else {
                    drawable = getResources().getDrawable(R.drawable.wechat, null);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                        .getMinimumHeight());// 设置边界

                tv.setCompoundDrawables(drawable, null, null, null);
                tv.setCompoundDrawablePadding(8);

                if (prices.size() != 0) {
                    if (prices.size() < findHailangResponse.size()) {
                        for (int findHailangResponse : prices)
                            if (findHailangResponse == item.getPayment_type()) {
                                ImageView imageView = buyCoinType.findViewById(R.id.img_preferential);
                                imageView.setVisibility(View.VISIBLE);
                            }
                    }
                }
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            FindHailangResponse findHailangResponse = (FindHailangResponse) adapter.getData().get(position);
            getPayment(position);
            if (findHailangResponse.getPayment_type() == 1) {
                byCoinsOrAmount(1, price, amount, total, position);
            } else if (findHailangResponse.getPayment_type() == 2) {
                byCoinsOrAmount(2, price, amount, total, position);
            } else {
                byCoinsOrAmount(3, price, amount, total, position);
            }
        });


        llBuyCoin.setOnClickListener(v -> {

            minimum = findHailangResponse.get(0).getOtc_active().getQuota().substring(0, findHailangResponse.get(0).getOtc_active().getQuota().indexOf("~"));

            if (etPurchaseAmount.getText().length() == 0) {
                if (buyPatterns.equals("CNY")) {
                    ToastUtils.showShort(R.string.purchase_coin_hint);
                } else {
                    ToastUtils.showShort(R.string.purchase_amount_hint);
                }
            } else {
                if (buyPatterns.equals("CNY") && Float.parseFloat(String.valueOf(etPurchaseAmount.getText())) < Float.parseFloat(minimum)) {
                    ToastUtils.showShort(getString(R.string.buy_coins_prompt2));
                } else if (!buyPatterns.equals("CNY") && Float.parseFloat(String.valueOf(etPurchaseAmount.getText())) < (Float.parseFloat(minimum) / Float.parseFloat(findHailangResponse.get(0).getOtc_active().getPrice()))) {
                    ToastUtils.showShort(getString(R.string.buy_amount_prompt2, String.valueOf(Float.parseFloat(minimum) / Float.parseFloat(findHailangResponse.get(0).getOtc_active().getPrice())), currency));
                } else {

                    TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
                    showAnimation.setDuration(350);
                    TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
                    dismissAnimation.setDuration(500);
                    buyCoinType = QuickPopupBuilder.with(getActivity())
                            .contentView(R.layout.popup_buycoin)
                            .config(new QuickPopupConfig()
                                    .withShowAnimation(showAnimation)
                                    .withDismissAnimation(dismissAnimation)
                                    .withClick(R.id.img_exit, null, true)
                            ).show();

                    recyclerView = buyCoinType.findViewById(R.id.rc_pay);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);

                    adapter.setNewData(findHailangResponse);

                }
            }
        });
    }


    private void setSign() {

        long timeStampSec = System.currentTimeMillis() / 1000;
        timestamp = String.format("%010d", timeStampSec);

        String secret = "currency=" + currency + "&nonce=" + timestamp + Constant.SECRET;
        sign = Sha256.getSHA256(secret);

        ServiceFactory.getInstance().otcService(Constant.OTC_URL, sign, Api.class)
                .findhailang(currency, timestamp)
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(data -> {
                    getFindHailangResponse(data);
                    String miniQuota = findHailangResponse.get(0).getOtc_active().getQuota().substring(0, findHailangResponse.get(0).getOtc_active().getQuota().indexOf("~"));
                    DecimalFormat df = new DecimalFormat("#.00");
                    Double a1 = (Double.parseDouble(findHailangResponse.get(0).getOtc_active().getAmount()) - Double.parseDouble(findHailangResponse.get(0).getOtc_active().getFreezed())) * Double.parseDouble(findHailangResponse.get(0).getOtc_active().getPrice());
                    tvUnitPrice.setText(getString(R.string.the_unit_price, data.get(0).getOtc_active().getPrice(), data.get(0).getOtc_active().getCurrency_type(), data.get(0).getOtc_active().getCurrency()));
                    tvLimit.setText(getString(R.string.limit, miniQuota, df.format(a1)));

                }, this::handleApiError);
    }

    private void getFindHailangResponse(ArrayList<FindHailangResponse> findHailangResponse) {
        this.findHailangResponse = findHailangResponse;
    }

    //支付信息popup
    private void byCoinsOrAmount(int TermOfPayment, String price, Double amount, Double total, int position) {
        buyCoinType.dismiss();
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
                                        toBuyCoin()
                                , true)
                        .withClick(R.id.img_exit, null, true)
                ).show();

        TextView tvTermOfPayment = byCoinsOrAmount.findViewById(R.id.tv_terms_of_payment);
        if (TermOfPayment == 1) {
            tvTermOfPayment.setText(R.string.bank_card);
            paymentType = "1";
        } else if (TermOfPayment == 2) {
            tvTermOfPayment.setText(R.string.pay_treasure);
            paymentType = "2";
        } else {
            tvTermOfPayment.setText(R.string.wechat_pay);
            paymentType = "3";
        }
        TextView tvPrice = byCoinsOrAmount.findViewById(R.id.tv_price);
        tvPrice.setText(price + "\u0020" + findHailangResponse.get(position).getOtc_active().getCurrency_type() + "/" + findHailangResponse.get(position).getOtc_active().getCurrency());
        TextView tvAmount = byCoinsOrAmount.findViewById(R.id.tv_amount);
        tvAmount.setText(amount + "\u0020" + findHailangResponse.get(position).getOtc_active().getCurrency());
        TextView tvTotal = byCoinsOrAmount.findViewById(R.id.tv_total);
        tvTotal.setText(total + "\u0020" + findHailangResponse.get(position).getOtc_active().getCurrency_type());
        TextView tvTitle = byCoinsOrAmount.findViewById(R.id.tv_title);

        Glide.with(this).load(logo).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                resource.setBounds(0, 0, resource.getMinimumWidth(), resource
                        .getMinimumHeight());
                tvTitle.setText(getString(R.string.buy_coin, currency));
                tvTitle.setCompoundDrawables(resource, null, null, null);
                tvTitle.setCompoundDrawablePadding(8);
            }
        });
    }

    //按金额购买和币种购买的接口调用
    private void toBuyCoin() {
        if (tvBuyPatterns.getText().equals("CNY")) {

            String secret = "active_id=" + findHailangResponse.get(0).getOtc_active().getActive_id() +
                    "&by_cny=" + etPurchaseAmount.getText().toString() +
                    "&currency=" + currency +
                    "&nonce=" + timestamp +
                    "&price=" + findHailangResponse.get(0).getOtc_active().getPrice() +
                    "&user_id=" + Constant.USERID + Constant.SECRET;
            sign = Sha256.getSHA256(secret);
            ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                    .byCoins(String.valueOf(findHailangResponse.get(0).getOtc_active().getActive_id()),
                            etPurchaseAmount.getText().toString(),
                            findHailangResponse.get(0).getOtc_active().getCurrency(),
                            timestamp,
                            findHailangResponse.get(0).getOtc_active().getPrice(),
                            String.valueOf(findHailangResponse.get(0).getOtc_active().getUser_id()),
                            paymentType)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.otc())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                    .subscribe(s -> {
                        Intent intent = new Intent(getContext(), PurchaseDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("ByBoinsResponse", s); //放入所需要传递的值
                        intent.putExtra("currency", currency);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        etPurchaseAmount.setText("");
                    }, this::handleApiError);
        } else {

            String secret = "active_id=" + findHailangResponse.get(0).getOtc_active().getActive_id() +
                    "&by_amount=" + etPurchaseAmount.getText().toString() +
                    "&currency=" + currency +
                    "&nonce=" + timestamp +
                    "&user_id=" + Constant.USERID + Constant.SECRET;
            sign = Sha256.getSHA256(secret);

            ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                    .byAmount(String.valueOf(findHailangResponse.get(0).getOtc_active().getUser_id()),
                            String.valueOf(findHailangResponse.get(0).getOtc_active().getCurrency()),
                            etPurchaseAmount.getText().toString(),
                            String.valueOf(findHailangResponse.get(0).getOtc_active().getActive_id()),
                            timestamp,
                            paymentType)
                    .compose(RxSchedulers.otc())
                    .compose(RxSchedulers.ioObserver())
                    .compose(bindToLifecycle())
                    .subscribe(s -> {
                        Intent intent = new Intent(getContext(), PurchaseDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("ByBoinsResponse", s);
                        intent.putExtra("currency", currency);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }, this::handleApiError);
        }
    }


    //获取点击后对应支付信息
    private void getPayment(int position) {
        price = findHailangResponse.get(position).getOtc_active().getPrice();//单价

        DecimalFormat df4 = new DecimalFormat("###.00");

        if (buyPatterns.equals("CNY")) {
            amount = Double.parseDouble(nf.format(Float.parseFloat(String.valueOf(etPurchaseAmount.getText())) / Float.parseFloat(findHailangResponse.get(position).getOtc_active().getPrice())));
            total = Double.parseDouble(String.valueOf(etPurchaseAmount.getText()));
        } else {
            amount = Double.parseDouble(nf.format(Float.parseFloat(String.valueOf(etPurchaseAmount.getText()))));
            total = Double.parseDouble(df4.format(Double.parseDouble(String.valueOf(etPurchaseAmount.getText())) * Double.parseDouble(findHailangResponse.get(position).getOtc_active().getPrice())));
        }
    }
}