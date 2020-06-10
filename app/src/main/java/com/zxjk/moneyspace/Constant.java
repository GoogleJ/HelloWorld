package com.zxjk.moneyspace;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.blankj.utilcode.util.DeviceUtils;
import com.zxjk.moneyspace.bean.response.LoginResponse;

import java.util.Locale;

import io.rong.imlib.model.Message;

public class Constant {
    //省市区数据
    public static final String CITY_DATA = "china_city_data.json";
    //    阿里OSS上传地址
    public static final String BASE_URL = "https://moneyspace.ztok.net/";  //release
//    public static final String BASE_URL = "http://192.168.1.74:8086/";
    public static final String OSS_URL = "https://moneyspace.oss-cn-hongkong.aliyuncs.com/upload/";

    public static final String SECRET = "D1230808098DE048DB81365E714B01B8";
    public static final String APP_CODE = "fb0e95b069f74f29a2f972f9454d7d1a";
    public static final String APP_DOWNLOAD_URL = "https://f.mokerr.com/moneyspace";
    public static final String APP_SHARE_URL = "http://moneyspace-share.ztok.net/?";

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_UNLOGIN = 601;

    public static final int CODE_OTC_SUCCESS = 200;
    public static final int CODE_OTC_TIMEOUT = 60030;

    public static final String LOCAL_CHANNEL_ID = "rc_notification_id";
    public static final String ACTION_BROADCAST2 = "Action:Broadcast:rongMsgArrive";
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
    //分享群二维码
    public static Bitmap shareGroupQR;
    public static String defaultRenegeNumber = "";

    public static void clear() {
        Constant.token = "";
        Constant.userId = "";
        Constant.phoneUuid = "";
        currentUser = null;
        tempMsg = null;
        shareGroupQR = null;
        defaultRenegeNumber = "";
    }

}
