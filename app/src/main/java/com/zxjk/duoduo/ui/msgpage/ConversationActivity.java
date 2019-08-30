package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.BurnAfterReadMessageLocalBean;
import com.zxjk.duoduo.bean.BurnAfterReadMessageLocalBeanDao;
import com.zxjk.duoduo.bean.ConversationInfo;
import com.zxjk.duoduo.bean.DaoMaster;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.db.OpenHelper;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxException;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.EnlargeImageActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.rongIM.CusConversationFragment;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.BusinessCardMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.TransferMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.BusinessCardPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.RedPacketPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.TransferPlugin;
import com.zxjk.duoduo.ui.widget.dialog.ExpiredEnvelopesDialog;
import com.zxjk.duoduo.ui.widget.dialog.RedEvelopesDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.RxScreenshotDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongCommonDefine;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.imlib.typingmessage.TypingStatus;
import io.rong.message.CommandMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
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

    /**
     * 会话信息
     */
    private String targetId;
    private UserInfo targetUserInfo;
    private GroupResponse groupInfo;
    private ConversationInfo conversationInfo = new ConversationInfo();
    private String conversationType;

    /**
     * 融云监听
     */
    private RongIM.OnSendMessageListener onSendMessageListener;
    private RongIMClient.OnReceiveMessageListener onReceiveMessageListener;
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

    /**
     * 截屏disposable
     */
    private Disposable screenCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDao();

        setContentView(R.layout.activity_conversation);

        List<String> pathSegments = getIntent().getData().getPathSegments();
        conversationType = pathSegments.get(pathSegments.size() - 1);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        extension = findViewById(io.rong.imkit.R.id.rc_extension);

        //系统消息UI调整
        if (conversationType.equals("system")) {
            tvTitle = findViewById(R.id.tv_title);
            targetId = getIntent().getData().getQueryParameter("targetId");
            switch (targetId) {
                case "147":
                    tvTitle.setText("支付凭证");
                    break;
                case "349":
                    tvTitle.setText("对局结果");
                    break;
                case "355":
                    tvTitle.setText("多多官方");
                    break;
                default:
                    tvTitle.setText("系统消息");
                    break;
            }
            extension.removeAllViews();
            return;
        }

        onReceiveMessage();

        registerSendMessageListener();

        handleBean();

        handleClickMsg();

        setMaxMessageSelectedCount();

        handleBurnAfterReadForReceivers();

        sendFakeC2CMsg();
    }

    private void sendFakeC2CMsg() {
        if (!conversationType.equals("private")) {
            return;
        }
        RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE,
                targetId, new RongIMClient.ResultCallback<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        if (conversation == null || conversation.getLatestMessage() == null) {
                            InformationNotificationMessage message = InformationNotificationMessage.obtain("本次会话已开启端对端加密");
                            message.setExtra("本次会话已开启端对端加密");
                            RongIM.getInstance().insertIncomingMessage(
                                    Conversation.ConversationType.PRIVATE,
                                    targetId, Constant.userId, new Message.ReceivedStatus(1), message, null
                            );
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
        }
    }

    /**
     * 处理阅后即焚发送逻辑(消息发送后)
     *
     * @param message 发送的消息，在content中加入信息
     */
    private void handleBurnAfterReadForSendersOnSent(Message message) {
        if (conversationInfo == null || conversationInfo.getMessageBurnTime() == -1) return;
        if (message.getObjectName().equals("RC:TxtMsg") || message.getObjectName().equals("RC:ImgMsg")
                || message.getObjectName().equals("RC:VcMsg")) {
            BurnAfterReadMessageLocalBean b = new BurnAfterReadMessageLocalBean();
            b.setMessageId(message.getMessageId());
            b.setBurnTime(System.currentTimeMillis() + (conversationInfo.getMessageBurnTime() * 1000));
            burnMsgDao.insert(b);
        }
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
                        }

                        if (TextUtils.isEmpty(extra)) {
                            //todo 这里判断存在问题，仅通过extra判断是否是阅后即焚？
                            continue;
                        }

                        ConversationInfo j = GsonUtils.fromJson(extra, ConversationInfo.class);

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
                                .obtain("对方截取了屏幕");
                        m.setExtra("对方截取了屏幕");

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
                handleBurnAfterReadForSendersOnSend(message);
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

    private void onReceiveMessage() {
        onReceiveMessageListener = (message, i) -> {
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
                if (!TextUtils.isEmpty(commandMessage.getName()) && commandMessage.getName().equals("screenCapture")) {
                    conversationInfo.setTargetCaptureScreenEnabled(Integer.parseInt(commandMessage.getData()));
                    if (conversationInfo.getTargetCaptureScreenEnabled() == 1) {
                        runOnUiThread(this::initScreenCapture);
                    } else {
                        runOnUiThread(() -> {
                            if (screenCapture != null && !screenCapture.isDisposed()) {
                                screenCapture.dispose();
                            }
                        });
                    }
                }
            } else {
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
                }

                if (TextUtils.isEmpty(extra)) {
                    return false;
                }

                ConversationInfo j = GsonUtils.fromJson(extra, ConversationInfo.class);
                if (j.getMessageBurnTime() != -1) {
                    BurnAfterReadMessageLocalBean b = new BurnAfterReadMessageLocalBean();
                    b.setMessageId(message.getMessageId());
                    b.setBurnTime(System.currentTimeMillis() + (j.getMessageBurnTime() * 1000));
                    burnMsgDao.insert(b);
                }
            }
            return false;
        };
        RongIM.setOnReceiveMessageListener(onReceiveMessageListener);
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
        targetId = getIntent().getData().getQueryParameter("targetId");

        if (conversationType.equals("private")) {
            // 私聊
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .personalChatConfig(targetId)
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .compose(bindToLifecycle())
                    .subscribe(response -> {
                        conversationInfo.setMessageBurnTime(response.getChatInfo().getIncinerationTime());
                        conversationInfo.setCaptureScreenEnabled(response.getChatInfo().getScreenCapture());
                        conversationInfo.setTargetCaptureScreenEnabled(response.getChatInfo().getScreenCaptureHide());
                        targetUserInfo = new UserInfo(targetId,
                                TextUtils.isEmpty(response.getCustomerForChat().getFriendNick()) ?
                                        response.getCustomerForChat().getNick() : response.getCustomerForChat().getFriendNick(),
                                Uri.parse(response.getCustomerForChat().getHeadPortrait()));
                        RongUserInfoManager.getInstance().setUserInfo(targetUserInfo);
                        handlePrivate();
                    }, this::handleApiError);
        } else if (conversationType.equals("group")) {
            // 群聊必须每次请求
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getGroupByGroupId(targetId)
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .compose(bindToLifecycle())
                    .subscribe(groupInfo -> {
                        conversationInfo.setMessageBurnTime(groupInfo.getChatInfo().getIncinerationTime());
                        conversationInfo.setCaptureScreenEnabled(groupInfo.getChatInfo().getScreenCapture());
                        Group ronginfo = RongUserInfoManager.getInstance().getGroupInfo(groupInfo.getGroupInfo().getId());
                        if (null == ronginfo ||
                                !ronginfo.getName().equals(groupInfo.getGroupInfo().getGroupNikeName()) ||
                                !ronginfo.getPortraitUri().toString().equals(groupInfo.getGroupInfo().getHeadPortrait())) {
                            RongUserInfoManager.getInstance().setGroupInfo(new Group(groupInfo.getGroupInfo().getId(), groupInfo.getGroupInfo().getGroupNikeName(), Uri.parse(groupInfo.getGroupInfo().getHeadPortrait())));
                        }

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
                        }

                        this.groupInfo = groupInfo;
                        initView();
                    }, ConversationActivity.this::handleApiError);
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
    }

    private void handleClickMsg() {
        conversationClickListener = new RongIM.ConversationClickListener() {
            @Override
            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
                if (conversationType == Conversation.ConversationType.GROUP) {
                    CommonUtils.resolveFriendList(ConversationActivity.this, userInfo.getUserId(), targetId);
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
                    case "RC:LBSMsg":
                        LocationMessage locationMessage = (LocationMessage) message.getContent();
                        Intent intent6 = new Intent(context, MessageLocationActivity.class);
                        intent6.putExtra("location", locationMessage);
                        startActivity(intent6);
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
                                                                        view, "12").toBundle());
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
                                        ExpiredEnvelopesDialog dialog = new ExpiredEnvelopesDialog(ConversationActivity.this);
                                        dialog.show(RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()),
                                                true, redPacketMessage.getRedId());
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
                                            ExpiredEnvelopesDialog dialog = new ExpiredEnvelopesDialog(ConversationActivity.this);
                                            dialog.show(RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()),
                                                    false, redPacketMessage.getRedId());
                                            Constant.tempMsg = message;
                                            onResume();
                                        }
                                    }
                                    if (s.getRedPackageState().equals("0")) {
                                        //可领取
                                        RedEvelopesDialog dialog = new RedEvelopesDialog(ConversationActivity.this);
                                        if (message.getConversationType().equals(Conversation.ConversationType.PRIVATE) && message.getSenderUserId().equals(Constant.userId)) {
                                            Intent intent1 = new Intent(context, PeopleUnaccalimedActivity.class);
                                            intent1.putExtra("isShow", false);
                                            intent1.putExtra("id", redPacketMessage.getRedId());
                                            startActivity(intent1);
                                        } else if (message.getConversationType().equals(Conversation.ConversationType.GROUP)) {
                                            dialog.setOnOpenListener(() -> ServiceFactory.getInstance().getBaseService(Api.class)
                                                    .receiveGroupRedPackage(redPacketMessage.getRedId(), redPacketMessage.getIsGame())
                                                    .compose(bindToLifecycle())
                                                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConversationActivity.this)))
                                                    .compose(RxSchedulers.normalTrans())
                                                    .subscribe(s2 -> {
                                                        if (!TextUtils.isEmpty(s2.getFinish())) {
                                                            new ExpiredEnvelopesDialog(ConversationActivity.this)
                                                                    .show(RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()),
                                                                            false, redPacketMessage.getRedId());
                                                            Constant.tempMsg = message;
                                                            onResume();
                                                            return;
                                                        }

                                                        if (!message.getSenderUserId().equals(Constant.userId)) {
                                                            InformationNotificationMessage message1 = InformationNotificationMessage.obtain(Constant.currentUser.getNick() + "领取了"
                                                                    + s2.getSendCustomerInfo().getUsernick() + "的红包");
                                                            RongIM.getInstance().sendDirectionalMessage(Conversation.ConversationType.GROUP, groupInfo.getGroupInfo().getId(), message1, new String[]{message.getSenderUserId()}
                                                                    , null, null, null);
                                                        } else {
                                                            InformationNotificationMessage message1 = InformationNotificationMessage.obtain("你领取了你的红包");
                                                            RongIM.getInstance().sendDirectionalMessage(Conversation.ConversationType.GROUP, groupInfo.getGroupInfo().getId(), message1, new String[]{Constant.userId}
                                                                    , null, null, null);
                                                        }

                                                        Intent intent1 = new Intent(context, PeopleUnaccalimedActivity.class);
                                                        intent1.putExtra("id", redPacketMessage.getRedId());
                                                        intent1.putExtra("isGame", redPacketMessage.getIsGame());

                                                        startActivity(intent1);
                                                    }, ConversationActivity.this::handleApiError));
                                            dialog.show(message, RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()));
                                        } else {
                                            dialog.setOnOpenListener(() -> ServiceFactory.getInstance().getBaseService(Api.class)
                                                    .receivePersonalRedPackage(redPacketMessage.getRedId())
                                                    .compose(bindToLifecycle())
                                                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ConversationActivity.this)))
                                                    .compose(RxSchedulers.normalTrans())
                                                    .subscribe(s1 -> {
                                                        InformationNotificationMessage message1 = InformationNotificationMessage.obtain(Constant.currentUser.getNick() + "领取了" +
                                                                s1.getSendUserInfo().getUsernick() + "的红包");
                                                        RongIM.getInstance().sendDirectionalMessage(Conversation.ConversationType.PRIVATE, targetId, message1, new String[]{targetId}
                                                                , null, null, null);

                                                        Intent intent2 = new Intent(ConversationActivity.this, PeopleRedEnvelopesActivity.class);
                                                        intent2.putExtra("msg", message);
                                                        startActivity(intent2);
                                                    }, ConversationActivity.this::handleApiError));
                                            dialog.show(message, RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()));
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
        RelativeLayout rl_end = findViewById(R.id.rl_end);
        rl_end.setVisibility(View.VISIBLE);
        rl_end.setOnClickListener(v -> detail());
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(targetUserInfo == null ? (groupInfo.getGroupInfo().getGroupNikeName() + "(" + groupInfo.getCustomers().size() + ")") : targetUserInfo.getName());
        registerOnTitleChange();
        initScreenCapture();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1000) {
            if (groupInfo != null) {
                tvTitle.setText(data.getStringExtra("title") + "(" + groupInfo.getCustomers().size() + ")");
                groupInfo = (GroupResponse) data.getSerializableExtra("group");
            }
        } else if (requestCode == 2000 && resultCode == 1000) {
            tvTitle.setText(data.getStringExtra("title"));
            boolean changeBurn = data.getBooleanExtra("changeBurn", false);
            boolean changeScreenCapture = data.getBooleanExtra("changeScreenCapture", false);
            if (changeBurn)
                conversationInfo.setMessageBurnTime(data.getIntExtra("burn", conversationInfo.getMessageBurnTime()));
            if (changeScreenCapture)
                conversationInfo.setCaptureScreenEnabled(data.getIntExtra("screenCapture", conversationInfo.getCaptureScreenEnabled()));
        }
    }

    @Override
    protected void onDestroy() {
        if (onReceiveMessageListener == null) {
            super.onDestroy();
            return;
        }
        onReceiveMessageListener = null;
        onSendMessageListener = null;
        typingStatusListener = null;
        conversationClickListener = null;
        RongIM.setOnReceiveMessageListener(null);
        RongIMClient.setTypingStatusListener(null);
        RongIM.getInstance().setSendMessageListener(null);
        RongIM.setConversationClickListener(null);
        super.onDestroy();
    }
}
