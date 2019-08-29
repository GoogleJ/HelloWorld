package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.minepage.adapter.DetailListAdapter;
import com.zxjk.duoduo.utils.DataUtils;

import java.util.ArrayList;

public class DetailListFragment extends BaseFragment {
    public String type;
    private TextView tvRedListTips1;
    private TextView tvRedListMoney;
    private SwipeRefreshLayout refresh;
    private RecyclerView recycler;
    private DetailListAdapter adapter;

    private boolean hasInitData = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        initView(container);

        if (type.equals("0")) {
            tvRedListTips1.setText(Constant.currentUser.getNick() + getString(R.string.shouru));
        } else {
            tvRedListTips1.setText(Constant.currentUser.getNick() + getString(R.string.zhichu));
        }

        return rootView;
    }

    private void initView(@Nullable ViewGroup container) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_detaillist, container, false);
        tvRedListTips1 = rootView.findViewById(R.id.tvRedListTips1);
        tvRedListMoney = rootView.findViewById(R.id.tvRedListMoney);
        refresh = rootView.findViewById(R.id.refresh);
        recycler = rootView.findViewById(R.id.recycler);
        adapter = new DetailListAdapter();
        adapter.setData(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
        refresh.setOnRefreshListener(this::initData);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        refresh.setRefreshing(true);
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getDetailList(type)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(detailListResposnes -> {
                    if (!hasInitData) {
                        hasInitData = true;
                    }
                    adapter.setData(detailListResposnes);
                    refresh.setRefreshing(false);
                    if (detailListResposnes != null && detailListResposnes.size() != 0) {
                        tvRedListMoney.setText(DataUtils.getTwoDecimals(String.valueOf(detailListResposnes.get(0).getSum())));
                    } else {
                        tvRedListMoney.setText("0.00");
                    }
//                    double total = 0.00;
//                    for (DetailListResposne d : detailListResposnes) {
//                        total = ArithUtils.add(total, Double.parseDouble(d.getHk()));
//                    }
//                    tvRedListMoney.setText(DataUtils.getTwoDecimals(String.valueOf(total)));
                }, t -> {
                    handleApiError(t);
                    refresh.setRefreshing(false);
                });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((null != rootView) && getUserVisibleHint() && !hasInitData) {
            initData();
        }
    }

    @Override
    public void onStart() {
        if (getUserVisibleHint() && !hasInitData) {
            initData();
        }
        super.onStart();
    }
}
