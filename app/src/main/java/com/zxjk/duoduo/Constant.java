package com.zxjk.duoduo;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.blankj.utilcode.util.DeviceUtils;
import com.zxjk.duoduo.bean.response.CreateWalletResponse;
import com.zxjk.duoduo.bean.response.LoginResponse;

import java.util.Locale;

import io.rong.imlib.model.Message;

public class Constant {
    //省市区数据
    public static final String CITY_DATA = "china_city_data.json";
    //阿里OSS上传地址
    public static final String OSS_URL = "https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/";
//    public static final String BASE_URL = "https://mochart.ztoken.cn";
            public static final String BASE_URL = "https://192.168.1.73:8085/";
//    public static final String BASE_URL = "https://duoduoweb.zzgb.net.cn";
    public static final String APP_CODE = "fb0e95b069f74f29a2f972f9454d7d1a";

    public static final int CODE_SUCCESS = 0;

    public static final int CODE_UNLOGIN = 601;

    public static final String FLAG_FIRSTLOGIN = "0";
    public static String HEAD_LOCATION = "86";
    public static String userId = "";
    public static String token = "";
    public static String phoneUuid =
            TextUtils.isEmpty(DeviceUtils.getMacAddress()) ? DeviceUtils.getAndroidID() : DeviceUtils.getMacAddress();
    public static String language = Locale.getDefault().toString().replace("_", "-");

    public static String authentication = "";

    public static LoginResponse currentUser = new LoginResponse();

    public static final String LOCAL_CHANNEL_ID = "rc_notification_id";

    public static int messageCount = 0;

    public static CreateWalletResponse walletResponse;
    //    public static List<FriendInfoResponse> friendsList;
    public static Message tempMsg;

    //分享群二维码
    public static Bitmap shareGroupQR;
    public static final String ACTION_BROADCAST1 = "Action:Broadcast:blockWalletCreated";
    public static final String ACTION_BROADCAST2 = "Action:Broadcast:rongMsgArrive";

    public static final String regUrl = "(([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[\\-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9\\.\\-]+|(?:www\\.|[\\-;:&=\\+\\$,\\w]+@)[A-Za-z0-9\\.\\-]+)((?:\\/[\\+~%\\/\\.\\w\\-_]*)?\\??(?:[\\-\\+=&;%@\\.\\w_]*)#?(?:[\\.\\!\\/\\\\\\w]*))?";

    public static void clear() {
        Constant.token = "";
        Constant.userId = "";
        Constant.phoneUuid = "";
        currentUser = null;
        walletResponse = null;
//        friendsList = null;
        tempMsg = null;
        shareGroupQR = null;
    }

}
