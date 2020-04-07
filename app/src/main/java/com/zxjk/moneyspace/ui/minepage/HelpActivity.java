package com.zxjk.moneyspace.ui.minepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;


/**
 * 帮助中心
 */
public class HelpActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setLightStatusBar(false);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.help_center));
        tv_title.setTextColor(ContextCompat.getColor(this, R.color.white));

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ImageView imageView = findViewById(R.id.ivBack);
        imageView.setImageResource(R.drawable.ico_back_white);

        findViewById(R.id.rlTitle).setBackgroundColor(ContextCompat.getColor(this, R.color.colorTheme));
    }

    public void jump(View view) {
        finish();
        startActivity(new Intent(this, OnlineServiceActivity.class));
    }
}
