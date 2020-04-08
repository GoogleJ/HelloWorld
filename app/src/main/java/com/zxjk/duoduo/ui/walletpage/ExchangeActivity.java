package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetNumbeOfTransactionResponse;
import com.zxjk.duoduo.bean.response.PayInfoResponse;
import com.zxjk.duoduo.bean.response.ReleaseSaleResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.AliPayUtils;
import com.zxjk.duoduo.utils.ArithUtils;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.DataUtils;
import com.zxjk.duoduo.utils.MD5Utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;


@SuppressLint("CheckResult")
public class ExchangeActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private TextView tvExchangePrice;
    private TextView tvExchangeTotal;
    private TextView tvExchangeLine1;
    private TextView tvExchangeLine3;
    private TextView tvExchangeLine4;
    private TextView tvExchangePayType;
    private TextView tv_confirm;
    private EditText etExchangeChooseCount;
    private EditText etMinMoney;
    private EditText etMaxMoney;

    private LinearLayout llChooseMinMax;
    private CheckBox checkbox_weChat;
    private CheckBox checkbox_aliPay;
    private CheckBox checkbox_bank;
    private TextView tv_weChatInfo;
    private TextView tv_aliPayInfo;
    private TextView tv_bankInfo;
    private TextView tvTipsExchange;
    private TextView tv_poundage;
    private String poundage = "";

    private List<String> paytypes = new ArrayList<>(3);
    private String buyType = "";
    private static final String PAYTYPE_WECHAT = "1";
    private static final String PAYTYPE_ALI = "2";
    private static final String PAYTYPE_BANK = "3";

    private boolean buyOrSale = true;

    private String totalPrice = "";
    private String poun = "";
    private String minExchangeFee = "";

    private String pos = "";

    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        initViews();

        ServiceFactory.getInstance().getBaseService(Api.class).getNumbeOfTransaction()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .flatMap((Function<GetNumbeOfTransactionResponse, ObservableSource<List<PayInfoResponse>>>) s -> {
                    runOnUiThread(() -> {
                        tvExchangePrice.setText(s.getHkPrice() + " CNY/MoT");
                        if (TextUtils.isEmpty(s.getExchangeRate()) || s.getExchangeRate().equals("0")) {
                            tv_poundage.setVisibility(View.GONE);
                        } else {
                            double pou = ArithUtils.mul(Double.parseDouble(s.getExchangeRate()), Double.parseDouble("100"));
                            tv_poundage.setText("交易手续费为" + DataUtils.getTwoDecimals(String.valueOf(pou)) + "%");
                            poun = DataUtils.getTwoDecimals(String.valueOf(pou)) + "%";
                            poundage = s.getExchangeRate();
                            minExchangeFee = s.getMinExchangeFee();
                        }
                    });

                    return ServiceFactory.getInstance().getBaseService(Api.class)
                            .getPayInfo().compose(RxSchedulers.normalTrans());
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(listBaseResponse -> {
                    for (PayInfoResponse res : listBaseResponse) {
                        if (res.getPayType().equals(PAYTYPE_WECHAT)) {
                            tv_weChatInfo.setText(res.getWechatNick());
                        } else if (res.getPayType().equals(PAYTYPE_ALI)) {
                            tv_aliPayInfo.setText(res.getZhifubaoNumber());
                        } else {
                            tv_bankInfo.setText(res.getOpenBank());
                        }
                    }
                }, this::handleApiError);
    }

    private void initViews() {
        tv_poundage = findViewById(R.id.tv_poundage);
        llChooseMinMax = findViewById(R.id.llChooseMinMax);
        RelativeLayout rl_weChat = findViewById(R.id.rl_weChat);
        RelativeLayout rl_aliPay = findViewById(R.id.rl_aliPay);
        RelativeLayout rl_bank = findViewById(R.id.rl_bank);
        checkbox_weChat = findViewById(R.id.checkbox_weChat);
        checkbox_aliPay = findViewById(R.id.checkbox_aliPay);
        checkbox_bank = findViewById(R.id.checkbox_bank);
        tv_weChatInfo = findViewById(R.id.tv_weChatInfo);
        tv_aliPayInfo = findViewById(R.id.tv_aliPayInfo);
        tv_bankInfo = findViewById(R.id.tv_bankInfo);
        tvTipsExchange = findViewById(R.id.tvTipsExchange);
        rl_weChat.setOnClickListener(this);
        rl_aliPay.setOnClickListener(this);
        rl_bank.setOnClickListener(this);

        tvExchangePrice = findViewById(R.id.tvExchangePrice);
        tvExchangeTotal = findViewById(R.id.tvExchangeTotal);
        RadioGroup rgExchangeTop = findViewById(R.id.rgExchangeTop);
        tvExchangeLine1 = findViewById(R.id.tvExchangeLine1);
        tvExchangeLine3 = findViewById(R.id.tvExchangeLine3);
        tvExchangeLine4 = findViewById(R.id.tvExchangeLine4);
        tvExchangePayType = findViewById(R.id.tvExchangePayType);
        tv_confirm = findViewById(R.id.tv_confirm);
        etExchangeChooseCount = findViewById(R.id.etExchangeChooseCount);
        etMinMoney = findViewById(R.id.etMinMoney);
        etMaxMoney = findViewById(R.id.etMaxMoney);
        etExchangeChooseCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int len = s.toString().length();
                if (len > 1 && text.startsWith("0")) {
                    s.replace(0, 1, "");
                }
                if (!s.toString().equals("")) {
                    totalPrice = decimalFormat.format(CommonUtils.mul(Double.parseDouble(tvExchangePrice.getText().toString().split(" ")[0]), Double.parseDouble(s.toString())));
                } else {
                    totalPrice = "0";
                }
                tvExchangeTotal.setText(totalPrice);
            }
        });
        etMinMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    return;
                }
                String text = s.toString().trim();
                int len = s.toString().length();
                if (len > 1 && text.startsWith("0")) {
                    s.replace(0, 1, "");
                }

                if (!TextUtils.isEmpty(etExchangeChooseCount.getText().toString().trim()) &&
                        Integer.parseInt(s.toString()) > Integer.parseInt(etExchangeChooseCount.getText().toString().trim())) {
                    ToastUtils.showShort(R.string.input_outrate);
                }
            }
        });
        etMaxMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    return;
                }
                String text = s.toString().trim();
                int len = s.toString().length();
                if (len > 1 && text.startsWith("0")) {
                    s.replace(0, 1, "");
                }
                if (!TextUtils.isEmpty(etExchangeChooseCount.getText().toString().trim()) &&
                        Integer.parseInt(s.toString()) > Integer.parseInt(etExchangeChooseCount.getText().toString().trim())) {
                    ToastUtils.showShort(R.string.input_outrate);
                }
            }
        });

        rgExchangeTop.setOnCheckedChangeListener(this);
        rgExchangeTop.check(R.id.rb1);
    }

    public void submit(View view) {
        if (TextUtils.isEmpty(etExchangeChooseCount.getText().toString())) {
            ToastUtils.showShort(getString(R.string.input_max_number));
            return;
        }
        if (!buyOrSale) {
            if (!TextUtils.isEmpty(etExchangeChooseCount.getText().toString())
                    && Integer.parseInt(etExchangeChooseCount.getText().toString()) < 50) {
                ToastUtils.showShort(getString(R.string.number_max));
                return;
            }
            if (!TextUtils.isEmpty(etMinMoney.getText().toString().trim())
                    && Integer.parseInt(etMinMoney.getText().toString().trim()) == 0) {
                ToastUtils.showShort(getString(R.string.number_min));
                return;
            }
            if (!TextUtils.isEmpty(etMinMoney.getText().toString()) && !TextUtils.isEmpty(etExchangeChooseCount.getText().toString())) {
                if (Integer.parseInt(etMinMoney.getText().toString()) > Integer.parseInt(etExchangeChooseCount.getText().toString())) {
                    ToastUtils.showShort("最小数量不能大于出售数量");
                    return;
                }
            }

            if (!TextUtils.isEmpty(etMaxMoney.getText().toString()) && !TextUtils.isEmpty(etExchangeChooseCount.getText().toString())) {
                if (Integer.parseInt(etMaxMoney.getText().toString()) > Integer.parseInt(etExchangeChooseCount.getText().toString())) {
                    ToastUtils.showShort("最大数量不能大于出售数量");
                    return;
                }
            }

            if (!TextUtils.isEmpty(etMinMoney.getText().toString()) &&
                    !TextUtils.isEmpty(etMaxMoney.getText().toString()) &&
                    Integer.parseInt(etMinMoney.getText().toString()) >
                            Integer.parseInt(etMaxMoney.getText().toString())) {
                ToastUtils.showShort(R.string.input_outrate1);
                return;
            }
        } else {
            if (Integer.parseInt(etExchangeChooseCount.getText().toString()) <= 0) {
                ToastUtils.showShort(getString(R.string.zero));
                return;
            }
        }

        if (TextUtils.isEmpty(buyType) && buyOrSale) {
            ToastUtils.showShort(R.string.select_buytype_tips);
            return;
        }
        if (paytypes.size() == 0 && !buyOrSale) {
            ToastUtils.showShort(R.string.select_saletype_tips);
            return;
        }

        if (buyOrSale) {
            NiceDialog.init().setLayoutId(R.layout.layout_general_dialog10).setConvertListener(new ViewConvertListener() {
                @Override
                protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                    //购买数量
                    holder.setText(R.id.tv_number, etExchangeChooseCount.getText().toString());
                    //购买总金额
                    holder.setText(R.id.tv_amount, totalPrice + "CNY");
                    //支付方式
                    if (!TextUtils.isEmpty(buyType)) {
                        switch (buyType) {
                            case "1":
                                holder.getView(R.id.iv_wechat).setVisibility(View.VISIBLE);
                                break;
                            case "2":
                                holder.getView(R.id.iv_alipay).setVisibility(View.VISIBLE);
                                break;
                            case "3":
                                holder.getView(R.id.iv_bank).setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                    //关闭
                    holder.setOnClickListener(R.id.rl_close, v -> dialog.dismiss());
                    //确认购买
                    holder.setOnClickListener(R.id.tv_confirm, v -> {
                        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
                        api.isConfine()
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.normalTrans())
                                .flatMap((Function<String, ObservableSource<ReleaseSaleResponse>>) s ->
                                        api.releaseSale(etExchangeChooseCount.getText().toString(), totalPrice,
                                                "1", buyType).compose(RxSchedulers.normalTrans()))
                                .map(r -> {
                                    if (!buyType.equals(PAYTYPE_ALI)) {
                                        return r;
                                    }
                                    String s = QRCodeDecoder.syncDecodeQRCode(
                                            Glide.with(ExchangeActivity.this)
                                                    .asBitmap()
                                                    .load(r.getReceiptPicture())
                                                    .submit().get());
                                    r.setAlyQR(s);
                                    return r;
                                })
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ExchangeActivity.this)))
                                .subscribe(s -> {
                                    //购买
                                    dialog.dismiss();
                                    Intent intent = new Intent(ExchangeActivity.this, ConfirmBuyActivity.class);
                                    if (buyType.equals(PAYTYPE_WECHAT)) {
                                        s.setReceiptNumber(s.getWechatNick());
                                    }
                                    intent.putExtra("data", s);
                                    s.setCreateTime(String.valueOf(System.currentTimeMillis()));
                                    intent.putExtra("rate", tvExchangePrice.getText().toString().split(" ")[0]);
                                    intent.putExtra("buytype", buyType);
                                    startActivity(intent);

                                    String alyQR = s.getAlyQR();
                                    if (!TextUtils.isEmpty(alyQR) && alyQR.contains("QR.ALIPAY.COM")) {
                                        if (AliPayUtils.hasInstalledAlipayClient(ExchangeActivity.this)) {
                                            AliPayUtils.startAlipayClient(ExchangeActivity.this, alyQR);
                                        } else {
                                            ToastUtils.showShort(R.string.installalipay);
                                        }
                                    } else {
                                        ToastUtils.showShort(R.string.alipay_qrerror);
                                    }
                                }, ExchangeActivity.this::handleApiError);
                    });
                }
            }).setDimAmount(0.5f).setShowBottom(true).setOutCancel(false).show(getSupportFragmentManager());
        } else {
            NiceDialog.init().setLayoutId(R.layout.layout_general_dialog9).setConvertListener(new ViewConvertListener() {
                @Override
                protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                    //出售数量
                    holder.setText(R.id.tv_number, etExchangeChooseCount.getText().toString());
                    //出售总金额
                    holder.setText(R.id.tv_amount, totalPrice + "CNY");
                    //手续费
                    if (TextUtils.isEmpty(poundage) || poundage.equals("0")) {
                        pos = "0";
                        holder.getView(R.id.tv_transactionFee).setVisibility(View.GONE);
                        holder.getView(R.id.tv_poundage).setVisibility(View.GONE);
                    } else {
                        double pous = ArithUtils.mul(Double.parseDouble(etExchangeChooseCount.getText().toString()), Double.parseDouble(poundage));
                        BigDecimal data1 = new BigDecimal(String.valueOf(pous));
                        BigDecimal data2 = new BigDecimal(minExchangeFee);
                        if (data1.compareTo(data2) < 0) {
                            pos = DataUtils.getTwoDecimals(minExchangeFee);
                            holder.setText(R.id.tv_poundage, pos + "MoT (最小手续费为" + minExchangeFee + ")");

                        } else {
                            pos = DataUtils.getCeilDecimals(String.valueOf(pous));
                            holder.setText(R.id.tv_poundage, pos + "MoT (" + poun + ")");
                        }
                    }
                    //收款方式
                    if (getPayTypes().contains(",")) {
                        String[] split = getPayTypes().split(",");
                        for (String str : split) {
                            switch (str) {
                                case "1":
                                    holder.getView(R.id.iv_wechat).setVisibility(View.VISIBLE);
                                    break;
                                case "2":
                                    holder.getView(R.id.iv_alipay).setVisibility(View.VISIBLE);
                                    break;
                                case "3":
                                    holder.getView(R.id.iv_bank).setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    } else {
                        switch (getPayTypes()) {
                            case "1":
                                holder.getView(R.id.iv_wechat).setVisibility(View.VISIBLE);
                                break;
                            case "2":
                                holder.getView(R.id.iv_alipay).setVisibility(View.VISIBLE);
                                break;
                            case "3":
                                holder.getView(R.id.iv_bank).setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                    //关闭
                    holder.setOnClickListener(R.id.rl_close, v -> dialog.dismiss());
                    //确认出售
                    holder.setOnClickListener(R.id.tv_confirm, v -> {
                        dialog.dismiss();
                        KeyboardUtils.hideSoftInput(ExchangeActivity.this);
                        new NewPayBoard(ExchangeActivity.this).show(psw -> ServiceFactory.getInstance().getBaseService(Api.class)
                                .releasePurchase(etExchangeChooseCount.getText().toString(), String.valueOf(totalPrice),
                                        "1", MD5Utils.getMD5(psw), getPayTypes(), etMinMoney.getText().toString().trim(), etMaxMoney.getText().toString().trim(), pos)
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.normalTrans())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ExchangeActivity.this)))
                                .subscribe(s -> {
                                    s.setUnSaledNum(s.getNumber());
                                    Intent intent = new Intent(ExchangeActivity.this, ConfirmSaleActivity.class);
                                    intent.putExtra("data", s);
                                    intent.putExtra("rate", tvExchangePrice.getText().toString());
                                    startActivity(intent);
                                }, ExchangeActivity.this::handleApiError));
                    });

                }
            }).setDimAmount(0.5f).setShowBottom(true).setOutCancel(false).show(getSupportFragmentManager());
        }
    }

    private String getPayTypes() {
        StringBuilder builder = new StringBuilder();
        for (String str : paytypes) {
            builder.append(str + ",");
        }
        return builder.substring(0, builder.length() - 1);
    }

    public void jump2List(View view) {
        Intent intent = new Intent(this, ExchangeListActivity.class);
        intent.putExtra("rate", tvExchangePrice.getText().toString());
        startActivity(intent);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        buyType = "";
        paytypes.clear();
        checkbox_weChat.setChecked(false);
        checkbox_aliPay.setChecked(false);
        checkbox_bank.setChecked(false);
        switch (checkedId) {
            case R.id.rb2:
                buyOrSale = false;
                tvExchangeLine1.setText(R.string.exchange1_sale1);
                tvExchangeLine3.setText(R.string.exchange1_sale2);
                tvExchangeLine4.setText(R.string.exchange1_sale3);
                tvExchangePayType.setText(R.string.exchange1_sale4);
                tv_confirm.setText(R.string.exchange1_sale5);
                llChooseMinMax.setVisibility(View.VISIBLE);
                tvTipsExchange.setVisibility(View.VISIBLE);

                tv_weChatInfo.setVisibility(View.VISIBLE);
                tv_aliPayInfo.setVisibility(View.VISIBLE);
                tv_bankInfo.setVisibility(View.VISIBLE);

                break;
            case R.id.rb1:
                buyOrSale = true;
                tvExchangeLine1.setText(R.string.exchange1_buy1);
                tvExchangeLine3.setText(R.string.exchange1_buy2);
                tvExchangeLine4.setText(R.string.exchange1_buy3);
                tvExchangePayType.setText(R.string.exchange1_buy4);
                tv_confirm.setText(R.string.exchange1_buy5);
                llChooseMinMax.setVisibility(View.GONE);
                tvTipsExchange.setVisibility(View.GONE);

                tv_weChatInfo.setVisibility(View.GONE);
                tv_aliPayInfo.setVisibility(View.GONE);
                tv_bankInfo.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (buyOrSale) {
            checkbox_weChat.setChecked(false);
            checkbox_aliPay.setChecked(false);
            checkbox_bank.setChecked(false);
            buyType = v.getId() == R.id.rl_weChat ? PAYTYPE_WECHAT : (v.getId() == R.id.rl_aliPay ? PAYTYPE_ALI : PAYTYPE_BANK);
            if (v.getId() == R.id.rl_weChat) {
                checkbox_weChat.setChecked(true);
            } else if (v.getId() == R.id.rl_aliPay) {
                checkbox_aliPay.setChecked(true);
            } else {
                checkbox_bank.setChecked(true);
            }
            return;
        }
        switch (v.getId()) {
            case R.id.rl_weChat:
                if (paytypes.contains(PAYTYPE_WECHAT)) {
                    paytypes.remove(PAYTYPE_WECHAT);
                    checkbox_weChat.setChecked(false);
                } else if (!tv_weChatInfo.getText().toString().equals(getString(R.string.not_perfect))) {
                    paytypes.add(PAYTYPE_WECHAT);
                    checkbox_weChat.setChecked(true);
                }
                break;
            case R.id.rl_aliPay:
                if (paytypes.contains(PAYTYPE_ALI)) {
                    paytypes.remove(PAYTYPE_ALI);
                    checkbox_aliPay.setChecked(false);
                } else if (!tv_aliPayInfo.getText().toString().equals(getString(R.string.not_perfect))) {
                    paytypes.add(PAYTYPE_ALI);
                    checkbox_aliPay.setChecked(true);
                }
                break;
            case R.id.rl_bank:
                if (paytypes.contains(PAYTYPE_BANK)) {
                    paytypes.remove(PAYTYPE_BANK);
                    checkbox_bank.setChecked(false);
                } else if (!tv_bankInfo.getText().toString().equals(getString(R.string.not_perfect))) {
                    paytypes.add(PAYTYPE_BANK);
                    checkbox_bank.setChecked(true);
                }
                break;
            default:
        }
    }
}
