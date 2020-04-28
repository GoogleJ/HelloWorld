package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetOrderInfoByTypeResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.Sha256;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

@SuppressLint("CheckResult")
public class OrderInfoByTypeActivity extends BaseActivity {

    private RecyclerView rlOrderInfoByType;
    private BaseQuickAdapter<GetOrderInfoByTypeResponse, BaseViewHolder> adapter;
    //    private SwipeRefreshLayout swipeRefreshLayout;
    private Api api;
    private TextView tvScreening;
    private String timestamp;
    private String sign;

    private String side = "";
    private String state = "";

    private QuickPopup OrderPop;

    private List<CheckBox> checkBoxs = new ArrayList<>();

    private int page = 0;
    private int numsPerPage = 10;
    private String customerIdentity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info_by_type);

        initView();

        initData();

    }

    private void initView() {
        rlOrderInfoByType = findViewById(R.id.rl_order_info_by_type);
        tvScreening = findViewById(R.id.tv_screening);
        customerIdentity = getIntent().getStringExtra("customerIdentity");
        findViewById(R.id.rl_back).setOnClickListener(v -> {
            finish();
        });
    }

    private void initData() {
        onRefreshLayout();
        tvScreening.setOnClickListener(v -> {

            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, 0f, 0f);
            showAnimation.setDuration(350);
            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, 0f);
            dismissAnimation.setDuration(300);
            OrderPop = QuickPopupBuilder.with(this)
                    .contentView(R.layout.dialog_order_screening)
                    .config(new QuickPopupConfig()
                            .backgroundColor(android.R.color.transparent)
                            .withShowAnimation(showAnimation)
                            .withDismissAnimation(dismissAnimation)
                            .withClick(R.id.radio1, v1 -> {
                                side = "0";
                                CheckBox radio1 = OrderPop.findViewById(R.id.radio1);
                                CheckBox radio2 = OrderPop.findViewById(R.id.radio2);
                                radio1.setBackgroundColor(Color.parseColor("#272E3F"));
                                radio1.setTextColor(Color.parseColor("#E7C39B"));
                                radio2.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                radio2.setTextColor(Color.parseColor("#272E3F"));
                            }, false)
                            .withClick(R.id.radio2, v1 -> {
                                side = "1";
                                CheckBox radio1 = OrderPop.findViewById(R.id.radio1);
                                CheckBox radio2 = OrderPop.findViewById(R.id.radio2);
                                radio2.setBackgroundColor(Color.parseColor("#272E3F"));
                                radio2.setTextColor(Color.parseColor("#E7C39B"));
                                radio1.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                radio1.setTextColor(Color.parseColor("#272E3F"));
                            }, false)
                            .withClick(R.id.radio3, v1 -> {
                                state = "0";
                                setCheckBox(R.id.radio3);
                            }, false)
                            .withClick(R.id.radio4, v1 -> {
                                state = "1";
                                setCheckBox(R.id.radio4);
                            }, false)
                            .withClick(R.id.radio5, v1 -> {
                                state = "3";
                                setCheckBox(R.id.radio5);
                            }, false)
                            .withClick(R.id.radio6, v1 -> {
                                state = "4";
                                setCheckBox(R.id.radio6);
                            }, false)
                            .withClick(R.id.radio7, v1 -> {
                                state = "6";
                                setCheckBox(R.id.radio7);
                            }, false)
                            .withClick(R.id.radio8, v1 -> {
                                state = "7";
                                setCheckBox(R.id.radio8);
                            }, false)
                            .withClick(R.id.radio9, v1 -> {
                                state = "8";
                                setCheckBox(R.id.radio9);
                            }, false)
                            .withClick(R.id.radio10, v1 -> {
                                state = "9";
                                setCheckBox(R.id.radio10);
                            }, false)
                            .withClick(R.id.tv_reset, v1 -> {
                                side = "";
                                state = "";
                                CheckBox radio1 = OrderPop.findViewById(R.id.radio1);
                                CheckBox radio2 = OrderPop.findViewById(R.id.radio2);
                                radio1.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                radio2.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                radio1.setTextColor(Color.parseColor("#272E3F"));
                                radio2.setTextColor(Color.parseColor("#272E3F"));
                                for (CheckBox chb : checkBoxs) {
                                    chb.setChecked(false);
                                    chb.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                    chb.setTextColor(Color.parseColor("#272E3F"));
                                }
                                onRefreshLayout();
                            }, false)
                            .withClick(R.id.tv_determine, v1 -> {
                                onRefreshLayout();
                            }, true)
                            .withClick(R.id.view1, null, true)
                    ).show();
            if (side.equals("0")) {
                CheckBox radio1 = OrderPop.findViewById(R.id.radio1);
                CheckBox radio2 = OrderPop.findViewById(R.id.radio2);
                radio1.setBackgroundColor(Color.parseColor("#272E3F"));
                radio1.setTextColor(Color.parseColor("#E7C39B"));
                radio2.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                radio2.setTextColor(Color.parseColor("#272E3F"));
            } else if (side.equals("1")) {
                CheckBox radio1 = OrderPop.findViewById(R.id.radio1);
                CheckBox radio2 = OrderPop.findViewById(R.id.radio2);
                radio2.setBackgroundColor(Color.parseColor("#272E3F"));
                radio2.setTextColor(Color.parseColor("#E7C39B"));
                radio1.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                radio1.setTextColor(Color.parseColor("#272E3F"));
            } else {
                CheckBox radio1 = OrderPop.findViewById(R.id.radio1);
                CheckBox radio2 = OrderPop.findViewById(R.id.radio2);
                radio1.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                radio2.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                radio1.setTextColor(Color.parseColor("#272E3F"));
                radio2.setTextColor(Color.parseColor("#272E3F"));
            }

            checkBoxs.add(OrderPop.findViewById(R.id.radio3));
            checkBoxs.add(OrderPop.findViewById(R.id.radio4));
            checkBoxs.add(OrderPop.findViewById(R.id.radio5));
            checkBoxs.add(OrderPop.findViewById(R.id.radio6));
            checkBoxs.add(OrderPop.findViewById(R.id.radio7));
            checkBoxs.add(OrderPop.findViewById(R.id.radio8));
            checkBoxs.add(OrderPop.findViewById(R.id.radio9));
            checkBoxs.add(OrderPop.findViewById(R.id.radio10));

            if (state.equals("0")) {
                setCheckBox(R.id.radio3);
            } else if (state.equals("1")) {
                setCheckBox(R.id.radio4);
            } else if (state.equals("3")) {
                setCheckBox(R.id.radio5);
            } else if (state.equals("4")) {
                setCheckBox(R.id.radio6);
            } else if (state.equals("6")) {
                setCheckBox(R.id.radio7);
            } else if (state.equals("7")) {
                setCheckBox(R.id.radio8);
            } else if (state.equals("8")) {
                setCheckBox(R.id.radio9);
            } else if (state.equals("9")) {
                setCheckBox(R.id.radio10);
            }
            if (customerIdentity.equals("0")) {
                OrderPop.findViewById(R.id.radio9).setVisibility(View.INVISIBLE);
            }
        });



        adapter = new BaseQuickAdapter<GetOrderInfoByTypeResponse, BaseViewHolder>(R.layout.item_order_info_by_type) {
            @Override
            protected void convert(BaseViewHolder helper, GetOrderInfoByTypeResponse item) {

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
                String sd = sdf.format(new Date(Long.parseLong(item.getCreateTime())));

                helper.setText(R.id.tv_nonce, sd)
                        .setText(R.id.iv_number, item.getNumber())
                        .setText(R.id.tv_total, item.getMoney());

                String text = getString(R.string.buy_coin, item.getCurrency());
                TextView tv = helper.getView(R.id.tv_currency);
                SpannableStringBuilder style = new SpannableStringBuilder(text);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGreen, null)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(style);

                TextView tvPaymentState = helper.getView(R.id.tv_payment_state);
                if (!TextUtils.isEmpty(item.getStatus())) {
                    if (TextUtils.isEmpty(item.getBothOrderId())) {
                        if (item.getBuyOrSell().equals("0")) {
                            getString(R.string.buy_coin, item.getCurrency());
                            style = new SpannableStringBuilder(text);
                            style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGreen, null)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv.setText(style);
                        } else {
                            text = getString(R.string.sell1, item.getCurrency());
                            style = new SpannableStringBuilder(text);
                            style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.the_order_state, null)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv.setText(style);
                        }
                        if (item.getStatus().equals("7")) {
                            tvPaymentState.setText(R.string.has_been_completed);
                            tvPaymentState.setTextColor(getResources().getColor(R.color.textcolor3, null));
                        } else if (item.getStatus().equals("1")) {
                            tvPaymentState.setText(R.string.has_been_cancelled);
                            tvPaymentState.setTextColor(getResources().getColor(R.color.textcolor3, null));
                        } else if (item.getStatus().equals("8")) {
                            tvPaymentState.setText("挂单中");
                            tvPaymentState.setTextColor(getResources().getColor(R.color.color_yellow_red, null));
                        } else if (item.getStatus().equals("9")) {
                            tvPaymentState.setText("交易中");
                            tvPaymentState.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                        }
                    } else {
                        if (item.getBuyOrSell().equals("0")) {
                            getString(R.string.buy_coin, item.getCurrency());
                            style = new SpannableStringBuilder(text);
                            style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGreen, null)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv.setText(style);
                        } else {
                            text = getString(R.string.sell1, item.getCurrency());
                            style = new SpannableStringBuilder(text);
                            style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.the_order_state, null)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv.setText(style);
                        }
                        if (item.getStatus().equals("0")) {
                            tvPaymentState.setText(R.string.for_the_payment);
                            tvPaymentState.setTextColor(getResources().getColor(R.color.black, null));
                        } else if (item.getStatus().equals("1") || item.getStatus().equals("2")) {
                            tvPaymentState.setText(R.string.has_been_cancelled);
                            tvPaymentState.setTextColor(getResources().getColor(R.color.textcolor3, null));
                        } else if (item.getStatus().equals("3")) {
                            tvPaymentState.setText(R.string.waiting_to_put_money);
                            tvPaymentState.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                        } else if (item.getStatus().equals("4")) {
                            tvPaymentState.setText(R.string.timeout);
                            tvPaymentState.setTextColor(getResources().getColor(R.color.the_order_state, null));
                        } else if (item.getStatus().equals("5")) {
                            tvPaymentState.setText("申诉完成");
                            tvPaymentState.setTextColor(getResources().getColor(R.color.textcolor3, null));
                        } else if (item.getStatus().equals("6")) {
                            tvPaymentState.setText(R.string.in_the_complaint);
                            tvPaymentState.setTextColor(getResources().getColor(R.color.the_order_state, null));
                        } else if (item.getStatus().equals("7")) {
                            tvPaymentState.setText(R.string.has_been_completed);
                            tvPaymentState.setTextColor(getResources().getColor(R.color.textcolor3, null));
                        }
                    }
                } else {
                    tvPaymentState.setText(R.string.unknown);
                }
            }
        };

        View inflate = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = inflate.findViewById(R.id.tv);
        ImageView iv = inflate.findViewById(R.id.iv);
        iv.setImageResource(R.drawable.ic_empty_orders);
        tv.setText(getString(R.string.no_data));
        adapter.setEmptyView(inflate);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            GetOrderInfoByTypeResponse listBean = (GetOrderInfoByTypeResponse) adapter.getData().get(position);

            long currentTime = System.currentTimeMillis();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date(currentTime);
            timestamp = dataOne(formatter.format(date));

            String secret;
            if (TextUtils.isEmpty(listBean.getBothOrderId())) {
                if (listBean.getBuyOrSell().equals("0")) {
                    secret = "buyOrSell=" + listBean.getBuyOrSell() +
                            "&buyOrderId=" + listBean.getBuyOrderId() +
                            "&nonce=" + timestamp + Constant.SECRET;
                } else {
                    secret = "buyOrSell=" + listBean.getBuyOrSell() +
                            "&nonce=" + timestamp +
                            "&sellOrderId=" + listBean.getSellOrderId() + Constant.SECRET;
                }
            } else {
                secret = "bothOrderId=" + listBean.getBothOrderId() +
                        "&buyOrSell=" + listBean.getBuyOrSell() +
                        "&nonce=" + timestamp + Constant.SECRET;
            }

            sign = Sha256.getSHA256(secret);

            ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                    .getOrderInfoById(timestamp, listBean.getBothOrderId(), listBean.getBuyOrderId(), listBean.getSellOrderId(), listBean.getBuyOrSell())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver())
                    .subscribe(s -> {
                        Intent intent;
                        String orderId;
                        if (TextUtils.isEmpty(s.getBothOrderId())) {

                            if (listBean.getBuyOrSell().equals("0")) {
                                orderId = listBean.getBuyOrderId();
                            } else {
                                orderId = listBean.getSellOrderId();
                            }
                            if (!listBean.getStatus().equals("8")) {
                                intent = new Intent(this, PurchaseDetailsActivity.class);
                                s.setBuyOrSell(listBean.getBuyOrSell());
                                intent.putExtra("GetOrderInfoById", s);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("count", listBean.getBuyOrSell());
                            } else {
                                intent = new Intent(this, AcceptorOrderActivity.class);
                                s.setBuyOrSell(listBean.getBuyOrSell());
                                intent.putExtra("GetOrderInfoById", s);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("count", listBean.getBuyOrSell());
                                intent.putExtra("payType", s.getPayType());
                            }

                        } else {
                            orderId = listBean.getBothOrderId();
                            intent = new Intent(this, PurchaseDetailsActivity.class);
                            s.setBuyOrSell(listBean.getBuyOrSell());
                            intent.putExtra("GetOrderInfoById", s);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("count", listBean.getBuyOrSell());
                        }
                        startActivity(intent);
                    }, this::handleApiError);
        });

        rlOrderInfoByType.setAdapter(adapter);
        rlOrderInfoByType.setItemAnimator(new DefaultItemAnimator());
        rlOrderInfoByType.setLayoutManager(new LinearLayoutManager(this));
    }


    private void onRefreshLayout() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date2 = new Date(currentTime);
        timestamp = dataOne(formatter.format(date2));
        String secret;
        if (!TextUtils.isEmpty(state) && !TextUtils.isEmpty(side)) {
            secret = "buyOrSell=" + side +
                    "&nonce=" + timestamp +
                    "&status=" + state + Constant.SECRET;
        } else if (!TextUtils.isEmpty(side)) {
            secret = "buyOrSell=" + side +
                    "&nonce=" + timestamp + Constant.SECRET;
        } else if (!TextUtils.isEmpty(state)) {
            secret = "nonce=" + timestamp +
                    "&status=" + state + Constant.SECRET;
        } else {
            secret = "nonce=" + timestamp + Constant.SECRET;
        }
        sign = Sha256.getSHA256(secret);

        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                .getOrderInfoByType(timestamp, state, side)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(d ->
                        {
                            adapter.setNewData(d);
                        },
                        this::handleApiError);
    }

    private void setCheckBox(int checkBox) {
        for (CheckBox chb : checkBoxs) {
            chb.setChecked(false);
            chb.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2,null));
            chb.setTextColor(Color.parseColor("#272E3F"));
        }

        CheckBox cb = OrderPop.findViewById(checkBox);
        cb.setBackgroundColor(Color.parseColor("#272E3F"));
        cb.setTextColor(Color.parseColor("#E7C39B"));
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        page = 0;
        onRefreshLayout();
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
}