package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetquickOrderInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

@SuppressLint("CheckResult")
public class OrderInfoByTypeActivity extends BaseActivity {
    private RecyclerView rlOrderInfoByType;
    private BaseQuickAdapter<GetquickOrderInfoResponse, BaseViewHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Api api;
    private LinearLayout llScreening;
    private String timestamp;
    private String sign;

    private String side = "";
    private String state = "";

    private QuickPopup orderPop;

    private List<CheckBox> checkBoxs = new ArrayList<>();

    private int page = 0;
    private int numsPerPage = 10;

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info_by_type);

        api = ServiceFactory.getInstance().getBaseService(Api.class);

        initView();

        initData();
    }

    private void initView() {
        rlOrderInfoByType = findViewById(R.id.rl_order_info_by_type);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        llScreening = findViewById(R.id.ll_screening);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    private void initData() {

        onRefreshLayout();
        swipeRefreshLayout.setRefreshing(true);
        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, -(ScreenUtils.getScreenHeight()), 0f);
        showAnimation.setDuration(250);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, -(ScreenUtils.getScreenHeight()));
        dismissAnimation.setDuration(500);
        llScreening.setOnClickListener(v -> {
            orderPop = QuickPopupBuilder.with(this)
                    .contentView(R.layout.dialog_order_screening)
                    .config(new QuickPopupConfig()
                                    .gravity(Gravity.BOTTOM)
                                    .withShowAnimation(showAnimation)
                                    .withDismissAnimation(dismissAnimation)
                                    .alignBackground(true)
                                    .alignBackgroundGravity(Gravity.TOP)
                                    .withClick(R.id.radio1, v1 -> {
                                        side = "BUY";
                                        orderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                        orderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item, null));
                                    }, false)
                                    .withClick(R.id.radio2, v1 -> {
                                        side = "SELL";
                                        orderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item, null));
                                        orderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                    }, false)
                                    .withClick(R.id.radio3, v1 -> {
                                        state = "UNFINISHED";
                                        setCheckBox(R.id.radio3);
                                    }, false)
                                    .withClick(R.id.radio4, v1 -> {
                                        state = "PAYED";
                                        setCheckBox(R.id.radio4);
                                    }, false)
                                    .withClick(R.id.radio5, v1 -> {
                                        state = "FINISHED";
                                        setCheckBox(R.id.radio5);
                                    }, false)
                                    .withClick(R.id.radio6, v1 -> {
                                        state = "CANCELED";
                                        setCheckBox(R.id.radio6);
                                    }, false)
                                    .withClick(R.id.radio7, v1 -> {
                                        state = "DISPUTE";
                                        setCheckBox(R.id.radio7);
                                    }, false)
                                    .withClick(R.id.tv_reset, v1 -> {
                                        side = "";
                                        state = "";
                                        orderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                        orderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                        for (CheckBox chb : checkBoxs) {
                                            chb.setChecked(false);
                                            chb.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                        }
                                        page = 0;
                                        swipeRefreshLayout.setRefreshing(true);
                                        onRefreshLayout();
                                    }, false)
                                    .withClick(R.id.tv_determine, v1 -> {
                                        page = 0;
                                        swipeRefreshLayout.setRefreshing(true);
                                        onRefreshLayout();
                                    }, true)
//                            .withClick(R.id.view1, null, true)
                    ).build();
            orderPop.showPopupWindow(v);
            if (side.equals("BUY")) {
                orderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                orderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item, null));
            } else if (side.equals("SELL")) {
                orderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item, null));
                orderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
            } else {
                orderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                orderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
            }

            checkBoxs.add(orderPop.findViewById(R.id.radio3));
            checkBoxs.add(orderPop.findViewById(R.id.radio4));
            checkBoxs.add(orderPop.findViewById(R.id.radio5));
            checkBoxs.add(orderPop.findViewById(R.id.radio6));
            checkBoxs.add(orderPop.findViewById(R.id.radio7));

            if (state.equals("UNFINISHED")) {
                setCheckBox(R.id.radio3);
            } else if (state.equals("PAYED")) {
                setCheckBox(R.id.radio4);
            } else if (state.equals("FINISHED")) {
                setCheckBox(R.id.radio5);
            } else if (state.equals("CANCELED")) {
                setCheckBox(R.id.radio6);
            } else if (state.equals("DISPUTE")) {
                setCheckBox(R.id.radio7);
            }

            orderPop.showPopupWindow(v);
        });

        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#0083BF"));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 0;
            onRefreshLayout();
        });

        adapter = new BaseQuickAdapter<GetquickOrderInfoResponse, BaseViewHolder>(R.layout.item_order_info_by_type) {
            @Override
            protected void convert(BaseViewHolder helper, GetquickOrderInfoResponse item) {
                String sd = sdf.format(new Date(Long.parseLong(item.getCreateTime())));

                helper.setText(R.id.tv_nonce, sd)
                        .setText(R.id.iv_price, item.getPrice())
                        .setText(R.id.tv_price, getString(R.string.unit_price, item.getCurrency()))
                        .setText(R.id.tv_amount, item.getCoinAmount())
                        .setText(R.id.tv_total, item.getTotal())
                        .setText(R.id.tv1, getString(R.string.total_amount, item.getCurrency()))
                        .setText(R.id.tv_amount2, getString(R.string.the_number, item.getCoinSymbol()));

                String text = getString(item.getType().equals("BUY") ? R.string.buy : R.string.sell) + item.getCoinSymbol();
                TextView tv = helper.getView(R.id.tv_currency);
                SpannableStringBuilder style = new SpannableStringBuilder(text);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGreen, null)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(style);

                TextView tvPaymentState = helper.getView(R.id.tv_payment_state);
                if (!TextUtils.isEmpty(item.getOrderStatus())) {
                    if (item.getOrderStatus().equals("UNFINISHED")) {
                        tvPaymentState.setText(R.string.for_the_payment);
                        tvPaymentState.setTextColor(getResources().getColor(R.color.black, null));
                    } else if (item.getOrderStatus().equals("PAYED")) {
                        tvPaymentState.setText(R.string.payment_has_been);
                        tvPaymentState.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                    } else if (item.getOrderStatus().equals("FINISHED") || item.getOrderStatus().equals("FORCEFINISHED")) {
                        tvPaymentState.setText(R.string.has_been_completed);
                        tvPaymentState.setTextColor(getResources().getColor(R.color.textcolor3, null));
                    } else if (item.getOrderStatus().equals("CANCELED") || item.getOrderStatus().equals("TIMEOUT") || item.getOrderStatus().equals("REFUND") || item.getOrderStatus().equals("FORCECANCEL")) {
                        tvPaymentState.setText(R.string.has_been_cancelled);
                        tvPaymentState.setTextColor(getResources().getColor(R.color.textcolor3, null));
                    } else if (item.getOrderStatus().equals("DISPUTE")) {
                        tvPaymentState.setText(R.string.in_the_complaint);
                        tvPaymentState.setTextColor(getResources().getColor(R.color.the_order_state, null));
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
            GetquickOrderInfoResponse listBean = (GetquickOrderInfoResponse) adapter.getData().get(position);
            Intent intent = new Intent(this, PurchaseDetailsActivity.class);
            intent.putExtra("otherOrderId", listBean.getOtherOrderId());
            startActivity(intent);
        });

        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(() -> {
            OrderInfoByTypeActivity.this.onRefreshLayout();
        }, rlOrderInfoByType);

        rlOrderInfoByType.setLayoutManager(new LinearLayoutManager(this));
        rlOrderInfoByType.setAdapter(adapter);
    }

    private void onRefreshLayout() {
        api.quickOrderInfo(side, null, null, state, String.valueOf(page), String.valueOf(numsPerPage))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .doOnTerminate(() -> (swipeRefreshLayout).setRefreshing(false))
                .subscribe(s -> {
                    s.size();
                    page += 1;
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
                }, this::handleApiError);
    }

    private void setCheckBox(int checkBox) {
        for (CheckBox chb : checkBoxs) {
            chb.setChecked(false);
            chb.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2));
        }

        CheckBox cb = orderPop.findViewById(checkBox);
        cb.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item, null));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        page = 0;
        swipeRefreshLayout.setRefreshing(true);
        onRefreshLayout();
    }
}