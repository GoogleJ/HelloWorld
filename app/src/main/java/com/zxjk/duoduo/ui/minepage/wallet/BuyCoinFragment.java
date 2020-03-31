package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.tabs.TabLayout;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetSymbolInfo;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.ArrayList;

@SuppressLint("CheckResult")
public class BuyCoinFragment extends BaseFragment {

    private TabLayout buyTabLayout;

    private ViewPager buyViewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<GetSymbolInfo.SymbolInfoBean> symbolInfoBean = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_buy_coin, container, false);

        initView();

        initData();

        return rootView;
    }

    private void initView() {
        buyTabLayout = rootView.findViewById(R.id.buy_tabLayout);
        buyViewPager = rootView.findViewById(R.id.buy_viewpager);
    }

    private void initData() {

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getSymbolInfo()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                .subscribe(response -> {
                    symbolInfoBean.addAll(response.getSymbolInfo());
                    setPagerTitle(response);
                });

    }

    private void setPagerTitle(GetSymbolInfo getSymbolInfo) {
        for (int i = 0; i < getSymbolInfo.getSymbolInfo().size(); i++) {
            BuyCoinViewPagerFragment fragment = BuyCoinViewPagerFragment.newInstance(getSymbolInfo.getSymbolInfo().get(i).getSymbol(),
                    getSymbolInfo.getSymbolInfo().get(i).getLogo(),
                    getSymbolInfo.getDefaultRenegeNumber(),
                    getSymbolInfo.getSymbolInfo().get(i).getAmountScale());
            MMKVUtils.getInstance().enCode("DefaultRenegeNumber", getSymbolInfo.getDefaultRenegeNumber());
            fragments.add(fragment);
        }

        buyViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        buyTabLayout.setupWithViewPager(buyViewPager);
        buyTabLayout.removeAllTabs();
        for (GetSymbolInfo.SymbolInfoBean symbolInfoBean : getSymbolInfo.getSymbolInfo()) {
            Glide.with(this).load(symbolInfoBean.getLogo()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    buyTabLayout.addTab(buyTabLayout.newTab().setText(symbolInfoBean.getSymbol()).setIcon(resource));
                }
            });
        }
    }
}