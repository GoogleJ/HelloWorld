package com.zxjk.duoduo;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.blankj.utilcode.util.DeviceUtils;
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.Locale;

import io.rong.imlib.model.Message;

public class Constant {
    //省市区数据
    public static final String CITY_DATA = "china_city_data.json";
    //    阿里OSS上传地址
//    public static final String OSS_URL = "https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/";  //release
//    public static final String BASE_URL = "https://mochart.ztoken.cn";  //release
    public static final String OSS_URL = "http://zhongxingjike1.oss-cn-beijing.aliyuncs.com/upload/"; //debug
    public static final String BASE_URL = "http://192.168.1.74:8085/";  //26g  74w  191h
//    public static final String BASE_URL = "http://47.93.180.210:8085/"; //test

    public static final String APP_CODE = "fb0e95b069f74f29a2f972f9454d7d1a";
    public static final String APP_DOWNLOAD_URL = "https://fkr.one/hilamg";
    public static final String APP_SHARE_URL = "http://hilamg-share-test.zhumengxuanang.com/?";

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_UNLOGIN = 601;

    public static final int CODE_OTC_SUCCESS = 200;
    public static final int CODE_OTC_TIMEOUT = 60030;

    //628活动链接地址
    public static final String URL_628ACTIVITY = "http://hilamg-otc-test.zhumengxuanang.com";

    public static final String LOCAL_CHANNEL_ID = "rc_notification_id";
    public static final String ACTION_BROADCAST1 = "Action:Broadcast:blockWalletCreated";
    public static final String ACTION_BROADCAST2 = "Action:Broadcast:rongMsgArrive";
    public static final String regUrl = "^[\\s\\S]*(http[s]?:\\/\\/)?([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?[\\s\\S]*$";
    public static final String speChat = ".*<.*|.*>.*";
    public static String HEAD_LOCATION = "86";
    public static String userId = "";
    public static String token = "";
    public static String phoneUuid =
            TextUtils.isEmpty(DeviceUtils.getMacAddress()) ? DeviceUtils.getAndroidID() : DeviceUtils.getMacAddress();
    public static String language = Locale.getDefault().toString().replace("_", "-");
    public static String authentication = "";
    public static LoginResponse currentUser = new LoginResponse();
    public static int messageCount = 0;
    public static Message tempMsg;
    public static Bitmap shareGroupQR;

    public static void clear() {
        Constant.token = "";
        Constant.userId = "";
        Constant.phoneUuid = "";
        currentUser = null;
        tempMsg = null;
        shareGroupQR = null;
        MMKVUtils.getInstance().remove("login");
    }

}
