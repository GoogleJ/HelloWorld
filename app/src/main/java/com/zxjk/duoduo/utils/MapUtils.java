package com.zxjk.duoduo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.StringDef;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MapUtils {
    private static final String TIP_ERROR = "未安装相关应用或版本过低，请安装或更新后重试";

    public static final String PACKAGE_BAIDU = "com.baidu.BaiduMap";
    public static final String PACKAGE_GAODE = "com.autonavi.minimap";
    public static final String PACKAGE_TECENT = "com.tencent.map";

    private static Double lat;
    private static Double lon;

    @StringDef({PACKAGE_BAIDU, PACKAGE_GAODE, PACKAGE_TECENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MapType {
    }

    private static MapUtils instance;

    private MapUtils() {

    }

    public static MapUtils locate(Double latitude, Double longitude) {
        if (instance == null) {
            instance = new MapUtils();
        }
        lat = latitude;
        lon = longitude;
        return instance;
    }

    public void openMap(Context context, @MapType String map) {
        if (!isAvailable(map)) {
            ToastUtils.showShort(TIP_ERROR);
            return;
        }

        switch (map) {
            case PACKAGE_BAIDU:
                openBaiDu(context);
                break;
            case PACKAGE_GAODE:
                openGaoDe(context);
                break;
            case PACKAGE_TECENT:
                openTecent(context);
                break;
        }
    }

    private boolean isAvailable(String map) {
        return AppUtils.isAppInstalled(map);
    }

    private void openGaoDe(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("androidamap://navi?sourceApplication=zxjk&poiname=我的目的地&lat=" + lat + "&lon=" + lon + "&dev=0"));
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showShort(TIP_ERROR);
        }
    }

    private void openBaiDu(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("baidumap://map/direction?destination=latlng:" + lat + "," + lon + "|name:目的地&coord_type=bd09ll&mode=driving"));
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showShort(TIP_ERROR);
        }
    }

    private void openTecent(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("qqmap://map/routeplan?type=walk&to=目的地&tocoord=" + lat + "," + lon + "&policy=1&referer=myapp"));
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showShort(TIP_ERROR);
        }
    }
}
