package com.zxjk.moneyspace.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.zxjk.moneyspace.R;

public class GlideUtil {
    private static final int LOAD_CIRCLE = 1;
    private static final int LOAD_CORNER = 2;
    private static final int LOAD_SOURCE = 3;

    public static void loadNormalImg(ImageView iv, String url) {
        loadImage(iv, url, 0, 0, null);
    }

    public static void loadNormalImg(ImageView iv, String url, RequestListener<Bitmap> listener) {
        loadImage(iv, url, 0, 0, listener);
    }

    public static void loadCircleImg(ImageView iv, String url) {
        loadImage(iv, url, LOAD_CIRCLE, 0, null);
    }

    public static void loadCircleImg(ImageView iv, String url, RequestListener<Bitmap> listener) {
        loadImage(iv, url, LOAD_CIRCLE, 0, listener);
    }

    public static void loadCornerImg(ImageView iv, String url, int radius) {
        loadImage(iv, url, LOAD_CORNER, radius, null);
    }

    public static void loadCornerImg(ImageView iv, String url, int radius, RequestListener<Bitmap> listener) {
        loadImage(iv, url, LOAD_CORNER, radius, listener);
    }

    //加载图片实现方法 Glide 可设置加载回调返回bitmap
    private static void loadImage(ImageView iv, String url, int mode, int radius, RequestListener<Bitmap> listener) {
        if (TextUtils.isEmpty(url)) {
            iv.setImageResource(R.drawable.errorimg_head);
            return;
        }

        RequestManager manager = Glide.with(iv.getContext());
        if (listener != null) {
            manager.asBitmap().listener(listener)
                    .load(url).apply(getRequestOptions(iv.getContext(), mode, radius)).into(iv);
            return;
        }
        manager.load(url).transition(DrawableTransitionOptions.withCrossFade()).apply(getRequestOptions(iv.getContext(), mode, radius)).into(iv);
    }

    private static RequestOptions getRequestOptions(Context context, int mode, int radius) {
        switch (mode) {
            case LOAD_CIRCLE:
                return RequestOptions.bitmapTransform(new CircleCrop()).error(R.drawable.errorimg_head);
            case LOAD_CORNER:
                return new RequestOptions()
                        .error(R.drawable.errorimg_head)
                        .transform(new CenterCrop(), new RoundedCorners(CommonUtils.dip2px(context, radius)));
            case LOAD_SOURCE:
                return new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .error(R.drawable.errorimg_head);
            default:
                return new RequestOptions()
                        .error(R.drawable.errorimg_head);
        }
    }
}
