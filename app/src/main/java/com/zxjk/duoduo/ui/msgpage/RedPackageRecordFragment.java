package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import com.zxjk.duoduo.bean.response.GetRedPackageRecordResponse;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.msgpage.adapter.RedPackageRecoderAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("CheckResult")
public class RedPackageRecordFragment extends BaseFragment {

    private String type;
    private List<GetRedPackageRecordResponse.RedpackageListBean> data = new ArrayList<>();
    private RedPackageRecoderAdapter redPackageRecoderAdapter;

    private TextView tvRedListTips1;
    private TextView tvRedListTips2;
    private TextView tvRedListTips3;
    private TextView tvRedListMoney;

    private boolean hasInitData = false;

    private SwipeRefreshLayout swipeRefreshLayout;

    public void setType(String type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.redpackagerecord, container, false);
        swipeRefreshLayout = rootView.findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#4585F5"));
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        redPackageRecoderAdapter = new RedPackageRecoderAdapter(data);
        recyclerView.setAdapter(redPackageRecoderAdapter);
        tvRedListTips1 = rootView.findViewById(R.id.tvRedListTips1);
        tvRedListTips2 = rootView.findViewById(R.id.tvRedListTips2);
        tvRedListTips3 = rootView.findViewById(R.id.tvRedListTips3);
        tvRedListMoney = rootView.findViewById(R.id.tvRedListMoney);

        if (type.equals("0")) {
            tvRedListTips1.setText(Constant.currentUser.getNick() + "共收到");
            tvRedListTips2.setText("共收到");
        } else {
            tvRedListTips1.setText(Constant.currentUser.getNick() + "共支出");
            tvRedListTips2.setText("共支出");
        }

        swipeRefreshLayout.setOnRefreshListener(this::initData);

        initData();

        return rootView;
    }

    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getRedPackageRecord(type)
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .compose(bindToLifecycle())
                .subscribe(response -> {
                    hasInitData = true;
                    swipeRefreshLayout.setRefreshing(false);

                    tvRedListTips3.setText(String.valueOf(response.getTotalRecord().getCount()));
                    tvRedListMoney.setText(String.valueOf(response.getTotalRecord().getTotal_money()));
                    redPackageRecoderAdapter.setData(response.getRedpackageList());
                    redPackageRecoderAdapter.notifyDataSetChanged();
                }, t -> {
                    swipeRefreshLayout.setRefreshing(false);
                    handleApiError(t);
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
