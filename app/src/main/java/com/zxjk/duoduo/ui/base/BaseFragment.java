package com.zxjk.duoduo.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.jakewharton.rxbinding3.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle3.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.network.rx.RxException;
import com.zxjk.duoduo.ui.LoginActivity;
import com.zxjk.duoduo.utils.MMKVUtils;
import com.zxjk.duoduo.utils.TakePicUtil;

import java.io.File;

import io.rong.imkit.RongIM;


@SuppressLint("CheckResult")
public class BaseFragment extends RxFragment {
    public View rootView;

    private RxPermissions rxPermissions;

    public File corpFile;

    public void getPermisson(BaseActivity.PermissionResult result, String... permissions) {
        getPermisson(null, result, permissions);
    }

    public void getPermisson(View view, BaseActivity.PermissionResult result, String... permissions) {
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
                .subscribe(permission -> {
                    if (!permission.granted) ToastUtils.showShort("请开启相关权限");

                    if (null != result) result.onResult(permission.granted);
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        rxPermissions = new RxPermissions(this);
    }

    public void handleApiError(Throwable throwable) {
        MobclickAgent.reportError(Utils.getApp(), throwable);
        if (throwable.getCause() instanceof RxException.DuplicateLoginExcepiton ||
                throwable instanceof RxException.DuplicateLoginExcepiton) {
            // 重复登录，挤掉线
            RongIM.getInstance().logout();
            Constant.clear();
            MMKVUtils.getInstance().enCode("isLogin", false);

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        ToastUtils.showShort(RxException.getMessage(throwable));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        corpFile = null;
        corpFile = TakePicUtil.onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }
}
