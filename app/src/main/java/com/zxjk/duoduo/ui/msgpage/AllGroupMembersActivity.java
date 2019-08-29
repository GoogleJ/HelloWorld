package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.AllGroupMembersResponse;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.AllGroupMemebersAdapter1;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("CheckResult")
public class AllGroupMembersActivity extends BaseActivity {
    private RecyclerView mRecyclerView;

    private AllGroupMemebersAdapter1 mAdapter;

    private List<AllGroupMembersResponse> list = new ArrayList<>();

    private GroupResponse groupResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_group_members);
        TextView tv_title = findViewById(R.id.tv_title);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_title.setText(getString(R.string.all_group_members_title));
        mRecyclerView = findViewById(R.id.recycler_view);
        initView();
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new AllGroupMemebersAdapter1();
        groupResponse = (GroupResponse) getIntent().getSerializableExtra("allGroupMembers");
        getGroupMemByGroupId(groupResponse.getGroupInfo().getId());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void getGroupMemByGroupId(String groupId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getGroupMemByGroupId(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(allGroupMembersResponses -> {
                    list.addAll(allGroupMembersResponses);
                    mAdapter.setNewData(list);
                    mAdapter.setOnItemClickListener((adapter, view, position) -> CommonUtils.resolveFriendList(this, list.get(position).getId(), groupId));
                }, this::handleApiError);
    }

}
