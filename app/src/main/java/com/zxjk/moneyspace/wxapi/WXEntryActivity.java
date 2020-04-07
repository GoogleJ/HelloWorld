package com.zxjk.moneyspace.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.umeng.socialize.weixin.view.WXCallbackActivity;
import com.zxjk.moneyspace.R;

public class WXEntryActivity extends WXCallbackActivity {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
    }

}
