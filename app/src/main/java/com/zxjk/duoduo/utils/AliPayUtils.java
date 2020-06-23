package com.zxjk.duoduo.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import java.net.URLEncoder;

public class AliPayUtils {

    public static boolean startAlipayClient(Activity activity, String urlCode) {
        return startIntentUrl(activity, doFormUri(urlCode));
    }

    private static String doFormUri(String urlCode) {
        try {
            urlCode = URLEncoder.encode(urlCode, "utf-8");
        } catch (Exception e) {
        }
        final String alipayqr = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + urlCode;
        String openUri = alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis();
        return openUri;
    }

    private static boolean startIntentUrl(Activity activity, String intentFullUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(intentFullUrl));
            activity.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
