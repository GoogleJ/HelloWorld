package com.zxjk.duoduo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.Lifecycle;

import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.AddFriendDetailsActivity;
import com.zxjk.duoduo.ui.msgpage.FriendDetailsActivity;
import com.zxjk.duoduo.ui.widget.dialog.LoadingDialog;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;

@SuppressLint("CheckResult")
public class CommonUtils {
    private static WeakReference<LoadingDialog> loadingDialog = new WeakReference<>(null);

    public static LoadingDialog initDialog(Context context) {
        return initDialog(context, null);
    }

    public static LoadingDialog initDialog(Context context, String loadText) {
        LoadingDialog d = loadingDialog.get();
        if (d != null) {
            d.dismissReally();
            loadingDialog.clear();
        }
        d = new LoadingDialog(context, loadText);
        loadingDialog = new WeakReference<>(d);
        return d;
    }

    public static void destoryDialog() {
        LoadingDialog d = loadingDialog.get();
        if (d != null) {
            d.dismiss();
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2dp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int getSex(String sex) {
        if (TextUtils.isEmpty(sex)) {
            return R.string.male;
        }
        return sex.equals("0") ? R.string.male : R.string.female;
    }

    public static int getAuthenticate(String authenticate) {
        if (TextUtils.isEmpty(authenticate)) {
            return R.string.authen_false;
        }
        return authenticate.equals("0") ? R.string.authen_true : ((authenticate.equals("1") ? R.string.authen_false : R.string.verifing));
    }

    public static String timeStamp2Date(String time) {
        Long timeLong = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//要转换的时间格式
        Date date;
        try {
            date = sdf.parse(sdf.format(timeLong));
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * double乘法
     *
     * @param value1
     * @param value2
     * @return
     */
    public static double mul(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(String.valueOf(value1));
        BigDecimal b2 = new BigDecimal(String.valueOf(value2));
        return b1.multiply(b2).doubleValue();
    }


    public static String getVersionName(Context context) {
        String name = "";
        try {
            name = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static void resolveFriendList(BaseActivity activity, String friendId, String groupId, boolean finish) {
//        if (Constant.friendsList == null) {
        LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(activity);
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .compose(RxSchedulers.normalTrans())
                .doOnNext(friendInfoResponses -> {
                    for (FriendInfoResponse f : friendInfoResponses) {
                        RongUserInfoManager.getInstance().setUserInfo(new UserInfo(f.getId(), TextUtils.isEmpty(f.getRemark()) ? f.getNick() : f.getRemark(), Uri.parse(f.getHeadPortrait())));
                    }
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(activity)))
                .subscribe(friendInfoResponses -> {
//                        Constant.friendsList = friendInfoResponses;
                    handleFriendList(friendInfoResponses, activity, friendId, groupId, finish);
                }, activity::handleApiError);
//        } else {
//            handleFriendList(activity, friendId, groupId, finish);
//        }
    }

    public static void resolveFriendList(BaseActivity activity, String friendId, boolean finish) {
        resolveFriendList(activity, friendId, "", finish);
    }

    public static void resolveFriendList(BaseActivity activity, String friendId) {
        resolveFriendList(activity, friendId, false);
    }

    public static void resolveFriendList(BaseActivity activity, String friendId, String groupId) {
        resolveFriendList(activity, friendId, groupId, false);
    }

    private static void handleFriendList(List<FriendInfoResponse> friendInfoResponses, BaseActivity activity, String userId, String groupId, boolean finish) {
        if (finish) {
            activity.finish();
        }
        if (userId.equals(Constant.userId)) {
            //扫到了自己
            Intent intent = new Intent(activity, FriendDetailsActivity.class);
            intent.putExtra("friendId", userId);
            activity.startActivity(intent);
            return;
        }
        for (FriendInfoResponse f : friendInfoResponses) {
            if (f.getId().equals(userId)) {
                //自己的好友，进入详情页（可聊天）
                Intent intent = new Intent(activity, FriendDetailsActivity.class);
                intent.putExtra("friendResponse", f);
                activity.startActivity(intent);
                return;
            }
        }

        //陌生人，进入加好友页面
        Intent intent = new Intent(activity, AddFriendDetailsActivity.class);
        intent.putExtra("friendId", userId);
        intent.putExtra("groupId", groupId);
        activity.startActivity(intent);
    }

}
