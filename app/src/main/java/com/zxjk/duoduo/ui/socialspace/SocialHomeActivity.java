package com.zxjk.duoduo.ui.socialspace;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class SocialHomeActivity extends BaseActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTrasnferStatusBar(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
    }
}
