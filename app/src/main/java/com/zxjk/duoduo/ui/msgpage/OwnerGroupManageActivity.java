package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.SendUrlAndsendImgBean;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.BurnMsgDialog;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.CommandMessage;
import io.rong.message.InformationNotificationMessage;

public class OwnerGroupManageActivity extends BaseActivity {
    private Api api;

    private RelativeLayout rl_back;
    private LinearLayout llPublic;
    private TextView tv_title;
    private Switch switchMuteAll;
    private Switch switchAddFriend;
    private Switch switchSendPic;
    private Switch switchSendUrl;
    private Switch switchGroupPublic;
    private TextView tvGroupTips2;
    private TextView tvGroupTips3;

    private GroupResponse group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_group_manage);

        group = (GroupResponse) getIntent().getSerializableExtra("group");

        api = ServiceFactory.getInstance().getBaseService(Api.class);

        initView();
        initData();
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back);
        tv_title = findViewById(R.id.tv_title);
        switchMuteAll = findViewById(R.id.switchMuteAll);
        switchAddFriend = findViewById(R.id.switchAddFriend);
        switchSendPic = findViewById(R.id.switchSendPic);
        switchSendUrl = findViewById(R.id.switchSendUrl);
        switchGroupPublic = findViewById(R.id.switchGroupPublic);
        tvGroupTips2 = findViewById(R.id.tvGroupTips2);
        tvGroupTips3 = findViewById(R.id.tvGroupTips3);
        llPublic = findViewById(R.id.llPublic);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        rl_back.setOnClickListener(v -> finish());
        tv_title.setText(R.string.group_manage);

        switchMuteAll.setChecked(!group.getGroupInfo().getIsBanned().equals("0"));

        switchAddFriend.setChecked(!group.getGroupInfo().getBanFriend().equals("0"));

        switchSendPic.setChecked(!group.getGroupInfo().getBanSendPicture().equals("0"));

        switchSendUrl.setChecked(!group.getGroupInfo().getBanSendLink().equals("0"));

        switchGroupPublic.setChecked(!group.getGroupInfo().getIsPublic().equals("0"));

        String tips = "";
        switch (group.getGroupInfo().getSlowMode()) {
            case "0":
                tips = getString(R.string.close);
                break;
            case "30":
                tips = getString(R.string.slowMode_time, "30", getString(R.string.second));
                break;
            case "60":
                tips = getString(R.string.slowMode_time, "1", getString(R.string.minute));
                break;
            case "300":
                tips = getString(R.string.slowMode_time, "5", getString(R.string.minute));
                break;
            case "600":
                tips = getString(R.string.slowMode_time, "10", getString(R.string.minute));
                break;
            case "3600":
                tips = getString(R.string.slowMode_time, "1", getString(R.string.hour));
                break;
        }
        tvGroupTips2.setText(tips);
        tvGroupTips3.setText(getString(R.string.current_limit_xx_person, group.getGroupInfo().getLimitNumber()));

        switchSendPic.setOnClickListener(v -> {
            MuteRemoveDialog dialog = new MuteRemoveDialog(OwnerGroupManageActivity.this, getString(R.string.queding), getString(R.string.cancel),
                    getString(R.string.ban_send_pic), getString(R.string.ban_send_pic1));
            dialog.setOnCommitListener(() -> switchSendPic.setChecked(!switchSendPic.isChecked()));
            dialog.setOnCancelListener(() -> api.groupOperation(group.getGroupInfo().getId(), switchSendPic.isChecked() ? "1" : "0", "1")
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        group.getGroupInfo().setBanSendPicture(switchSendPic.isChecked() ? "1" : "0");
                        SendUrlAndsendImgBean bean = new SendUrlAndsendImgBean(group);
                        CommandMessage commandMessage = CommandMessage.obtain("sendUrlAndsendImg", GsonUtils.toJson(bean));
                        Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP,
                                commandMessage);
                        RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                        String text;
                        if (switchSendPic.isChecked()) {
                            text = getString(R.string.ban_sendpic_open);
                        } else {
                            text = getString(R.string.ban_sendpic_close);
                        }
                        InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                        Message message1 = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
                        RongIM.getInstance().sendMessage(message1, "", "", (IRongCallback.ISendMessageCallback) null);
                    }, t -> {
                        switchSendPic.setChecked(!switchSendPic.isChecked());
                        handleApiError(t);
                    }));
            dialog.setOnCloseListener(() -> switchSendPic.setChecked(!switchSendPic.isChecked()));
            dialog.show();
        });

        switchSendUrl.setOnClickListener(v -> {
            MuteRemoveDialog dialog = new MuteRemoveDialog(OwnerGroupManageActivity.this, getString(R.string.queding),
                    getString(R.string.cancel), getString(R.string.ban_send_link), getString(R.string.ban_send_link1));
            dialog.setOnCommitListener(() -> switchSendUrl.setChecked(!switchSendUrl.isChecked()));
            dialog.setOnCancelListener(() -> api.groupOperation(group.getGroupInfo().getId(), switchSendUrl.isChecked() ? "1" : "0", "2")
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        group.getGroupInfo().setBanSendLink(switchSendUrl.isChecked() ? "1" : "0");
                        SendUrlAndsendImgBean bean = new SendUrlAndsendImgBean(group);
                        CommandMessage commandMessage = CommandMessage.obtain("sendUrlAndsendImg", GsonUtils.toJson(bean));
                        Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP,
                                commandMessage);
                        RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                        String text;
                        if (switchSendUrl.isChecked()) {
                            text = getString(R.string.ban_send_link_open);
                        } else {
                            text = getString(R.string.ban_send_link_close);
                        }
                        InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                        Message message1 = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
                        RongIM.getInstance().sendMessage(message1, "", "", (IRongCallback.ISendMessageCallback) null);
                    }, t -> {
                        switchSendUrl.setChecked(!switchSendUrl.isChecked());
                        handleApiError(t);
                    }));
            dialog.setOnCloseListener(() -> switchSendUrl.setChecked(!switchSendUrl.isChecked()));
            dialog.show();
        });

        switchAddFriend.setOnClickListener(v -> {
            MuteRemoveDialog dialog = new MuteRemoveDialog(OwnerGroupManageActivity.this, getString(R.string.queding),
                    getString(R.string.cancel), getString(R.string.ban_add_friend), getString(R.string.ban_add_friend1));
            dialog.setOnCommitListener(() -> switchAddFriend.setChecked(!switchAddFriend.isChecked()));
            dialog.setOnCancelListener(() -> api.groupOperation(group.getGroupInfo().getId(), switchAddFriend.isChecked() ? "1" : "0", "0")
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        group.getGroupInfo().setBanFriend(switchAddFriend.isChecked() ? "1" : "0");
                        String text;
                        if (switchAddFriend.isChecked()) {
                            text = getString(R.string.ban_add_friend_open);
                        } else {
                            text = getString(R.string.ban_add_friend_close);
                        }
                        InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                        Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
                        RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                    }, t -> {
                        switchAddFriend.setChecked(!switchAddFriend.isChecked());
                        handleApiError(t);
                    }));
            dialog.setOnCloseListener(() -> switchAddFriend.setChecked(!switchAddFriend.isChecked()));
            dialog.show();
        });

        switchMuteAll.setOnClickListener(v -> {
            MuteRemoveDialog dialog = new MuteRemoveDialog(OwnerGroupManageActivity.this, getString(R.string.queding), getString(R.string.cancel),
                    getString(R.string.mute_confirm), getString(R.string.mute_confirm1));
            dialog.setOnCommitListener(() -> switchMuteAll.setChecked(!switchMuteAll.isChecked()));
            dialog.setOnCancelListener(() -> api.muteGroups(group.getGroupInfo().getId(), switchMuteAll.isChecked() ? "add" : "remove")
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        group.getGroupInfo().setIsBanned(switchMuteAll.isChecked() ? "1" : "0");
                        String text;
                        if (switchMuteAll.isChecked()) {
                            text = getString(R.string.mute_all_open);
                        } else {
                            text = getString(R.string.mute_all_close);
                        }
                        InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                        Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
                        RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                    }, t -> {
                        switchMuteAll.setChecked(!switchMuteAll.isChecked());
                        handleApiError(t);
                    }));
            dialog.setOnCloseListener(() -> switchMuteAll.setChecked(!switchMuteAll.isChecked()));
            dialog.show();
        });

        switchGroupPublic.setOnClickListener(v -> {
            MuteRemoveDialog dialog = new MuteRemoveDialog(OwnerGroupManageActivity.this, getString(R.string.queding), getString(R.string.cancel),
                    getString(R.string.social_public), getString(R.string.social_public1));
            dialog.setOnCommitListener(() -> switchGroupPublic.setChecked(!switchGroupPublic.isChecked()));
            dialog.setOnCancelListener(() -> api.groupOperation(group.getGroupInfo().getId(), switchGroupPublic.isChecked() ? "1" : "0", "3")
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> group.getGroupInfo().setIsPublic(switchGroupPublic.isChecked() ? "1" : "0"),
                            t -> {
                                switchGroupPublic.setChecked(!switchGroupPublic.isChecked());
                                handleApiError(t);
                            }));
            dialog.setOnCloseListener(() -> switchGroupPublic.setChecked(!switchGroupPublic.isChecked()));
            dialog.show();
        });
    }

    /**
     * 禁言管理
     *
     * @param v view
     */
    public void muteManage(View v) {
        Intent intent = new Intent(this, MuteManageActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        startActivity(intent);
    }

    //新人进群红包设置
    public void newRedPackage(View view) {
        Intent intent = new Intent(this, EnterGroupGetRedActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        startActivity(intent);
    }

    //付费进群
    public void payEnterGroup(View view) {
        Intent intent = new Intent(this, PayEnterGroupActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        startActivity(intent);
    }

    public void func1(View view) {
        Intent intent = new Intent(this, UpdateGroupLimitActivity.class);
        intent.putExtra("group", group);
        startActivityForResult(intent, 1);
    }

    //加群方式
    public void addWays(View view) {
        ToastUtils.showShort(R.string.developing);
    }

    /**
     * 慢速模式
     *
     * @param view
     */
    @SuppressLint("CheckResult")
    public void func2(View view) {
        BurnMsgDialog burnMsgDialog = new BurnMsgDialog(this);
        burnMsgDialog.setOnCommitListener(str -> {
            if (group.getGroupInfo().getSlowMode().equals(str)) {
                return;
            }
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .groupOperation(group.getGroupInfo().getId(), str, "4")
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> {
                        String text = "";
                        String tips = "";
                        switch (str) {
                            case "0":
                                tips = getString(R.string.close);
                                text = getString(R.string.close_slow_mode);
                                break;
                            case "30":
                                text = getString(R.string.owner_open_slowmode, "30", getString(R.string.second));
                                tips = getString(R.string.slowMode_time, "30", getString(R.string.second));
                                break;
                            case "60":
                                text = getString(R.string.owner_open_slowmode, "1", getString(R.string.minute));
                                tips = getString(R.string.slowMode_time, "1", getString(R.string.minute));
                                break;
                            case "300":
                                text = getString(R.string.owner_open_slowmode, "5", getString(R.string.minute));
                                tips = getString(R.string.slowMode_time, "5", getString(R.string.minute));
                                break;
                            case "600":
                                text = getString(R.string.owner_open_slowmode, "10", getString(R.string.minute));
                                tips = getString(R.string.slowMode_time, "10", getString(R.string.minute));
                                break;
                            case "3600":
                                text = getString(R.string.owner_open_slowmode, "1", getString(R.string.hour));
                                tips = getString(R.string.slowMode_time, "1", getString(R.string.hour));
                                break;
                        }
                        tvGroupTips2.setText(tips);
                        InformationNotificationMessage messageContent = InformationNotificationMessage.obtain(text);
                        messageContent.setExtra(messageContent.getMessage());
                        Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, messageContent);
                        RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                        group.getGroupInfo().setSlowMode(str);
                    }, this::handleApiError);
        });
        burnMsgDialog.showSlowMode(parseTime(tvGroupTips2.getText().toString()));
    }

    private int parseTime(String time) {
        if (time.equals(getString(R.string.slowMode_time, "30", getString(R.string.second)))) {
            return 1;
        }

        if (time.equals(getString(R.string.slowMode_time, "1", getString(R.string.second)))) {
            return 2;
        }

        if (time.equals(getString(R.string.slowMode_time, "5", getString(R.string.minute)))) {
            return 3;
        }

        if (time.equals(getString(R.string.slowMode_time, "10", getString(R.string.minute)))) {
            return 4;
        }

        if (time.equals(getString(R.string.slowMode_time, "1", getString(R.string.hour)))) {
            return 5;
        }

        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1 && data != null) {
            group = (GroupResponse) data.getSerializableExtra("group");
            tvGroupTips3.setText(getString(R.string.current_limit_xx_person, group.getGroupInfo().getLimitNumber()));
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("group", group);
        setResult(2, intent);
        super.finish();
    }
}
