package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
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
import com.zxjk.duoduo.bean.response.AllGroupMembersResponse;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.ChooseNewOwnerAdapter;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;

@SuppressLint("CheckResult")
public class ChooseNewOwnerActivity extends BaseActivity implements TextWatcher {
    private RecyclerView mRecyclerView;
    private ChooseNewOwnerAdapter mAdapter;
    private String groupId;
    private boolean fromSocial;
    private EditText searchEdit;
    private List<AllGroupMembersResponse> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_new_owner);
        groupId = getIntent().getStringExtra("groupId");
        fromSocial = getIntent().getBooleanExtra("fromSocial", false);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.choose_a_new_owner_title);
        searchEdit = findViewById(R.id.search_edit);
        searchEdit.addTextChangedListener(this);
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
                holder.setText(R.id.tv_content, R.string.confirm_choose_tobe_new_owner);
                holder.setText(R.id.tv_cancel, R.string.cancel);
                holder.setText(R.id.tv_notarize,R.string.queding);
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v -> {
                    updateGroupOwner(groupId, mAdapter.getData().get(position).getId(), mAdapter.getData().get(position).getNick());
                    dialog.dismiss();
                });

            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager()));
    }

    private void getGroupMemByGroupId(String groupId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getGroupMemByGroupId(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(allGroupMembersResponses -> {
                    data.addAll(allGroupMembersResponses);
                    allGroupMembersResponses.remove(0);
                    mAdapter.setNewData(allGroupMembersResponses);
                }, this::handleApiError);
    }

    private void updateGroupOwner(String groupId, String customerId, String newOwnerNick) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updateGroupOwner(groupId, customerId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    ToastUtils.showShort(ChooseNewOwnerActivity.this.getString(R.string.transfer_group_successful));

                    InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(getString(R.string.xxx_become_new_owner, newOwnerNick));
                    Message message = Message.obtain(groupId, Conversation.ConversationType.GROUP, notificationMessage);
                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                    if (fromSocial) {
                        Intent back2Home = new Intent(this, HomeActivity.class);
                        back2Home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(back2Home);
                        return;
                    }
                    Intent intent = new Intent(ChooseNewOwnerActivity.this, ConversationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }, this::handleApiError);

    }


    private void search(String str) {
        List<AllGroupMembersResponse> groupMember = new ArrayList<>();

        for (AllGroupMembersResponse contact : data) {
            boolean isNameContains = (!TextUtils.isEmpty(contact.getMobile()) && contact.getMobile().toLowerCase().contains(str.toLowerCase())) ||
                    (!TextUtils.isEmpty(contact.getNick()) && contact.getNick().toLowerCase().contains(str.toLowerCase())) ||
                    (!TextUtils.isEmpty(contact.getRemark()) && contact.getRemark().toLowerCase().contains(str.toLowerCase()));
            if (isNameContains) {
                if (!groupMember.contains(contact)) {
                    groupMember.add(contact);
                }
            }
        }
        mAdapter.setNewData(groupMember);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(searchEdit.getText().toString().trim())) {
                mAdapter.setNewData(data);
        } else {
                search(searchEdit.getText().toString().trim());
        }
    }
}
