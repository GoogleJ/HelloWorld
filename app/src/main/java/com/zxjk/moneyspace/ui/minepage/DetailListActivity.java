package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetSerialBean;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.NewsLoadMoreView;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.text.SimpleDateFormat;

public class DetailListActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recycler;

    private BaseQuickAdapter<GetSerialBean, BaseViewHolder> adapter;

    private boolean firstInitData = true;

    private String pageSizeStr = "15";
    private int page;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int greenColor;
    private int redColor;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list);

        greenColor = Color.parseColor("#40B65E");
        redColor = Color.parseColor("#FC6660");

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.tradeOrders));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        swipeRefresh = findViewById(R.id.swipeRefresh);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<GetSerialBean, BaseViewHolder>(R.layout.item_alltrade_list) {
            @Override
            protected void convert(BaseViewHolder helper, GetSerialBean item) {
                TextView tvDate = helper.getView(R.id.tvDate);
                TextView tvTips = helper.getView(R.id.tvTips);
                if (!TextUtils.isEmpty(item.getRemarks())) {
                    tvTips.setVisibility(View.VISIBLE);
                    tvTips.setText(item.getRemarks());
                } else {
                    tvTips.setVisibility(View.GONE);
                }
                if (helper.getAdapterPosition() == 0) {
                    tvDate.setVisibility(View.VISIBLE);
                } else if (getData().get(helper.getAdapterPosition() - 1).getMonth().equals(item.getMonth())) {
                    tvDate.setVisibility(View.GONE);
                } else {
                    tvDate.setVisibility(View.VISIBLE);
                }

                tvDate.setText(item.getMonth());

                helper.setText(R.id.tvTitle, item.getSerialTitle())
                        .setText(R.id.tvTime, sdf.format(Long.parseLong(item.getCreateTime())));

                TextView tvSymbol = helper.getView(R.id.tvSymbol);
                TextView tvMoney = helper.getView(R.id.tvMoney);
                tvSymbol.setText(item.getSymbol());

                if (item.getSerialType().equals("1")) {
                    tvSymbol.setTextColor(redColor);
                    tvMoney.setTextColor(redColor);
                    tvMoney.setText("-" + item.getAmount());
                } else {
                    tvSymbol.setTextColor(greenColor);
                    tvMoney.setTextColor(greenColor);
                    tvMoney.setText("+" + item.getAmount());
                }

                ImageView ivType = helper.getView(R.id.ivType);
                GlideUtil.loadNormalImg(ivType, item.getLogo());
            }
        };

        adapter.setLoadMoreView(new NewsLoadMoreView());
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(() -> initData(false), recycler);
        swipeRefresh.setOnRefreshListener(() -> {
            page = 0;
            initData(true);
        });
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorTheme));

        View inflate = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = inflate.findViewById(R.id.tv);
        ImageView iv = inflate.findViewById(R.id.iv);
        tv.setText(R.string.emptylist1);
        iv.setImageResource(R.drawable.ic_empty_orders);
        adapter.setEmptyView(inflate);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(DetailListActivity.this, DetailInfoActivity.class);
            intent.putExtra("data", DetailListActivity.this.adapter.getData().get(position));
            startActivity(intent);
        });

        recycler.setAdapter(adapter);

        initData(true);
    }

    @SuppressLint("CheckResult")
    private void initData(boolean isRefresh) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getSerial(pageSizeStr, String.valueOf(page), "android")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .doOnSubscribe(disposable -> {
                    if (isRefresh) swipeRefresh.setRefreshing(true);
                })
                .doOnTerminate(() -> {
                    if (isRefresh) swipeRefresh.setRefreshing(false);
                })
                .subscribe(list -> {
                    page += 1;
                    if (firstInitData) {
                        firstInitData = false;
                        adapter.setNewData(list);
                        adapter.disableLoadMoreIfNotFullPage();
                    } else {
                        if (list.size() != 0) {
                            adapter.loadMoreComplete();
                        } else {
                            adapter.loadMoreEnd(false);
                        }

                        if (page == 1) {
                            adapter.setNewData(list);
                            adapter.disableLoadMoreIfNotFullPage();
                        } else {
                            adapter.addData(list);
                        }
                    }
                }, t -> {
                    if (!firstInitData) adapter.loadMoreFail();
                    handleApiError(t);
                });
    }

}
