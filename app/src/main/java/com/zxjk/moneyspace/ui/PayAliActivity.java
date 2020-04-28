package com.zxjk.moneyspace.ui;

import android.os.Bundle;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;

public class PayAliActivity extends BaseActivity {

    private String qrdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_ali);

        qrdata = getIntent().getStringExtra("qrdata");

    }
}
