package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.security.cloud.build.O;
import com.google.android.material.tabs.TabLayout;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

public class EcologyActivity extends BaseActivity {
    private String[] ecologyByTypes = {"金融", "公益", "商城", "旅游", "游戏", "生活服务"};

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
