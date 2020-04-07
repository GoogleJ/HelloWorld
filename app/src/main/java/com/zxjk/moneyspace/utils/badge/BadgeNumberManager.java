package com.zxjk.moneyspace.utils.badge;

import android.content.Context;
import android.os.Build;

public class BadgeNumberManager {

    private Context mContext;

    static {
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase(MobileBrand.HUAWEI)) {
            IMPL = new ImplHuaWei();
        } else if (manufacturer.equalsIgnoreCase(MobileBrand.XIAOMI)) {
            IMPL = new ImplXiaoMi();
        } else if (manufacturer.equalsIgnoreCase(MobileBrand.VIVO)) {
            IMPL = new ImplVIVO();
        } else if (manufacturer.equalsIgnoreCase(MobileBrand.OPPO)) {
            IMPL = new ImplOPPO();
        } else {
            IMPL = new ImplBase();
        }
    }

    private BadgeNumberManager(Context context) {
        mContext = context;
    }

    public static BadgeNumberManager from(Context context) {
        return new BadgeNumberManager(context);
    }

    private static final Impl IMPL;

    public void setBadgeNumber(int number) {
        IMPL.setBadgeNumber(mContext, number);
    }

    interface Impl {

        void setBadgeNumber(Context context, int number);

    }

    static class ImplHuaWei implements Impl {

        @Override
        public void setBadgeNumber(Context context, int number) {
            BadgeNumberManagerHuaWei.setBadgeNumber(context, number);
        }
    }

    static class ImplXiaoMi implements Impl {

        @Override
        public void setBadgeNumber(Context context, int number) {
            //小米机型的桌面应用角标API跟通知绑定在一起了，所以单独做处理
        }
    }

    static class ImplVIVO implements Impl {

        @Override
        public void setBadgeNumber(Context context, int number) {
            BadgeNumberManagerVIVO.setBadgeNumber(context, number);
        }
    }

    static class ImplOPPO implements Impl {

        @Override
        public void setBadgeNumber(Context context, int number) {
            BadgeNumberManagerOPPO.setBadgeNumber(context, number);
        }
    }


    static class ImplBase implements Impl {

        @Override
        public void setBadgeNumber(Context context, int number) {

        }
    }
}
