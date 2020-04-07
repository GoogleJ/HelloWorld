package com.zxjk.moneyspace.ui;

import android.content.Context;
import io.rong.push.PushType;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

public class RongPushMessageReceiver extends PushMessageReceiver {


    @Override
    public boolean onNotificationMessageArrived(Context context, PushType pushType, PushNotificationMessage message) {
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushType pushType, PushNotificationMessage pushNotificationMessage) {
        return false;
    }

}
