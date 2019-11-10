package com.zxjk.duoduo.network.rx;

import android.app.Dialog;

import com.zxjk.duoduo.bean.response.BaseResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.zxjk.duoduo.Constant.CODE_SUCCESS;
import static com.zxjk.duoduo.Constant.CODE_UNLOGIN;

public class RxSchedulers {

    //不带对话框的网络请求
    public static <T> ObservableTransformer<T, T> ioObserver() {
        return ioObserver(null);
    }

    //带对话框的网络请求
    public static <T> ObservableTransformer<T, T> ioObserver(Dialog d) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    if (d == null) return;
                    d.show();
                })
                .doOnDispose(() -> {
                    if (d != null) d.dismiss();
                })
                .doOnNext(t -> {
                    if (d != null) d.dismiss();
                })
                .doOnError(t -> {
                    if (d != null) d.dismiss();
                })
                .doOnComplete((Action) () -> {
                    if (d != null) d.dismiss();
                });
    }

    //返回结果预处理
    public static <T> ObservableTransformer<BaseResponse<T>, T> normalTrans() {
        return upstream -> upstream.flatMap((Function<BaseResponse<T>, ObservableSource<T>>) response -> {
            if (response.code == CODE_SUCCESS) {
                return Observable.just(response.data);
            } else if (response.code == CODE_UNLOGIN) {
                return Observable.error(new RxException.DuplicateLoginExcepiton("重复登录"));
            } else {
                return Observable.error(new RxException.ParamsException(response.msg, response.code));
            }
        });
    }

}
