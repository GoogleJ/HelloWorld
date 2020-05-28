package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CommunityListBean;
import com.zxjk.duoduo.bean.response.GroupChatResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

public class SociaListFragment extends BaseFragment {

    List<CommunityListBean> list = new ArrayList<>();
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
        recyclerView = rootView.findViewById(R.id.recycler_view);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .communityList()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    list = s;
                    adapter.setNewData(list);
                }, this::handleApiError);

        adapter = new BaseQuickAdapter<CommunityListBean, BaseViewHolder>(R.layout.item_socia_list,null) {
            @Override
            protected void convert(BaseViewHolder helper, CommunityListBean item) {
                helper.setText(R.id.group_nike_name,item.getCommunityName())
                        .setText(R.id.group_sign,item.getIntroduction())
                        .setText(R.id.group_owner_name,item.getOwnerNick()+"."+"成员"+item.getMembers());

                ImageView imageView = helper.getView(R.id.group_head_portrait);
                GlideUtil.loadNormalImg(imageView, item.getCommunityLogo());
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            CommunityListBean b = (CommunityListBean) adapter.getData().get(position);
            RongIM.getInstance().startGroupChat(getContext(),b.getGroupId(),"");
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }
}