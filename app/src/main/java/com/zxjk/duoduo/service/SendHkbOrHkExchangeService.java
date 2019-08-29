package com.zxjk.duoduo.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;

import io.reactivex.schedulers.Schedulers;

public class SendHkbOrHkExchangeService extends Service {

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
        String arg1 = intent.getStringExtra("arg1");
        String arg2 = intent.getStringExtra("arg2");
        String arg3 = intent.getStringExtra("arg3");
        String arg4 = intent.getStringExtra("arg4");
        ServiceFactory.getInstance().getBaseService(Api.class)
                .sendHkbOrHkExchange(arg1, arg2, arg3, arg4)
                .subscribeOn(Schedulers.io())
                .subscribe(s -> stopSelf(), t -> stopSelf());
        return super.onStartCommand(intent, flags, startId);
    }
}
