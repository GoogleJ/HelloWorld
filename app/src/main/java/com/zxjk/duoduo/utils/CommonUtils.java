package com.zxjk.duoduo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

@SuppressLint("CheckResult")
public class CommonUtils {
    private static WeakReference<LoadingDialog> loadingDialog = new WeakReference<>(null);

    public static LoadingDialog initDialog(Context context) {
        return initDialog(context, null);
    }

    public static LoadingDialog initDialog(Context context, String loadText) {
        return initDialog(context, -1, loadText);
    }

    public static LoadingDialog initDialog(Context context, long timestamp) {
        return initDialog(context, timestamp, "");
    }

    public static LoadingDialog initDialog(Context context, long timestamp, String loadText) {
        LoadingDialog d = loadingDialog.get();
        if (d != null) {
            d.dismissReally();
            loadingDialog.clear();
        }
        d = new LoadingDialog(context, loadText);
        if (timestamp != -1) {
            d.setDelayTimeStamp(timestamp);
        }
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
        LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(activity);
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendInfoById(friendId)
                .compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(activity)))
                .subscribe(r -> handleFriendList(r, activity, friendId, groupId, finish),
                        activity::handleApiError);
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

    private static void handleFriendList(FriendInfoResponse friendInfo, BaseActivity activity, String userId, String groupId, boolean finish) {
        if (finish) {
            activity.finish();
        }
        if (userId.equals(Constant.userId)) {
            //自己
            Intent intent = new Intent(activity, FriendDetailsActivity.class);
            intent.putExtra("friendId", userId);
            activity.startActivity(intent);
            return;
        }

        if (!TextUtils.isEmpty(friendInfo.getIsFriend()) && friendInfo.getIsFriend().equals("1")) {
            //好友，进入详情页（可聊天）
            Intent intent = new Intent(activity, FriendDetailsActivity.class);
            intent.putExtra("friendId", userId);
            activity.startActivity(intent);
            return;
        }

        //陌生人，进入加好友页面
        Intent intent = new Intent(activity, AddFriendDetailsActivity.class);
        intent.putExtra("friendId", userId);
        intent.putExtra("groupId", groupId);
        activity.startActivity(intent);
    }

}
