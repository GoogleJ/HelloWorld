package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

/**
 * 关于多多
 */
@SuppressLint("SetTextI18n")
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    private void initView() {
        TextView tv_versionName = findViewById(R.id.tv_versionName);
        tv_versionName.setText(CommonUtils.getVersionName(this));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

}
