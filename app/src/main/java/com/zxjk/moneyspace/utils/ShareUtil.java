package com.zxjk.moneyspace.utils;

import android.app.Activity;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zxjk.moneyspace.R;

public class ShareUtil {

    public static void shareImg(Activity activity, UMImage image) {
        shareImg(activity, image, new ShareListener(), SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
    }

    public static void shareLink(Activity activity, UMWeb link) {
        shareLink(activity, link, new ShareListener(), SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
    }

    public static void shareImg(Activity activity, UMImage image, ShareListener listener) {
        shareImg(activity, image, listener, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
    }

    public static void shareLink(Activity activity, UMWeb link, ShareListener listener) {
        shareLink(activity, link, listener, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
    }

    public static void shareImg(Activity activity, UMImage image, ShareListener listener, SHARE_MEDIA... var1) {
        new ShareAction(activity).withMedia(image).setDisplayList(var1)
                .setCallback(listener).open();
    }

    public static void shareLink(Activity activity, UMWeb link, ShareListener listener, SHARE_MEDIA... var1) {
        new ShareAction(activity).withMedia(link).setDisplayList(var1)
                .setCallback(listener).open();
    }

    public static class ShareListener implements UMShareListener {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {

        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            ToastUtils.showShort("分享失败，请稍后重试");
            MobclickAgent.reportError(Utils.getApp(), throwable);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    }
}
