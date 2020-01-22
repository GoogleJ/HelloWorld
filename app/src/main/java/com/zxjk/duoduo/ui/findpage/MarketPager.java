package com.zxjk.duoduo.ui.findpage;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.MarketsResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

@SuppressLint("CheckResult")
public class MarketPager extends BaseFragment {
    private Api api;
    private BaseQuickAdapter<MarketsResponse, BaseViewHolder> adapter;
    private Disposable disposable;
    private SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        api = ServiceFactory.getInstance().getBaseService(Api.class);

        rootView = inflater.inflate(R.layout.marketpager, container, false);

        swipeRefresh = rootView.findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(Color.parseColor("#4585F5"));
        swipeRefresh.setOnRefreshListener(() -> {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            initData();
        });

        RecyclerView recycler = rootView.findViewById(R.id.recycler);

        recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BaseQuickAdapter<MarketsResponse, BaseViewHolder>(R.layout.item_find_market, null) {
            @Override
            protected void convert(BaseViewHolder helper, MarketsResponse bean) {
                helper.setText(R.id.tvCointype, bean.getCoin())
                        .setText(R.id.tvValue, (bean.getTotalValue()))
                        .setText(R.id.tvLastRMB, bean.getLastRMB())
                        .setText(R.id.tvChange, bean.getChange())
                        .setText(R.id.tvLastDollar, "$" + bean.getLastDollar());
                GlideUtil.loadNormalImg(helper.getView(R.id.ivCoinIcon), bean.getLogo());

                View view = helper.getView(R.id.tvChange);
                if (bean.getChange().startsWith("-")) {
                    view.setBackgroundResource(R.drawable.shape_market_decrease);
                } else {
                    view.setBackgroundResource(R.drawable.shape_market_increase);
                }
            }
        };

        recycler.setAdapter(adapter);

        adapter.isFirstOnly(true);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);

        initData();

        return rootView;
    }

    private void initData() {
        api.markets()
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .doOnNext(marketsResponses -> {
                    if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
                        swipeRefresh.setRefreshing(false);
                    }
                })
                .doOnError(marketsResponses -> {
                    if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
                        swipeRefresh.setRefreshing(false);
                    }
                })
                .subscribe(data -> {
                    adapter.replaceData(data);
                    if (disposable != null && !disposable.isDisposed()) {
                        disposable.dispose();
                    }
                    disposable = Observable.timer(5, TimeUnit.SECONDS)
                            .subscribe(c -> initData());
                }, this::handleApiError);
    }
}
