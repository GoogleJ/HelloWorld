package com.zxjk.duoduo.ui.minepage;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class CooperateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooperate);
    }

    public void confirm(View view) {
        ToastUtils.showShort(R.string.developing);
    }

    public void back(View view) {
        finish();
    }
}
