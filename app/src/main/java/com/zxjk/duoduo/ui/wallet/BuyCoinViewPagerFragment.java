package com.zxjk.duoduo.ui.wallet;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.QuickOrderRequest;
import com.zxjk.duoduo.bean.response.GetOTCPayInfoResponse;
import com.zxjk.duoduo.bean.response.GetQuickTickerResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.ui.widget.dialog.BuyCoinDialog;
import com.zxjk.duoduo.utils.DataUtils;
import com.zxjk.duoduo.utils.MD5Utils;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

@SuppressLint("CheckResult")
public class BuyCoinViewPagerFragment extends BaseFragment {

    private static final String KEY = "title";
    private int decimalDigits = 5;// 小数的位数
    private int integerDigits = 10;// 整数的位数
    private TextView tvBuyCoinSwitch;
    private LinearLayout llBuyCoin;
    private TextView tvUnitPrice;
    private TextView tvLimit;
    private TextView tvBuyPatterns;
    private TextView tvPurchaseAmount;
    private EditText etPurchaseAmount;
    private RecyclerView recyclerView;
    private TextView btnBuyCoin;
    private BaseQuickAdapter<GetOTCPayInfoResponse.PayTypeListBean, BaseViewHolder> adapter;
    private TextView tvAll;
    private TextView tvQuestions;
    private TextView tvNoviceGuide;

    private String defaultRenegeNumber;
    private String buyPatterns = "CNY";
    private String currency;
    private String logo;
    private Double amount;//购买数量:当前购买类型为CNY时,需要用（输入金额/单价）来获得购买数量,当前购买类型为数量时,直接获取输入框内容
    private Double total;//实付金额:当前类型为CNY时,直接获取输入框内容,当前购买类型为数量时, (输入数量*单价)获得实付金额

    private GetQuickTickerResponse getQuickTickerResponse;

    private QuickPopup byCoinsOrAmount;
    private QuickPopup buyCoinType;

    private NumberFormat nf;
    private String amountScale;
    private String buyType;
    private QuickOrderRequest quickOrderRequest = new QuickOrderRequest();
    private BuyCoinDialog buyCoinDialog;


    public static BuyCoinViewPagerFragment newInstance(String str, String logo, String defaultRenegeNumber, String amountScale, int buyType) {
        BuyCoinViewPagerFragment fragment = new BuyCoinViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, str);
        bundle.putString("logo", logo);
        bundle.putString("DefaultRenegeNumber", defaultRenegeNumber);
        bundle.putString("AmountScale", amountScale);
        bundle.putInt("buyType", buyType);
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
        if (getArguments().getInt("buyType") == 0) {
            buyType = "BUY";
            quickOrderRequest.setType("BUY");
        } else {
            buyType = "SELL";
            quickOrderRequest.setType("SELL");
        }

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
        btnBuyCoin = rootView.findViewById(R.id.btn_buy_coin);
        tvAll = rootView.findViewById(R.id.tv_all);
        tvQuestions = rootView.findViewById(R.id.tv_questions);
        tvNoviceGuide = rootView.findViewById(R.id.tv_novice_guide);
    }

    private void initData() {

        nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(Integer.parseInt(amountScale));

        if (getArguments().getInt("buyType") == 0) {
            if (!MMKVUtils.getInstance().decodeBool("appFirstBuyCoin")) {
                MMKVUtils.getInstance().enCode("appFirstBuyCoin", true);
                NiceDialog niceDialog = new NiceDialog();
                niceDialog.init().setLayoutId(R.layout.dialog_tutorial).setConvertListener(new ViewConvertListener() {
                    @SuppressLint("CheckResult")
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.ll1, v -> {
                            showDialog();
                            dialog.dismiss();
                        });
                        holder.setOnClickListener(R.id.ll2, v -> {
                            Intent intent = new Intent(getActivity(), GuidelinesWebActivity.class);
                            intent.putExtra("url", "http://192.168.1.247/hilamg_tradeGuide/how_buy.html");
                            startActivity(intent);
                            showDialog();
                            dialog.dismiss();
                        });
                    }
                }).setDimAmount(0.5f).setOutCancel(false).show(getChildFragmentManager());
            } else {
                showDialog();
            }
//            tvAll.setVisibility(View.GONE);
        } else {
//            tvAll.setVisibility(View.VISIBLE);
        }
        tvBuyCoinSwitch.setOnClickListener(v -> {
            if (getArguments().getInt("buyType") == 0) {
                if (tvBuyPatterns.getText().equals("CNY")) {
                    tvBuyPatterns.setText(getQuickTickerResponse.getCoinSymbol());
                    buyPatterns = getQuickTickerResponse.getCoinSymbol();
                    decimalDigits = 5;
                    tvPurchaseAmount.setText(R.string.purchase_quantity);
                    etPurchaseAmount.setHint(R.string.purchase_amount_hint);
                    tvBuyCoinSwitch.setText(R.string.buying_coin);
                    quickOrderRequest.setPriceType("1");
                    tvLimit.setText("单笔订单范围:" + getQuickTickerResponse.getMinAmount() + "~" + getQuickTickerResponse.getMaxAmount() + "个");
                } else {
                    tvBuyPatterns.setText("CNY");
                    buyPatterns = "CNY";
                    decimalDigits = 2;
                    tvPurchaseAmount.setText(R.string.purchase_coin);
                    etPurchaseAmount.setHint(R.string.purchase_coin_hint);
                    tvBuyCoinSwitch.setText(R.string.buying_patterns);
                    quickOrderRequest.setPriceType("2");
                    tvLimit.setText(getString(R.string.limit, getQuickTickerResponse.getMinQuota(), getQuickTickerResponse.getMaxQuota()));
                }
                btnBuyCoin.setText(R.string.quick_buy_coins);

            } else {
                if (tvBuyPatterns.getText().equals("CNY")) {
                    tvBuyPatterns.setText(getQuickTickerResponse.getCoinSymbol());
                    buyPatterns = getQuickTickerResponse.getCoinSymbol();
                    decimalDigits = 5;
                    tvPurchaseAmount.setText("出售数量");
                    etPurchaseAmount.setHint("请输入出售数量");
                    tvBuyCoinSwitch.setText("按金额出售");
                    quickOrderRequest.setPriceType("1");
                    tvLimit.setText("单笔订单范围:" + getQuickTickerResponse.getMinAmount() + "~" + getQuickTickerResponse.getMaxAmount() + "个");
                } else {
                    tvBuyPatterns.setText("CNY");
                    buyPatterns = "CNY";
                    decimalDigits = 2;
                    tvPurchaseAmount.setText("出售金额");
                    etPurchaseAmount.setHint("请输入出售金额");
                    tvBuyCoinSwitch.setText("按数量出售");
                    quickOrderRequest.setPriceType("2");
                    tvLimit.setText(getString(R.string.limit, getQuickTickerResponse.getMinQuota(), getQuickTickerResponse.getMaxQuota()));

                }
                btnBuyCoin.setText("一键出售");

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

                if (!TextUtils.isEmpty(etPurchaseAmount.getText())) {
                    if (buyPatterns.equals("CNY")) {
                        if (editable.length() != 0) {
                            if (Double.parseDouble(String.valueOf(editable)) > Double.parseDouble(getQuickTickerResponse.getMaxQuota())) {
                                etPurchaseAmount.setText(getQuickTickerResponse.getMaxQuota());
                                ToastUtils.showShort(getString(R.string.buy_coins_prompt, getQuickTickerResponse.getMaxQuota()));
                            }
                        }
                    } else {
                        if (getArguments().getInt("buyType") == 0) {
                            if (Double.parseDouble(String.valueOf(editable)) > Double.parseDouble(getQuickTickerResponse.getMaxAmount())) {
                                ToastUtils.showShort(getString(R.string.buy_amount_prompt, getQuickTickerResponse.getMaxAmount(), getQuickTickerResponse.getCoinSymbol()));
                                etPurchaseAmount.setText(getQuickTickerResponse.getMaxAmount());
                            }
                        } else {
                            if (Double.parseDouble(String.valueOf(editable)) > Double.parseDouble(getQuickTickerResponse.getMaxAmount())) {
                                ToastUtils.showShort(getString(R.string.buy_amount_prompt_sell, getQuickTickerResponse.getMaxAmount(), getQuickTickerResponse.getCoinSymbol()));
                                etPurchaseAmount.setText(getQuickTickerResponse.getMaxAmount());
                            } else if (Double.parseDouble(getQuickTickerResponse.getBalance()) < Double.parseDouble(getQuickTickerResponse.getMaxAmount()) && Double.parseDouble(getQuickTickerResponse.getBalance()) > Double.parseDouble(getQuickTickerResponse.getMinAmount()) && Double.parseDouble(String.valueOf(editable)) > Double.parseDouble(getQuickTickerResponse.getBalance())) {
                                ToastUtils.showShort(getString(R.string.buy_amount_prompt_sell, getQuickTickerResponse.getBalance(), getQuickTickerResponse.getCoinSymbol()));
                                etPurchaseAmount.setText(getQuickTickerResponse.getBalance());
                            }
                        }
                    }
                }
            }
        });

        llBuyCoin.setOnClickListener(v -> {

            if (etPurchaseAmount.getText().length() == 0) {
                if (getArguments().getInt("buyType") == 0) {
                    if (buyPatterns.equals("CNY")) {
                        ToastUtils.showShort(R.string.purchase_coin_hint);
                        return;
                    } else {
                        ToastUtils.showShort(R.string.purchase_amount_hint);
                        return;
                    }
                } else {
                    ToastUtils.showShort("请输入出售数量");
                    return;
                }
            } else {
                if (buyPatterns.equals("CNY") && Double.parseDouble(String.valueOf(etPurchaseAmount.getText())) < Double.parseDouble(getQuickTickerResponse.getMinQuota())) {
                    ToastUtils.showShort(getString(R.string.buy_coins_prompt2));
                    etPurchaseAmount.setText(getQuickTickerResponse.getMinQuota());
                    return;
                } else if (!buyPatterns.equals("CNY")) {
                    if (getArguments().getInt("buyType") == 0) {
                        if (Double.parseDouble(String.valueOf(etPurchaseAmount.getText())) < Double.parseDouble(getQuickTickerResponse.getMinAmount())) {
                            ToastUtils.showShort(getString(R.string.buy_amount_prompt2, getQuickTickerResponse.getMinAmount(), getQuickTickerResponse.getCoinSymbol()));
                            etPurchaseAmount.setText(getQuickTickerResponse.getMinAmount());
                            return;
                        }
                    } else if (getArguments().getInt("buyType") == 1) {
                        if (Double.parseDouble(getQuickTickerResponse.getBalance()) < Double.parseDouble(getQuickTickerResponse.getMinAmount())) {
                            ToastUtils.showShort("余额小于最小限额无法交易");
                            etPurchaseAmount.setText("");
                            return;
                        } else if (Double.parseDouble(String.valueOf(etPurchaseAmount.getText())) < Double.parseDouble(getQuickTickerResponse.getMinAmount()) && Double.parseDouble(getQuickTickerResponse.getBalance()) > Double.parseDouble(getQuickTickerResponse.getMinAmount())) {
                            ToastUtils.showShort("最小出售数量为" + getQuickTickerResponse.getMinAmount() + getQuickTickerResponse.getCoinSymbol());
                            etPurchaseAmount.setText(getQuickTickerResponse.getMinAmount());
                            return;
                        }
                    }
                }
            }
            if (buyPatterns.equals("CNY")) {
                DecimalFormat df = new DecimalFormat("#.00000");
                Double a1 = (Double.parseDouble(etPurchaseAmount.getText().toString()) / Double.parseDouble(getQuickTickerResponse.getPrice()));
                amount = Double.parseDouble(df.format(a1));
                total = Double.parseDouble(etPurchaseAmount.getText().toString());
            } else {
                DecimalFormat df = new DecimalFormat("#.00");
                Double a1 = (Double.parseDouble(getQuickTickerResponse.getPrice()) * Double.parseDouble(etPurchaseAmount.getText().toString()));
                Double str = Double.parseDouble(df.format(a1));
                amount = Double.parseDouble(etPurchaseAmount.getText().toString());
                total = str;
            }
            getOTCPayInfo();
        });

        tvAll.setOnClickListener(v -> {
            etPurchaseAmount.setText(getQuickTickerResponse.getMaxQuota());
        });

        tvQuestions.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), BuyCoinPaymentActivity.class));
        });

        tvNoviceGuide.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GuidelinesWebActivity.class);
            intent.putExtra("url", "http://192.168.1.247/hilamg_tradeGuide/how_buy.html");
            startActivity(intent);
        });

    }

    //实时行情获取
    private void setSign() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .quickTicker(buyType, currency)
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(data -> {
                    getFindHailangResponse(data);

                    if (getArguments().getInt("buyType") == 0) {
                        tvBuyPatterns.setText(data.getCoinSymbol());
                        buyPatterns = data.getCoinSymbol();
                        decimalDigits = 5;
                        tvPurchaseAmount.setText(R.string.purchase_quantity);
                        etPurchaseAmount.setHint(R.string.purchase_amount_hint);
                        tvBuyCoinSwitch.setText(R.string.buying_coin);
                        btnBuyCoin.setText(R.string.quick_buy_coins);
                    } else {
                        tvBuyPatterns.setText(getQuickTickerResponse.getCoinSymbol());
                        buyPatterns = getQuickTickerResponse.getCoinSymbol();
                        decimalDigits = 5;
                        tvPurchaseAmount.setText("出售数量");
                        etPurchaseAmount.setHint("请输入出售数量");
                        tvBuyCoinSwitch.setText("按金额出售");
                        btnBuyCoin.setText("一键出售");
                    }
                    quickOrderRequest.setPriceType("1");
                    tvUnitPrice.setText(getString(R.string.the_unit_price, data.getPrice(), data.getCurrencySymbol(), data.getCoinSymbol()));
                    tvLimit.setText("单笔订单范围:" + data.getMinAmount() + "~" + data.getMaxAmount() + "个");
                }, this::handleApiError);
    }

    private void getFindHailangResponse(GetQuickTickerResponse findHailangResponse) {
        this.getQuickTickerResponse = findHailangResponse;
    }


    //获取收付款信息
    private void getOTCPayInfo() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getOTCPayInfo(buyType, getQuickTickerResponse.getCoinSymbol())
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(data -> {
                    if (2 == data.getTrade()) {
                        buyCoinDialog = new BuyCoinDialog(getActivity(),
                                "闭市通知",
                                "抱歉，当前时间提供一键买卖的商家处于闭市状态，请过几分钟再来。如闭市过长时间请联系客服投诉。",
                                "我知道了",
                                false);
                        buyCoinDialog.show();
                    } else if (3 == data.getTrade()) {
                        buyCoinDialog = new BuyCoinDialog(getActivity(),
                                "收款提示",
                                "请完善你的收款方式，务必保证提交的收款方式归属人与你实名认证信息相符！",
                                "去完善",
                                false).start(new Intent(getActivity(), AuthentificationOfMessageActivity.class));
                        buyCoinDialog.show();
                    } else {
                        popupBuyCoin(data);
                    }
                }, this::handleApiError);
    }


    //支付方式pop
    private void popupBuyCoin(GetOTCPayInfoResponse getOTCPayInfoResponse) {
        adapter = new BaseQuickAdapter<GetOTCPayInfoResponse.PayTypeListBean, BaseViewHolder>(R.layout.item_pay_popup) {
            @Override
            protected void convert(BaseViewHolder helper, GetOTCPayInfoResponse.PayTypeListBean item) {
                TextView tv = helper.getView(R.id.tv1);
                ImageView imgPreferential = helper.getView(R.id.img_preferential);
                Drawable drawable;
                if (item.getIsDiscounts() == 1) {
                    helper.getView(R.id.tv_isdiscounts).setVisibility(View.VISIBLE);
                }
                if ("EBANK".equals(item.getPayType())) {
                    tv.setText(R.string.bank_card);
                    drawable = getResources().getDrawable(R.drawable.bank_card, null);
                } else if ("ALIPAY".equals(item.getPayType())) {
                    tv.setText(R.string.pay_treasure);
                    drawable = getResources().getDrawable(R.drawable.pay_treasure, null);
                } else {
                    tv.setText(R.string.wechat_pay);
                    drawable = getResources().getDrawable(R.drawable.wechat, null);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                        .getMinimumHeight());// 设置边界

                tv.setCompoundDrawables(drawable, null, null, null);
                tv.setCompoundDrawablePadding(8);

                if (item.getOpen() == 0) {
                    imgPreferential.setVisibility(View.VISIBLE);
                }
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            GetOTCPayInfoResponse.PayTypeListBean payTypeListBean = (GetOTCPayInfoResponse.PayTypeListBean) adapter.getData().get(position);
            if (payTypeListBean.getOpen() == 1) {
                byCoinsOrAmount(payTypeListBean.getPayType(), getQuickTickerResponse.getPrice(), amount, total, position);
            } else {
                ToastUtils.showShort("该支付暂时不可用");
            }
        });

        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(350);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);
        buyCoinType = QuickPopupBuilder.with(getActivity())
                .contentView(R.layout.popup_buycoin)
                .config(new QuickPopupConfig()
                        .withShowAnimation(showAnimation)
                        .withDismissAnimation(dismissAnimation)
                        .withClick(R.id.img_exit, null, true))
                .show();

        recyclerView = buyCoinType.findViewById(R.id.rc_pay);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        if (getArguments().getInt("buyType") == 0) {
            Random random = new Random();
            int n = random.nextInt(getOTCPayInfoResponse.getPayTypeList().size());
            getOTCPayInfoResponse.getPayTypeList().get(n).setIsDiscounts(1);
        }

        adapter.setNewData(getOTCPayInfoResponse.getPayTypeList());
    }


    //支付信息popup
    private void byCoinsOrAmount(String TermOfPayment, String price, Double amount, Double total, int position) {
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
                                {
                                    if ("BUY".equals(buyType)) {
                                        toBuyCoin();
                                    } else {
                                        new NewPayBoard(getContext())
                                                .show(pwd -> {
                                                    quickOrderRequest.setPayPwd(MD5Utils.getMD5(pwd));
                                                    toBuyCoin();
                                                });
                                    }
                                }
                                , true)
                        .withClick(R.id.img_exit, null, true)
                ).show();

        TextView tvSellHint = byCoinsOrAmount.findViewById(R.id.tv_sell_hint);
        if ("SELL".equals(buyType)) {
            tvSellHint.setVisibility(View.VISIBLE);
        }
        TextView tv1 = byCoinsOrAmount.findViewById(R.id.tv1);
        TextView tvTermOfPayment = byCoinsOrAmount.findViewById(R.id.tv_terms_of_payment);
        if ("EBANK".equals(TermOfPayment)) {
            quickOrderRequest.setPayType("EBANK");
            tvTermOfPayment.setText(R.string.bank_card);
        } else if ("ALIPAY".equals(TermOfPayment)) {
            quickOrderRequest.setPayType("ALIPAY");
            tvTermOfPayment.setText(R.string.pay_treasure);
        } else if ("WEIXIN".equals(TermOfPayment)) {
            quickOrderRequest.setPayType("WEIXIN");
            tvTermOfPayment.setText(R.string.wechat);
        }

        TextView tvPrice = byCoinsOrAmount.findViewById(R.id.tv_price);
        tvPrice.setText(price + "\u0020" + getQuickTickerResponse.getCurrencySymbol() + "/" + getQuickTickerResponse.getCoinSymbol());
        TextView tvAmount = byCoinsOrAmount.findViewById(R.id.tv_amount);
        tvAmount.setText(amount + "\u0020" + getQuickTickerResponse.getCoinSymbol());
        TextView tvTotal = byCoinsOrAmount.findViewById(R.id.tv_total);
        tvTotal.setText(total + "\u0020" + getQuickTickerResponse.getCurrencySymbol());
        TextView tvTitle = byCoinsOrAmount.findViewById(R.id.tv_title);
        Button btnBuyOrSell = byCoinsOrAmount.findViewById(R.id.bt_to_buy);
        TextView tv3 = byCoinsOrAmount.findViewById(R.id.tv3);
        TextView tv4 = byCoinsOrAmount.findViewById(R.id.tv4);
        if ("BUY".equals(buyType)) {
            btnBuyOrSell.setText(R.string.Determine_to_buy);
            tv1.setText(R.string.payment_way2);
            tv3.setText("购买数量");
        } else {
            btnBuyOrSell.setText("确定出售");
            tv1.setText("收款方式");
            tv3.setText("出售数量");
            tv4.setText("实收金额");
        }

        Glide.with(this).load(logo).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                resource.setBounds(0, 0, resource.getMinimumWidth(), resource
                        .getMinimumHeight());
                if ("BUY".equals(buyType)) {
                    tvTitle.setText(getString(R.string.buy_coin, getQuickTickerResponse.getCoinSymbol()));
                } else {
                    tvTitle.setText("出售" + getQuickTickerResponse.getCoinSymbol());
                }
                tvTitle.setCompoundDrawables(resource, null, null, null);
                tvTitle.setCompoundDrawablePadding(8);
            }
        });
    }

    //按金额购买和币种购买的接口调用
    private void toBuyCoin() {
        if ("1".equals(quickOrderRequest.getPriceType())) {
            quickOrderRequest.setCoinAmount(etPurchaseAmount.getText().toString());
        } else {
            quickOrderRequest.setTotal(etPurchaseAmount.getText().toString());
        }
        quickOrderRequest.setCoinSymbol(getQuickTickerResponse.getCoinSymbol());
        quickOrderRequest.setCurrency(getQuickTickerResponse.getCurrencySymbol());

        ServiceFactory.getInstance().getBaseService(Api.class)
                .quickOrder(GsonUtils.toJson(quickOrderRequest))
                .compose(RxSchedulers.otc())
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(data -> {
                    if (1 == data.getBizCode()) {
                        buyCoinDialog = new BuyCoinDialog(getActivity(),
                                "实名认证提示",
                                "应第三方金融风控的需求，需要完成实名认证才能进行交易。本平台对用户实名信息不做中心化存储。",
                                "去认证",
                                false).start(new Intent(getActivity(), AuthentificationOfMessageActivity.class));
                        buyCoinDialog.show();
                    } else if (2 == data.getBizCode()) {
                        Intent intent = new Intent(getActivity(), ReceiptTypeActivity.class);
                        intent.putExtra("type", "MOBILE");
                        intent.putExtra("isAuthentification", "1");
                        intent.putExtra("data", GsonUtils.toJson(quickOrderRequest));
                        startActivity(intent);
                    } else {
                        Intent intent;
                        if ("BUY".equals(buyType)) {
                            intent = new Intent(getContext(), BuyCoinPaymentActivity.class);
                            intent.putExtra("befrom", "0");
                        } else {
                            intent = new Intent(getContext(), PurchaseDetailsActivity.class);
                        }
                        intent.putExtra("otherOrderId", data.getOrder().getOtherOrderId());
                        startActivity(intent);
                    }
                }, this::handleApiError);
    }

    private void showDialog() {
        buyCoinDialog = new BuyCoinDialog(getActivity(),
                "交易须知",
                "此应用可以快捷买卖数字货币，但是伪造身份认证信息，以及为他人代买、提币，协助他人犯罪，您将会被司法追责。" + "\n\n鉴于洗钱活动将严重危害数字资产交易的发展，损害用户的正当权益，根据《中华人民共和国反洗钱法》，本产品为全面履行反洗钱和反恐融资法律的相关规定，如遇涉黑或犯罪资金，将拒绝放币，并报由公安机关处理。",
                "我知道了",
                true).setKey("TRADINGNOTES");
        //判断提示框是否显示
        if (!MMKVUtils.getInstance().decodeBool("TRADINGNOTES")) {
            buyCoinDialog.show();
        } else {
            long time = MMKVUtils.getInstance().decodeLong("TRADINGNOTES");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date d1 = df.parse(DataUtils.timeStamp2Date(DataUtils.getCurTimeLong(), "yyyy-MM-dd HH:mm:ss"));
                Date d2 = df.parse(DataUtils.timeStamp2Date(time, "yyyy-MM-dd HH:mm:ss"));
                long diff = d1.getTime() - d2.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
                if (minutes > 1) {
                    MMKVUtils.getInstance().remove("TRADINGNOTES");
                    buyCoinDialog.show();
                }
            } catch (Exception e) {

            }
        }

    }


}