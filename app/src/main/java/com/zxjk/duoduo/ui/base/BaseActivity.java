package com.zxjk.duoduo.ui.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.jakewharton.rxbinding3.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.DaoMaster;
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.db.OpenHelper;
import com.zxjk.duoduo.network.rx.RxException;
import com.zxjk.duoduo.ui.NewLoginActivity;
import com.zxjk.duoduo.utils.LanguageUtil;
import com.zxjk.duoduo.utils.MMKVUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import top.zibin.luban.Luban;

@SuppressLint({"CheckResult", "Registered"})
public class BaseActivity extends RxAppCompatActivity {
    private boolean enableTouchHideKeyBoard = true;
    public RxPermissions rxPermissions = new RxPermissions(this);
    public File corpFile;

    public interface PermissionResult {
        void onResult(boolean granted);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Constant.currentUser == null || TextUtils.isEmpty(Constant.token) || TextUtils.isEmpty(Constant.userId)) {
            if (MMKVUtils.getInstance().decodeBool("isLogin")) {
                long date2 = TimeUtils.getNowMills();
                long date1 = MMKVUtils.getInstance().decodeLong("date1");
                if ((date2 - date1) / (24 * 60 * 60 * 1000) < 7) {
                    LoginResponse login = MMKVUtils.getInstance().decodeParcelable("login");
                    Constant.currentUser = login;
                    Constant.token = login.getToken();
                    Constant.userId = login.getId();
                    if (RongIM.getInstance().getCurrentConnectionStatus()
                            != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED || RongIM.getInstance().getCurrentConnectionStatus()
                            != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING) {
                        RongIM.connect(login.getRongToken(), new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {
                                MMKVUtils.getInstance().enCode("isLogin", false);
                                Constant.clear();
                                ToastUtils.showShort(getString(R.string.login_again));
                                Intent intent = new Intent(BaseActivity.this, NewLoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onSuccess(String userid) {
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                MMKVUtils.getInstance().enCode("isLogin", false);
                                Constant.clear();
                                ToastUtils.showShort(getString(R.string.login_again));
                                Intent intent = new Intent(BaseActivity.this, NewLoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                } else {
                    ToastUtils.showShort(getString(R.string.login_again));
                    MMKVUtils.getInstance().enCode("isLogin", false);
                    startActivity(new Intent(this, NewLoginActivity.class));
                    finish();
                }
            }
        }
//        if (Application.daoSession == null) {
//            OpenHelper open = new
//                    OpenHelper(Utils.getApp(), Constant.currentUser.getId(), null);
//            Application.daoSession = new DaoMaster(open.getWritableDatabase()).newSession();
//        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            fixOrientation();
        }
        setLightStatusBar(true);
        super.onCreate(savedInstanceState);
    }

    public void setEnableTouchHideKeyBoard(boolean enableTouchHideKeyBoard) {
        this.enableTouchHideKeyBoard = enableTouchHideKeyBoard;
    }

    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    private void fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    public void setLightStatusBar(boolean isLight) {
        if (isLight) {
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorTheme));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public void setTrasnferStatusBar(boolean isTransfer) {
        if (isTransfer) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorTheme));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public void getPermisson(PermissionResult result, String... permissions) {
        getPermisson(null, result, permissions);
    }

    public void getPermisson(View view, PermissionResult result, String... permissions) {
        if (view != null) {
            RxView.clicks(view)
                    .compose(rxPermissions.ensureEachCombined(permissions))
                    .subscribe(permission -> {
                        if (!permission.granted) ToastUtils.showShort("请开启相关权限");

                        if (null != result) result.onResult(permission.granted);
                    });
            return;
        }
        rxPermissions.requestEachCombined(permissions)
                .subscribe(granted -> {
                    if (!granted.granted) ToastUtils.showShort("请开启相关权限");

                    if (null != result) result.onResult(granted.granted);
                });
    }

    public void handleApiError(Throwable throwable) {
        MobclickAgent.reportError(Utils.getApp(), throwable);
        if (throwable.getCause() instanceof RxException.DuplicateLoginExcepiton ||
                throwable instanceof RxException.DuplicateLoginExcepiton) {
            // 重复登录，挤掉线
            back2Login();
        }
        ToastUtils.showShort(RxException.getMessage(throwable));
    }

    public void back2Login() {
        if (getLocalClassName().equals("NewLoginActivity")) return;
        RongIM.getInstance().logout();
        Constant.clear();
        MMKVUtils.getInstance().enCode("isLogin", false);

        Intent intent = new Intent(BaseActivity.this, NewLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public interface OnZipFileFinish {
        void onFinish(List<File> result);
    }

    public void zipFile(List<String> files, OnZipFileFinish onFinish) {
        Observable.just(files)
                .observeOn(Schedulers.io())
                .map(origin -> Luban.with(Utils.getApp()).load(origin).get())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(result -> {
                    if (onFinish != null) onFinish.onFinish(result);
                });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        corpFile = null;
        corpFile = TakePicUtil.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        KeyboardUtils.hideSoftInput(this);
        super.finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (enableTouchHideKeyBoard && ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS
                );
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // Return whether touch the view.
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtil.getInstance(newBase).setLocal(newBase));
    }
}
