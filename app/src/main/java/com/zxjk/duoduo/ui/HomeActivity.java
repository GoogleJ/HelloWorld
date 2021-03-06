package com.zxjk.duoduo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.tbruyelle.rxpermissions2.Permission;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.BuildConfig;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.BurnAfterReadMessageLocalBeanDao;
import com.zxjk.duoduo.bean.ConversationInfo;
import com.zxjk.duoduo.bean.DaoMaster;
import com.zxjk.duoduo.bean.DataTemp628;
import com.zxjk.duoduo.bean.response.AllGroupMembersResponse;
import com.zxjk.duoduo.bean.response.GetAppVersionResponse;
import com.zxjk.duoduo.db.BurnAfterReadMessageLocalBean;
import com.zxjk.duoduo.db.OpenHelper;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.findpage.HilamgServiceActivity;
import com.zxjk.duoduo.ui.minepage.OnlineServiceActivity;
import com.zxjk.duoduo.ui.minepage.SettingActivity;
import com.zxjk.duoduo.ui.minepage.UserInfoActivity;
import com.zxjk.duoduo.ui.msgpage.ContactFragment;
import com.zxjk.duoduo.ui.msgpage.MsgFragment;
import com.zxjk.duoduo.ui.msgpage.MyQrCodeActivity;
import com.zxjk.duoduo.ui.msgpage.ShareGroupQRActivity;
import com.zxjk.duoduo.ui.wallet.WalletFragment;
import com.zxjk.duoduo.ui.widget.DrawerView;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MMKVUtils;
import com.zxjk.duoduo.utils.badge.BadgeNumberManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongMessageItemLongClickActionManager;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.widget.provider.MessageItemLongClickAction;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CommandMessage;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

import static com.ashokvarma.bottomnavigation.BottomNavigationBar.BACKGROUND_STYLE_STATIC;

public class HomeActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {
    public BadgeItem badgeItem2;
    private MsgFragment msgFragment;
    private ContactFragment contactFragment;
    private WalletFragment findFragment;

    private ViewPager pager;
    private DrawerView drawer;
    private TextView tvNick;
    private ImageView ivHead;
    private View dotService;
    private BottomNavigationBar m_bottom_bar;
    private int msgCount1;
    private BurnAfterReadMessageLocalBeanDao dao;
    private long updateProgress;

    @Override
    protected void onDestroy() {
        RongIM.setOnReceiveMessageListener(null);
        super.onDestroy();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        cleanBadge();
        startBurnMsgInterval(0);
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        int selectPage = getIntent().getIntExtra("selectPage", -1);
        if (selectPage != -1) {
            pager.setCurrentItem(selectPage);
            getIntent().putExtra("selectPage", -1);
        }
    }

    @SuppressLint({"WrongConstant", "CheckResult"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BarUtils.transparentStatusBar(this);

        setContentView(R.layout.activity_home);

        initView();

        initFragment();

        registerRongMsgReceiver();

        getRuntimePermission();

        createChannel();

        if (!BuildConfig.DEBUG) {
            getVersion(false);
        }

        getNewFriendCount();

        initMessageLongClickAction();

        initGreenDaoSession();

        initRongUserProvider();

        initRongMention();

        dotService = findViewById(R.id.dotService);
        if (System.currentTimeMillis() > 1593273600000L && System.currentTimeMillis() < 1593532799000L) {
            dotService.setVisibility(View.VISIBLE);
        }
        if (getIntent().getBooleanExtra("attachAD", false)) {
            DataTemp628 data = new DataTemp628();
            data.setId(Constant.currentUser.getId());
            data.setToken(Constant.currentUser.getToken());
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url", Constant.URL_628ACTIVITY + "/?" + AesUtil.getInstance().encrypt(GsonUtils.toJson(data)));
            startActivity(intent);
        }

        handleQR();
    }

    private void handleQR() {
        if (!TextUtils.isEmpty(getIntent().getStringExtra("resultUri"))) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getStringExtra("resultUri")));
            startActivity(intent);
        }
    }

    private void initRongMention() {
        RongMentionManager.getInstance().setGroupMembersProvider((s, callBack) ->
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .getGroupMemByGroupId(s)
                        .compose(bindUntilEvent(ActivityEvent.DESTROY))
                        .compose(RxSchedulers.ioObserver())
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(list -> {
                            ArrayList<UserInfo> result = new ArrayList<>(list.size());
                            for (AllGroupMembersResponse r : list) {
                                UserInfo member = new UserInfo(r.getId(), r.getNick(), Uri.parse(r.getHeadPortrait()));
                                result.add(member);
                            }
                            callBack.onGetGroupMembersResult(result);
                        }, t -> ToastUtils.showShort(R.string.function_fail))
        );
    }

    @SuppressLint("CheckResult")
    private void initRongUserProvider() {
        RongIM.setGroupInfoProvider(id -> {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getGroupChatInfoByGroupId(id)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .subscribeOn(Schedulers.io())
                    .subscribe(group -> {
                                if (group.getGroupType() == 1) {
                                    RongIM.getInstance().refreshGroupInfoCache(new Group(id, group.getGroupNikeName()
                                            + "おれは人间をやめるぞ！ジョジョ―――ッ!", Uri.parse(group.getHeadPortrait())));
                                } else {
                                    RongIM.getInstance().refreshGroupInfoCache(new Group(id, group.getGroupNikeName(),
                                            Uri.parse(group.getHeadPortrait())));
                                }
                            },
                            t -> {
                            });

            return null;
        }, true);

        RongIM.setUserInfoProvider(id -> {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getCustomerBasicInfoById(id)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .subscribeOn(Schedulers.io())
                    .subscribe(user -> {
                                UserInfo userInfo = new UserInfo(id, user.getNick(), Uri.parse(user.getHeadPortrait()));
                                if (!TextUtils.isEmpty(user.getIsSystem()) && user.getIsSystem().equals("1")) {
                                    userInfo.setExtra("system");
                                }
                                RongIM.getInstance().refreshUserInfoCache(userInfo);
                            },
                            t -> {
                            });
            return null;
        }, true);
    }

    @SuppressLint("CheckResult")
    private void registerRongMsgReceiver() {
        RongIM.setOnReceiveMessageListener((message, i) -> {

            Intent intent = new Intent(Constant.ACTION_BROADCAST2);
            intent.putExtra("msg", message);
            intent.putExtra("count", i);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            //update badge
            if (!AppUtils.isAppForeground()) {
                BadgeNumberManager.from(this).setBadgeNumber(++Constant.messageCount);
            }

            if (!TextUtils.isEmpty(message.getObjectName()) && message.getObjectName().equals("RC:CmdMsg")) {
                CommandMessage commandMessage = (CommandMessage) message.getContent();
                switch (commandMessage.getName()) {
                    case "deleteFriend":
                        RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE, message.getSenderUserId(), null);
                        RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, message.getSenderUserId(), null);
                        break;
                    case "addFriend":
                        runOnUiThread(() -> {
                            long newFriendCount = MMKVUtils.getInstance().decodeLong("newFriendCount");
                            newFriendCount += 1;
                            MMKVUtils.getInstance().enCode("newFriendCount", newFriendCount);
                            if (newFriendCount == 0) {
                                badgeItem2.hide();
                            } else {
                                badgeItem2.show(true);
                                if (newFriendCount >= 100) {
                                    badgeItem2.setText("99+");
                                } else {
                                    badgeItem2.setText(String.valueOf(newFriendCount));
                                }
                                if (contactFragment.getDotNewFriend() != null) {
                                    contactFragment.getDotNewFriend().setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        break;
                    case "agreeFriend":
                        break;
                    case "ForceClearAllLocalHistory":
                        String groupId2BeClear = commandMessage.getData();
                        RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, groupId2BeClear, null);
                        break;
                }
            }
            return false;
        });
    }

    @SuppressLint("CheckResult")
    private void getRuntimePermission() {
        rxPermissions.requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                .flatMap((Function<Permission, ObservableSource<?>>) permission -> {
                    if (permission.granted) {
                        throw new RuntimeException();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        ToastUtils.showShort(R.string.open_permission_reason);
                    } else {
                        ToastUtils.showShort(R.string.open_permission_reason1);
                    }
                    return Observable.timer(2500, TimeUnit.MILLISECONDS);
                })
                .subscribe(permission -> back2Login(), t -> {
                });
    }

    @SuppressLint("CheckResult")
    private void startBurnMsgInterval(int second) {
        Observable
                .timer(second, TimeUnit.SECONDS, Schedulers.io())
                .compose(bindToLifecycle())
                .flatMap((Function<Long, ObservableSource<List<BurnAfterReadMessageLocalBean>>>) a -> Observable.create(e -> {
                    dao.detachAll();
                    List<BurnAfterReadMessageLocalBean> msgs = dao.queryBuilder()
                            .where(BurnAfterReadMessageLocalBeanDao.Properties.BurnTime.le(System.currentTimeMillis())).list();

                    if (msgs.size() == 0) {
                        e.onNext(new ArrayList<>());
                        return;
                    }

                    int[] ids = new int[msgs.size()];
                    for (int i = 0; i < msgs.size(); i++) {
                        ids[i] = msgs.get(i).getMessageId();
                    }
                    RongIM.getInstance().deleteMessages(ids, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean b) {
                            e.onNext(msgs);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            e.onNext(new ArrayList<>());
                            MobclickAgent.reportError(Utils.getApp(), "融云出错(阅后即焚HomeActIntervel):" + errorCode.getMessage() + errorCode.getValue());
                        }
                    });
                }))
                .subscribe(list -> {
                    if (list.size() != 0) {
                        dao.deleteInTx(list);
                    }
                    startBurnMsgInterval(5);
                });
    }

    private void initGreenDaoSession() {
        OpenHelper open = new
                OpenHelper(Utils.getApp(), Constant.currentUser.getId(), null);
        Application.daoSession = new DaoMaster(open.getWritableDatabase()).newSession();
        dao = Application.daoSession.getBurnAfterReadMessageLocalBeanDao();
    }

    private void initMessageLongClickAction() {
        MessageItemLongClickAction action1 = new MessageItemLongClickAction.Builder()
                .title(getString(R.string.transfer1))
                .showFilter(message -> {
                            boolean b = message.getSentStatus() == Message.SentStatus.SENT &&
                                    (message.getObjectName().equals("RC:TxtMsg") ||
                                            message.getObjectName().equals("RC:ImgMsg") ||
                                            message.getObjectName().equals("RC:FileMsg") ||
                                            message.getObjectName().equals("RC:VcMsg") ||
                                            message.getObjectName().equals("RC:HQVCMsg"));

                            String extra = "";
                            if (message.getContent() instanceof TextMessage) {
                                TextMessage textMessage = (TextMessage) message.getContent();
                                extra = textMessage.getExtra();
                            } else if (message.getContent() instanceof ImageMessage) {
                                ImageMessage imageMessage = (ImageMessage) message.getContent();
                                extra = imageMessage.getExtra();
                            } else if (message.getContent() instanceof VoiceMessage) {
                                VoiceMessage voiceMessage = (VoiceMessage) message.getContent();
                                extra = voiceMessage.getExtra();
                            }
                            try {
                                ConversationInfo info = GsonUtils.fromJson(extra, ConversationInfo.class);
                                if (info.getMessageBurnTime() != -1) {
                                    b = false;
                                }
                            } catch (Exception e) {
                            }

                            return b;
                        }
                )
                .actionListener((context, message) -> {
                    RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        @Override
                        public void onSuccess(List<Conversation> conversations) {
                            Intent intent = new Intent(HomeActivity.this, ShareGroupQRActivity.class);
                            intent.putParcelableArrayListExtra("data", (ArrayList<Conversation>) conversations);
                            intent.putExtra("action", "transfer");
                            intent.putExtra("messagecontent", message.getContent());
                            startActivity(intent);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                        }
                    });
                    return true;
                }).build();

        RongMessageItemLongClickActionManager.getInstance().addMessageItemLongClickAction(action1);
    }

    private void createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager != null &&
                    mNotificationManager.getNotificationChannel(Constant.LOCAL_CHANNEL_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(Constant.LOCAL_CHANNEL_ID, "新消息", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setLightColor(Color.WHITE);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationChannel.setShowBadge(true);
                notificationChannel.setBypassDnd(true);
                notificationChannel.setVibrationPattern(new long[]{100});
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private void cleanBadge() {
        Constant.messageCount = 0;
        BadgeNumberManager.from(this).setBadgeNumber(0);
    }

    private void initView() {
        tvNick = findViewById(R.id.tvNick);
        TextView tvHilamgID = findViewById(R.id.tvHilamgID);
        ivHead = findViewById(R.id.ivHead);
        tvHilamgID.setText(getString(R.string.duoduo_id) + " " + Constant.currentUser.getDuoduoId());

        pager = findViewById(R.id.pager);
        drawer = findViewById(R.id.drawer);
        m_bottom_bar = findViewById(R.id.m_bottom_bar);

        BadgeItem badgeItem = new BadgeItem();
        badgeItem.setHideOnSelect(false)
                .setBackgroundColor("#FF4C48")
                .setBorderWidth(0);

        badgeItem.setText("0");

        badgeItem2 = new BadgeItem();
        badgeItem2.setHideOnSelect(false)
                .setBackgroundColor("#FF4C48")
                .setBorderWidth(0);

        m_bottom_bar.setMode(BottomNavigationBar.MODE_FIXED);

        m_bottom_bar
                .setBackgroundStyle(BACKGROUND_STYLE_STATIC)
                .setBarBackgroundColor("#ffffff")
                .setActiveColor(R.color.colorTheme)
                .setInActiveColor("#000000")
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_message_selected, getString(R.string.home_bottom1)).setInactiveIconResource(R.drawable.tab_icon_message_unselected).setBadgeItem(badgeItem))
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_friend_selected, getString(R.string.home_bottom2)).setInactiveIconResource(R.drawable.tab_icon_friend_unselected).setBadgeItem(badgeItem2))
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_wallet_selected, getString(R.string.home_bottom3)).setInactiveIconResource(R.drawable.tab_icon_wallet_unselected))
                .setFirstSelectedPosition(0)
                .initialise();

        m_bottom_bar.setTabSelectedListener(this);

        RongIM.getInstance().addUnReadMessageCountChangedObserver(count -> {
            msgCount1 = count;
            if ((msgCount1) == 0) {
                badgeItem.hide();
                return;
            }
            badgeItem.setText(String.valueOf(msgCount1));
            if ((msgCount1) > 99) {
                badgeItem.setText("99+");
            }
            badgeItem.show(true);
        }, Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM);
    }

    private void getNewFriendCount() {
        long newFriendCount = MMKVUtils.getInstance().decodeLong("newFriendCount");
        if (newFriendCount == 0) {
            badgeItem2.hide();
        } else {
            badgeItem2.show(true);
            if (newFriendCount >= 100) {
                badgeItem2.setText("99+");
            } else {
                badgeItem2.setText(String.valueOf(newFriendCount));
            }
            if (contactFragment.getDotNewFriend() != null) {
                contactFragment.getDotNewFriend().setVisibility(View.VISIBLE);
            }
        }
    }

    //获取版本
    @SuppressLint("CheckResult")
    public void getVersion(boolean showTip) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getAppVersion()
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .compose(RxSchedulers.ioObserver())
                .subscribe(response -> {
                    if (response.code != Constant.CODE_SUCCESS) {
                        return;
                    }
                    GetAppVersionResponse data = response.data;
                    String appVersionName = AppUtils.getAppVersionName();

                    File file = new File(Utils.getApp().getCacheDir(), data.getVersion() + ".apk");
                    if (file.exists()) {
                        file.delete();
                    }

                    if (!appVersionName.equals(data.getVersion())) {
                        NiceDialog.init().setLayoutId(R.layout.dialog_update).setConvertListener(new ViewConvertListener() {
                            @Override
                            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                                TextView tv = holder.getView(R.id.tv);
                                tv.setText(Html.fromHtml(data.getUpdateContent()));
                                ((TextView) holder.getView(R.id.tv)).setMovementMethod(new ScrollingMovementMethod());
                                TextView tvUpdate = holder.getView(R.id.tvUpdate);
                                FrameLayout flUpgrade = holder.getView(R.id.flUpgrade);

                                downloadOrInstall(tvUpdate, flUpgrade, data);
                            }
                        }).setDimAmount(0.5f).setOutCancel("0".equals(response.data.getIsEnforcement())).show(getSupportFragmentManager());
                    } else if (showTip) {
                        ToastUtils.showShort(R.string.newestVersion);
                    }
                }, t -> {
                });
    }

    private void downloadOrInstall(TextView tvUpdate, FrameLayout flUpgrade, GetAppVersionResponse data) {
        flUpgrade.setOnClickListener(v -> {
            //后台下载APK并更新
            flUpgrade.setClickable(false);
            ServiceFactory.downloadFile(data.getVersion(), data.getUpdateAddress(), new ServiceFactory.DownloadListener() {
                @Override
                public void onStart(long max) {
                    updateProgress = max;
                    ToastUtils.showShort(R.string.update_start);
                }

                @Override
                public void onProgress(long progress) {
                    runOnUiThread(() -> tvUpdate.setText((int) ((float) progress / updateProgress * 100) + "%"));
                }

                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        flUpgrade.setClickable(true);
                        tvUpdate.setText(R.string.dianjianzhuang);
                        flUpgrade.setOnClickListener(v1 -> {
                            File file = new File(Utils.getApp().getCacheDir(), data.getVersion() + ".apk");// 设置路径
                            String[] command = {"chmod", "777", file.getPath()};
                            ProcessBuilder builder = new ProcessBuilder(command);
                            try {
                                builder.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Intent intent = installIntent(file.getPath());
                            if (intent != null) {
                                startActivity(intent);
                            }
                        });
                        flUpgrade.performClick();
                    });
                }

                @Override
                public void onFailure() {
                    runOnUiThread(() -> {
                        File file = new File(Utils.getApp().getCacheDir(), data.getVersion() + ".apk");
                        if (file.exists()) {
                            file.delete();
                        }
                        downloadOrInstall(tvUpdate, flUpgrade, data);
                        flUpgrade.setClickable(true);
                        tvUpdate.setText(R.string.dianjichongshi);
                    });
                    ToastUtils.showShort(R.string.update_failure);
                }
            });
        });
    }

    private Intent installIntent(String path) {
        try {
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".FileProvider", file),
                        "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("CheckResult")
    public void ipfs(View view) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getHilamgMillUrl()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(url -> {
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title", "ipfs");
                    intent.putExtra("type", "mall");
                    drawer.close(intent);
                }, this::handleApiError);
    }

    public void setting(View view) {
        drawer.close(new Intent(this, SettingActivity.class));
    }

    public void myQR(View view) {
        drawer.close(new Intent(this, MyQrCodeActivity.class));
    }

    public void setInfo(View view) {
        drawer.close(new Intent(this, UserInfoActivity.class));
    }

    public void service(View view) {
        drawer.close(new Intent(this, OnlineServiceActivity.class));
    }

    public void invite(View view) {
        UMWeb link = new UMWeb(Constant.APP_SHARE_URL + AesUtil.getInstance().encrypt("id=" + Constant.userId));
        link.setTitle("我在使用Hilamg聊天");
        link.setDescription("加密私聊、社群管理、数字\n" +
                "支付尽在Hilamg ，你也来\n" +
                "试试吧～");
        link.setThumb(new UMImage(this, R.drawable.ic_hilamglogo4));
        new ShareAction(this).withMedia(link).setPlatform(SHARE_MEDIA.WEIXIN).share();
    }

    public void hilamgService(View view) {
        drawer.close(new Intent(this, HilamgServiceActivity.class));
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    private void initFragment() {
        msgFragment = new MsgFragment();
        contactFragment = new ContactFragment();
        findFragment = new WalletFragment();

        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return msgFragment;
                }
                if (position == 1) {
                    return contactFragment;
                }
                if (position == 2) {
                    return findFragment;
                }
                return msgFragment;
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            }
        });
        pager.setOffscreenPageLimit(3);

        pager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        m_bottom_bar.selectTab(position);
                    }
                });
    }


    @Override
    public void onTabSelected(int position) {
        if (position == 2) {
            setTrasnferStatusBar(false);
        } else {
            setTrasnferStatusBar(true);
        }
        pager.setCurrentItem(position, true);
    }

    @Override
    public void onTabUnselected(int position) {
    }

    @Override
    public void onTabReselected(int position) {
    }

    public void onHeadClick() {
        drawer.switchState();
        tvNick.setText(Constant.currentUser.getNick());
        GlideUtil.loadCircleImg(ivHead, Constant.currentUser.getHeadPortrait());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            msgFragment.close(null);
        }
        return super.dispatchTouchEvent(ev);
    }
}