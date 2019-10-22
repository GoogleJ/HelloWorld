package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.bean.response.GetTransferAllResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.widget.NewsLoadMoreView;

import java.text.SimpleDateFormat;

import io.reactivex.Observable;

public class OrdersFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private boolean hasInitData;

    public String type;
    public String address;
    public String symbol;

    private BaseQuickAdapter adapter;

    private int page = 1;
    private int offset = 10;

    private int color1;
    private int color2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        color1 = Color.parseColor("#40B65E");
        color2 = Color.parseColor("#FC6660");

        rootView = new SwipeRefreshLayout(getContext());
        ((SwipeRefreshLayout) rootView).setColorSchemeColors(Color.parseColor("#4585F5"));
        recyclerView = new RecyclerView(getContext());
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        ((SwipeRefreshLayout) rootView).addView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rootView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        ((SwipeRefreshLayout) rootView).setOnRefreshListener(() -> {
            page = 1;
            offset = 15;
            initData();
        });

        adapter = new BaseQuickAdapter<GetTransferAllResponse.ListBean, BaseViewHolder>(R.layout.item_blockorder) {
            @Override
            protected void convert(BaseViewHolder helper, GetTransferAllResponse.ListBean bean) {
                helper.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), BlockOrderDetailActivity.class);
                    intent.putExtra("data", bean);
                    startActivity(intent);
                });

                TextView tvTime = helper.getView(R.id.tvTime);
                TextView tvCount = helper.getView(R.id.tvCount);
                TextView tvStatus = helper.getView(R.id.tvStatus);

                tvTime.setText(new SimpleDateFormat("yyyy.MM.dd HH:mm").format(Long.parseLong(bean.getCreateTime())));
                tvStatus.setText((bean.getTxreceiptStatus().equals("0")) ? R.string.failed : (bean.getTxreceiptStatus().equals("1") ? R.string.success : R.string.procssing));

                if ("2".equals(bean.getSerialType()) || "3".equals(bean.getSerialType())) {
                    tvCount.setTextColor(color1);
                    tvCount.setText("+" + bean.getBalance());
                } else if ("1".equals(bean.getSerialType()) && "0".equals(bean.getInOrOut())) {
                    tvCount.setTextColor(color1);
                    tvCount.setText("+" + bean.getBalance());
                } else if ("1".equals(bean.getSerialType()) && "1".equals(bean.getInOrOut())) {
                    tvCount.setTextColor(color2);
                    tvCount.setText("-" + bean.getBalance());
                } else if ("0".equals(bean.getSerialType()) && "0".equals(bean.getInOrOut())) {
                    tvCount.setTextColor(color1);
                    tvCount.setText("+" + bean.getBalance());
                } else {
                    tvCount.setTextColor(color2);
                    tvCount.setText("-" + bean.getBalance());
                }
            }
        };

        adapter.setLoadMoreView(new NewsLoadMoreView());
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this::initData, recyclerView);
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = inflate.findViewById(R.id.tv);
        ImageView iv = inflate.findViewById(R.id.iv);
        tv.setText(R.string.emptylist1);
        iv.setImageResource(R.drawable.ic_empty_orders);
        adapter.setEmptyView(inflate);

        recyclerView.setAdapter(adapter);

        ((SwipeRefreshLayout) rootView).setRefreshing(true);
        initData();
        return rootView;
    }

    @SuppressLint("CheckResult")
    private void initData() {
        Api service = ServiceFactory.getInstance().getBaseService(Api.class);
        Observable<BaseResponse<GetTransferAllResponse>> upstream;

        switch (type) {
            case "1":
                upstream = service.getTransferAll(address, String.valueOf(page), String.valueOf(offset), symbol);
                break;
            case "2":
                upstream = service.getTransferOut(address, String.valueOf(page), String.valueOf(offset), symbol);
                break;
            case "3":
                upstream = service.getTransferIn(address, String.valueOf(page), String.valueOf(offset), symbol);
                break;
            case "4":
                upstream = service.getTransfer(address, String.valueOf(page), String.valueOf(offset), symbol);
                break;
            default:
                upstream = service.getTransferAll(address, String.valueOf(page), String.valueOf(offset), symbol);
        }

        upstream.compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .doOnTerminate(() -> ((SwipeRefreshLayout) rootView).setRefreshing(false))
                .subscribe(response -> {
                    page += 1;
                    if (!hasInitData) {
                        hasInitData = true;
                        adapter.setNewData(response.getList());
                        adapter.disableLoadMoreIfNotFullPage();
                    } else {
                        if (response.isHasNextPage()) {
                            adapter.loadMoreComplete();
                        } else {
                            adapter.loadMoreEnd(false);
                        }

                        if (page == 2) {
                            adapter.setNewData(response.getList());
                            adapter.disableLoadMoreIfNotFullPage();
                        } else {
                            adapter.addData(response.getList());
                        }
                    }
                }, t -> {
                    adapter.loadMoreFail();
                    handleApiError(t);
                });
    }
}
