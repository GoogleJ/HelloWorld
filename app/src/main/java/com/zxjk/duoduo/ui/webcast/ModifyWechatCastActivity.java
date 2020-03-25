package com.zxjk.duoduo.ui.webcast;

import android.os.Bundle;
import android.view.View;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class ModifyWechatCastActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_wechat_cast);
    }

    public void deleteCast(View view) {
    }

    public void back(View view) {
        finish();
    }

    public void selectTime(View view) {
    }
}
