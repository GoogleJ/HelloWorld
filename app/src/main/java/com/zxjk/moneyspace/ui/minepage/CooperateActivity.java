package com.zxjk.moneyspace.ui.minepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;

public class CooperateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooperate);
    }

    public void confirm(View view) {
        startActivity(new Intent(this, OnlineServiceActivity.class));
    }

    public void back(View view) {
        finish();
    }
}
