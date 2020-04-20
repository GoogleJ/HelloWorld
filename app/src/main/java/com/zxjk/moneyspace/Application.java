package com.zxjk.moneyspace;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.security.rp.RPSDK;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.mmkv.MMKV;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.zxjk.moneyspace.bean.DaoSession;
import com.zxjk.moneyspace.ui.SaasLoginSelectActivity;
import com.zxjk.moneyspace.ui.msgpage.rongIM.BasePluginExtensionModule;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.BusinessCardMessage;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.GroupCardMessage;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.NewsCardMessage;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.SystemMessage;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.TransferMessage;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.BurnHQVoiceMessageProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.BurnImageMessageItemProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.BurnTextMessageProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.BurnVoiceMessageProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.BusinessCardProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.GroupCardProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.MInfoNotificationMsgItemProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.NewsCardProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.RedPacketProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.SystemMessageProvider;
import com.zxjk.moneyspace.ui.msgpage.rongIM.provider.TransferProvider;
import com.zxjk.moneyspace.utils.LanguageUtil;
import com.zxjk.moneyspace.utils.MMKVUtils;
import com.zxjk.moneyspace.utils.MyCrashHandler;
import com.zxjk.moneyspace.utils.WebDataUtils;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.SightMessageItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.message.SightMessage;

import static io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT;

public class Application extends android.app.Application {
    public static OSS oss;

    public static DaoSession daoSession;

    private WebDataUtils webDataUtils;

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
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.enableLog) {
            Thread.setDefaultUncaughtExceptionHandler(MyCrashHandler.newInstance());
        }

        webDataUtils = new WebDataUtils();

        //init MMKV
        MMKV.initialize(this);

        //工具类初始化
        Utils.init(this);

        //OSS初始化
        new Thread(this::initOSS).start();

        //融云初始化
        initRongSDK();

        //监听融云连接状态变化
        registerConnectionStatusListener();

        //Umeng
//        initUmeng();

        RPSDK.initialize(getApplicationContext());

        QbSdk.initX5Environment(this, null);
    }

    public WebDataUtils getWebDataUtils() {
        return webDataUtils;
    }

    private void initUmeng() {
        UMConfigure.init(this, "5e215672cb23d2eb7f0000dc", "Hilamg_official", UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        UMConfigure.setProcessEvent(true);
        MobclickAgent.setCatchUncaughtExceptions(!BuildConfig.enableLog);

        PlatformConfig.setWeixin("wx022863eb70b07dcf", "ed5bd6099c398f443a1eadc9a8bda259");
        PlatformConfig.setQQZone("101838814", "2133a77b5e0abc441ca9646089399898");
    }

    private void registerConnectionStatusListener() {
        RongIMClient.setConnectionStatusListener(connectionStatus -> {
            if (connectionStatus == KICKED_OFFLINE_BY_OTHER_CLIENT) {
                ToastUtils.showShort(R.string.duplicated_login);
                RongIM.getInstance().logout();
                Constant.clear();
                MMKVUtils.getInstance().enCode("isLogin", false);

                Intent intent = new Intent(getApplicationContext(), SaasLoginSelectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void initRongSDK() {
        RongIM.init(this);
        RongIM.registerMessageType(RedPacketMessage.class);
        RongIM.registerMessageType(BusinessCardMessage.class);
        RongIM.registerMessageType(TransferMessage.class);
        RongIM.registerMessageType(GroupCardMessage.class);
        RongIM.registerMessageType(SightMessage.class);
//        RongIM.registerMessageType(CusEmoteTabMessage.class);
        RongIM.registerMessageType(NewsCardMessage.class);
        RongIM.registerMessageType(SystemMessage.class);
        RongIM.registerMessageTemplate(new MInfoNotificationMsgItemProvider());
        RongIM.registerMessageTemplate(new SightMessageItemProvider());
        RongIM.registerMessageTemplate(new BurnVoiceMessageProvider());
        RongIM.registerMessageTemplate(new BurnHQVoiceMessageProvider());
        RongIM.registerMessageTemplate(new RedPacketProvider());
        RongIM.registerMessageTemplate(new TransferProvider());
        RongIM.registerMessageTemplate(new BusinessCardProvider());
        RongIM.registerMessageTemplate(new GroupCardProvider());
        RongIM.registerMessageTemplate(new BurnTextMessageProvider());
        RongIM.registerMessageTemplate(new BurnImageMessageItemProvider());
//        RongIM.registerMessageTemplate(new CusEmoteTabMessageProvider());
        RongIM.registerMessageTemplate(new NewsCardProvider());
        RongIM.registerMessageTemplate(new SystemMessageProvider());
        RongIM.getInstance().enableNewComingMessageIcon(true);
        RongIM.getInstance().enableUnreadMessageIcon(true);
        setMyExtensionModule();
    }

    private void initOSS() {
        String AK = "LTAI3V54BzteDdTi";
        String SK = "h59RLWudf6XMXO4bSqSOwsK3nBHXSK";
        OSSPlainTextAKSKCredentialProvider ossPlainTextAKSKCredentialProvider =
                new OSSPlainTextAKSKCredentialProvider(AK, SK);
        String endpoint;
        if (BuildConfig.DEBUG) {
            endpoint = "oss-cn-beijing.aliyuncs.com";
        } else {
            endpoint = "oss-cn-hongkong.aliyuncs.com";
        }
        oss = new OSSClient(this, endpoint, ossPlainTextAKSKCredentialProvider);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageUtil.getInstance(base).setLocal(base));
    }

    @Override
    public void onTerminate() {
        Constant.clear();
        super.onTerminate();
    }
}
