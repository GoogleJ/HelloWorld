package com.zxjk.duoduo.ui.externalfunc;

import android.os.Bundle;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class PayConfirmActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirm);

        String orderId = getIntent().getStringExtra("orderId");
    }
}
