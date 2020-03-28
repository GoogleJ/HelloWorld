package com.zxjk.duoduo.ui.webcast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.CastDao;
import com.zxjk.duoduo.bean.response.WechatChatRoomPermission;
import com.zxjk.duoduo.db.Cast;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import org.greenrobot.greendao.query.DeleteQuery;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.CommandMessage;
import io.rong.message.InformationNotificationMessage;

public class WechatChatRoomManageActivity extends BaseActivity {
    private Switch sw1;
    private Switch sw2;
    private Switch sw3;

    private String roomId;
    private WechatChatRoomPermission chatRoomPermission;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wechat_chat_room_manage);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.cast_manage);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        sw1 = findViewById(R.id.sw1);
        sw2 = findViewById(R.id.sw2);
        sw3 = findViewById(R.id.sw3);

        roomId = getIntent().getStringExtra("roomId");

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getRoomPermissionByRoomId(roomId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(chatRoomPermission -> {
                            this.chatRoomPermission = chatRoomPermission;
                            sw1.setChecked(chatRoomPermission.getIsBanned().equals("1"));
                            sw2.setChecked(chatRoomPermission.getBanSendVoice().equals("1"));
                            sw3.setChecked(chatRoomPermission.getBanSendLink().equals("1"));
                        },
                        t -> {
                            handleApiError(t);
                            finish();
                        });

        sw1.setOnClickListener(v -> ServiceFactory.getInstance().getBaseService(Api.class)
                .updateRoomPermissionByRoomId(roomId, "isBanned", sw1.isChecked() ? "1" : "0")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(
                            sw1.isChecked() ? getString(R.string.open_ban_all) : getString(R.string.close_ban_all));
                    Message message = Message.obtain(roomId, Conversation.ConversationType.CHATROOM, notificationMessage);
                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                    chatRoomPermission.setIsBanned(sw1.isChecked() ? "1" : "0");
                    CommandMessage command = CommandMessage.obtain("chatRoomConfig", GsonUtils.toJson(chatRoomPermission));
                    RongIM.getInstance().sendMessage(Message.obtain(roomId, Conversation.ConversationType.CHATROOM, command), "", "", (IRongCallback.ISendMessageCallback) null);
                }, t -> {
                    handleApiError(t);
                    sw1.setChecked(!sw1.isChecked());
                }));

        sw2.setOnClickListener(v -> ServiceFactory.getInstance().getBaseService(Api.class)
                .updateRoomPermissionByRoomId(roomId, "banSendVoice", sw2.isChecked() ? "1" : "0")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(
                            sw2.isChecked() ? getString(R.string.open_ban_sendvoice) : getString(R.string.close_ban_sendvoice));
                    Message message = Message.obtain(roomId, Conversation.ConversationType.CHATROOM, notificationMessage);
                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                    chatRoomPermission.setBanSendVoice(sw2.isChecked() ? "1" : "0");
                    CommandMessage command = CommandMessage.obtain("chatRoomConfig", GsonUtils.toJson(chatRoomPermission));
                    RongIM.getInstance().sendMessage(Message.obtain(roomId, Conversation.ConversationType.CHATROOM, command), "", "", (IRongCallback.ISendMessageCallback) null);
                }, t -> {
                    handleApiError(t);
                    sw2.setChecked(!sw2.isChecked());
                }));

        sw3.setOnClickListener(v -> ServiceFactory.getInstance().getBaseService(Api.class)
                .updateRoomPermissionByRoomId(roomId, "banSendLink", sw3.isChecked() ? "1" : "0")
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain(
                            sw3.isChecked() ? getString(R.string.ban_send_link_open) : getString(R.string.ban_send_link_close));
                    Message message = Message.obtain(roomId, Conversation.ConversationType.CHATROOM, notificationMessage);
                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                    chatRoomPermission.setBanSendLink(sw3.isChecked() ? "1" : "0");
                    CommandMessage command = CommandMessage.obtain("chatRoomConfig", GsonUtils.toJson(chatRoomPermission));
                    RongIM.getInstance().sendMessage(Message.obtain(roomId, Conversation.ConversationType.CHATROOM, command), "", "", (IRongCallback.ISendMessageCallback) null);
                }, t -> {
                    handleApiError(t);
                    sw3.setChecked(!sw3.isChecked());
                }));
    }

    @SuppressLint("CheckResult")
    public void closeCast(View view) {
        MuteRemoveDialog dialog = new MuteRemoveDialog(this, getString(R.string.cancel), getString(R.string.queding),
                getString(R.string.hinttext), getString(R.string.confirm_close_cast));
        dialog.setOnCommitListener(() -> ServiceFactory.getInstance().getBaseService(Api.class)
                .endLive(roomId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    DeleteQuery<Cast> deleteQuery = Application.daoSession.queryBuilder(Cast.class)
                            .where(CastDao.Properties.RoomId.eq(roomId)).buildDelete();
                    deleteQuery.executeDeleteWithoutDetachingEntities();
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    InformationNotificationMessage infoMessage = InformationNotificationMessage.obtain(getString(R.string.current_chatroom_over));
                    RongIM.getInstance().sendMessage(Message.obtain(roomId, Conversation.ConversationType.CHATROOM, infoMessage), "", "", (IRongCallback.ISendMessageCallback) null);
                }, this::handleApiError));
        dialog.show();
    }
}
