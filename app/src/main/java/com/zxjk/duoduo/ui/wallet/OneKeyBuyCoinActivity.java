package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class OneKeyBuyCoinActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int mTitles[] = {R.string.to_buy, R.string.to_sell};

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key_buy_coin);
        setTrasnferStatusBar(true);

        initView();

        initData();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        findViewById(R.id.rl_end).setOnClickListener(v -> startActivity(new Intent(this, OrderInfoByTypeActivity.class)));
    }

    private void initData() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return position == 0 ? new BuyCoinFragment() : new SellCoinFragment();
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getString(mTitles[position]);
            }
        });

        for (int i = 0; i < 2; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View inflate = View.inflate(this, R.layout.tablayout_title_text, null);
            TextView textView = inflate.findViewById(R.id.text1);
            textView.setText(mTitles[i]);
            tab.setCustomView(textView);
            textView.setGravity(Gravity.BOTTOM);
            tabLayout.addTab(tab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                View view = tab.getCustomView();
                if (null == view) {
                    tab.setCustomView(R.layout.tablayout_title_text);
                }
                TextView textView = tab.getCustomView().findViewById(R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.white, null));
                textView.setText(tab.getText());
                textView.setTextAppearance(R.style.TabLayoutTextSize);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (null == view) {
                    tab.setCustomView(R.layout.tablayout_title_text);
                }
                TextView textView = tab.getCustomView().findViewById(R.id.text1);
                textView.setText(tab.getText());
                textView.setTextColor(Color.parseColor("#7CA9E3"));
                textView.setTextAppearance(R.style.TabLayoutTextSize_two);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        tabLayout.setupWithViewPager(viewPager);

    }
}
