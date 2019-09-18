package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.SendUrlAndsendImgBean;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.CommandMessage;

public class OwnerGroupManageActivity extends BaseActivity {

    private Api api;

    private RelativeLayout rl_back;
    private TextView tv_title;
    private Switch switchMuteAll;
    private Switch switchAddFriend;
    private Switch switchSendPic;
    private Switch switchSendUrl;

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
    }

    @SuppressLint("CheckResult")
    private void initData() {
        rl_back.setOnClickListener(v -> finish());
        tv_title.setText(R.string.group_manage);

        switchMuteAll.setChecked(!group.getGroupInfo().getIsBanned().equals("0"));

        switchAddFriend.setChecked(!group.getGroupInfo().getBanFriend().equals("0"));

        switchSendPic.setChecked(!group.getGroupInfo().getBanSendPicture().equals("0"));

        switchSendUrl.setChecked(!group.getGroupInfo().getBanSendLink().equals("0"));

        switchSendPic.setOnClickListener(view -> api.groupOperation(group.getGroupInfo().getId(), switchSendPic.isChecked() ? "1" : "0", "1")
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
                }, t -> {
                    switchSendPic.setChecked(!switchSendPic.isChecked());
                    handleApiError(t);
                }));

        switchSendUrl.setOnClickListener(view -> api.groupOperation(group.getGroupInfo().getId(), switchSendUrl.isChecked() ? "1" : "0", "2")
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
                }, t -> {
                    switchSendUrl.setChecked(!switchSendUrl.isChecked());
                    handleApiError(t);
                }));

        switchAddFriend.setOnClickListener(v -> {
            MuteRemoveDialog dialog = new MuteRemoveDialog(OwnerGroupManageActivity.this, "确定", "取消", "禁止互加好友确认", "是否确定开启成员不能互加好友");
            dialog.setOnCommitListener(() -> switchAddFriend.setChecked(!switchAddFriend.isChecked()));
            dialog.setOnCancelListener(() -> api.groupOperation(group.getGroupInfo().getId(), switchAddFriend.isChecked() ? "1" : "0", "0")
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> group.getGroupInfo().setBanFriend(switchAddFriend.isChecked() ? "1" : "0"), t -> {
                        switchAddFriend.setChecked(!switchAddFriend.isChecked());
                        handleApiError(t);
                    }));
            dialog.setOnCloseListener(() -> switchAddFriend.setChecked(!switchAddFriend.isChecked()));
            dialog.show();
        });

        switchMuteAll.setOnClickListener(v -> {
            MuteRemoveDialog dialog = new MuteRemoveDialog(OwnerGroupManageActivity.this, "确定", "取消", "禁言确认", "是否确定开启全员禁言功能");
            dialog.setOnCommitListener(() -> switchMuteAll.setChecked(!switchMuteAll.isChecked()));
            dialog.setOnCancelListener(() -> api.muteGroups(group.getGroupInfo().getId(), switchMuteAll.isChecked() ? "add" : "remove")
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(s -> group.getGroupInfo().setIsBanned(switchMuteAll.isChecked() ? "1" : "0"), t -> {
                        switchMuteAll.setChecked(!switchMuteAll.isChecked());
                        handleApiError(t);
                    }));
            dialog.setOnCloseListener(() -> switchMuteAll.setChecked(!switchMuteAll.isChecked()));
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

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("group", group);
        setResult(4, intent);
        super.finish();
    }
}
