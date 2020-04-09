package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zxjk.duoduo.bean.SendUrlAndsendImgBean;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.UpdateUserInfoActivity;
import com.zxjk.duoduo.ui.widget.dialog.BurnMsgDialog;
import com.zxjk.duoduo.ui.widget.dialog.ConfirmDialog;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.CommandMessage;
import io.rong.message.InformationNotificationMessage;

public class NewSocialManageActivity extends BaseActivity {
    private View root;

    private TextView tvSocialName;
    private Switch swGroupPublic;
    private TextView tvForceRecallStatus;
    private TextView tvSlowModeStatus;
    private Switch swMuteAll;
    private TextView tvGroupLimit;
    private Switch swForbidAddF;
    private Switch swForbidPic;
    private Switch swForbidVoice;
    private Switch swForbidUrl;
    private TextView tvExpandTip;
    private ImageView ivExpand;
    private TextView tvMyGroupNick;
    private Switch swSetTop;
    private Switch swMute;
    private LinearLayout llMoreFunc;
    private LinearLayout llClearAllHistory;

    private GroupResponse groupInfo;

    private Api api;

    private String identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_social_manage);

        initData();
    }

    private void initData() {
        api = ServiceFactory.getInstance().getBaseService(Api.class);

        groupInfo = (GroupResponse) getIntent().getSerializableExtra("group");

        if (groupInfo.getGroupInfo().getGroupOwnerId().equals(Constant.userId)) {
            ViewStub owner = findViewById(R.id.stubOwner);
            root = owner.inflate();
            identity = "2";
        } else if (groupInfo.getIsAdmin().equals("1")) {
            ViewStub manager = findViewById(R.id.stubManager);
            root = manager.inflate();
            identity = "1";
        } else {
            ViewStub member = findViewById(R.id.stubMember);
            root = member.inflate();
            identity = "0";
        }

        initView();

        if (tvSlowModeStatus != null) {
            String slowModeStatus = "";
            switch (groupInfo.getGroupInfo().getSlowMode()) {
                case "0":
                    slowModeStatus = getString(R.string.close);
                    break;
                case "30":
                    slowModeStatus = getString(R.string.slowMode_time, "30", getString(R.string.second));
                    break;
                case "60":
                    slowModeStatus = getString(R.string.slowMode_time, "1", getString(R.string.minute));
                    break;
                case "300":
                    slowModeStatus = getString(R.string.slowMode_time, "5", getString(R.string.minute));
                    break;
                case "600":
                    slowModeStatus = getString(R.string.slowMode_time, "10", getString(R.string.minute));
                    break;
                case "3600":
                    slowModeStatus = getString(R.string.slowMode_time, "1", getString(R.string.hour));
                    break;
            }
            tvSlowModeStatus.setText(slowModeStatus);
        }

        if (llClearAllHistory != null) {
            if (!TextUtils.isEmpty(groupInfo.getGroupPermission().getOpenGlobalClean())
                    && groupInfo.getGroupPermission().getOpenGlobalClean().equals("1")) {
                llClearAllHistory.setVisibility(View.VISIBLE);
            } else {
                llClearAllHistory.setVisibility(View.GONE);
            }
        }

        if (tvGroupLimit != null) {
            tvGroupLimit.setText(getString(R.string.current_limit_xx_person, groupInfo.getGroupInfo().getLimitNumber()));
        }

        if (tvSocialName != null) {
            tvSocialName.setText(groupInfo.getGroupInfo().getGroupNikeName());
        }

        if (tvForceRecallStatus != null) {
            if (identity.equals("2") || (identity.equals("1") && groupInfo.getGroupPermission().getForceRecall().equals("1"))) {
                tvForceRecallStatus.setText(R.string.enabled);
            } else {
                tvForceRecallStatus.setText(R.string.no_authorization);
            }
        }

        if (tvMyGroupNick != null) {
            tvMyGroupNick.setText(Constant.currentUser.getNick());
        }

        if (swForbidPic != null) {
            swForbidPic.setChecked(!groupInfo.getGroupInfo().getBanSendPicture().equals("0"));
            swForbidPic.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.ban_send_pic), getString(R.string.ban_send_pic1));
                dialog.setOnCancelListener(() -> swForbidPic.setChecked(!swForbidPic.isChecked()));
                dialog.setOnCommitListener(() -> api.groupOperation(groupInfo.getGroupInfo().getId(), swForbidPic.isChecked() ? "1" : "0", "1")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            groupInfo.getGroupInfo().setBanSendPicture(swForbidPic.isChecked() ? "1" : "0");
                            SendUrlAndsendImgBean bean = new SendUrlAndsendImgBean(groupInfo);
                            CommandMessage commandMessage = CommandMessage.obtain("sendUrlAndsendImg", GsonUtils.toJson(bean));
                            Message message = Message.obtain(groupInfo.getGroupInfo().getId(), Conversation.ConversationType.GROUP,
                                    commandMessage);
                            RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                            String text;
                            if (swForbidPic.isChecked()) {
                                text = getString(R.string.ban_sendpic_open);
                            } else {
                                text = getString(R.string.ban_sendpic_close);
                            }
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                            Message message1 = Message.obtain(groupInfo.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
                            RongIM.getInstance().sendMessage(message1, "", "", (IRongCallback.ISendMessageCallback) null);
                        }, t -> {
                            swForbidPic.setChecked(!swForbidPic.isChecked());
                            handleApiError(t);
                        }));
                dialog.setOnCloseListener(() -> swForbidPic.setChecked(!swForbidPic.isChecked()));
                dialog.show();
            });
        }

        if (swForbidUrl != null) {
            swForbidUrl.setChecked(!groupInfo.getGroupInfo().getBanSendLink().equals("0"));
            swForbidUrl.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.ban_send_link), getString(R.string.ban_send_link1));
                dialog.setOnCancelListener(() -> swForbidUrl.setChecked(!swForbidUrl.isChecked()));
                dialog.setOnCommitListener(() -> api.groupOperation(groupInfo.getGroupInfo().getId(), swForbidUrl.isChecked() ? "1" : "0", "2")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            groupInfo.getGroupInfo().setBanSendLink(swForbidUrl.isChecked() ? "1" : "0");
                            SendUrlAndsendImgBean bean = new SendUrlAndsendImgBean(groupInfo);
                            CommandMessage commandMessage = CommandMessage.obtain("sendUrlAndsendImg", GsonUtils.toJson(bean));
                            Message message = Message.obtain(groupInfo.getGroupInfo().getId(), Conversation.ConversationType.GROUP,
                                    commandMessage);
                            RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                            String text;
                            if (swForbidUrl.isChecked()) {
                                text = getString(R.string.ban_send_link_open);
                            } else {
                                text = getString(R.string.ban_send_link_close);
                            }
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                            Message message1 = Message.obtain(groupInfo.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
                            RongIM.getInstance().sendMessage(message1, "", "", (IRongCallback.ISendMessageCallback) null);
                        }, t -> {
                            swForbidUrl.setChecked(!swForbidUrl.isChecked());
                            handleApiError(t);
                        }));
                dialog.setOnCloseListener(() -> swForbidUrl.setChecked(!swForbidUrl.isChecked()));
                dialog.show();
            });
        }

        if (swForbidVoice != null) {
            swForbidVoice.setChecked(!groupInfo.getGroupInfo().getBanSendVoice().equals("0"));
            swForbidVoice.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.ban_send_voice), getString(R.string.ban_send_voice1));
                dialog.setOnCancelListener(() -> swForbidVoice.setChecked(!swForbidVoice.isChecked()));
                dialog.setOnCommitListener(() -> api.groupOperation(groupInfo.getGroupInfo().getId(), swForbidVoice.isChecked() ? "1" : "0", "5")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            groupInfo.getGroupInfo().setBanSendVoice(swForbidVoice.isChecked() ? "1" : "0");
                            SendUrlAndsendImgBean bean = new SendUrlAndsendImgBean(groupInfo);
                            CommandMessage commandMessage = CommandMessage.obtain("sendUrlAndsendImg", GsonUtils.toJson(bean));
                            Message message = Message.obtain(groupInfo.getGroupInfo().getId(), Conversation.ConversationType.GROUP,
                                    commandMessage);
                            RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                            String text;
                            if (swForbidVoice.isChecked()) {
                                text = getString(R.string.ban_send_voice_open);
                            } else {
                                text = getString(R.string.ban_send_voice_close);
                            }
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                            Message message1 = Message.obtain(groupInfo.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
                            RongIM.getInstance().sendMessage(message1, "", "", (IRongCallback.ISendMessageCallback) null);
                        }, t -> {
                            swForbidVoice.setChecked(!swForbidVoice.isChecked());
                            handleApiError(t);
                        }));
                dialog.setOnCloseListener(() -> swForbidVoice.setChecked(!swForbidVoice.isChecked()));
                dialog.show();
            });
        }

        if (swForbidAddF != null) {
            swForbidAddF.setChecked(!groupInfo.getGroupInfo().getBanFriend().equals("0"));
            swForbidAddF.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.ban_add_friend), getString(R.string.ban_add_friend1));
                dialog.setOnCancelListener(() -> swForbidAddF.setChecked(!swForbidAddF.isChecked()));
                dialog.setOnCommitListener(() -> api.groupOperation(groupInfo.getGroupInfo().getId(), swForbidAddF.isChecked() ? "1" : "0", "0")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            groupInfo.getGroupInfo().setBanFriend(swForbidAddF.isChecked() ? "1" : "0");
                            String text;
                            if (swForbidAddF.isChecked()) {
                                text = getString(R.string.ban_add_friend_open);
                            } else {
                                text = getString(R.string.ban_add_friend_close);
                            }
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                            Message message = Message.obtain(groupInfo.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
                            RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                        }, t -> {
                            swForbidAddF.setChecked(!swForbidAddF.isChecked());
                            handleApiError(t);
                        }));
                dialog.setOnCloseListener(() -> swForbidAddF.setChecked(!swForbidAddF.isChecked()));
                dialog.show();
            });
        }

        if (swMuteAll != null) {
            swMuteAll.setChecked(!groupInfo.getGroupInfo().getIsBanned().equals("0"));
            swMuteAll.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.mute_confirm), getString(R.string.mute_confirm1));
                dialog.setOnCancelListener(() -> swMuteAll.setChecked(!swMuteAll.isChecked()));
                dialog.setOnCommitListener(() -> api.muteGroups(groupInfo.getGroupInfo().getId(), swMuteAll.isChecked() ? "add" : "remove")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            groupInfo.getGroupInfo().setIsBanned(swMuteAll.isChecked() ? "1" : "0");
                            String text;
                            if (swMuteAll.isChecked()) {
                                text = getString(R.string.mute_all_open);
                            } else {
                                text = getString(R.string.mute_all_close);
                            }
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                            Message message = Message.obtain(groupInfo.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
                            RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                        }, t -> {
                            swMuteAll.setChecked(!swMuteAll.isChecked());
                            handleApiError(t);
                        }));
                dialog.setOnCloseListener(() -> swMuteAll.setChecked(!swMuteAll.isChecked()));
                dialog.show();
            });
        }

        if (swGroupPublic != null) {
            swGroupPublic.setChecked(!groupInfo.getGroupInfo().getIsPublic().equals("0"));
            swGroupPublic.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.social_public), getString(R.string.social_public1));
                dialog.setOnCancelListener(() -> swGroupPublic.setChecked(!swGroupPublic.isChecked()));
                dialog.setOnCommitListener(() -> api.groupOperation(groupInfo.getGroupInfo().getId(), swGroupPublic.isChecked() ? "1" : "0", "3")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> groupInfo.getGroupInfo().setIsPublic(swGroupPublic.isChecked() ? "1" : "0"),
                                t -> {
                                    swGroupPublic.setChecked(!swGroupPublic.isChecked());
                                    handleApiError(t);
                                }));
                dialog.setOnCloseListener(() -> swGroupPublic.setChecked(!swGroupPublic.isChecked()));
                dialog.show();
            });
        }

        if (swMute != null) {
            RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, groupInfo.getGroupInfo().getId(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                @Override
                public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                    if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.NOTIFY) {
                        swMute.setChecked(false);
                    } else {
                        swMute.setChecked(true);
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                }
            });
            swMute.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Conversation.ConversationNotificationStatus status;
                if (isChecked) {
                    status = Conversation.ConversationNotificationStatus.DO_NOT_DISTURB;
                } else {
                    status = Conversation.ConversationNotificationStatus.NOTIFY;
                }
                RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.GROUP, groupInfo.getGroupInfo().getId(), status, null);
            });
        }

        if (swSetTop != null) {
            RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, groupInfo.getGroupInfo().getId(), new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    if (conversation != null) swSetTop.setChecked(conversation.isTop());
                    else swSetTop.setEnabled(false);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                }
            });
            swSetTop.setOnCheckedChangeListener((buttonView, isChecked) -> RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, groupInfo.getGroupInfo().getId(), isChecked, null));
        }
    }

    private void initView() {
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.ic_social_end_pop2);

        tvSocialName = root.findViewById(R.id.tvSocialName);
        swGroupPublic = root.findViewById(R.id.swGroupPublic);
        tvForceRecallStatus = root.findViewById(R.id.tvForceRecallStatus);
        tvSlowModeStatus = root.findViewById(R.id.tvSlowModeStatus);
        swMuteAll = root.findViewById(R.id.swMuteAll);
        tvGroupLimit = root.findViewById(R.id.tvGroupLimit);
        swForbidAddF = root.findViewById(R.id.swForbidAddF);
        swForbidPic = root.findViewById(R.id.swForbidPic);
        swForbidVoice = root.findViewById(R.id.swForbidVoice);
        swForbidUrl = root.findViewById(R.id.swForbidUrl);
        tvExpandTip = root.findViewById(R.id.tvExpandTip);
        ivExpand = root.findViewById(R.id.ivExpand);
        tvMyGroupNick = root.findViewById(R.id.tvMyGroupNick);
        swSetTop = root.findViewById(R.id.swSetTop);
        swMute = root.findViewById(R.id.swMute);
        llMoreFunc = root.findViewById(R.id.llMoreFunc);
        llClearAllHistory = root.findViewById(R.id.llClearAllHistory);
    }

    public void changeGroupName(View view) {
        if (!identity.equals("0")) {
            Intent intent = new Intent(this, UpdateUserInfoActivity.class);
            intent.putExtra("type", 4);
            intent.putExtra("groupId", groupInfo.getGroupInfo().getId());
            intent.putExtra("group", groupInfo);
            intent.putExtra("fromSocial", true);
            startActivityForResult(intent, 1);
        } else {
            ToastUtils.showShort(getString(R.string.no_update_nick));
        }
    }

    public void airDrop(View view) {
        Intent intent = new Intent(this, EnterGroupGetRedActivity.class);
        intent.putExtra("groupId", groupInfo.getGroupInfo().getId());
        startActivity(intent);
    }

    public void payEnter(View view) {
        Intent intent = new Intent(this, PayEnterGroupActivity.class);
        intent.putExtra("groupId", groupInfo.getGroupInfo().getId());
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    public void slowMode(View view) {
        BurnMsgDialog burnMsgDialog = new BurnMsgDialog(this);
        burnMsgDialog.setOnCommitListener(str -> {
            if (groupInfo.getGroupInfo().getSlowMode().equals(str)) {
                return;
            }
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .groupOperation(groupInfo.getGroupInfo().getId(), str, "4")
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
                        tvSlowModeStatus.setText(tips);
                        InformationNotificationMessage messageContent = InformationNotificationMessage.obtain(text);
                        messageContent.setExtra(messageContent.getMessage());
                        Message message = Message.obtain(groupInfo.getGroupInfo().getId(), Conversation.ConversationType.GROUP, messageContent);
                        RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                        groupInfo.getGroupInfo().setSlowMode(str);
                    }, this::handleApiError);
        });
        burnMsgDialog.showSlowMode(parseTime(tvSlowModeStatus.getText().toString()));
    }

    public void clearLocalHistory(View view) {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, getString(R.string.hinttext));
                holder.setText(R.id.tv_content, getString(R.string.is_clear_conversation_history));
                holder.setText(R.id.tv_cancel, getString(R.string.cancel));
                holder.setText(R.id.tv_notarize, getString(R.string.queding));
                holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v1 ->
                        RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP,
                                groupInfo.getGroupInfo().getId(), new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        dialog.dismiss();

                                        ToastUtils.showShort(getString(R.string.clearance));
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

    public void clearHistory(View view) {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, getString(R.string.hinttext));
                holder.setText(R.id.tv_content, getString(R.string.is_clear_all_conversation_history));
                holder.setText(R.id.tv_cancel, getString(R.string.cancel));
                holder.setText(R.id.tv_notarize, getString(R.string.queding));
                holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v1 -> {
                    CommandMessage command = CommandMessage.obtain("ForceClearAllLocalHistory", groupInfo.getGroupInfo().getId());
                    Message message = Message.obtain(groupInfo.getGroupInfo().getId(), Conversation.ConversationType.GROUP, command);
                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                    RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, groupInfo.getGroupInfo().getId(), null);

                    dialog.dismiss();
                    Intent intent = new Intent(NewSocialManageActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                });
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    public void report(View view) {
        startActivity(new Intent(this, SkinReportActivity.class));
    }

    public void managerSetting(View view) {
        Intent intent = new Intent(this, OwnerGroupAuthorityActivity.class);
        intent.putExtra("group", groupInfo);
        startActivityForResult(intent, 1);
    }

    public void muteSetting(View view) {
        if (identity.equals("1")) {
            if (groupInfo.getGroupPermission() == null || TextUtils.isEmpty(groupInfo.getGroupPermission().getOpenBanned()) ||
                    !groupInfo.getGroupPermission().getOpenBanned().equals("1")) {
                ToastUtils.showShort(R.string.no_authorization);
                return;
            }
        }
        Intent intent = new Intent(this, MuteManageActivity.class);
        intent.putExtra("groupId", groupInfo.getGroupInfo().getId());
        startActivity(intent);
    }

    public void upgradeGroupLimit(View view) {
        Intent intent = new Intent(this, UpdateGroupLimitActivity.class);
        intent.putExtra("group", groupInfo);
        startActivityForResult(intent, 1);
    }

    public void moreFunc(View view) {
        if (llMoreFunc == null) return;
        if (llMoreFunc.getVisibility() == View.VISIBLE) {
            llMoreFunc.setVisibility(View.GONE);
            ivExpand.setImageResource(R.drawable.ic_new_socialmanage21);
            tvExpandTip.setText(R.string.more_group_func);
        } else {
            llMoreFunc.setVisibility(View.VISIBLE);
            ivExpand.setImageResource(R.drawable.ic_new_socialmanage15);
            tvExpandTip.setText(R.string.fold);
        }
    }

    public void transfer(View view) {
        Intent intent = new Intent(this, ChooseNewOwnerActivity.class);
        intent.putExtra("groupId", groupInfo.getGroupInfo().getId());
        intent.putExtra("fromSocial", true);
        startActivity(intent);
    }

    public void funBottom(View view) {
        ConfirmDialog confirmDialog;
        if (identity.equals("2")) {
            confirmDialog = new ConfirmDialog(this, getString(R.string.hinttext), getString(R.string.is_confirm_disband_group),
                    v -> disBandGroup(groupInfo.getGroupInfo().getId(), Constant.userId));
        } else {
            confirmDialog = new ConfirmDialog(this, getString(R.string.hinttext), getString(R.string.is_confirm_exit_group1),
                    v -> exitGroup(groupInfo.getGroupInfo().getId(), Constant.userId));
        }
        confirmDialog.show();
    }

    @SuppressLint("CheckResult")
    public void exitGroup(String groupId, String customerId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .exitGroup(groupId, customerId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId, null);
                    Intent intent = new Intent(NewSocialManageActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    ToastUtils.showShort(getString(R.string.you_have_left_the_group_chat1));
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    public void disBandGroup(String groupId, String groupOwnerId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .disBandGroup(groupId, groupOwnerId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId, null);
                    Intent intent = new Intent(NewSocialManageActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    ToastUtils.showShort(getString(R.string.you_have_disbanded_the_group1));
                }, this::handleApiError);
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
        if (data == null) return;

        if (data.getSerializableExtra("group") != null) {
            groupInfo = (GroupResponse) data.getSerializableExtra("group");
        }

        tvGroupLimit.setText(getString(R.string.current_limit_xx_person, groupInfo.getGroupInfo().getLimitNumber()));

        if (!TextUtils.isEmpty(data.getStringExtra("result"))) {
            groupInfo.getGroupInfo().setGroupNikeName(data.getStringExtra("result"));
            tvSocialName.setText(data.getStringExtra("result"));
        }
    }

    @Override
    public void finish() {
        if (groupInfo != null) {
            Intent intent = new Intent();
            intent.putExtra("group", groupInfo);
            this.setResult(1000, intent);
        }
        super.finish();
    }
}
