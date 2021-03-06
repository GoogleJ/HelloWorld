package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.umeng.analytics.MobclickAgent;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.BurnAfterReadMessageLocalBeanDao;
import com.zxjk.duoduo.bean.ConversationInfo;
import com.zxjk.duoduo.bean.DaoMaster;
import com.zxjk.duoduo.bean.SendUrlAndsendImgBean;
import com.zxjk.duoduo.bean.SlowModeLocalBeanDao;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.bean.response.WechatChatRoomPermission;
import com.zxjk.duoduo.db.BurnAfterReadMessageLocalBean;
import com.zxjk.duoduo.db.OpenHelper;
import com.zxjk.duoduo.db.SlowModeLocalBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxException;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.rongIM.CusConversationFragment;
import com.zxjk.duoduo.rongIM.message.BusinessCardMessage;
import com.zxjk.duoduo.rongIM.message.CusEmoteTabMessage;
import com.zxjk.duoduo.rongIM.message.FakeC2CMessage;
import com.zxjk.duoduo.rongIM.message.GroupCardMessage;
import com.zxjk.duoduo.rongIM.message.NewsCardMessage;
import com.zxjk.duoduo.rongIM.message.RedPacketMessage;
import com.zxjk.duoduo.rongIM.message.TransferMessage;
import com.zxjk.duoduo.rongIM.plugin.BusinessCardPlugin;
import com.zxjk.duoduo.rongIM.plugin.CastPlugin;
import com.zxjk.duoduo.rongIM.plugin.FilePlugin;
import com.zxjk.duoduo.rongIM.plugin.RedPacketPlugin;
import com.zxjk.duoduo.rongIM.plugin.SightPlugin;
import com.zxjk.duoduo.rongIM.plugin.SocialApplicationPlugin;
import com.zxjk.duoduo.rongIM.plugin.TransferPlugin;
import com.zxjk.duoduo.ui.EnlargeImageActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.cast.WechatCastDetailActivity;
import com.zxjk.duoduo.ui.cast.WechatChatRoomManageActivity;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.ui.widget.dialog.NewRedDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.RxScreenshotDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.InputBar;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongMessageItemLongClickActionManager;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imkit.widget.provider.MessageItemLongClickAction;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongCommonDefine;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.imlib.typingmessage.TypingStatus;
import io.rong.message.CommandMessage;
import io.rong.message.FileMessage;
import io.rong.message.HQVoiceMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.SightMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

@SuppressLint("CheckResult")
public class ConversationActivity extends BaseActivity {
    /**
     * Views
     */
    private CusConversationFragment fragment;
    private MessageListAdapter messageAdapter;
    private TextView tvTitle;
    private LinearLayout llLoading;

    /**
     * 会话信息
     */
    private String targetId;
    private UserInfo targetUserInfo;
    private GroupResponse groupInfo;
    private ConversationInfo conversationInfo = new ConversationInfo();
    private String conversationType;
    private WechatChatRoomPermission chatRoomPermission;

    /**
     * 融云监听
     */
    private BroadcastReceiver rongMsgReceiver;
    private RongIM.OnSendMessageListener onSendMessageListener;
    private RongIMClient.TypingStatusListener typingStatusListener;
    private RongIM.ConversationClickListener conversationClickListener;

    /**
     * 融云底部操作栏
     */
    private RongExtension extension;

    /**
     * db
     */
    private BurnAfterReadMessageLocalBeanDao burnMsgDao;
    private SlowModeLocalBeanDao slowModeLocalBeanDao;

    /**
     * 截屏disposable
     */
    private Disposable screenCapture;

    private TextView fullScreen;
    private TextView tvNumberOfPeople;

    private PLVideoTextureView mVideoView;
    private int mOrientation;
    private AlbumOrientationEventListener mAlbumOrientationEventListener;
    private Boolean orientationType = false;
    private Disposable loadingDisposable;

    private AtomicInteger wrongTime = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setEnableTouchHideKeyBoard(false);

        initDao();

        setContentView(R.layout.activity_conversation);

        List<String> pathSegments = getIntent().getData().getPathSegments();
        conversationType = pathSegments.get(pathSegments.size() - 1);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        llLoading = findViewById(R.id.llLoading);

        extension = findViewById(io.rong.imkit.R.id.rc_extension);

        if (conversationType.equals("system")) {
            findViewById(R.id.ll).setVisibility(View.VISIBLE);
            TextView tv_title = findViewById(R.id.tv_title);
            tv_title.setText(R.string.hilamg_official);
            GlideUtil.loadCircleImg(findViewById(R.id.ivHead), "http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1575277814072.png");
            extension.removeAllViews();
            return;
        }

        registerMsgReceiver();

        registerSendMessageListener();

        handleBean();

        handleClickMsg();

        setMaxMessageSelectedCount();

        handleBurnAfterReadForReceivers();
    }

    private void sendFakeC2CMsg(String name) {
        if (!conversationType.equals("private")) {
            return;
        }
        RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE,
                targetId, new RongIMClient.ResultCallback<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        if (conversation == null || conversation.getLatestMessage() == null) {
                            FakeC2CMessage message = new FakeC2CMessage();
                            message.setName(name);
                            RongIM.getInstance().insertIncomingMessage(
                                    Conversation.ConversationType.PRIVATE,
                                    targetId, Constant.userId, new Message.ReceivedStatus(1), message, null);
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }
                });
    }

    private void initDao() {
        if (Application.daoSession == null) {
            OpenHelper open = new
                    OpenHelper(Utils.getApp(), Constant.currentUser.getId(), null);
            Application.daoSession = new DaoMaster(open.getWritableDatabase()).newSession();
        }

        burnMsgDao = Application.daoSession.getBurnAfterReadMessageLocalBeanDao();
        slowModeLocalBeanDao = Application.daoSession.getSlowModeLocalBeanDao();
    }

    /**
     * 处理阅后即焚发送逻辑(消息发送前)
     *
     * @param message 发送的消息，在content中加入信息
     */
    private void handleBurnAfterReadForSendersOnSend(Message message) {
        if (conversationInfo == null || conversationInfo.getMessageBurnTime() == -1) return;
        String s = GsonUtils.toJson(conversationInfo);
        if (message.getContent() instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message.getContent();
            textMessage.setExtra(s);
        } else if (message.getContent() instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) message.getContent();
            imageMessage.setExtra(s);
        } else if (message.getContent() instanceof VoiceMessage) {
            VoiceMessage voiceMessage = (VoiceMessage) message.getContent();
            voiceMessage.setExtra(s);
        } else if (message.getContent() instanceof CusEmoteTabMessage) {
            CusEmoteTabMessage cusEmoteTabMessage = (CusEmoteTabMessage) message.getContent();
            cusEmoteTabMessage.setExtra(s);
        } else if (message.getContent() instanceof HQVoiceMessage) {
            HQVoiceMessage hqVoiceMessage = (HQVoiceMessage) message.getContent();
            hqVoiceMessage.setExtra(s);
        }
    }

    /**
     * 处理阅后即焚发送逻辑(消息发送后)
     *
     * @param message 发送的消息，在content中加入信息
     */
    private void handleBurnAfterReadForSendersOnSent(Message message) {
        Map<String, Object> map = new HashMap<>();
        if (conversationInfo == null || conversationInfo.getMessageBurnTime() == -1) {
            map.put("MsgType", "NormalMsg");
        } else if (message.getObjectName().equals("RC:TxtMsg") || message.getObjectName().equals("RC:ImgMsg")
                || message.getObjectName().equals("RC:VcMsg") || message.getObjectName().equals("MCusEmoteTabMsg")
                || message.getObjectName().equals("RC:HQVCMsg")) {
            BurnAfterReadMessageLocalBean b = new BurnAfterReadMessageLocalBean();
            b.setMessageId(message.getMessageId());
            b.setBurnTime(System.currentTimeMillis() + (conversationInfo.getMessageBurnTime() * 1000));
            burnMsgDao.insert(b);
            map.put("MsgType", "BurnMsg");
        }
        MobclickAgent.onEventObject(Utils.getApp(), "msg_normalOrBurn", map);
    }

    /**
     * 处理阅后即焚接收逻辑
     * todo 获取未读消息/获取latestmessages失败情况的处理
     */
    private void handleBurnAfterReadForReceivers() {
        Observable
                .create((ObservableOnSubscribe<Integer>) e ->
                        RongIM.getInstance().getUnreadCount(conversationType.equals("private") ?
                                        Conversation.ConversationType.PRIVATE : Conversation.ConversationType.GROUP
                                , targetId, new RongIMClient.ResultCallback<Integer>() {
                                    @Override
                                    public void onSuccess(Integer count) {
                                        if (count == 0) {
                                            e.onComplete();
                                            return;
                                        }
                                        e.onNext(count);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        e.onError(new RxException.ParamsException(errorCode.getMessage(), errorCode.getValue()));
                                    }
                                }))
                .flatMap((Function<Integer, ObservableSource<List<Message>>>) i ->
                        Observable.create(e -> RongIM.getInstance().getLatestMessages(conversationType.equals("private") ?
                                        Conversation.ConversationType.PRIVATE : Conversation.ConversationType.GROUP
                                , targetId, i, new RongIMClient.ResultCallback<List<Message>>() {
                                    @Override
                                    public void onSuccess(List<Message> messages) {
                                        e.onNext(messages);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        e.onError(new RxException.ParamsException(errorCode.getMessage(), errorCode.getValue()));
                                    }
                                })))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(messages -> {
                    long currentTime = System.currentTimeMillis();
                    List<BurnAfterReadMessageLocalBean> list = new ArrayList<>(messages.size());
                    for (Message m : messages) {
                        String extra = null;

                        switch (m.getObjectName()) {
                            case "RC:TxtMsg":
                                TextMessage textMessage = (TextMessage) m.getContent();
                                extra = textMessage.getExtra();
                                break;
                            case "RC:ImgMsg":
                                ImageMessage imageMessage = (ImageMessage) m.getContent();
                                extra = imageMessage.getExtra();
                                break;
                            case "RC:VcMsg":
                                VoiceMessage voiceMessage = (VoiceMessage) m.getContent();
                                extra = voiceMessage.getExtra();
                                break;
                            case "MCusEmoteTabMsg":
                                CusEmoteTabMessage cusEmoteTabMessage = (CusEmoteTabMessage) m.getContent();
                                extra = cusEmoteTabMessage.getExtra();
                                break;
                            case "RC:HQVCMsg":
                                HQVoiceMessage hqVoiceMessage = (HQVoiceMessage) m.getContent();
                                extra = hqVoiceMessage.getExtra();
                        }

                        if (TextUtils.isEmpty(extra)) {
                            //todo 这里判断存在问题，仅通过extra判断是否是阅后即焚？
                            continue;
                        }

                        ConversationInfo j = GsonUtils.fromJson(extra, ConversationInfo.class);
                        if (j.getMessageBurnTime() == -1) {
                            continue;
                        }
                        BurnAfterReadMessageLocalBean b = new BurnAfterReadMessageLocalBean();
                        b.setMessageId(m.getMessageId());
                        b.setBurnTime(currentTime + (j.getMessageBurnTime() * 1000));
                        list.add(b);
                    }
                    if (list.size() != 0) {
                        burnMsgDao.insertInTx(list);
                    }
                }, t -> ToastUtils.showShort("code:" + ((RxException.ParamsException) t).getCode() + "msg:" + t.getMessage()));
    }

    private void initScreenCapture() {
        if (conversationInfo != null && conversationInfo.getTargetCaptureScreenEnabled() != 0) {
            if (screenCapture != null && !screenCapture.isDisposed()) {
                screenCapture.dispose();
            }
            screenCapture = RxScreenshotDetector.start(this)
                    .compose(bindUntilEvent(ActivityEvent.PAUSE))
                    .compose(RxSchedulers.ioObserver())
                    .subscribe(s -> {
                        InformationNotificationMessage m = InformationNotificationMessage
                                .obtain(getString(R.string.theOther_capture_screen));
                        m.setExtra(getString(R.string.theOther_capture_screen));

                        Message message = Message.obtain(targetId, conversationType.equals("private")
                                ? Conversation.ConversationType.PRIVATE : Conversation.ConversationType.GROUP, m);

                        RongIMClient.getInstance().sendMessage(message, null, null, (IRongCallback.ISendMessageCallback) null);
                    }, Throwable::printStackTrace);
        }
    }

    private void setMaxMessageSelectedCount() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        fragment = (CusConversationFragment) fragments.get(0);
        messageAdapter = fragment.getMessageAdapter();
        messageAdapter.setMaxMessageSelectedCount(9);
    }

    private void registerSendMessageListener() {
        onSendMessageListener = new RongIM.OnSendMessageListener() {
            @Override
            public Message onSend(Message message) {
                if (llLoading.getVisibility() == View.VISIBLE) {
                    ToastUtils.showShort(R.string.loading_pleasewait);
                    return null;
                }

                handleBurnAfterReadForSendersOnSend(message);

                if (handleMsgForbiden(message)) {
                    return null;
                }

                if (groupInfo != null &&
                        (!groupInfo.getGroupInfo().getGroupOwnerId().equals(Constant.userId) && !groupInfo.getIsAdmin().equals("1"))) {
                    if (!TextUtils.isEmpty(groupInfo.getGroupInfo().getSlowMode()) && !groupInfo.getGroupInfo().getSlowMode().equals("0")) {
                        slowModeLocalBeanDao.detachAll();
                        List<SlowModeLocalBean> localSlowList =
                                slowModeLocalBeanDao.queryBuilder()
                                        .where(SlowModeLocalBeanDao.Properties.GroupId.eq(targetId)).build().list();
                        if (localSlowList != null && localSlowList.size() != 0) {
                            SlowModeLocalBean slowModeLocalBean = localSlowList.get(0);
                            long passTime = (System.currentTimeMillis() - slowModeLocalBean.getLastMsgSentTime()) / 1000;
                            if (passTime < Integer.parseInt(groupInfo.getGroupInfo().getSlowMode())) {
                                String timeLeftStr = parstTimeLeftForSlowMode(
                                        Integer.parseInt(groupInfo.getGroupInfo().getSlowMode()) - passTime);
                                String toastTips = "群主开启了慢速模式，距下次发言时间:" + timeLeftStr;
                                ToastUtils.showShort(toastTips);
                                return null;
                            } else {
                                slowModeLocalBean.setLastMsgSentTime(System.currentTimeMillis());
                                slowModeLocalBeanDao.update(slowModeLocalBean);
                            }
                        } else {
                            SlowModeLocalBean slowModeLocalBean = new SlowModeLocalBean();
                            slowModeLocalBean.setGroupId(targetId);
                            slowModeLocalBean.setLastMsgSentTime(System.currentTimeMillis());
                            slowModeLocalBeanDao.insert(slowModeLocalBean);
                        }
                    }
                }

                if (chatRoomPermission != null) {
                    String ownerId = getIntent().getStringExtra("chatRoomOwnerId");
                    if (!TextUtils.isEmpty(ownerId) && !ownerId.equals(Constant.userId)) {
                        if ("1".equals(chatRoomPermission.getIsBanned())) {
                            ToastUtils.showShort(R.string.msg_forbidden);
                            return null;
                        } else if ("1".equals(chatRoomPermission.getBanSendLink()) &&
                                message.getContent() instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message.getContent();
                            if (RegexUtils.isMatch(Constant.regUrl, textMessage.getContent())) {
                                ToastUtils.showShort(R.string.url_forbidden);
                                return null;
                            }
                        } else if ("1".equals(chatRoomPermission.getBanSendVoice()) &&
                                (message.getContent() instanceof VoiceMessage || message.getContent() instanceof HQVoiceMessage)) {
                            ToastUtils.showShort(R.string.voice_forbidden);
                            return null;
                        }
                    }
                }

                return message;
            }

            @Override
            public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
                handleBurnAfterReadForSendersOnSent(message);
                if (message.getObjectName().equals("RC:InfoNtf")) {
                    InformationNotificationMessage n = (InformationNotificationMessage) message.getContent();
                    if (!TextUtils.isEmpty(n.getExtra())
                            && n.getExtra().equals("对方截取了屏幕")) {
                        RongIMClient.getInstance().deleteMessages(new int[]{message.getMessageId()}, null);
                    }
                }
                return false;
            }
        };

        RongIM.getInstance().setSendMessageListener(onSendMessageListener);
    }

    private String parstTimeLeftForSlowMode(long timeLeft) {
        if (timeLeft < 60) {
            return timeLeft + "秒";
        }
        if (timeLeft < 3600) {
            return timeLeft / 60 + "分钟";
        }

        return timeLeft / 60 / 60 + "小时";
    }

    private boolean handleMsgForbiden(Message message) {
        if (!conversationType.equals("group")) {
            return false;
        }
        if (!groupInfo.getGroupInfo().getGroupOwnerId().equals(Constant.userId) && !groupInfo.getIsAdmin().equals("1")) {
            if (message.getContent() instanceof TextMessage &&
                    groupInfo.getGroupInfo().getBanSendLink().equals("1")) {
                TextMessage textMessage = (TextMessage) message.getContent();
                if (RegexUtils.isMatch(Constant.regUrl, textMessage.getContent())) {
                    ToastUtils.showShort(R.string.url_forbidden);
                    return true;
                }
            } else if (message.getContent() instanceof ImageMessage &&
                    groupInfo.getGroupInfo().getBanSendPicture().equals("1")) {
                ToastUtils.showShort(R.string.picture_forbidden);
                return true;
            } else if (message.getContent() instanceof CusEmoteTabMessage && groupInfo.getGroupInfo().getBanSendPicture().equals("1")) {
                ToastUtils.showShort(R.string.picture_forbidden);
                return true;
            } else if ((message.getContent() instanceof VoiceMessage || message.getContent() instanceof HQVoiceMessage) && groupInfo.getGroupInfo().getBanSendVoice().equals("1")) {
                ToastUtils.showShort(R.string.voice_forbidden);
                return true;
            }
        }
        return false;
    }

    private void registerMsgReceiver() {
        rongMsgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message message = intent.getParcelableExtra("msg");
                if (message == null) return;

                if (TextUtils.isEmpty(message.getObjectName())) return;

                if (message.getObjectName().equals("app:transfer")) {
                    //收到一条转账消息(已领取)
                    for (int j = 0; j < messageAdapter.getCount(); j++) {
                        if (messageAdapter.getItem(j).getObjectName().equals("app:transfer")) {
                            TransferMessage t = (TransferMessage) messageAdapter.getItem(j).getContent();
                            if (t.getTransferId().equals(((TransferMessage) message.getContent()).getTransferId())) {
                                int finalJ = j;
                                RongIM.getInstance().setMessageExtra(messageAdapter.getItem(j).getMessageId()
                                        , "1", new RongIMClient.ResultCallback<Boolean>() {
                                            @Override
                                            public void onSuccess(Boolean aBoolean) {
                                                messageAdapter.getItem(finalJ).setExtra("1");
                                                messageAdapter.notifyDataSetInvalidated();
                                            }

                                            @Override
                                            public void onError(RongIMClient.ErrorCode errorCode) {
                                            }
                                        });
                            }
                        }
                    }
                } else if (message.getObjectName().equals("RC:CmdMsg")) {
                    //对方开启截屏通知
                    CommandMessage commandMessage = (CommandMessage) message.getContent();
                    if (!TextUtils.isEmpty(commandMessage.getName())) {
                        switch (commandMessage.getName()) {
                            case "screenCapture":
                                try {
                                    conversationInfo.setTargetCaptureScreenEnabled(Integer.parseInt(commandMessage.getData()));
                                    if (conversationInfo.getTargetCaptureScreenEnabled() == 1) {
                                        initScreenCapture();
                                    } else if (screenCapture != null && !screenCapture.isDisposed()) {
                                        screenCapture.dispose();
                                    }
                                } catch (Exception e) {
                                }
                                break;
                            case "sendUrlAndsendImg":
                                try {
                                    SendUrlAndsendImgBean sendUrlAndsendImgBean = GsonUtils.fromJson(commandMessage.getData(), SendUrlAndsendImgBean.class);
                                    groupInfo.getGroupInfo().setBanSendLink(sendUrlAndsendImgBean.getSendUrl());
                                    groupInfo.getGroupInfo().setBanSendPicture(sendUrlAndsendImgBean.getSendImg());
                                    groupInfo.getGroupInfo().setBanSendVoice(sendUrlAndsendImgBean.getSendVoice());
                                } catch (Exception e) {
                                }
                                break;
                            case "chatRoomConfig":
                                try {
                                    chatRoomPermission = GsonUtils.fromJson(commandMessage.getData(), WechatChatRoomPermission.class);
                                } catch (Exception e) {
                                }
                                break;
                        }
                    }
                } else if (message.getObjectName().equals("RC:InfoNtf")) {
                    //小灰条
                    InformationNotificationMessage notificationMessage = (InformationNotificationMessage) message.getContent();
                    if (!TextUtils.isEmpty(notificationMessage.getExtra())) {
                        if (notificationMessage.getExtra().contains("慢速模式")) {
                            handleReceiveSlowMode(notificationMessage);
                        }

                        if (groupInfo != null) {
                            if (notificationMessage.getMessage().equals(getString(R.string.ban_add_friend_open))) {
                                groupInfo.getGroupInfo().setBanFriend("1");
                            } else if (notificationMessage.getMessage().equals(getString(R.string.ban_add_friend_close))) {
                                groupInfo.getGroupInfo().setBanFriend("0");
                            }
                        }
                    }
                } else if (message.getSenderUserId().equals(targetId)) {
                    String extra = "";
                    switch (message.getObjectName()) {
                        case "RC:TxtMsg":
                            TextMessage textMessage = (TextMessage) message.getContent();
                            extra = textMessage.getExtra();
                            break;
                        case "RC:ImgMsg":
                            ImageMessage imageMessage = (ImageMessage) message.getContent();
                            extra = imageMessage.getExtra();
                            break;
                        case "RC:VcMsg":
                            VoiceMessage voiceMessage = (VoiceMessage) message.getContent();
                            extra = voiceMessage.getExtra();
                            break;
                        case "MCusEmoteTabMsg":
                            CusEmoteTabMessage cusEmoteTabMessage = (CusEmoteTabMessage) message.getContent();
                            extra = cusEmoteTabMessage.getExtra();
                            break;
                    }

                    if (TextUtils.isEmpty(extra)) return;

                    try {
                        ConversationInfo j = GsonUtils.fromJson(extra, ConversationInfo.class);
                        if (j.getMessageBurnTime() != -1) {
                            BurnAfterReadMessageLocalBean b = new BurnAfterReadMessageLocalBean();
                            b.setMessageId(message.getMessageId());
                            b.setBurnTime(System.currentTimeMillis() + (j.getMessageBurnTime() * 1000));
                            burnMsgDao.insert(b);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(rongMsgReceiver, new IntentFilter(Constant.ACTION_BROADCAST2));
    }

    //群主开启慢速模式
    private void handleReceiveSlowMode(InformationNotificationMessage message) {
        if (message.getExtra().contains("关闭")) {
            groupInfo.getGroupInfo().setSlowMode("0");
        } else {
            String slowModeTimeStr = message.getExtra().substring(15);
            switch (slowModeTimeStr) {
                case "30秒":
                    groupInfo.getGroupInfo().setSlowMode("30");
                    break;
                case "1分钟":
                    groupInfo.getGroupInfo().setSlowMode("60");
                    break;
                case "5分钟":
                    groupInfo.getGroupInfo().setSlowMode("300");
                    break;
                case "10分钟":
                    groupInfo.getGroupInfo().setSlowMode("600");
                    break;
                case "1小时":
                    groupInfo.getGroupInfo().setSlowMode("3600");
                    break;
            }
        }
    }

    private void registerOnTitleChange() {
        typingStatusListener = (type, targetId1, typingStatusSet) -> {
            if (type.equals(Conversation.ConversationType.PRIVATE) && targetId1.equals(getIntent().getData().getQueryParameter("targetId"))) {
                //count表示当前会话中正在输入的用户数量，目前只支持单聊，所以判断大于0就可以给予显示了
                int count = typingStatusSet.size();
                if (count > 0) {
                    Iterator iterator = typingStatusSet.iterator();
                    TypingStatus status = (TypingStatus) iterator.next();
                    String objectName = status.getTypingContentType();

                    MessageTag textTag = TextMessage.class.getAnnotation(MessageTag.class);
                    MessageTag voiceTag = VoiceMessage.class.getAnnotation(MessageTag.class);
                    if (objectName.equals(textTag.value())) {
                        //显示“对方正在输入”
                        runOnUiThread(() -> tvTitle.setText(R.string.conversation_inputing));
                    } else if (objectName.equals(voiceTag.value())) {
                        //显示"对方正在讲话"
                        runOnUiThread(() -> tvTitle.setText(R.string.conversation_speaking));
                    }
                } else {
                    //当前会话没有用户正在输入，标题栏仍显示原来标题
                    runOnUiThread(() -> tvTitle.setText(targetUserInfo.getName()));
                }
            }
        };
        RongIMClient.setTypingStatusListener(typingStatusListener);
    }

    private void handleBean() {
        wrongTime.incrementAndGet();

        targetId = getIntent().getData().getQueryParameter("targetId");

        if (loadingDisposable == null) {
            loadingDisposable = Observable.timer(700, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe(l -> llLoading.setVisibility(View.VISIBLE));
        }

        switch (conversationType) {
            case "private":
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .personalChatConfig(targetId)
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver())
                        .compose(bindToLifecycle())
                        .subscribe(response -> {
                            if (loadingDisposable != null && !loadingDisposable.isDisposed()) {
                                loadingDisposable.dispose();
                            }
                            llLoading.setVisibility(View.GONE);

                            conversationInfo.setMessageBurnTime(response.getChatInfo().getIncinerationTime());
                            conversationInfo.setCaptureScreenEnabled(response.getChatInfo().getScreenCapture());
                            conversationInfo.setTargetCaptureScreenEnabled(response.getChatInfo().getScreenCaptureHide());

                            if (!targetId.equals(Constant.userId)) {
                                targetUserInfo = new UserInfo(targetId,
                                        TextUtils.isEmpty(response.getCustomerForChat().getFriendNick()) ?
                                                response.getCustomerForChat().getNick() : response.getCustomerForChat().getFriendNick(),
                                        Uri.parse(response.getCustomerForChat().getHeadPortrait()));
                                RongIM.getInstance().refreshUserInfoCache(targetUserInfo);
                            }

                            handlePrivate();

                            if (targetUserInfo != null) {
                                sendFakeC2CMsg(targetUserInfo.getName());
                            }
                        }, t -> {
                            if (wrongTime.get() <= 3) {
                                handleBean();
                            } else {
                                handleApiError(t);
                                finish();
                            }
                        });
                break;
            case "group":
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .getGroupByGroupId(targetId)
                        .compose(RxSchedulers.normalTrans())
                        .doOnNext(groupResponse -> {
                            //refresh local cache when serve logic change
                            Group ronginfo = RongUserInfoManager.getInstance().getGroupInfo(groupResponse.getGroupInfo().getId());
                            if (ronginfo == null) return;

                            String tempName = groupResponse.getGroupInfo().getGroupNikeName();
                            if (groupResponse.getGroupInfo().getGroupType().equals("1")) {
                                tempName = tempName + "おれは人间をやめるぞ！ジョジョ―――ッ!";
                            }
                            String tempGroupHead = groupResponse.getGroupInfo().getHeadPortrait();
                            //refresh local cache when serve logic change
                            if (ronginfo.getPortraitUri() == null ||
                                    TextUtils.isEmpty(ronginfo.getPortraitUri().toString()) ||
                                    TextUtils.isEmpty(ronginfo.getName()) ||
                                    !ronginfo.getName().equals(tempName) ||
                                    !ronginfo.getPortraitUri().toString().equals(tempGroupHead)) {
                                RongIM.getInstance().refreshGroupInfoCache(new Group(groupResponse.getGroupInfo().getId(), tempName, Uri.parse(tempGroupHead)));
                            }
                        })
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver())
                        .subscribe(groupInfo -> {
                            if (loadingDisposable != null && !loadingDisposable.isDisposed()) {
                                loadingDisposable.dispose();
                            }
                            llLoading.setVisibility(View.GONE);

                            handleNewReceiveRed(groupInfo);

                            handleGroupPlugin(groupInfo);

                            this.groupInfo = groupInfo;

                            handleGroupOwnerInit();

                            initView();
                        }, t -> {
                            if (wrongTime.get() <= 3) {
                                handleBean();
                            } else {
                                handleApiError(t);
                                finish();
                            }
                        });

                ServiceFactory.getInstance().getBaseService(Api.class)
                        .getGroupLiveGoingInfo(targetId)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver())
                        .subscribe(list -> {
                            if (list.size() != 0) {
                                ViewStub casting = findViewById(R.id.stubCasting);
                                View castingView = casting.inflate();
                                TextView tvCastTopic = castingView.findViewById(R.id.tvCastTopic);
                                tvCastTopic.setText(getString(R.string.cast_topic1, list.get(0).getTopic()));
                                castingView.setOnClickListener(v -> {
                                    Intent intent = new Intent(this, WechatCastDetailActivity.class);
                                    intent.putExtra("roomId", list.get(0).getRoomId());
                                    startActivity(intent);
                                });
                            }
                        }, t -> {
                        });
                break;
            case "chatroom":
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .getRoomPermissionByRoomId(targetId)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver())
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(chatRoomPermission -> {
                            if (loadingDisposable != null && !loadingDisposable.isDisposed()) {
                                loadingDisposable.dispose();
                            }
                            llLoading.setVisibility(View.GONE);

                            this.chatRoomPermission = chatRoomPermission;

                            handleChatRoom();

                            String liveType = getIntent().getStringExtra("liveType");

                            if (!TextUtils.isEmpty(liveType) && liveType.equals("0")) {
                                ViewStub casting = findViewById(R.id.stubCasting);
                                View inflate = casting.inflate();
                                chatRoomLive(inflate);
                            } else if (!TextUtils.isEmpty(liveType) && liveType.equals("1")) {

                                ViewStub stubVideo = findViewById(R.id.stubVideo);
                                View stubVideoInflate = stubVideo.inflate();
                                chatRoomLive(stubVideoInflate);

                                fullScreen = stubVideoInflate.findViewById(R.id.full_screen);
                                tvNumberOfPeople = stubVideoInflate.findViewById(R.id.tv_number_of_people);

                                mVideoView = stubVideoInflate.findViewById(R.id.PLVideoTextureView);

                                View loadingView = stubVideoInflate.findViewById(R.id.LoadingView);
                                mVideoView.setBufferingIndicator(loadingView);

                                mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);

                                mVideoView.setVideoPath(getIntent().getStringExtra("playUrl"));
                                mVideoView.start();

                                TextView tvTitle = stubVideoInflate.findViewById(R.id.tv_title);
                                tvTitle.setText("主题:" + getIntent().getStringExtra("topic"));
                                LinearLayout ll_GroupNikeName = stubVideoInflate.findViewById(R.id.ll_group_nike_name);
                                TextView tvGroupNikeName = stubVideoInflate.findViewById(R.id.tv_group_nike_name);
                                tvGroupNikeName.setText(getIntent().getStringExtra("groupNikeName"));
                                CircleImageView ivHead = stubVideoInflate.findViewById(R.id.ivHead);
                                Glide.with(this).load(Constant.currentUser.getHeadPortrait()).into(ivHead);

                                stubVideoInflate.findViewById(R.id.img_back).setOnClickListener(v -> {
                                    orientationType = false;
                                    exitFullScreen();
                                    findViewById(R.id.conversation).setVisibility(View.VISIBLE);
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                    findViewById(R.id.rlTitle).setVisibility(View.VISIBLE);
                                    ll_GroupNikeName.setVisibility(View.GONE);
                                    stubVideoInflate.findViewById(R.id.img_back).setVisibility(View.GONE);
                                });

                                fullScreen.setOnClickListener(v -> {
                                    int orientation = getRequestedOrientation();
                                    if (orientation == 0 || orientation == 8 || orientation == 9) {
                                        orientationType = false;
                                        exitFullScreen();
                                        findViewById(R.id.conversation).setVisibility(View.VISIBLE);
                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                        findViewById(R.id.rlTitle).setVisibility(View.VISIBLE);
                                        ll_GroupNikeName.setVisibility(View.GONE);
                                        stubVideoInflate.findViewById(R.id.img_back).setVisibility(View.GONE);
                                    } else {
                                        orientationType = true;
                                        setFullScreen();
                                        findViewById(R.id.conversation).setVisibility(View.GONE);
                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                        findViewById(R.id.rlTitle).setVisibility(View.GONE);
                                        ll_GroupNikeName.setVisibility(View.VISIBLE);
                                        stubVideoInflate.findViewById(R.id.img_back).setVisibility(View.VISIBLE);
                                    }
                                });

                                if (Constant.userId.equals(getIntent().getStringExtra("roomOwnerId"))) {
                                    extension.setInputBarStyle(InputBar.Style.STYLE_EXTENSION_CONTAINER);
                                } else {
                                    extension.setInputBarStyle(InputBar.Style.STYLE_CONTAINER);
                                }

                                mAlbumOrientationEventListener = new AlbumOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
                                if (mAlbumOrientationEventListener.canDetectOrientation()) {
                                    mAlbumOrientationEventListener.enable();
                                }
                            }
                        }, t -> {
                            if (wrongTime.get() <= 3) {
                                handleBean();
                            } else {
                                handleApiError(t);
                                finish();
                            }
                        });
                break;
        }
    }

    private void setFullScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); // Activity全屏显示，且状态栏被覆盖掉
    }

    private void exitFullScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); // Activity全屏显示，但是状态栏不会被覆盖掉，而是正常显示，只是Activity顶端布局会被覆盖住
    }


    private void setDrawables(Drawable drawable, TextView textView, String text) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());// 设置边界
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setCompoundDrawablePadding(8);
    }

    private void chatRoomLive(View inflate) {
        inflate.findViewById(R.id.lottieCasting).setVisibility(View.GONE);
        TextView tv1 = inflate.findViewById(R.id.tvTips1);
        TextView tv2 = inflate.findViewById(R.id.tvTips2);

        tv1.setTextColor(Color.parseColor("#999999"));
        tv2.setTextColor(Color.parseColor("#FB6E5D"));
        tv1.setText(R.string.watch_nums);

        TextView tvCastTopic = inflate.findViewById(R.id.tvCastTopic);
        tvCastTopic.setText(getString(R.string.cast_topic1, getIntent().getStringExtra("castTopic")));

        upgradeWatchNumsForWechatCast(tv2);
    }

    private void upgradeWatchNumsForWechatCast(TextView tvNums) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getOnlineUsers(targetId)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .flatMap((Function<String, ObservableSource<?>>) s -> {
                    if (!TextUtils.isEmpty(s)) {
                        tvNums.setText(s);
                        setDrawables(getResources().getDrawable(R.drawable.ic_numberofpeople, null), tvNumberOfPeople, s);
                    }

                    return Observable.timer(30, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).compose(bindUntilEvent(ActivityEvent.DESTROY));
                })
                .subscribe(s -> upgradeWatchNumsForWechatCast(tvNums), t -> {
                });
    }

    private void handleChatRoom() {
        List<MessageItemLongClickAction> actionList = RongMessageItemLongClickActionManager.getInstance().getMessageItemLongClickActions();
        Iterator<MessageItemLongClickAction> iterator1 = actionList.iterator();
        while (iterator1.hasNext()) {
            MessageItemLongClickAction next = iterator1.next();
            if (next.getTitle(this).equals(getString(R.string.force_recall))
                    || next.getTitle(this).equals(getString(R.string.force_recall_update))) {
                iterator1.remove();
            }
        }

        String ownerId = getIntent().getStringExtra("chatRoomOwnerId");

        List<IPluginModule> pluginModules = extension.getPluginModules();
        Iterator<IPluginModule> iterator = pluginModules.iterator();

        tvTitle = findViewById(R.id.tv_title);

        String title = getIntent().getStringExtra("chatRoomName");
        tvTitle.setText(title);

        ImageView iv_end = findViewById(R.id.iv_end);
        RelativeLayout rl_end = findViewById(R.id.rl_end);
        rl_end.setVisibility(View.VISIBLE);
        if (Constant.userId.equals(ownerId)) {
            rl_end.setOnClickListener(v -> {

                Intent intent = new Intent(this, WechatChatRoomManageActivity.class);
                intent.putExtra("roomId", targetId);
                intent.putExtra("liveType", getIntent().getStringExtra("liveType"));
                startActivity(intent);
            });
            iv_end.setImageResource(R.drawable.ic_social_end);

            while (iterator.hasNext()) {
                IPluginModule next = iterator.next();
                if (next instanceof TransferPlugin || next instanceof BusinessCardPlugin || next instanceof SightPlugin
                        || next instanceof FilePlugin) {
                    iterator.remove();
                    extension.removePlugin(next);
                }
            }
        } else {
            rl_end.setOnClickListener(v -> {
                Intent intent = new Intent(this, SocialHomeActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("groupId"));
                startActivity(intent);
            });
            iv_end.setImageResource(R.drawable.ic_social_end);

            extension.setInputBarStyle(InputBar.Style.STYLE_SWITCH_CONTAINER);
            extension.hideMoreActionLayout();
        }
    }

    /**
     * 处理群主初始化逻辑
     */
    private void handleGroupOwnerInit() {
        boolean isOwner = groupInfo.getGroupInfo().getGroupOwnerId().equals(Constant.userId);

        List<MessageItemLongClickAction> actionList = RongMessageItemLongClickActionManager.getInstance().getMessageItemLongClickActions();
        Iterator<MessageItemLongClickAction> iterator = actionList.iterator();
        while (iterator.hasNext()) {
            MessageItemLongClickAction next = iterator.next();
            if (next.getTitle(this).equals(getString(R.string.force_recall))
                    || next.getTitle(this).equals(getString(R.string.force_recall_update))) {
                iterator.remove();
            }
        }

        if (isOwner || groupInfo.getGroupPermission() != null && groupInfo.getGroupPermission().getForceRecall().equals("1")) {
            MessageItemLongClickAction forceRecallAction = new MessageItemLongClickAction.Builder()
                    .title(getString(R.string.force_recall))
                    .showFilter(uiMessage -> {
                        String senderUserId = uiMessage.getSenderUserId();
                        groupInfo.getGroupInfo().getGroupOwnerId();
                        MessageContent messageContent = uiMessage.getContent();
                        return (messageContent instanceof TextMessage || messageContent instanceof VoiceMessage || messageContent instanceof ImageMessage
                                || messageContent instanceof CusEmoteTabMessage || messageContent instanceof BusinessCardMessage ||
                                messageContent instanceof NewsCardMessage || messageContent instanceof GroupCardMessage ||
                                messageContent instanceof LocationMessage || messageContent instanceof SightMessage || messageContent instanceof FileMessage ||
                                messageContent instanceof RedPacketMessage || messageContent instanceof HQVoiceMessage) &&
                                !senderUserId.equals(Constant.userId) && !senderUserId.equals(groupInfo.getGroupInfo().getGroupOwnerId());
                    })
                    .actionListener((context, uiMessage) -> {
                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .recallGroupMessage(uiMessage.getMessage().getUId())
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConversationActivity.this)))
                                .compose(RxSchedulers.normalTrans())
                                .subscribe(s -> {
                                }, ConversationActivity.this::handleApiError);
                        return true;
                    }).build();

            MessageItemLongClickAction forceRecallAction2 = new MessageItemLongClickAction.Builder()
                    .title(getString(R.string.force_recall_update))
                    .showFilter(uiMessage -> {
                        String senderUserId = uiMessage.getSenderUserId();
                        groupInfo.getGroupInfo().getGroupOwnerId();
                        MessageContent messageContent = uiMessage.getContent();
                        return (messageContent instanceof TextMessage || messageContent instanceof VoiceMessage || messageContent instanceof ImageMessage
                                || messageContent instanceof CusEmoteTabMessage || messageContent instanceof BusinessCardMessage ||
                                messageContent instanceof NewsCardMessage || messageContent instanceof GroupCardMessage ||
                                messageContent instanceof LocationMessage || messageContent instanceof SightMessage || messageContent instanceof FileMessage ||
                                messageContent instanceof RedPacketMessage || messageContent instanceof HQVoiceMessage) &&
                                !senderUserId.equals(Constant.userId) && !senderUserId.equals(groupInfo.getGroupInfo().getGroupOwnerId());
                    })
                    .actionListener((context, uiMessage) -> {
                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .recallAndMuted(uiMessage.getSenderUserId(), groupInfo.getGroupInfo().getId())
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConversationActivity.this)))
                                .compose(RxSchedulers.normalTrans())
                                .subscribe(s -> {
                                }, ConversationActivity.this::handleApiError);
                        return true;
                    }).build();

            RongMessageItemLongClickActionManager.getInstance().addMessageItemLongClickAction(forceRecallAction, 0);
            RongMessageItemLongClickActionManager.getInstance().addMessageItemLongClickAction(forceRecallAction2, 0);
        }
    }

    /**
     * 进群领红包
     *
     * @param groupInfo 群组信息
     */
    private void handleNewReceiveRed(GroupResponse groupInfo) {
        if (!groupInfo.getRedPacketInfo().getRedNewPersonStatus().equals("1")) return;

        if ("0".equals(groupInfo.getRedPacketInfo().getIsGetNewPersonRed())) {
            NewRedDialog newRedDialog = new NewRedDialog(ConversationActivity.this, NewRedDialog.TYPE1_NORMAL);
            newRedDialog.setOpenListener(()
                    -> ServiceFactory.getInstance().getBaseService(Api.class)
                    .receiveNewPersonRedPackage(groupInfo.getGroupInfo().getId())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConversationActivity.this)))
                    .subscribe(r -> {
                                if (r.getResult().equals("0")) {
                                    new NewRedDialog(ConversationActivity.this, NewRedDialog.TYPE3_RECEIVED).showReceived(r.getEveryoneAwardCount(), r.getSymbol());
                                } else {
                                    new NewRedDialog(ConversationActivity.this, NewRedDialog.TYPE2_EXPIRED).showExpired1();
                                }
                            },
                            ConversationActivity.this::handleApiError));
            newRedDialog.show(groupInfo.getGroupInfo().getOwnerHeadPortrait(),
                    groupInfo.getGroupInfo().getGroupOwnerNick(), getString(R.string.newredtip));
        }
    }

    private void handleGroupPlugin(GroupResponse groupInfo) {
        List<IPluginModule> pluginModules = extension.getPluginModules();

        if (groupInfo.getGroupInfo().getIsDelete().equals("1")) {
            //群不存在，删除plugin
            Iterator<IPluginModule> iterator = pluginModules.iterator();
            while (iterator.hasNext()) {
                IPluginModule next = iterator.next();
                iterator.remove();
                extension.removePlugin(next);
            }
        } else {
            //群组plugin
            Iterator<IPluginModule> iterator = pluginModules.iterator();
            while (iterator.hasNext()) {
                IPluginModule next = iterator.next();
                if (next instanceof TransferPlugin || next instanceof BusinessCardPlugin) {
                    iterator.remove();
                    extension.removePlugin(next);
                }
            }
            if (groupInfo.getGroupInfo().getGroupType().equals("1")) {
                if (groupInfo.getGroupInfo().getGroupOwnerId().equals(Constant.userId)) {
                    pluginModules.add(new CastPlugin());
                } else if (groupInfo.getIsAdmin().equals("1")) {
                    if (groupInfo.getGroupPermission().getOpenWxLive().equals("1") || groupInfo.getGroupPermission().getOpenVideo().equals("1")) {
                        pluginModules.add(new CastPlugin());
                    }
                }
            }

            if (groupInfo.getGroupInfo().getGroupOwnerId().equals(Constant.userId) || groupInfo.getIsAdmin().equals("1")) {
                pluginModules.add(new SocialApplicationPlugin(true));
            } else {
                pluginModules.add(new SocialApplicationPlugin(false));
            }

        }
    }

    private void handlePrivate() {
        initView();
        if (targetId.equals(Constant.userId)) {
            List<IPluginModule> pluginModules = extension.getPluginModules();
            Iterator<IPluginModule> iterator = pluginModules.iterator();
            while (iterator.hasNext()) {
                IPluginModule next = iterator.next();
                if (next instanceof TransferPlugin || next instanceof RedPacketPlugin) {
                    iterator.remove();
                    extension.removePlugin(next);
                }
            }
        }

        List<MessageItemLongClickAction> actionList = RongMessageItemLongClickActionManager.getInstance().getMessageItemLongClickActions();
        Iterator<MessageItemLongClickAction> iterator = actionList.iterator();
        while (iterator.hasNext()) {
            MessageItemLongClickAction next = iterator.next();
            if (next.getTitle(this).equals(getString(R.string.force_recall))
                    || next.getTitle(this).equals(getString(R.string.force_recall_update))) {
                iterator.remove();
            }
        }
    }

    private void handleClickMsg() {
        conversationClickListener = new RongIM.ConversationClickListener() {
            @Override
            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
                if (conversationType == Conversation.ConversationType.GROUP) {
                    if (groupInfo != null && !TextUtils.isEmpty(groupInfo.getGroupInfo().getBanFriend())) {
                        if (!groupInfo.getGroupInfo().getBanFriend().equals("1")) {
                            CommonUtils.resolveFriendList(ConversationActivity.this, userInfo.getUserId(), targetId);
                        }
                    }
                } else {
                    Intent intent = new Intent(ConversationActivity.this, FriendDetailsActivity.class);
                    intent.putExtra("friendId", userInfo.getUserId());
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
                return false;
            }

            @Override
            public boolean onMessageClick(Context context, View view, Message message) {
                switch (message.getObjectName()) {
                    case "RC:RLStart":
                    case "RC:LBSMsg":
                        ToastUtils.showShort(R.string.cantdone);
                        return true;
                    case "MMyCardMsg":
                        BusinessCardMessage businessCardMessage = (BusinessCardMessage) message.getContent();
                        CommonUtils.resolveFriendList(ConversationActivity.this, businessCardMessage.getUserId());
                        break;
                    case "RC:ImgMsg":
                        Intent intent5 = new Intent(ConversationActivity.this, EnlargeImageActivity.class);
                        ArrayList<Message> messageList = new ArrayList<>();
                        RongIMClient.getInstance().getHistoryMessages(message.getConversationType(), message.getTargetId(), "RC:ImgMsg"
                                , message.getMessageId(), Integer.MAX_VALUE, RongCommonDefine.GetMessageDirection.FRONT, new RongIMClient.ResultCallback<List<Message>>() {
                                    @Override
                                    public void onSuccess(List<Message> messages) {
                                        if (messages != null) {
                                            Collections.reverse(messages);
                                            messageList.addAll(messages);
                                        }
                                        intent5.putExtra("index", messageList.size());
                                        messageList.add(message);
                                        RongIMClient.getInstance().getHistoryMessages(message.getConversationType(), message.getTargetId(), "RC:ImgMsg"
                                                , message.getMessageId(), Integer.MAX_VALUE, RongCommonDefine.GetMessageDirection.BEHIND, new RongIMClient.ResultCallback<List<Message>>() {
                                                    @Override
                                                    public void onSuccess(List<Message> messages) {
                                                        if (messages != null) {
                                                            messageList.addAll(messages);
                                                        }
                                                        Bundle bundle = new Bundle();
                                                        bundle.putParcelableArrayList("images", messageList);
                                                        intent5.putExtra("images", bundle);
                                                        intent5.putExtra("image", "");
                                                        startActivity(intent5,
                                                                ActivityOptionsCompat.makeSceneTransitionAnimation(ConversationActivity.this,
                                                                        view, "a").toBundle());
                                                    }

                                                    @Override
                                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                    }
                                });
                        return true;
                    case "app:transfer":
                        //转账
                        Intent intent = new Intent(context, TransferInfoActivity.class);
                        intent.putExtra("msg", message);
                        intent.putExtra("targetUserInfo", targetUserInfo);
                        startActivity(intent);
                        break;
                    case "MRedPackageMsg":
                        //红包
                        RedPacketMessage redPacketMessage = (RedPacketMessage) message.getContent();
                        //获取红包状态
                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .getRedPackageStatus(redPacketMessage.getRedId(), redPacketMessage.getIsGame())
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.normalTrans())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConversationActivity.this)))
                                .subscribe(s -> {
                                    if (TextUtils.isEmpty(message.getExtra())) {
                                        Constant.tempMsg = message;
                                    }
                                    if (s.getRedPackageState().equals("1")) {
                                        //红包已过期
                                        Constant.tempMsg = message;
                                        onResume();
                                        NewRedDialog dialog = new NewRedDialog(ConversationActivity.this, NewRedDialog.TYPE2_EXPIRED);
                                        dialog.showExpired1(redPacketMessage.getRedId());
                                    }
                                    if (s.getRedPackageState().equals("3")) {
                                        Intent intent1 = new Intent(context, PeopleUnaccalimedActivity.class);
                                        intent1.putExtra("isGame", redPacketMessage.getIsGame());
                                        intent1.putExtra("id", redPacketMessage.getRedId());
                                        startActivity(intent1);
                                    }
                                    if (s.getRedPackageState().equals("2")) {
                                        if (message.getConversationType().equals(Conversation.ConversationType.PRIVATE)) {
                                            Intent intent1 = new Intent(context, PeopleUnaccalimedActivity.class);
                                            intent1.putExtra("id", redPacketMessage.getRedId());
                                            if (message.getSenderUserId().equals(Constant.userId)) {
                                                intent1.putExtra("isShow", false);
                                            }
                                            startActivity(intent1);
                                        } else {
                                            //手慢了，已被领完
                                            NewRedDialog newRedDialog = new NewRedDialog(ConversationActivity.this, NewRedDialog.TYPE2_EXPIRED);
                                            newRedDialog.showExpired2(redPacketMessage.getRedId());
                                            Constant.tempMsg = message;
                                            onResume();
                                        }
                                    }
                                    if (s.getRedPackageState().equals("0")) {
                                        //可领取
                                        NewRedDialog newRedDialog = new NewRedDialog(ConversationActivity.this, NewRedDialog.TYPE1_NORMAL);
                                        if (message.getConversationType().equals(Conversation.ConversationType.PRIVATE) && message.getSenderUserId().equals(Constant.userId)) {
                                            Intent intent1 = new Intent(context, PeopleUnaccalimedActivity.class);
                                            intent1.putExtra("isShow", false);
                                            intent1.putExtra("id", redPacketMessage.getRedId());
                                            startActivity(intent1);
                                        } else if (message.getConversationType().equals(Conversation.ConversationType.GROUP)
                                                || message.getConversationType().equals(Conversation.ConversationType.CHATROOM)) {
                                            newRedDialog.setOpenListener(() -> ServiceFactory.getInstance().getBaseService(Api.class)
                                                    .receiveGroupRedPackage(redPacketMessage.getRedId(), redPacketMessage.getIsGame())
                                                    .compose(bindToLifecycle())
                                                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConversationActivity.this)))
                                                    .compose(RxSchedulers.normalTrans())
                                                    .subscribe(s2 -> {
                                                        if (!TextUtils.isEmpty(s2.getFinish())) {
                                                            new NewRedDialog(ConversationActivity.this, NewRedDialog.TYPE2_EXPIRED)
                                                                    .showExpired2(redPacketMessage.getRedId());
                                                            Constant.tempMsg = message;
                                                            onResume();
                                                            return;
                                                        }

                                                        boolean formWechatCast = message.getConversationType().equals(Conversation.ConversationType.CHATROOM);

                                                        if (s2.getRedPackageInfo() != null && s2.getCustomerInfo() != null && s2.getSendCustomerInfo() != null) {
                                                            if (!message.getSenderUserId().equals(Constant.userId)) {

                                                                InformationNotificationMessage message1 = InformationNotificationMessage.obtain(
                                                                        getString(R.string.xx_receive_xx_red, Constant.currentUser.getNick(), s2.getSendCustomerInfo().getUsernick())
                                                                );
                                                                RongIM.getInstance().sendDirectionalMessage(
                                                                        formWechatCast ? Conversation.ConversationType.CHATROOM : Conversation.ConversationType.GROUP,
                                                                        targetId, message1, new String[]{message.getSenderUserId()}
                                                                        , null, null, null);
                                                            } else {
                                                                InformationNotificationMessage message1 = InformationNotificationMessage.obtain(getString(R.string.receiveRed));
                                                                RongIM.getInstance().insertIncomingMessage(
                                                                        formWechatCast ? Conversation.ConversationType.CHATROOM : Conversation.ConversationType.GROUP,
                                                                        targetId, Constant.userId, new Message.ReceivedStatus(1), message1, null);
                                                            }
                                                        }

                                                        Intent intent1 = new Intent(context, PeopleUnaccalimedActivity.class);
                                                        intent1.putExtra("id", redPacketMessage.getRedId());
                                                        intent1.putExtra("isGame", redPacketMessage.getIsGame());

                                                        startActivity(intent1);
                                                    }, ConversationActivity.this::handleApiError));

                                            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
                                            if (userInfo == null) {
                                                newRedDialog.show("", "", redPacketMessage.getRemark());
                                            } else {
                                                newRedDialog.show(userInfo.getPortraitUri().toString(), userInfo.getName(), redPacketMessage.getRemark());
                                            }
                                        } else {
                                            newRedDialog.setOpenListener(() -> ServiceFactory.getInstance().getBaseService(Api.class)
                                                    .receivePersonalRedPackage(redPacketMessage.getRedId())
                                                    .compose(bindToLifecycle())
                                                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConversationActivity.this)))
                                                    .compose(RxSchedulers.normalTrans())
                                                    .subscribe(s1 -> {

                                                        InformationNotificationMessage message1 = InformationNotificationMessage.obtain(
                                                                getString(R.string.xx_receive_xx_red, Constant.currentUser.getNick(), s1.getSendUserInfo().getUsernick())
                                                        );
                                                        RongIM.getInstance().sendDirectionalMessage(Conversation.ConversationType.PRIVATE, targetId, message1, new String[]{targetId}
                                                                , null, null, null);

                                                        Intent intent2 = new Intent(ConversationActivity.this, PeopleRedEnvelopesActivity.class);
                                                        intent2.putExtra("msg", message);
                                                        intent2.putExtra("symbol", s1.getSymbol());
                                                        startActivity(intent2);
                                                    }, ConversationActivity.this::handleApiError));

                                            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
                                            if (userInfo == null) {
                                                newRedDialog.show("", "", redPacketMessage.getRemark());
                                            } else {
                                                newRedDialog.show(userInfo.getPortraitUri().toString(), userInfo.getName(), redPacketMessage.getRemark());
                                            }
                                        }
                                    }
                                }, t -> ToastUtils.showShort(RxException.getMessage(t)));
                        break;
                }
                return false;
            }

            @Override
            public boolean onMessageLinkClick(Context context, String s, Message message) {
                return false;
            }

            @Override
            public boolean onMessageLongClick(Context context, View view, Message message) {
                return false;
            }
        };
        RongIM.setConversationClickListener(conversationClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initScreenCapture();

        if (Constant.tempMsg != null) {
            for (int i = 0; i < messageAdapter.getCount(); i++) {
                if (messageAdapter.getItem(i).getMessageId() == Constant.tempMsg.getMessageId()) {
                    RongIM.getInstance().setMessageExtra(Constant.tempMsg.getMessageId(), "1", null);
                    messageAdapter.getItem(i).setExtra("1");
                    messageAdapter.notifyDataSetInvalidated();
                    Constant.tempMsg = null;
                    break;
                }
            }
        }
    }

    private void detail() {
        if (groupInfo != null && groupInfo.getGroupInfo().getIsDelete().equals("1")) {
            ToastUtils.showShort(R.string.deleted_group);
            return;
        }
        List<String> pathSegments = getIntent().getData().getPathSegments();
        String conversationType = pathSegments.get(pathSegments.size() - 1);

        Intent intent = new Intent(ConversationActivity.this, ChatInformationActivity.class);
        intent.putExtra("bean", targetUserInfo);
        intent.putExtra("conversationInfo", conversationInfo);

        if (conversationType.equals("private")) {
            startActivityForResult(intent, 2000);
        } else {
            if (groupInfo == null) {
                return;
            }
            Intent intent1 = new Intent(this, GroupChatInformationActivity.class);
            intent1.putExtra("group", groupInfo);
            startActivityForResult(intent1, 1000);
        }
    }

    private void initView() {
        ImageView ivHead = findViewById(R.id.ivHead);
        TextView tvTitleTips = findViewById(R.id.tvTitleTips);

        tvTitle = findViewById(R.id.tv_title);
        RelativeLayout rl_end = findViewById(R.id.rl_end);
        rl_end.setVisibility(View.VISIBLE);
        rl_end.setOnClickListener(v -> detail());
        findViewById(R.id.ll).setOnClickListener(v -> detail());

        tvTitle.setText(targetUserInfo == null ?
                (groupInfo == null ? (Constant.currentUser.getNick())
                        : (groupInfo.getGroupInfo().getGroupNikeName() + "(" + groupInfo.getGroupInfo().getCustomerNumber() + ")"))
                : targetUserInfo.getName());

        //private logic
        if (targetId.equals(Constant.userId)) {
            rl_end.setVisibility(View.GONE);
            findViewById(R.id.ll).setVisibility(View.GONE);
        }
        if (targetUserInfo != null) {
            findViewById(R.id.ivLock).setVisibility(View.VISIBLE);
            tvTitleTips.setText(R.string.tips_conversation_private);
            GlideUtil.loadCircleImg(ivHead, targetUserInfo.getPortraitUri().toString());
        }

        //group logic
        if (groupInfo != null && groupInfo.getGroupInfo().getGroupType().equals("1")) {
            findViewById(R.id.ivSocialDetail).setVisibility(View.VISIBLE);
            ImageView iv_end = findViewById(R.id.iv_end);
            iv_end.setImageDrawable(getDrawable(R.drawable.ic_social_end));
            rl_end.setOnClickListener(v -> {
                Intent intent = new Intent(this, NewSocialManageActivity.class);
                intent.putExtra("group", groupInfo);
                startActivityForResult(intent, 1000);
            });
            tvTitle.setText(groupInfo.getGroupInfo().getGroupNikeName() + "(" + groupInfo.getGroupInfo().getCustomerNumber() + ")");
            findViewById(R.id.ll).setOnClickListener(v -> {
                Intent intent = new Intent(this, SocialHomeActivity.class);
                intent.putExtra("group", groupInfo);
                intent.putExtra("id", targetId);
                startActivityForResult(intent, 1000);
            });

            findViewById(R.id.ivHead).setOnClickListener(v -> {
                Intent intent = new Intent(this, SocialHomeActivity.class);
                intent.putExtra("group", groupInfo);
                intent.putExtra("id", targetId);
                startActivityForResult(intent, 1000);
            });
            tvTitleTips.setText(R.string.tips_conversation_group);
            GlideUtil.loadCircleImg(ivHead, groupInfo.getGroupInfo().getHeadPortrait());
        }

        registerOnTitleChange();
        initScreenCapture();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1000) {
            if (groupInfo != null) {
                groupInfo = (GroupResponse) data.getSerializableExtra("group");
                if (groupInfo.getGroupInfo().getGroupType().equals("1")) {
                    tvTitle.setText(groupInfo.getGroupInfo().getGroupNikeName() + "(" + groupInfo.getGroupInfo().getCustomerNumber() + ")");
                } else {
                    tvTitle.setText(getString(R.string.groupname_num, groupInfo.getGroupInfo().getGroupNikeName(), groupInfo.getGroupInfo().getCustomerNumber()));
                }
            }
        } else if (requestCode == 2000 && resultCode == 1000) {
            tvTitle.setText(data.getStringExtra("title"));
            targetUserInfo.setName(data.getStringExtra("title"));
            conversationInfo.setMessageBurnTime(data.getIntExtra("burn", conversationInfo.getMessageBurnTime()));
            conversationInfo.setCaptureScreenEnabled(data.getIntExtra("screenCapture", conversationInfo.getCaptureScreenEnabled()));
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment == null || !fragment.onBackPressed()) {
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        if (rongMsgReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(rongMsgReceiver);
        }
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        onSendMessageListener = null;
        typingStatusListener = null;
        conversationClickListener = null;
        RongIMClient.setTypingStatusListener(null);
        RongIM.getInstance().setSendMessageListener(null);
        RongIM.setConversationClickListener(null);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 当前为横屏
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

        }
        // 当前为竖屏
        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    private class AlbumOrientationEventListener extends OrientationEventListener {
        public AlbumOrientationEventListener(Context context) {
            super(context);
        }

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            int newOrientation = ((orientation + 45) / 90 * 90) % 360;
            if (newOrientation != mOrientation) {
                mOrientation = newOrientation;
                if (orientationType) {
                    if (mOrientation == 90) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    } else if (mOrientation == 270) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }
            }
        }
    }
}