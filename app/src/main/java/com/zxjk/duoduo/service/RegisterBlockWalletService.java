package com.zxjk.duoduo.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.google.gson.Gson;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.bean.response.CreateWalletResponse;
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RegisterBlockWalletService extends Service {
    private String walletAddress = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Constant.currentUser == null) {
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        ServiceFactory.getInstance().getBaseService(Api.class)
                .createWallet(Constant.currentUser.getDuoduoId())
                .compose(RxSchedulers.normalTrans())
                .subscribeOn(Schedulers.io())
                .flatMap((Function<CreateWalletResponse, ObservableSource<BaseResponse<LoginResponse>>>) s -> {
                    walletAddress = s.getWalletAddress();
                    LoginResponse loginResponse = new LoginResponse(Constant.currentUser.getId());
                    loginResponse.setWalletAddress(walletAddress);
                    return ServiceFactory.getInstance().getBaseService(Api.class)
                            .updateUserInfo(new Gson().toJson(loginResponse));
                })
                .subscribe(s -> {
                    Constant.currentUser.setWalletAddress(walletAddress);
                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);

                    stopSelf();
                }, t -> Observable.timer(20, TimeUnit.SECONDS)
                        .subscribe(l -> {
                            if (AppUtils.isAppForeground()) startService(intent);
                        }));

        return super.onStartCommand(intent, flags, startId);
    }
}
