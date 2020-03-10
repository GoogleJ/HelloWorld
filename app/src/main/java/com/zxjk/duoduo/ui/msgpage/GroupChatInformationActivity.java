package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.AllGroupMembersResponse;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.UpdateUserInfoActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.AllGroupMemebersAdapter;
import com.zxjk.duoduo.ui.widget.dialog.ConfirmDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.RecyclerItemAverageDecoration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

@SuppressLint("CheckResult")
public class GroupChatInformationActivity extends BaseActivity {

    private static final int REQUEST_REMOVE = 7;
    private ArrayList<String> selectedIds = new ArrayList<>();

    private TextView groupChatName;
    private TextView see_more_group_members;
    private RecyclerView groupChatRecyclerView;
    private RelativeLayout rl_groupManage;
    private LinearLayout ll_groupowner;
    private LinearLayout ll_groupmanager;

    //群公告
    private TextView announcement;
    //解散群
    private TextView dissolutionGroup;

    private AllGroupMemebersAdapter mAdapter;

    private Intent intent;
    private TextView tv_title;
    private GroupResponse group;
    private List<AllGroupMembersResponse> allGroupMembersResponseList;

    private Switch switch1;
    private Switch switch2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_information);
        tv_title = findViewById(R.id.tv_title);
        ll_groupowner = findViewById(R.id.ll_groupowner);
        ll_groupmanager = findViewById(R.id.ll_groupmanager);

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        see_more_group_members = findViewById(R.id.see_more_group_members);

        rl_groupManage = findViewById(R.id.rl_groupManage);

        group = (GroupResponse) getIntent().getSerializableExtra("group");

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getGroupMemByGroupId(group.getGroupInfo().getId())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(allGroupMembersResponseList -> {
                    this.allGroupMembersResponseList = allGroupMembersResponseList;
                    initView();
                }, t -> {
                    handleApiError(t);
                    finish();
                });
    }

    private void initView() {
        RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, group.getGroupInfo().getId(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.NOTIFY) {
                    switch2.setChecked(false);
                } else {
                    switch2.setChecked(true);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Conversation.ConversationNotificationStatus status;
            if (isChecked) {
                status = Conversation.ConversationNotificationStatus.DO_NOT_DISTURB;
            } else {
                status = Conversation.ConversationNotificationStatus.NOTIFY;
            }
            RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.GROUP, group.getGroupInfo().getId(), status, null);
        });

        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, group.getGroupInfo().getId(), new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if (conversation != null) switch1.setChecked(conversation.isTop());
                else switch1.setEnabled(false);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, group.getGroupInfo().getId(), isChecked, null));

        tv_title.setText(getString(R.string.chat_message, String.valueOf(allGroupMembersResponseList.size())));
        groupChatName = findViewById(R.id.group_chat_name);
        groupChatRecyclerView = findViewById(R.id.group_chat_recycler_view);

        announcement = findViewById(R.id.announcement);
        dissolutionGroup = findViewById(R.id.dissolution_group);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        groupChatRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new AllGroupMemebersAdapter();
        groupChatRecyclerView.addItemDecoration(new RecyclerItemAverageDecoration(0, 0, 5));
        groupChatRecyclerView.setAdapter(mAdapter);
        if (allGroupMembersResponseList.size() <= 15) {
            see_more_group_members.setVisibility(View.GONE);
            mAdapter.setNewData(allGroupMembersResponseList);
        } else {
            see_more_group_members.setVisibility(View.VISIBLE);
            mAdapter.setNewData(allGroupMembersResponseList.subList(0, 15));
        }
        mAdapter.setOnItemClickListener((adapter, view, position) -> CommonUtils.resolveFriendList(this, allGroupMembersResponseList.get(position).getId(), group.getGroupInfo().getId()));

        initFooterView();

        groupChatName.setText(group.getGroupInfo().getGroupNikeName());
        announcement.setText(group.getGroupInfo().getGroupNotice());
        announcement.setVisibility(View.VISIBLE);

        if (Constant.userId.equals(group.getGroupInfo().getGroupOwnerId())) {
            dissolutionGroup.setText(getString(R.string.dissolution_group));
        } else {
            dissolutionGroup.setText(getString(R.string.exit_group));
        }
    }

    private void initFooterView() {
        View footerView = LayoutInflater.from(this).inflate(R.layout.view_bottom_del, null);
        ImageView delMembers = footerView.findViewById(R.id.delete_members);
        ImageView addMembers = footerView.findViewById(R.id.add_members);
        if (group.getGroupInfo().getGroupOwnerId().equals(Constant.userId)) {
            //群主才能踢人
            delMembers.setVisibility(View.VISIBLE);
            delMembers.setOnClickListener(v -> {
                intent = new Intent(GroupChatInformationActivity.this, CreateGroupActivity.class);
                intent.putExtra("eventType", 3);
                intent.putExtra("groupId", group.getGroupInfo().getId());
                startActivityForResult(intent, REQUEST_REMOVE);
            });
        }

        addMembers.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(group.getMaxNumber())) {
                if (allGroupMembersResponseList.size() >= Integer.parseInt(group.getMaxNumber())) {
                    ToastUtils.showShort(getString(R.string.group_max_number));
                    return;
                }
            }
            intent = new Intent(GroupChatInformationActivity.this, CreateGroupActivity.class);
            intent.putExtra("eventType", 2);
            intent.putExtra("groupId", group.getGroupInfo().getId());
            startActivity(intent);
        });
        mAdapter.addFooterView(footerView);
    }

    /**
     * 跳转群公告
     */
    public void announcement(View view) {
        if (!group.getGroupInfo().getGroupOwnerId().equals(Constant.userId)) {
            return;
        }
        Intent intent = new Intent(this, GroupAnnouncementActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        startActivityForResult(intent, 1);
    }

    /**
     * 查看全部群成员
     *
     * @param view
     */
    public void groupAllMembers(View view) {
        Intent intent = new Intent(this, AllGroupMembersActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        intent.putExtra("allGroupMembers", group);
        startActivity(intent);
    }

    /**
     * 管理群
     * 群转让
     *
     * @param view
     */
    public void groupManagement(View view) {
        Intent intent = new Intent(this, ChooseNewOwnerActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        startActivity(intent);
    }

    /**
     * 解散和退出群聊
     */
    public void dissolutionGroup(View view) {
        ConfirmDialog confirmDialog;
        if (Constant.userId.equals(group.getGroupInfo().getGroupOwnerId())) {
            confirmDialog = new ConfirmDialog(this, getString(R.string.hinttext),
                    getString(R.string.is_confirm_delete_group), v -> disBandGroup(group.getGroupInfo().getId(), Constant.userId));
        } else {
            confirmDialog = new ConfirmDialog(this, getString(R.string.hinttext),
                    getString(R.string.is_confirm_exit_group), v -> exitGroup(group.getGroupInfo().getId(), Constant.userId));
        }
        confirmDialog.show();
    }

    /**
     * 解散群
     *
     * @param groupId
     * @param groupOwnerId
     */
    public void disBandGroup(String groupId, String groupOwnerId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .disBandGroup(groupId, groupOwnerId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId
                        , new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                Intent intent = new Intent(GroupChatInformationActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                ToastUtils.showShort(getString(R.string.you_have_disbanded_the_group));
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                            }
                        }), this::handleApiError);
    }

    /**
     * 退出群
     *
     * @param groupId
     * @param customerId
     */
    public void exitGroup(String groupId, String customerId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .exitGroup(groupId, customerId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId,
                        new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                Intent intent = new Intent(GroupChatInformationActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                ToastUtils.showShort(getString(R.string.you_have_left_the_group_chat));
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                            }
                        }), this::handleApiError);
    }

    //清理历史记录
    public void clearHistory(View view) {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, R.string.hinttext);
                holder.setText(R.id.tv_content, R.string.is_clear_conversation_history);
                holder.setText(R.id.tv_cancel, R.string.cancel);
                holder.setText(R.id.tv_notarize, R.string.m_transfer_info_commit_btn);
                holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v1 -> RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, group.getGroupInfo().getId(), new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dialog.dismiss();
                        Intent intent = new Intent(GroupChatInformationActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        dialog.dismiss();
                        ToastUtils.showShort(R.string.function_fail);
                    }
                }));
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    public void groupChat(View view) {
        if (group.getGroupInfo().getGroupOwnerId().equals(Constant.currentUser.getId())) {
            Intent intent = new Intent(this, UpdateUserInfoActivity.class);
            intent.putExtra("type", 4);
            intent.putExtra("data", group);
            intent.putExtra("groupId", group.getGroupInfo().getId());
            startActivityForResult(intent, 1);
        } else {
            ToastUtils.showShort(getString(R.string.no_update_nick));
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("title", groupChatName.getText().toString());
        intent.putExtra("group", group);
        setResult(1000, intent);
        super.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 2) {
            group.getGroupInfo().setGroupNikeName(data.getStringExtra("result"));
            groupChatName.setText(data.getStringExtra("result"));
        } else if (requestCode == 1 && resultCode == 3) {
            group.getGroupInfo().setGroupNotice(data.getStringExtra("result"));
            announcement.setText(data.getStringExtra("result"));
        } else if (requestCode == 1 && resultCode == 4) {
            group = (GroupResponse) data.getSerializableExtra("group");
        }

        if (data != null && resultCode == 7) {
            if (requestCode == REQUEST_REMOVE) {
                selectedIds = data.getStringArrayListExtra("deletemanagers");
                Iterator<AllGroupMembersResponse> it = allGroupMembersResponseList.iterator();

                for (String id : selectedIds) {
                    while (it.hasNext()) {
                        String id1 = it.next().getId();
                        if (id1.equals(id)) {
                            it.remove();
                        }
                    }
                }

                if (allGroupMembersResponseList.size() <= 15) {
                    see_more_group_members.setVisibility(View.GONE);
                    mAdapter.setNewData(allGroupMembersResponseList);
                } else {
                    see_more_group_members.setVisibility(View.VISIBLE);
                    mAdapter.setNewData(allGroupMembersResponseList.subList(0, 15));
                }
                tv_title.setText(getString(R.string.chat_message, String.valueOf(allGroupMembersResponseList.size())));
            }
        }
    }

    public void report(View view) {
        startActivity(new Intent(this, SkinReportActivity.class));
    }

    //群主权限管理
    public void ownerAuthority(View view) {
        Intent intent = new Intent(this, OwnerGroupAuthorityActivity.class);
        intent.putExtra("group", group);
        startActivityForResult(intent, 1);
    }

    //群主群管理
    public void ownerGroupManage(View view) {
        Intent intent = new Intent(this, OwnerGroupManageActivity.class);
        intent.putExtra("group", group);
        startActivityForResult(intent, 1);
    }

    //管理员禁言管理
    public void managerMute(View view) {
        GroupResponse.PermissionBean permission = group.getGroupPermission();
        if (permission.getOpenBanned().equals("0")) {
            ToastUtils.showShort(R.string.nopermisson);
            return;
        }
        Intent intent = new Intent(this, MuteManageActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        startActivity(intent);
    }

    //管理员视频直播
    public void managerVideo(View view) {
        GroupResponse.PermissionBean permission = group.getGroupPermission();
        if (permission.getOpenVideo().equals("0")) {
            ToastUtils.showShort(R.string.nopermisson);
            return;
        }
        ToastUtils.showShort(R.string.developing);
    }

    //管理员语音直播
    public void managerAudio(View view) {
        GroupResponse.PermissionBean permission = group.getGroupPermission();
        if (permission.getOpenAudio().equals("0")) {
            ToastUtils.showShort(R.string.nopermisson);
            return;
        }
        ToastUtils.showShort(R.string.developing);
    }

}
