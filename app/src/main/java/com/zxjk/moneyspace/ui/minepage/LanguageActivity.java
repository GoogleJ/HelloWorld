package com.zxjk.moneyspace.ui.minepage;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.HomeActivity;
import com.zxjk.moneyspace.ui.SaasLoginSelectActivity;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.LanguageUtil;

public class LanguageActivity extends BaseActivity {

    private TextView tvEnglish;
    private TextView tvChinese;
    private LinearLayout llEnglish;
    private LinearLayout llChinese;
    private TextView tvEnd;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        tvChinese = findViewById(R.id.tv_chinese);
        tvEnglish = findViewById(R.id.tv_english);
        llChinese = findViewById(R.id.ll_chinese);
        llEnglish = findViewById(R.id.ll_english);
        tvEnd = findViewById(R.id.tv_end);

        if ("english".equals(LanguageUtil.getInstance(this).getCurrentLanguage())) {
            tvChinese.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tvEnglish.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            tvChinese.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvEnglish.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        llChinese.setOnClickListener(v -> {
            tvChinese.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvEnglish.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            type = 0;
        });

        llEnglish.setOnClickListener(v -> {
            tvChinese.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tvEnglish.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            type = 1;
        });

        tvEnd.setOnClickListener(v -> {
            if(type == 0){
                LanguageUtil.getInstance(LanguageActivity.this).changeLanguage(LanguageUtil.CHINESE);
            }else if(type == 1){
                LanguageUtil.getInstance(LanguageActivity.this).changeLanguage(LanguageUtil.ENGLISH);
            }
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        });
    }
}
