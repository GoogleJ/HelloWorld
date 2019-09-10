package com.zxjk.duoduo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.mmkv.MMKV;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.zxjk.duoduo.bean.ConversationTimeBean;
import com.zxjk.duoduo.bean.DaoSession;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.ui.LoginActivity;
import com.zxjk.duoduo.ui.msgpage.rongIM.BasePluginExtensionModule;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.BusinessCardMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.DuoDuoMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.GameResultMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.GroupCardMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.SystemMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.TransferMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.BurnImageMessageItemProvider;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.BurnTextMessageProvider;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.BurnVoiceMessageProvider;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.BusinessCardProvider;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.DuoDuoMessageProvider;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.GameResultMessageProvider;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.GroupCardProvider;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.MInfoNotificationMsgItemProvider;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.RedPacketProvider;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.SystemProvider;
import com.zxjk.duoduo.ui.msgpage.rongIM.provider.TransferProvider;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.SightMessageItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import io.rong.message.SightMessage;
import io.rong.push.RongPushClient;
import io.rong.push.pushconfig.PushConfig;

import static io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT;

public class Application extends android.app.Application {

    public static OSS oss;

    private static final String WX_SHARE_ID = "wx95412ba899539c33";

    public static DaoSession daoSession;

    private long conversationOpenTime;

    @Override
    public void onCreate() {
        super.onCreate();

        //init MMKV
        MMKV.initialize(this);

        //工具类初始化
        Utils.init(this);

        //OSS初始化
        new Thread(this::initOSS).start();

        //融云推送设置
        initRongPush();

        //融云初始化
        initRongSDK();

        //监听融云连接状态变化
        registerConnectionStatusListener();

        //监听act lifecycle
        actLifecycle();

        //Umeng
        initUmeng();
    }

    private void initUmeng() {
        UMConfigure.init(this, "5d749f223fc1958bf7000854", "Fir", UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        UMConfigure.setProcessEvent(true);

        PlatformConfig.setWeixin("wx95412ba899539c33", "4e676a73b46f12e8251560a612bba1b3");
        PlatformConfig.setQQZone("101784998", "b370d5b099d93b3dc9bc91286ca83e9b");
    }

    private void actLifecycle() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                if (activity.getClass().getSimpleName().equals("ConversationActivity")) {
                    conversationOpenTime = System.currentTimeMillis();
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @SuppressLint("CheckResult")
            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (activity.getClass().getSimpleName().equals("ConversationActivity")) {
                    ConversationTimeBean b = new ConversationTimeBean();
                    b.setDayTimeMills(System.currentTimeMillis());

                    String json = MMKVUtils.getInstance().decodeString("conversationTimeBean");
                    if (!TextUtils.isEmpty(json)) {
                        b = GsonUtils.fromJson(json, ConversationTimeBean.class);
                        if (b.isHasComplete()) return;
                    }

                    long conversationTime = b.getTotalMills();
                    if (!TimeUtils.isToday(b.getDayTimeMills())) {
                        b.setHasComplete(false);
                        b.setDayTimeMills(System.currentTimeMillis());
                        conversationTime = 0;
                    }

                    long l = conversationTime + System.currentTimeMillis() - conversationOpenTime;

                    ConversationTimeBean finalB = b;
                    if ((l >= 1200000)) {
                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .savePointInfo("3")
                                .subscribe(s -> {
                                    finalB.setHasComplete(true);
                                    MMKVUtils.getInstance().enCode("conversationTimeBean", GsonUtils.toJson(finalB));
                                }, t -> {
                                });
                    } else {
                        finalB.setTotalMills(l);
                        MMKVUtils.getInstance().enCode("conversationTimeBean", GsonUtils.toJson(finalB));
                    }
                }
            }
        });
    }

    private void registerConnectionStatusListener() {
        RongIMClient.setConnectionStatusListener(connectionStatus -> {
            if (connectionStatus == KICKED_OFFLINE_BY_OTHER_CLIENT) {
                ToastUtils.showShort(R.string.duplicated_login);
                RongIM.getInstance().logout();
                Constant.clear();
                MMKVUtils.getInstance().enCode("isLogin", false);

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void initRongPush() {
        PushConfig config = new PushConfig.Builder()
                .enableMiPush("2882303761517995445", "5101799544445")
                .enableHWPush(true)
                .build();
        RongPushClient.setPushConfig(config);
    }

    @SuppressLint("CheckResult")
    private void initRongSDK() {
        RongIM.init(this);
        RongIM.registerMessageType(RedPacketMessage.class);
        RongIM.registerMessageType(BusinessCardMessage.class);
        RongIM.registerMessageType(TransferMessage.class);
        RongIM.registerMessageType(GroupCardMessage.class);
        RongIM.registerMessageType(SystemMessage.class);
        RongIM.registerMessageType(SightMessage.class);
        RongIM.registerMessageType(GameResultMessage.class);
        RongIM.registerMessageType(DuoDuoMessage.class);
        RongIM.registerMessageTemplate(new MInfoNotificationMsgItemProvider());
        RongIM.registerMessageTemplate(new SightMessageItemProvider());
        RongIM.registerMessageTemplate(new BurnVoiceMessageProvider());
        RongIM.registerMessageTemplate(new RedPacketProvider());
        RongIM.registerMessageTemplate(new TransferProvider());
        RongIM.registerMessageTemplate(new BusinessCardProvider());
        RongIM.registerMessageTemplate(new GroupCardProvider());
        RongIM.registerMessageTemplate(new SystemProvider());
        RongIM.registerMessageTemplate(new GameResultMessageProvider());
        RongIM.registerMessageTemplate(new DuoDuoMessageProvider());
        RongIM.registerMessageTemplate(new BurnTextMessageProvider());
        RongIM.registerMessageTemplate(new BurnImageMessageItemProvider());
        RongIM.getInstance().setMessageAttachedUserInfo(true);
        RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
        RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目
        setMyExtensionModule();
        RongIM.getInstance().setGroupMembersProvider((groupId, callback) -> ServiceFactory.getInstance().getBaseService(Api.class)
                .getGroupByGroupId(groupId)
                .subscribeOn(Schedulers.io())
                .subscribe(r -> {
                    ArrayList<UserInfo> strings = new ArrayList<>(r.data.getCustomers().size());
                    for (GroupResponse.CustomersBean b : r.data.getCustomers()) {
                        UserInfo userInfo = new UserInfo(b.getId(), b.getNick(), Uri.parse(b.getHeadPortrait()));
                        strings.add(userInfo);
                    }
                    callback.onGetGroupMembersResult(strings);
                }, t -> {
                }));
    }

    //初始化阿里云OSS上传服务
    private void initOSS() {
        String AK = "LTAI3V54BzteDdTi";
        String SK = "h59RLWudf6XMXO4bSqSOwsK3nBHXSK";
        OSSPlainTextAKSKCredentialProvider ossPlainTextAKSKCredentialProvider =
                new OSSPlainTextAKSKCredentialProvider(AK, SK);
        String endpoint = "oss-cn-hongkong.aliyuncs.com";
        oss = new OSSClient(this, endpoint, ossPlainTextAKSKCredentialProvider);
    }

    public static void setMyExtensionModule() {
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();

        if (moduleList != null) {
            IExtensionModule defaultModule = null;
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
            RongExtensionManager.getInstance().registerExtensionModule(new BasePluginExtensionModule());
        }
    }

    @Override
    public void onTerminate() {
        Constant.clear();
        super.onTerminate();
    }
}
