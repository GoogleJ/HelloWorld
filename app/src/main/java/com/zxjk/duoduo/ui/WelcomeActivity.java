package com.zxjk.duoduo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class WelcomeActivity extends BaseActivity {

    private Disposable skipDisposable;
    private boolean attachAD;

    @SuppressLint("CheckResult")
    public void checkUserState() {
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.root).setOnClickListener(v -> {
            attachAD = true;
            ToastUtils.showShort(R.string.loading_pleasewait1);
        });

        if (MMKVUtils.getInstance().decodeBool("isLogin")) {
            goLoginByServer();
        } else {
            findViewById(R.id.tvSkip).setVisibility(View.VISIBLE);
            findViewById(R.id.tvSkip)
                    .setOnClickListener(v -> {
                        if (null != skipDisposable && !skipDisposable.isDisposed()) {
                            skipDisposable.dispose();
                            startActivity(new Intent(this, NewLoginActivity.class));
                            finish();
                        }
                    });
            skipDisposable = Observable.timer(2000, TimeUnit.MILLISECONDS)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver())
                    .subscribe(aLong -> {
                        startActivity(new Intent(this, NewLoginActivity.class));
                        finish();
                    });
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setEnableCheckConstant(false);

        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        BarUtils.setStatusBarVisibility(this, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        if (TextUtils.isEmpty(Constant.phoneUuid)) {
            Constant.phoneUuid = TextUtils.isEmpty(DeviceUtils.getMacAddress()) ? DeviceUtils.getAndroidID() : DeviceUtils.getMacAddress();
        }
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        checkUserState();
    }

    private void goLoginByServer() {
        LoginResponse login = MMKVUtils.getInstance().decodeParcelable("login");
        if (login != null) {
            Constant.currentUser = login;
            Constant.token = login.getToken();
            Constant.userId = login.getId();
            // 连接融云
            RongIM.connect(login.getRongToken(), new RongIMClient.ConnectCallback() {

                @Override
                public void onTokenIncorrect() {
                    MMKVUtils.getInstance().enCode("isLogin", false);
                    Constant.clear();
                    ToastUtils.showShort(getString(R.string.login_again));
                    Intent intent = new Intent(WelcomeActivity.this, NewLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onSuccess(String userid) {
                    UserInfo userInfo = new UserInfo(userid, Constant.currentUser.getNick(), Uri.parse(Constant.currentUser.getHeadPortrait()));
                    RongIM.getInstance().setCurrentUserInfo(userInfo);
                    startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                    finish();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    MMKVUtils.getInstance().enCode("isLogin", false);
                    Constant.clear();
                    ToastUtils.showShort(getString(R.string.login_again));
                    Intent intent = new Intent(WelcomeActivity.this, NewLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            ToastUtils.showShort(getString(R.string.login_again));
            MMKVUtils.getInstance().enCode("isLogin", false);
            startActivity(new Intent(this, NewLoginActivity.class));
            finish();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (attachAD) {
            intent.putExtra("attachAD", true);
        }
        super.startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.anim_welcome_exit);
    }
}
