package com.zxjk.duoduo.export;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.ui.WelcomeActivity;

public class ExportLauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();

        if (TextUtils.isEmpty(Constant.userId)) {
            intent.setClass(this, WelcomeActivity.class);
            intent.putExtra("export", true);
        } else {
            intent.setClass(this, ExportConfirmOrderActivty.class);
        }

        startActivity(intent);
        finish();
    }
}
