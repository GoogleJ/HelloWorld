package com.zxjk.moneyspace.ui.findpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseFragment;

public class FindFragment extends BaseFragment {
    private ViewPager pager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initView(inflater, container);

        initPager();

        return rootView;
    }

    private void initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_find, container, false);
        pager = rootView.findViewById(R.id.pager);
    }

    private void initPager() {
        pager = rootView.findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return new NewsPager();
            }

            @Override
            public int getCount() {
                return 1;
            }
        });
    }
}
