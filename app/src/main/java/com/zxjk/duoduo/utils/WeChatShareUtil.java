package com.zxjk.duoduo.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.blankj.utilcode.util.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class WeChatShareUtil {

    public static IWXAPI wxShare;

    @SuppressLint("CheckResult")
    public static void shareImg(BaseActivity activity, int targetScene, Bitmap bitmap) {
        if (!wxShare.isWXAppInstalled()) {
            ToastUtils.showShort("您还没有安装微信");
            return;
        }

        Observable.create((ObservableOnSubscribe<SendMessageToWX.Req>) emitter -> {
            Bitmap bmp;
            if (bitmap == null) {
                bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.shareimg_wechat);
            } else {
                bmp = bitmap;
            }
            Bitmap mBp = Bitmap.createScaledBitmap(bmp, 540, 960, true);

            WXImageObject imgObj = new WXImageObject(mBp);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;
            msg.title = "多多优社";
            mBp.recycle();
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("img");
            req.message = msg;
            req.scene = targetScene;

            emitter.onNext(req);
        }).compose(activity.bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(activity, "处理图片中")))
                .subscribe(req -> wxShare.sendReq(req));
    }

    @SuppressLint("CheckResult")
    public static void shareImg(BaseActivity activity, int targetScene) {
        shareImg(activity, targetScene, null);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
