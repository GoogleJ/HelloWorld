package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

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
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_versionName = findViewById(R.id.tv_versionName);
        tv_versionName.setText(CommonUtils.getVersionName(this));
        tv_title.setText(getString(R.string.about_duo_duo));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

}
