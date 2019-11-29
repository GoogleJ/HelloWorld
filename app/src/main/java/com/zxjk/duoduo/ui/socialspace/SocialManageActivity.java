package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CommunityInfoResponse;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.InviteForSocialActivity;
import com.zxjk.duoduo.ui.minepage.UpdateUserInfoActivity;
import com.zxjk.duoduo.ui.msgpage.ChooseNewOwnerActivity;
import com.zxjk.duoduo.ui.msgpage.MuteManageActivity;
import com.zxjk.duoduo.ui.msgpage.OwnerGroupAuthorityActivity;
import com.zxjk.duoduo.ui.msgpage.OwnerGroupManageActivity;
import com.zxjk.duoduo.ui.msgpage.SkinReportActivity;
import com.zxjk.duoduo.ui.widget.dialog.ConfirmDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class SocialManageActivity extends BaseActivity {

    private CommunityInfoResponse response;

    private TextView tvSocialName;
    private TextView tvNick;
    private LinearLayout llOwner;
    private LinearLayout llNewOwner;
    private LinearLayout llReport;
    private Switch switch1;
    private Switch switch2;
    private LinearLayout ll_groupmanager;
    private TextView tvBottom;

    private GroupResponse group;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_manage);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.ic_social_end_pop2);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        response = getIntent().getParcelableExtra("data");

        tvSocialName = findViewById(R.id.tvSocialName);
        tvNick = findViewById(R.id.tvNick);
        llOwner = findViewById(R.id.llOwner);
        llNewOwner = findViewById(R.id.llNewOwner);
        llReport = findViewById(R.id.llReport);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        ll_groupmanager = findViewById(R.id.ll_groupmanager);
        tvBottom = findViewById(R.id.tvBottom);

        tvNick.setText(Constant.currentUser.getNick());
        String identity = response.getIdentity();
        if (!"0".equals(identity)) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getGroupByGroupId(response.getGroupId())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(group -> this.group = group, this::handleApiError);
        }
        switch (identity) {
            case "0":
                //成员
                llReport.setVisibility(View.VISIBLE);
                break;
            case "1":
                //管理
                ll_groupmanager.setVisibility(View.VISIBLE);
                break;
            case "2":
                //群主
                llOwner.setVisibility(View.VISIBLE);
                llNewOwner.setVisibility(View.VISIBLE);
                tvBottom.setText(R.string.close_social);
                break;
        }

        RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, response.getGroupId(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
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
            RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.GROUP, response.getGroupId(), status, null);
        });

        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, response.getGroupId(), new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if (conversation != null) switch1.setChecked(conversation.isTop());
                else switch1.setEnabled(false);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, response.getGroupId(), isChecked, null));
    }

    public void socialName(View view) {
        if (!response.getIdentity().equals("0")) {
            GroupResponse group = new GroupResponse();
            GroupResponse.GroupInfoBean groupInfoBean = new GroupResponse.GroupInfoBean();
            groupInfoBean.setId(response.getGroupId());
            group.setGroupInfo(groupInfoBean);
            Intent intent = new Intent(this, UpdateUserInfoActivity.class);
            intent.putExtra("type", 4);
            intent.putExtra("data", group);
            startActivityForResult(intent, 1);
        } else {
            ToastUtils.showShort(getString(R.string.no_update_nick));
        }
    }

    public void inviteWechat(View view) {
        Intent intent = new Intent(this, InviteForSocialActivity.class);
        intent.putExtra("groupId", response.getGroupId());
        intent.putExtra("groupName", response.getName());
        startActivity(intent);
    }

    public void clearHistory(View view) {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, "提示");
                holder.setText(R.id.tv_content, "确定要清空当前聊天记录吗？");
                holder.setText(R.id.tv_cancel, "取消");
                holder.setText(R.id.tv_notarize, "确认");
                holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v1 -> RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, response.getGroupId(), new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dialog.dismiss();
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

    public void report(View view) {
        startActivity(new Intent(this, SkinReportActivity.class));
    }

    public void chooseNewOwner(View view) {
        Intent intent = new Intent(this, ChooseNewOwnerActivity.class);
        intent.putExtra("groupId", response.getGroupId());
        intent.putExtra("fromSocial", true);
        startActivity(intent);
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
        intent.putExtra("fromsocial", true);
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

    public void funBottom(View view) {
        ConfirmDialog confirmDialog;
        if (response.getIdentity().equals("2")) {
            confirmDialog = new ConfirmDialog(this, "提示", "是否确定解散社群", v -> disBandGroup(response.getGroupId(), Constant.userId));
        } else {
            confirmDialog = new ConfirmDialog(this, "提示", "是否确定退出社群", v -> exitGroup(response.getGroupId(), Constant.userId));
        }
        confirmDialog.show();
    }

    /**
     * 解散群
     *
     * @param groupId
     * @param groupOwnerId
     */
    @SuppressLint("CheckResult")
    public void disBandGroup(String groupId, String groupOwnerId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .disBandGroup(groupId, groupOwnerId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId, null);
                    Intent intent = new Intent(SocialManageActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    ToastUtils.showShort(getString(R.string.you_have_disbanded_the_group1));
                }, this::handleApiError);
    }

    /**
     * 退出群
     *
     * @param groupId
     * @param customerId
     */
    @SuppressLint("CheckResult")
    public void exitGroup(String groupId, String customerId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .exitGroup(groupId, customerId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId, null);
                    Intent intent = new Intent(SocialManageActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    ToastUtils.showShort(getString(R.string.you_have_left_the_group_chat1));
                }, this::handleApiError);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 2) {
            response.setName(data.getStringExtra("result"));
            tvSocialName.setText(data.getStringExtra("result"));
        }
    }
}
