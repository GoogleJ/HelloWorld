package com.zxjk.moneyspace.ui;

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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.VibrateUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.tbruyelle.rxpermissions2.Permission;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.umeng.analytics.MobclickAgent;
import com.zxjk.moneyspace.Application;
import com.zxjk.moneyspace.BuildConfig;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.BurnAfterReadMessageLocalBeanDao;
import com.zxjk.moneyspace.bean.ConversationInfo;
import com.zxjk.moneyspace.bean.DaoMaster;
import com.zxjk.moneyspace.bean.response.AllGroupMembersResponse;
import com.zxjk.moneyspace.bean.response.GetAppVersionResponse;
import com.zxjk.moneyspace.db.BurnAfterReadMessageLocalBean;
import com.zxjk.moneyspace.db.OpenHelper;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.findpage.FindFragment;
import com.zxjk.moneyspace.ui.minepage.MineFragment;
import com.zxjk.moneyspace.ui.msgpage.ContactFragment;
import com.zxjk.moneyspace.ui.msgpage.MsgFragment;
import com.zxjk.moneyspace.ui.msgpage.ShareGroupQRActivity;
import com.zxjk.moneyspace.ui.msgpage.rongIM.GroupConversationProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.PrivateConversationProvider;
import com.zxjk.moneyspace.utils.MMKVUtils;
import com.zxjk.moneyspace.utils.badge.BadgeNumberManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongContext;
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

    public static final int REQUEST_REWARD = 1001;
    public BadgeItem badgeItem2;
    private Fragment mFragment;
    private BadgeItem badgeItem3;
    private MsgFragment msgFragment;
    private ContactFragment contactFragment;
    private FindFragment findFragment;
    private MineFragment mineFragment;
    private BottomNavigationBar m_bottom_bar;

    //私聊数
    private int msgCount1;

    private BurnAfterReadMessageLocalBeanDao dao;
    private long max1;

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

    @SuppressLint({"WrongConstant", "CheckResult"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        registerRongMsgReceiver();

        getRuntimePermission();

        createChannel();

        if (!BuildConfig.DEBUG) {
            getVersion(false);
        }

        initFragment();

        initView();

        getNewFriendCount();

        initMessageLongClickAction();

        initGreenDaoSession();

        RongContext.getInstance().registerConversationTemplate(new PrivateConversationProvider());
        RongContext.getInstance().registerConversationTemplate(new GroupConversationProvider());

        initRongUserProvider();

        initRongMention();
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

    public void showFourthBadge() {
        if (badgeItem3 != null) badgeItem3.show(true);
    }

    public void hideFourthBadge() {
        if (badgeItem3 != null) badgeItem3.hide(true);
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
                        contactFragment.onResume();
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
                            }
                            if (contactFragment.getDotNewFriend() != null) {
                                contactFragment.getDotNewFriend().setVisibility(View.VISIBLE);
                            }
                        });
                        break;
                    case "agreeFriend":
                        contactFragment.onResume();
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
                .title("转发")
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
        m_bottom_bar = findViewById(R.id.m_bottom_bar);

        BadgeItem badgeItem = new BadgeItem();
        badgeItem.setHideOnSelect(false)
                .setBackgroundColorResource(R.color.red_eth_in)
                .setBorderWidth(0);
        badgeItem.setText("0");

        badgeItem2 = new BadgeItem();
        badgeItem2.setHideOnSelect(false)
                .setBackgroundColorResource(R.color.red_eth_in)
                .setBorderWidth(0);

        badgeItem3 = new BadgeItem();
        badgeItem3.setHideOnSelect(false)
                .setBackgroundColorResource(R.color.red_eth_in)
                .setBorderWidth(0);
        badgeItem3.setText("1");
        badgeItem3.hide();

        //设置Item选中颜色方法
        m_bottom_bar.setActiveColor(R.color.colorAccent)
                //设置Item未选中颜色方法
                .setInActiveColor(R.color.colorPrimary)
                //背景颜色
                .setBarBackgroundColor("#FFFFFF");

        m_bottom_bar.setMode(BottomNavigationBar.MODE_FIXED);

        m_bottom_bar
                // 背景样式
                .setBackgroundStyle(BACKGROUND_STYLE_STATIC)
                // 背景颜色
                .setBarBackgroundColor("#ffffff")
                .setActiveColor(R.color.colorTheme)
                .setInActiveColor("#000000")
                // 添加Item
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_message_selected, getString(R.string.home_bottom1)).setInactiveIconResource(R.drawable.tab_icon_message_unselected).setBadgeItem(badgeItem))
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_friend_selected, getString(R.string.home_bottom2)).setInactiveIconResource(R.drawable.tab_icon_friend_unselected).setBadgeItem(badgeItem2))
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_find_selected, getString(R.string.home_bottom3)).setInactiveIconResource(R.drawable.tab_icon_find_unselected))
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_my_selected, getString(R.string.home_bottom4)).setInactiveIconResource(R.drawable.tab_icon_my_unselected).setBadgeItem(badgeItem3))
                //设置默认选中位置
                .setFirstSelectedPosition(0)
                // 提交初始化（完成配置）
                .initialise();

        m_bottom_bar.setTabSelectedListener(this);

        RongIM.getInstance().addUnReadMessageCountChangedObserver(count -> {
            msgCount1 = count;
            if (msgFragment.getBadgeTitleViews()[0] != null) {
                if (msgCount1 == 0) {
                    msgFragment.getBadgeTitleViews()[0].getBadgeView().setVisibility(View.INVISIBLE);
                } else {
                    msgFragment.getBadgeTitleViews()[0].getBadgeView().setVisibility(View.VISIBLE);
                }
            }
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
                        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
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
                    max1 = max;
                    ToastUtils.showShort(R.string.update_start);
                }

                @Override
                public void onProgress(long progress) {
                    runOnUiThread(() -> tvUpdate.setText((int) ((float) progress / max1 * 100) + "%"));
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
        findFragment = new FindFragment();
        mineFragment = new MineFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_content, msgFragment)
                .commit();
        mFragment = msgFragment;
    }

    @Override
    public void onTabSelected(int position) {
        if (position == 3) {
            setTrasnferStatusBar(true);
        } else {
            setLightStatusBar(true);
        }
        switch (position) {
            case 0:
                switchFragment(msgFragment);
                break;
            case 1:
                switchFragment(contactFragment);
                break;
            case 2:
                switchFragment(findFragment);
                break;
            case 3:
                switchFragment(mineFragment);
                break;
            default:
        }
    }

    @Override
    public void onTabUnselected(int position) {
    }

    @Override
    public void onTabReselected(int position) {
    }

    private void switchFragment(Fragment fragment) {
        if (MMKVUtils.getInstance().decodeBool("bottom_vibrate")) {
            VibrateUtils.vibrate(50);
        }
        if (mFragment != fragment) {
            if (!fragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(mFragment)
                        .add(R.id.fragment_content, fragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(mFragment).show(fragment).commit();
            }
            mFragment = fragment;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        switch (requestCode) {
            case REQUEST_REWARD:
                if (resultCode != 1) {
                    return;
                }
                if (data.getBooleanExtra("fromReward", false)) {
                    String action = data.getStringExtra("action");
                    switch (action) {
                        case "shareNews":
                            m_bottom_bar.selectTab(2, true);
                            break;
                        case "social":
                            m_bottom_bar.selectTab(0, true);
                            msgFragment.msgFragmentSelect();
                            break;
                    }
                }
                break;
        }
    }

}