package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;

public class EcologyActivity extends BaseActivity {
    private int[] ecologyByTypes = {R.string.financial, R.string.public_benefit, R.string.mall, R.string.travel, R.string.game, R.string.life_services};

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecology);

        initView();

        initData();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tab_ecology_by_type);
        viewPager = findViewById(R.id.ecology_view_pager);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    @SuppressLint("CheckResult")
    private void initData() {

                    viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ) {
                        @NonNull
                        @Override
                        public Fragment getItem(int position) {
                            EcologyFragment ecologyFragment = EcologyFragment.newInstance(position);
                            return ecologyFragment;
                        }

            @Override
            public int getCount() {
                return ecologyByTypes.length;
            }
        });


        tabLayout.setupWithViewPager(viewPager);
        tabLayout.removeAllTabs();
        for (int i = 0; i < ecologyByTypes.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(ecologyByTypes[i]));
        }
    }
}
