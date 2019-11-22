package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.ChooseNewOwnerAdapter;
import com.zxjk.duoduo.utils.CommonUtils;

@SuppressLint("CheckResult")
public class ChooseNewOwnerActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private ChooseNewOwnerAdapter mAdapter;
    private String groupId;
    private boolean fromSocial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_new_owner);
        groupId = getIntent().getStringExtra("groupId");
        fromSocial = getIntent().getBooleanExtra("fromSocial", false);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.choose_a_new_owner_title);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new ChooseNewOwnerAdapter();
        getGroupMemByGroupId(groupId);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> NiceDialog.init().setLayoutId(R.layout.layout_general_dialog4).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_content, "确定选择为新群主吗?");
                holder.setText(R.id.tv_cancel, "取消");
                holder.setText(R.id.tv_notarize, "确定");
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v -> {
                    updateGroupOwner(groupId, mAdapter.getData().get(position).getId());
                    dialog.dismiss();
                });

            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager()));
    }

    /**
     * 查询群成员
     *
     * @param groupId
     */
    public void getGroupMemByGroupId(String groupId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getGroupMemByGroupId(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(allGroupMembersResponses -> {
                    allGroupMembersResponses.remove(0);
                    mAdapter.setNewData(allGroupMembersResponses);
                }, this::handleApiError);
    }

    /**
     * 准让群主
     *
     * @param groupId
     * @param customerId
     */
    public void updateGroupOwner(String groupId, String customerId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updateGroupOwner(groupId, customerId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    ToastUtils.showShort(ChooseNewOwnerActivity.this.getString(R.string.transfer_group_successful));
                    if (fromSocial) {
                        finish();
                        return;
                    }
                    Intent intent = new Intent(ChooseNewOwnerActivity.this, ConversationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }, this::handleApiError);

    }
}
