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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetOrderInfoByTypeResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.NewsLoadMoreView;
import com.zxjk.moneyspace.utils.Sha256;

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
    private BaseQuickAdapter<GetOrderInfoByTypeResponse.ListBean, BaseViewHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        tvScreening = findViewById(R.id.tv_screening);
        findViewById(R.id.rl_back).setOnClickListener(v -> {
            finish();
        });
    }

    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
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
                                side = "1";
                                OrderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                OrderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item, null));
                            }, false)
                            .withClick(R.id.radio2, v1 -> {
                                side = "2";
                                OrderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item, null));
                                OrderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                            }, false)
                            .withClick(R.id.radio3, v1 -> {
                                state = "1";
                                setCheckBox(R.id.radio3);
                            }, false)
                            .withClick(R.id.radio4, v1 -> {
                                state = "2";
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
                                state = "5";
                                setCheckBox(R.id.radio7);
                            }, false)
                            .withClick(R.id.radio8, v1 -> {
                                state = "7";
                                setCheckBox(R.id.radio8);
                            }, false)
                            .withClick(R.id.tv_reset, v1 -> {
                                side = "";
                                state = "";
                                OrderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                OrderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                for (CheckBox chb : checkBoxs) {
                                    chb.setChecked(false);
                                    chb.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                                }
                                page = 0;
                                onRefreshLayout();
                            }, false)
                            .withClick(R.id.tv_determine, v1 -> {
                                page = 0;
                                onRefreshLayout();
                            }, true)
                            .withClick(R.id.view1, null, true)
                    ).show();
            if (side.equals("1")) {
                OrderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                OrderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item, null));
            } else if (side.equals("2")) {
                OrderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item, null));
                OrderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
            } else {
                OrderPop.findViewById(R.id.radio2).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
                OrderPop.findViewById(R.id.radio1).setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2, null));
            }

            checkBoxs.add(OrderPop.findViewById(R.id.radio3));
            checkBoxs.add(OrderPop.findViewById(R.id.radio4));
            checkBoxs.add(OrderPop.findViewById(R.id.radio5));
            checkBoxs.add(OrderPop.findViewById(R.id.radio6));
            checkBoxs.add(OrderPop.findViewById(R.id.radio7));
            checkBoxs.add(OrderPop.findViewById(R.id.radio8));


            if (state.equals("1")) {
                setCheckBox(R.id.radio3);
            } else if (state.equals("2")) {
                setCheckBox(R.id.radio4);
            } else if (state.equals("3")) {
                setCheckBox(R.id.radio5);
            } else if (state.equals("4")) {
                setCheckBox(R.id.radio6);
            } else if (state.equals("5")) {
                setCheckBox(R.id.radio7);
            } else if (state.equals("7")) {
                setCheckBox(R.id.radio8);
            }
        });

        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#4585F5"));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 0;
            onRefreshLayout();
        });

        adapter = new BaseQuickAdapter<GetOrderInfoByTypeResponse.ListBean, BaseViewHolder>(R.layout.item_order_info_by_type) {
            @Override
            protected void convert(BaseViewHolder helper, GetOrderInfoByTypeResponse.ListBean item) {

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM/dd");
                String sd = sdf.format(new Date(Long.parseLong(item.getCreateTime())));

                helper.setText(R.id.tv_nonce, sd)
                        .setText(R.id.iv_price, item.getPrice())
                        .setText(R.id.tv_amount, item.getAmount() + "\u0020" + item.getCurrency())
                        .setText(R.id.tv_total, item.getTotal());

                String text = getString(R.string.buy_coin, item.getCurrency());
                TextView tv = helper.getView(R.id.tv_currency);
                SpannableStringBuilder style = new SpannableStringBuilder(text);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGreen, null)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(style);

                TextView tvPaymentState = helper.getView(R.id.tv_payment_state);
                if (!TextUtils.isEmpty(item.getState())) {
                    if (item.getState().equals("1")) {
                        tvPaymentState.setText(R.string.for_the_payment);
                        tvPaymentState.setTextColor(getResources().getColor(R.color.black, null));
                    } else if (item.getState().equals("2")) {
                        tvPaymentState.setText(R.string.payment_has_been);
                        tvPaymentState.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                    } else if (item.getState().equals("3")) {
                        tvPaymentState.setText(R.string.has_been_completed);
                        tvPaymentState.setTextColor(getResources().getColor(R.color.textcolor3, null));
                    } else if (item.getState().equals("4")) {
                        tvPaymentState.setText(R.string.has_been_cancelled);
                        tvPaymentState.setTextColor(getResources().getColor(R.color.textcolor3, null));
                    } else if (item.getState().equals("5")) {
                        tvPaymentState.setText(R.string.in_the_complaint);
                        tvPaymentState.setTextColor(getResources().getColor(R.color.the_order_state, null));
                    } else if (item.getState().equals("7")) {
                        tvPaymentState.setText(R.string.timeout);
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
            GetOrderInfoByTypeResponse.ListBean listBean = (GetOrderInfoByTypeResponse.ListBean) adapter.getData().get(position);

            long timeStampSec = System.currentTimeMillis() / 1000;
            timestamp = String.format("%010d", timeStampSec);
            String secret = "nonce=" + timestamp +
                    "&trans_id=" + listBean.getTransId() +
                    "&user_id=" + Constant.USERID + Constant.SECRET;
            sign = Sha256.getSHA256(secret);

            ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                    .orderInfo(timestamp, listBean.getTransId(), Constant.USERID, listBean.getPaymentType(), listBean.getCreateTime())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver())
                    .subscribe(s -> {
                        Intent intent = new Intent(this, PurchaseDetailsActivity.class);
                        intent.putExtra("ByBoinsResponse", s);
                        startActivity(intent);
                    }, this::handleApiError);
        });

        rlOrderInfoByType.setAdapter(adapter);
        rlOrderInfoByType.setItemAnimator(new DefaultItemAnimator());
        rlOrderInfoByType.setLayoutManager(new LinearLayoutManager(this));

        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.setLoadMoreView(new NewsLoadMoreView());
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(OrderInfoByTypeActivity.this::onRefreshLayout, rlOrderInfoByType);
    }


    private void onRefreshLayout() {
        api.getOrderInfoByType(String.valueOf(page), String.valueOf(numsPerPage), side, state)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .doOnTerminate(() -> (swipeRefreshLayout).setRefreshing(false))
                .subscribe(s -> {

                    page += 1;
                    if (page == 1) {
                        adapter.setNewData(s.getList());
                        adapter.disableLoadMoreIfNotFullPage();
                    } else {
                        adapter.addData(s.getList());
                        if (s.getList().size() >= numsPerPage) {
                            adapter.loadMoreComplete();
                        } else {
                            adapter.loadMoreEnd(false);
                        }
                    }
                });
    }

    private void setCheckBox(int checkBox) {
        for (CheckBox chb : checkBoxs) {
            chb.setChecked(false);
            chb.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item2));
        }

        CheckBox cb = OrderPop.findViewById(checkBox);
        cb.setBackground(getResources().getDrawable(R.drawable.shape_checkbox_item, null));
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        page = 0;
        onRefreshLayout();
    }
}