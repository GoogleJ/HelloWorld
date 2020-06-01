package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CommunityListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imkit.RongIM;

public class SociaListFragment extends BaseFragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<CommunityListBean, BaseViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_socoa_list, container, false);

        initView();

        initData();

        return rootView;
    }

    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView;
        recyclerView = swipeRefreshLayout.findViewById(R.id.recycler);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorTheme);
    }

    private void initData() {
        swipeRefreshLayout.setOnRefreshListener(this::communityList);

        adapter = new BaseQuickAdapter<CommunityListBean, BaseViewHolder>(R.layout.item_socia_list, null) {
            @Override
            protected void convert(BaseViewHolder helper, CommunityListBean item) {
                helper.setText(R.id.group_nike_name, item.getCommunityName())
                        .setText(R.id.group_sign, item.getIntroduction())
                        .setText(R.id.group_owner_name, item.getOwnerNick())
                        .setText(R.id.group_member, getString(R.string.member, item.getMembers()));

                GlideUtil.loadCornerImg(helper.getView(R.id.group_head_portrait), item.getCommunityLogo(), 6);
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            CommunityListBean b = (CommunityListBean) adapter.getData().get(position);
            RongIM.getInstance().startGroupChat(getContext(), b.getGroupId(), "");
        });

        adapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.empty_publicgroup, null, false));

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("CheckResult")
    private void communityList() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .communityList()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .doOnSubscribe(disposable -> {
                    if (null != swipeRefreshLayout) {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                })
                .doOnTerminate(() -> {
                    if (null != swipeRefreshLayout) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                })
                .subscribe(adapter::replaceData, this::handleApiError);
    }

    @Override
    public void onResume() {
        super.onResume();
        communityList();
    }
}