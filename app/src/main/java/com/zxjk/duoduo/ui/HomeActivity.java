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
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.VibrateUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.tbruyelle.rxpermissions2.Permission;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.BuildConfig;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.DuoDuoFileProvider;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.BurnAfterReadMessageLocalBean;
import com.zxjk.duoduo.bean.BurnAfterReadMessageLocalBeanDao;
import com.zxjk.duoduo.bean.DaoMaster;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.bean.response.GetAppVersionResponse;
import com.zxjk.duoduo.db.OpenHelper;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.findpage.FindFragment;
import com.zxjk.duoduo.ui.minepage.MineFragment;
import com.zxjk.duoduo.ui.msgpage.ContactFragment;
import com.zxjk.duoduo.ui.msgpage.MsgFragment;
import com.zxjk.duoduo.ui.msgpage.ShareGroupQRActivity;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.DuoDuoMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.GameResultMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.SystemMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.TransferMessage;
import com.zxjk.duoduo.utils.MMKVUtils;
import com.zxjk.duoduo.utils.badge.BadgeNumberManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongMessageItemLongClickActionManager;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.MessageItemLongClickAction;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CommandMessage;
import io.rong.message.NotificationMessage;
import io.rong.message.VoiceMessage;
import io.rong.pushperm.ResultCallback;
import io.rong.pushperm.RongPushPremissionsCheckHelper;

import static com.ashokvarma.bottomnavigation.BottomNavigationBar.BACKGROUND_STYLE_STATIC;

public class HomeActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    private Fragment mFragment;
    public BadgeItem badgeItem2;
    private MsgFragment msgFragment;
    private ContactFragment contactFragment;
    private FindFragment findFragment;
    private MineFragment mineFragment;

    //私聊数
    private int msgCount1;
    //群聊数
    private int msgCount2;

    private BurnAfterReadMessageLocalBeanDao dao;

    @Override
    protected void onDestroy() {
        RongIM.setOnReceiveMessageListener(null);
        super.onDestroy();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        startBurnMsgInterval(0);

        cleanBadge();

        Observable.timer(1, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    if (ActivityUtils.getTopActivity() != this) {
                        return;
                    }
                    RongIM.setOnReceiveMessageListener((message, i) -> {
                        //update badge
                        if (!AppUtils.isAppForeground()) {
                            BadgeNumberManager.from(this).setBadgeNumber(++Constant.messageCount);
                        }
                        //handle command(delete add newfriend)
                        if (message.getObjectName().equals("RC:CmdMsg")) {
                            CommandMessage commandMessage = (CommandMessage) message.getContent();
                            if (commandMessage.getName().equals("deleteFriend")) {
                                RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE, message.getSenderUserId(), null);
                                RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, message.getSenderUserId(), null);
                                contactFragment.onResume();
                            } else if (commandMessage.getName().equals("addFriend")) {
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
                            } else if (commandMessage.getName().equals("agreeFriend")) {
                                contactFragment.onResume();
                            }
                        }
                        return false;
                    });
                });
        super.onResume();
    }

    @SuppressLint({"WrongConstant", "CheckResult"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        getRuntimePermission();

        createChannel();

        getVersion(false);

        initFriendList();

        initFragment();

        initView();

        getNewFriendCount();

        //checkPermission(); 检测是否开启自启动和通知

        initMessageLongClickAction();

        initGreenDaoSession();
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
                .timer(second, TimeUnit.SECONDS, Schedulers.single())
                .observeOn(Schedulers.single())
                .compose(second == 0 ? bindToLifecycle() : bindUntilEvent(ActivityEvent.PAUSE))
                .doOnComplete(() -> {
                    if (dao.getDatabase().inTransaction()) dao.getDatabase().endTransaction();
                })
                .doOnDispose(() -> {
                    if (dao.getDatabase().inTransaction()) dao.getDatabase().endTransaction();
                })
                .flatMap((Function<Long, ObservableSource<Boolean>>) a -> Observable.create(e -> {
                    if (!dao.getDatabase().inTransaction()) dao.getDatabase().beginTransaction();
                    List<BurnAfterReadMessageLocalBean> msgs = dao.queryBuilder()
                            .where(BurnAfterReadMessageLocalBeanDao.Properties.BurnTime.le(System.currentTimeMillis())).list();

                    if (msgs.size() == 0) {
                        e.onNext(Boolean.FALSE);
                        return;
                    }

                    dao.deleteInTx(msgs);

                    int[] ids = new int[msgs.size()];
                    for (int i = 0; i < msgs.size(); i++) {
                        ids[i] = msgs.get(i).getMessageId();
                    }
                    RongIM.getInstance().deleteMessages(ids, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean b) {
                            e.onNext(b);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            e.onNext(Boolean.FALSE);
                            ToastUtils.showLong("融云出错:" + errorCode.getMessage() + errorCode.getValue());
                        }
                    });
                }))
                .subscribe(b -> {
                    if (b && dao.getDatabase().inTransaction())
                        dao.getDatabase().setTransactionSuccessful();

                    if (dao.getDatabase().inTransaction())
                        dao.getDatabase().endTransaction();

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
                    MessageContent messageContent = message.getContent();
                    return !(messageContent instanceof NotificationMessage)
                            && !(messageContent instanceof VoiceMessage)
                            && !(messageContent instanceof RedPacketMessage)
                            && !(messageContent instanceof TransferMessage)
                            && !(messageContent instanceof SystemMessage)
                            && !(messageContent instanceof GameResultMessage)
                            && !(messageContent instanceof DuoDuoMessage)
                            && message.getSentStatus() != Message.SentStatus.FAILED
                            && message.getSentStatus() != Message.SentStatus.CANCELED;
                })
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
                })
                .build();

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

    private void checkPermission() {
        RongPushPremissionsCheckHelper.checkPermissionsAndShowDialog(this, new ResultCallback() {
            @Override
            public void onAreadlyOpened(String value) {

            }

            @Override
            public boolean onBeforeShowDialog(String value) {
                return false;
            }

            @Override
            public void onGoToSetting(String value) {

            }

            @Override
            public void onFailed(String value, FailedType type) {

            }
        });
    }

    private void initView() {
        BottomNavigationBar m_bottom_bar = findViewById(R.id.m_bottom_bar);

        BadgeItem badgeItem = new BadgeItem();
        badgeItem.setHideOnSelect(false)
                .setBackgroundColorResource(R.color.red_eth_in)
                .setBorderWidth(0);
        badgeItem.setText("0");

        badgeItem2 = new BadgeItem();
        badgeItem2.setHideOnSelect(false)
                .setBackgroundColorResource(R.color.red_eth_in)
                .setBorderWidth(0);

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
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_message_selected, "消息").setInactiveIconResource(R.drawable.tab_icon_message_unselected).setBadgeItem(badgeItem))
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_friend_selected, "通讯录").setInactiveIconResource(R.drawable.tab_icon_friend_unselected).setBadgeItem(badgeItem2))
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_find_selected, "发现").setInactiveIconResource(R.drawable.tab_icon_find_unselected))
                .addItem(new BottomNavigationItem(R.drawable.tab_icon_my_selected, "我的").setInactiveIconResource(R.drawable.tab_icon_my_unselected))
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
            if ((msgCount1 + msgCount2) == 0) {
                badgeItem.hide();
                return;
            }
            badgeItem.setText(String.valueOf(msgCount1 + msgCount2));
            if ((msgCount1 + msgCount2) > 99) {
                badgeItem.setText("99+");
            }
            badgeItem.show(true);
        }, Conversation.ConversationType.PRIVATE);
        RongIM.getInstance().addUnReadMessageCountChangedObserver(count -> {
            msgCount2 = count;
            if (msgFragment.getBadgeTitleViews()[1] != null) {
                if (msgCount2 == 0) {
                    msgFragment.getBadgeTitleViews()[1].getBadgeView().setVisibility(View.INVISIBLE);
                } else {
                    msgFragment.getBadgeTitleViews()[1].getBadgeView().setVisibility(View.VISIBLE);
                }
            }
            if ((msgCount1 + msgCount2) == 0) {
                badgeItem.hide();
                return;
            }
            badgeItem.setText(String.valueOf(msgCount1 + msgCount2));
            if ((msgCount1 + msgCount2) > 99) {
                badgeItem.setText("99+");
            }
            badgeItem.show(true);
        }, Conversation.ConversationType.GROUP);
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

    private long max1;

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
                                holder.setText(R.id.tv, data.getUpdateContent());
                                ((TextView) holder.getView(R.id.tv)).setMovementMethod(new ScrollingMovementMethod());
                                holder.getView(R.id.ivClose).setVisibility(data.getIsEnforcement().equals("0") ? View.VISIBLE : View.GONE);
                                TextView tvUpdate = holder.getView(R.id.tvUpdate);
                                tvUpdate.setOnClickListener(v -> {
                                    //后台下载APK并更新
                                    ServiceFactory.downloadFile(data.getVersion(), data.getUpdateAddress(), new ServiceFactory.DownloadListener() {
                                        @Override
                                        public void onStart(long max) {
                                            runOnUiThread(() -> tvUpdate.setClickable(false));
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
                                                tvUpdate.setClickable(true);
                                                tvUpdate.setText(R.string.dianjianzhuang);
                                                tvUpdate.setOnClickListener(v1 -> {
                                                    File file = new File(Utils.getApp().getCacheDir(), data.getVersion() + ".apk");// 设置路径
                                                    Intent intent = installIntent(file.getPath());
                                                    if (intent != null) {
                                                        startActivity(intent);
                                                    }
                                                });
                                                tvUpdate.performClick();
                                            });
                                        }

                                        @Override
                                        public void onFailure() {
                                            runOnUiThread(() -> {
                                                File file = new File(Utils.getApp().getCacheDir(), data.getVersion() + ".apk");
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                                tvUpdate.setClickable(true);
                                                tvUpdate.setText(R.string.dianjichongshi);
                                            });
                                            ToastUtils.showShort(R.string.update_failure);
                                        }
                                    });
                                });
                                holder.setOnClickListener(R.id.ivClose, v -> dialog.dismiss());
                            }
                        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
                    } else if (showTip) {
                        ToastUtils.showShort(R.string.newestVersion);
                    }
                }, t -> {
                });
    }

    private Intent installIntent(String path) {
        try {
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(DuoDuoFileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".FileProvider", file),
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

    @SuppressLint("CheckResult")
    private void initFriendList() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.ioObserver())
                .subscribe(response -> {
//                    Constant.friendsList = response.data;
                    for (FriendInfoResponse f : response.data) {
                        RongUserInfoManager.getInstance().setUserInfo(new UserInfo(f.getId(), TextUtils.isEmpty(f.getRemark()) ? f.getNick() : f.getRemark(), Uri.parse(f.getHeadPortrait())));
                    }
                }, t -> {
//                    //重复登录不再递归，避免过多请求
//                    if (t.getCause() instanceof RxException.DuplicateLoginExcepiton ||
//                            t instanceof RxException.DuplicateLoginExcepiton) {
//                        return;
//                    }
//                    Observable.timer(10, TimeUnit.SECONDS)
//                            .subscribe(aLong -> initFriendList());
                });
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
        VibrateUtils.vibrate(50);
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
}