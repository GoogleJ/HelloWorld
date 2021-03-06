package com.zxjk.duoduo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class WelcomeActivity extends BaseActivity {

    private Disposable skipDisposable;

    @SuppressLint("CheckResult")
    public void checkUserState() {
        setContentView(R.layout.activity_welcome);

        if (null != MMKVUtils.getInstance().decodeParcelable("login")) {
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
            skipDisposable = Observable.timer(500, TimeUnit.MILLISECONDS)
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

    @SuppressLint("CheckResult")
    private void goLoginByServer() {
        LoginResponse login = MMKVUtils.getInstance().decodeParcelable("login");
        Constant.currentUser = login;
        Constant.token = login.getToken();
        Constant.userId = login.getId();

        Observable.timer(50, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .subscribe(l -> {
                    startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                    finish();
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.anim_welcome_exit);
    }
}
