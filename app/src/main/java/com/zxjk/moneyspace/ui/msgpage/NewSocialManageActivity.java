package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.SendUrlAndsendImgBean;
import com.zxjk.moneyspace.bean.response.AllGroupMembersResponse;
import com.zxjk.moneyspace.bean.response.GroupResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.HomeActivity;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.UpdateUserInfoActivity;
import com.zxjk.moneyspace.ui.msgpage.adapter.AllGroupMemebersAdapter;
import com.zxjk.moneyspace.ui.widget.dialog.BurnMsgDialog;
import com.zxjk.moneyspace.ui.widget.dialog.ConfirmDialog;
import com.zxjk.moneyspace.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.RecyclerItemAverageDecoration;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.CommandMessage;
import io.rong.message.InformationNotificationMessage;

public class NewSocialManageActivity extends BaseActivity {
    private static final int REQUEST_REMOVE = 7;
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

    private GroupResponse group;

    private Api api;

    private String identity;

    private RecyclerView groupChatRecyclerView;
    private TextView see_more_group_members;
    private AllGroupMemebersAdapter mAdapter;
    private List<AllGroupMembersResponse> allGroupMembersResponseList;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_social_manage);

        api = ServiceFactory.getInstance().getBaseService(Api.class);

        group = (GroupResponse) getIntent().getSerializableExtra("group");

        api.getGroupMemByGroupId(group.getGroupInfo().getId())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(allGroupMembersResponseList -> {
                    this.allGroupMembersResponseList = allGroupMembersResponseList;
                    groupChatRecyclerView = findViewById(R.id.group_chat_recycler_view);
                    see_more_group_members = findViewById(R.id.see_more_group_members);
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

                    initData();

                    initView();
                }, t -> {
                    handleApiError(t);
                    finish();
                });
    }

    private void initFooterView() {
        View footerView = LayoutInflater.from(this).inflate(R.layout.view_bottom_del, null);
        ImageView delMembers = footerView.findViewById(R.id.delete_members);
        ImageView addMembers = footerView.findViewById(R.id.add_members);
        if (group.getGroupInfo().getGroupOwnerId().equals(Constant.userId)) {
            //群主才能踢人
            delMembers.setVisibility(View.VISIBLE);
            delMembers.setOnClickListener(v -> {
                Intent  intent = new Intent(this, CreateGroupActivity.class);
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
            Intent intent = new Intent(this, CreateGroupActivity.class);
            intent.putExtra("eventType", 2);
            intent.putExtra("groupId", group.getGroupInfo().getId());
            startActivity(intent);
        });
        mAdapter.addFooterView(footerView);
    }

    private void initData() {
        if (group.getGroupInfo().getGroupOwnerId().equals(Constant.userId)) {
            ViewStub owner = findViewById(R.id.stubOwner);
            root = owner.inflate();
            identity = "2";
        } else if (group.getIsAdmin().equals("1")) {
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
            switch (group.getGroupInfo().getSlowMode()) {
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
            if (!TextUtils.isEmpty(group.getGroupPermission().getOpenGlobalClean())
                    && group.getGroupPermission().getOpenGlobalClean().equals("1")) {
                llClearAllHistory.setVisibility(View.VISIBLE);
            } else {
                llClearAllHistory.setVisibility(View.GONE);
            }
        }

        if (tvGroupLimit != null) {
            tvGroupLimit.setText(getString(R.string.current_limit_xx_person, group.getGroupInfo().getLimitNumber()));
        }

        if (tvSocialName != null) {
            tvSocialName.setText(group.getGroupInfo().getGroupNikeName());
        }

        if (tvForceRecallStatus != null) {
            if (identity.equals("2") || (identity.equals("1") && group.getGroupPermission().getForceRecall().equals("1"))) {
                tvForceRecallStatus.setText(R.string.enabled);
            } else {
                tvForceRecallStatus.setText(R.string.no_authorization);
            }
        }

        if (tvMyGroupNick != null) {
            tvMyGroupNick.setText(Constant.currentUser.getNick());
        }

        if (swForbidPic != null) {
            swForbidPic.setChecked(!group.getGroupInfo().getBanSendPicture().equals("0"));
            swForbidPic.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.ban_send_pic), getString(R.string.ban_send_pic1));
                dialog.setOnCancelListener(() -> swForbidPic.setChecked(!swForbidPic.isChecked()));
                dialog.setOnCommitListener(() -> api.groupOperation(group.getGroupInfo().getId(), swForbidPic.isChecked() ? "1" : "0", "1")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            group.getGroupInfo().setBanSendPicture(swForbidPic.isChecked() ? "1" : "0");
                            SendUrlAndsendImgBean bean = new SendUrlAndsendImgBean(group);
                            CommandMessage commandMessage = CommandMessage.obtain("sendUrlAndsendImg", GsonUtils.toJson(bean));
                            Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP,
                                    commandMessage);
                            RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                            String text;
                            if (swForbidPic.isChecked()) {
                                text = getString(R.string.ban_sendpic_open);
                            } else {
                                text = getString(R.string.ban_sendpic_close);
                            }
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                            Message message1 = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
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
            swForbidUrl.setChecked(!group.getGroupInfo().getBanSendLink().equals("0"));
            swForbidUrl.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.ban_send_link), getString(R.string.ban_send_link1));
                dialog.setOnCancelListener(() -> swForbidUrl.setChecked(!swForbidUrl.isChecked()));
                dialog.setOnCommitListener(() -> api.groupOperation(group.getGroupInfo().getId(), swForbidUrl.isChecked() ? "1" : "0", "2")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            group.getGroupInfo().setBanSendLink(swForbidUrl.isChecked() ? "1" : "0");
                            SendUrlAndsendImgBean bean = new SendUrlAndsendImgBean(group);
                            CommandMessage commandMessage = CommandMessage.obtain("sendUrlAndsendImg", GsonUtils.toJson(bean));
                            Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP,
                                    commandMessage);
                            RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                            String text;
                            if (swForbidUrl.isChecked()) {
                                text = getString(R.string.ban_send_link_open);
                            } else {
                                text = getString(R.string.ban_send_link_close);
                            }
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                            Message message1 = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
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
            swForbidVoice.setChecked(!group.getGroupInfo().getBanSendVoice().equals("0"));
            swForbidVoice.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.ban_send_voice), getString(R.string.ban_send_voice1));
                dialog.setOnCancelListener(() -> swForbidVoice.setChecked(!swForbidVoice.isChecked()));
                dialog.setOnCommitListener(() -> api.groupOperation(group.getGroupInfo().getId(), swForbidVoice.isChecked() ? "1" : "0", "5")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            group.getGroupInfo().setBanSendVoice(swForbidVoice.isChecked() ? "1" : "0");
                            SendUrlAndsendImgBean bean = new SendUrlAndsendImgBean(group);
                            CommandMessage commandMessage = CommandMessage.obtain("sendUrlAndsendImg", GsonUtils.toJson(bean));
                            Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP,
                                    commandMessage);
                            RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                            String text;
                            if (swForbidVoice.isChecked()) {
                                text = getString(R.string.ban_send_voice_open);
                            } else {
                                text = getString(R.string.ban_send_voice_close);
                            }
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                            Message message1 = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
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
            swForbidAddF.setChecked(!group.getGroupInfo().getBanFriend().equals("0"));
            swForbidAddF.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.ban_add_friend), getString(R.string.ban_add_friend1));
                dialog.setOnCancelListener(() -> swForbidAddF.setChecked(!swForbidAddF.isChecked()));
                dialog.setOnCommitListener(() -> api.groupOperation(group.getGroupInfo().getId(), swForbidAddF.isChecked() ? "1" : "0", "0")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            group.getGroupInfo().setBanFriend(swForbidAddF.isChecked() ? "1" : "0");
                            String text;
                            if (swForbidAddF.isChecked()) {
                                text = getString(R.string.ban_add_friend_open);
                            } else {
                                text = getString(R.string.ban_add_friend_close);
                            }
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                            Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
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
            swMuteAll.setChecked(!group.getGroupInfo().getIsBanned().equals("0"));
            swMuteAll.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.mute_confirm), getString(R.string.mute_confirm1));
                dialog.setOnCancelListener(() -> swMuteAll.setChecked(!swMuteAll.isChecked()));
                dialog.setOnCommitListener(() -> api.muteGroups(group.getGroupInfo().getId(), swMuteAll.isChecked() ? "add" : "remove")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            group.getGroupInfo().setIsBanned(swMuteAll.isChecked() ? "1" : "0");
                            String text;
                            if (swMuteAll.isChecked()) {
                                text = getString(R.string.mute_all_open);
                            } else {
                                text = getString(R.string.mute_all_close);
                            }
                            InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(text);
                            Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, notificationMessage);
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
            swGroupPublic.setChecked(!group.getGroupInfo().getIsPublic().equals("0"));
            swGroupPublic.setOnClickListener(v -> {
                MuteRemoveDialog dialog = new MuteRemoveDialog(NewSocialManageActivity.this, getString(R.string.cancel), getString(R.string.queding),
                        getString(R.string.social_public), getString(R.string.social_public1));
                dialog.setOnCancelListener(() -> swGroupPublic.setChecked(!swGroupPublic.isChecked()));
                dialog.setOnCommitListener(() -> api.groupOperation(group.getGroupInfo().getId(), swGroupPublic.isChecked() ? "1" : "0", "3")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> group.getGroupInfo().setIsPublic(swGroupPublic.isChecked() ? "1" : "0"),
                                t -> {
                                    swGroupPublic.setChecked(!swGroupPublic.isChecked());
                                    handleApiError(t);
                                }));
                dialog.setOnCloseListener(() -> swGroupPublic.setChecked(!swGroupPublic.isChecked()));
                dialog.show();
            });
        }

        if (swMute != null) {
            RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, group.getGroupInfo().getId(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
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
                RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.GROUP, group.getGroupInfo().getId(), status, null);
            });
        }

        if (swSetTop != null) {
            RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, group.getGroupInfo().getId(), new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    if (conversation != null) swSetTop.setChecked(conversation.isTop());
                    else swSetTop.setEnabled(false);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                }
            });
            swSetTop.setOnCheckedChangeListener((buttonView, isChecked) -> RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, group.getGroupInfo().getId(), isChecked, null));
        }
    }

    private void initView() {
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.ic_social_end_pop2);

        tvSocialName = findViewById(R.id.tvSocialName);
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
            intent.putExtra("groupId", group.getGroupInfo().getId());
            intent.putExtra("group", group);
            intent.putExtra("fromSocial", true);
            startActivityForResult(intent, 1);
        } else {
            ToastUtils.showShort(getString(R.string.no_update_nick));
        }
    }

    public void airDrop(View view) {
        Intent intent = new Intent(this, EnterGroupGetRedActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        startActivity(intent);
    }

    public void payEnter(View view) {
        Intent intent = new Intent(this, PayEnterGroupActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    public void slowMode(View view) {
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
                        tvSlowModeStatus.setText(tips);
                        InformationNotificationMessage messageContent = InformationNotificationMessage.obtain(text);
                        messageContent.setExtra(messageContent.getMessage());
                        Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, messageContent);
                        RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                        group.getGroupInfo().setSlowMode(str);
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
                                group.getGroupInfo().getId(), new RongIMClient.ResultCallback<Boolean>() {
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
                    CommandMessage command = CommandMessage.obtain("ForceClearAllLocalHistory", group.getGroupInfo().getId());
                    Message message = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, command);
                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                    RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, group.getGroupInfo().getId(), null);

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
        intent.putExtra("group", group);
        startActivityForResult(intent, 1);
    }

    public void muteSetting(View view) {
        if (identity.equals("1")) {
            if (group.getGroupPermission() == null || TextUtils.isEmpty(group.getGroupPermission().getOpenBanned()) ||
                    !group.getGroupPermission().getOpenBanned().equals("1")) {
                ToastUtils.showShort(R.string.no_authorization);
                return;
            }
        }
        Intent intent = new Intent(this, MuteManageActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        startActivity(intent);
    }

    public void upgradeGroupLimit(View view) {
        Intent intent = new Intent(this, UpdateGroupLimitActivity.class);
        intent.putExtra("group", group);
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
        intent.putExtra("groupId", group.getGroupInfo().getId());
        intent.putExtra("fromSocial", true);
        startActivity(intent);
    }

    public void funBottom(View view) {
        ConfirmDialog confirmDialog;
        if (identity.equals("2")) {
            confirmDialog = new ConfirmDialog(this, getString(R.string.hinttext), getString(R.string.is_confirm_disband_group),
                    v -> disBandGroup(group.getGroupInfo().getId(), Constant.userId));
        } else {
            confirmDialog = new ConfirmDialog(this, getString(R.string.hinttext), getString(R.string.is_confirm_exit_group1),
                    v -> exitGroup(group.getGroupInfo().getId(), Constant.userId));
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
            group = (GroupResponse) data.getSerializableExtra("group");
        }

        tvGroupLimit.setText(getString(R.string.current_limit_xx_person, group.getGroupInfo().getLimitNumber()));

        if (!TextUtils.isEmpty(data.getStringExtra("result"))) {
            group.getGroupInfo().setGroupNikeName(data.getStringExtra("result"));
            tvSocialName.setText(data.getStringExtra("result"));
        }
    }

    public void groupAllMembers(View view) {
        Intent intent = new Intent(this, AllGroupMembersActivity.class);
        intent.putExtra("groupId", group.getGroupInfo().getId());
        intent.putExtra("allGroupMembers", group);
        startActivity(intent);
    }

    public void groupQR(View view) {
        Intent intent = new Intent(this, GroupQRActivity.class);
        intent.putExtra("data", group);
        startActivity(intent);
    }

    @Override
    public void finish() {
        if (group != null) {
            Intent intent = new Intent();
            intent.putExtra("group", group);
            this.setResult(1000, intent);
        }
        super.finish();
    }
}