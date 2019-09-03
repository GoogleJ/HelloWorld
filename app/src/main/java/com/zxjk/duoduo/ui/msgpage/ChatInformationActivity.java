package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.ConversationInfo;
import com.zxjk.duoduo.bean.request.UpdateChatConfigRequest;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.BurnMsgDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.InformationNotificationMessage;

@SuppressLint("CheckResult")
public class ChatInformationActivity extends BaseActivity {

    private TextView tv_qm;
    private UserInfo userInfo;
    private Switch switch1;
    private Switch switch2;
    private Switch switch3;
    private TextView tv_name;
    private TextView tvBurnTime;

    private ConversationInfo conversationInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_information);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.lqxx));
        ImageView iv_head = findViewById(R.id.iv_head);
        tv_name = findViewById(R.id.tv_name);
        tvBurnTime = findViewById(R.id.tvBurnTime);
        tv_qm = findViewById(R.id.tv_qm);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);

        userInfo = getIntent().getParcelableExtra("bean");

        conversationInfo = (ConversationInfo) getIntent().getSerializableExtra("conversationInfo");

        if (conversationInfo != null)
            tvBurnTime.setText(parseTime(conversationInfo.getMessageBurnTime()));

        GlideUtil.loadCircleImg(iv_head, userInfo.getPortraitUri().toString());

        tv_name.setText(userInfo.getName());

        RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.PRIVATE, userInfo.getUserId(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
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

        switch3.setChecked(conversationInfo.getCaptureScreenEnabled() == 1);
        switch3.setOnClickListener(v -> {
            UpdateChatConfigRequest request = new UpdateChatConfigRequest();
            request.setType("personal");
            request.setTargetId(userInfo.getUserId());
            request.setScreenCapture(switch3.isChecked() ? 1 : 0);
            request.setIncinerationTime(conversationInfo.getMessageBurnTime());
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .updateChatConfig(GsonUtils.toJson(request))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .flatMap((Function<String, ObservableSource<String>>) s -> Observable.create(e -> {
                        conversationInfo.setCaptureScreenEnabled(switch3.isChecked() ? 1 : 0);
                        InformationNotificationMessage message = InformationNotificationMessage.obtain(Constant.currentUser.getNick()
                                + (switch3.isChecked() ? "开启了截屏通知" : "关闭了截屏通知"));
                        RongIM.getInstance().insertIncomingMessage(
                                Conversation.ConversationType.PRIVATE,
                                userInfo.getUserId(), Constant.userId, new Message.ReceivedStatus(1), message, new RongIMClient.ResultCallback<Message>() {
                                    @Override
                                    public void onSuccess(Message message) {
                                        e.onComplete();
                                        if (!switch3.isChecked()) {
                                            ToastUtils.showShort(R.string.close_capturescreen_success);
                                            return;
                                        }
                                        ToastUtils.showShort(R.string.open_capturescreen_success);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        e.onComplete();
                                    }
                                }
                        );
                    }))
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ChatInformationActivity.this)))
                    .subscribe(s -> {
                    }, t -> {
                        ChatInformationActivity.this.handleApiError(t);
                        switch3.setChecked(!switch3.isChecked());
                    });
        });

        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Conversation.ConversationNotificationStatus status;
            if (isChecked) {
                status = Conversation.ConversationNotificationStatus.DO_NOT_DISTURB;
            } else {
                status = Conversation.ConversationNotificationStatus.NOTIFY;
            }
            RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.PRIVATE, userInfo.getUserId(), status, null);
        });

        RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE, userInfo.getUserId(), new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if (conversation != null) switch1.setChecked(conversation.isTop());
                else switch1.setEnabled(false);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> RongIM.getInstance().setConversationToTop(Conversation.ConversationType.PRIVATE, userInfo.getUserId(), isChecked, null));

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCustomerInfoById(userInfo.getUserId())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(response -> tv_qm.setText(response.getSignature()), this::handleApiError);

        findViewById(R.id.rl_info).setOnClickListener(v -> {
            Intent intent = new Intent(ChatInformationActivity.this, FriendDetailsActivity.class);
            intent.putExtra("friendId", userInfo.getUserId());
            startActivityForResult(intent, 1000);
        });

        findViewById(R.id.rl_add).setOnClickListener(v -> {
            Intent intent = new Intent(ChatInformationActivity.this, CreateGroupActivity.class);
            intent.putExtra("eventType", 1);
            startActivity(intent);
        });

        findViewById(R.id.rl_qk).setOnClickListener(v ->
                NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.setText(R.id.tv_title, "提示");
                        holder.setText(R.id.tv_content, "确定要清空当前聊天记录吗？");
                        holder.setText(R.id.tv_cancel, "取消");
                        holder.setText(R.id.tv_notarize, "确认");
                        holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                        holder.setOnClickListener(R.id.tv_notarize, v12 -> RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE
                                , userInfo.getUserId(), new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(ChatInformationActivity.this, HomeActivity.class);
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
                }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager()));

        findViewById(R.id.rl_juBao).setOnClickListener(v -> startActivity(new Intent(ChatInformationActivity.this, SkinReportActivity.class)));

        findViewById(R.id.rl_burnmessage).setOnClickListener(v -> {
            BurnMsgDialog burnMsgDialog = new BurnMsgDialog(this);
            burnMsgDialog.setOnCommitListener(str -> {
                UpdateChatConfigRequest request = new UpdateChatConfigRequest();
                request.setType("personal");
                request.setTargetId(userInfo.getUserId());
                request.setScreenCapture(conversationInfo.getCaptureScreenEnabled());
                request.setIncinerationTime(parseStr(str));

                ServiceFactory.getInstance().getBaseService(Api.class)
                        .updateChatConfig(GsonUtils.toJson(request))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ChatInformationActivity.this)))
                        .compose(RxSchedulers.normalTrans())
                        .flatMap((Function<String, ObservableSource<String>>) s -> Observable.create(e -> {
                            conversationInfo.setMessageBurnTime(parseStr(str));
                            String tip = str.equals("关闭") ? getString(R.string.closeburn) : (getString(R.string.openburn) + (str.equals("即刻焚烧") ? "" : str) + getString(R.string.openburn1));
                            InformationNotificationMessage message = InformationNotificationMessage.obtain("您" + tip);
                            RongIM.getInstance().insertIncomingMessage(
                                    Conversation.ConversationType.PRIVATE,
                                    userInfo.getUserId(), Constant.userId, new Message.ReceivedStatus(1), message, new RongIMClient.ResultCallback<Message>() {
                                        @Override
                                        public void onSuccess(Message message) {
                                            tvBurnTime.setText(str);
                                            if (str.equals("关闭")) {
                                                ToastUtils.showShort(R.string.close_burn_success);
                                                return;
                                            }
                                            ToastUtils.showShort(R.string.open_burn_success);
                                            e.onComplete();
                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode errorCode) {
                                            e.onComplete();
                                        }
                                    }
                            );
                        }))
                        .subscribe(s -> {
                        }, this::handleApiError);
            });
            burnMsgDialog.show(parseIndex(tvBurnTime.getText().toString()));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1000) {
            String remark = data.getStringExtra("remark");
            tv_name.setText(remark);
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("title", tv_name.getText().toString());
        setResult(1000, intent);
        super.finish();
    }

    private String parseTime(int second) {
        if (conversationInfo == null) return null;

        if (second == 0) {
            return "即刻焚烧";
        }

        if (second == 10) {
            return "10秒";
        }

        if (second == 300) {
            return "5分钟";
        }

        if (second == 3600) {
            return "1小时";
        }

        if (second == 86400) {
            return "24小时";
        }

        return "关闭";
    }

    private int parseStr(String str) {
        if (conversationInfo == null) return -1;
        switch (str) {
            case "即刻焚烧":
                return 0;
            case "10秒":
                return 10;
            case "5分钟":
                return 300;
            case "1小时":
                return 3600;
            case "24小时":
                return 86400;
        }
        return -1;
    }

    private int parseIndex(String str) {
        switch (str) {
            case "":
                return 0;
            case "即刻焚烧":
                return 1;
            case "10秒":
                return 2;
            case "5分钟":
                return 3;
            case "1小时":
                return 4;
            case "24小时":
                return 5;
        }
        return 0;
    }
}
