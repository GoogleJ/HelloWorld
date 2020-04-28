package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetOTCSymbolInfo;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseFragment;
import com.zxjk.moneyspace.ui.minepage.SelfSelectionFragment;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.Sha256;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@SuppressLint("CheckResult")
public class BuyCoinFragment extends BaseFragment {

    private TabLayout buyTabLayout;

    private ViewPager buyViewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String customerIdentity = "";
    private String timestamp;
    private String sign;
    private int count;
    private int type;

    public static BuyCoinFragment newInstance(String customerIdentity, int count, int type) {

        BuyCoinFragment fragment = new BuyCoinFragment();
        Bundle bundle = new Bundle();
        bundle.putString("CustomerIdentity", customerIdentity);
        bundle.putInt("count", count);
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public String dataOne(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

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

        customerIdentity = getArguments().getString("CustomerIdentity");
        count = getArguments().getInt("count");
        type = getArguments().getInt("type");

        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(currentTime);
        timestamp = dataOne(formatter.format(date));
        String secret = "nonce=" + timestamp + Constant.SECRET;
        sign = Sha256.getSHA256(secret);
        ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                .getOTCSymbolInfo(timestamp)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                .subscribe(getOTCSymbolInfo -> {
                            setPagerTitle(getOTCSymbolInfo);
                            Constant.defaultRenegeNumber = getOTCSymbolInfo.getDefaultRenegeNumber();
                        },
                        this::handleApiError);
    }

    private void setPagerTitle(GetOTCSymbolInfo getOTCSymbolInfo) {
        if (type == 0) {
            for (int i = 0; i < getOTCSymbolInfo.getCurrencyList().size(); i++) {
                if (fragments.size() != 0) {
                    fragments.clear();
                }
                BuyCoinViewPagerFragment fragment = BuyCoinViewPagerFragment.newInstance(getOTCSymbolInfo.getCurrencyList().get(i).getCurrency(),
                        getOTCSymbolInfo.getCurrencyList().get(i).getPrice(),
                        getOTCSymbolInfo.getCurrencyList().get(i).getRate(),
                        getOTCSymbolInfo.getCurrencyList().get(i).getBalance(),
                        customerIdentity,
                        count,
                        getOTCSymbolInfo.getPayInfoList());
                fragments.add(fragment);
            }
        } else {
            for (int i = 0; i < getOTCSymbolInfo.getCurrencyList().size(); i++) {
                if (fragments.size() != 0) {
                    fragments.clear();
                }
                SelfSelectionFragment fragment = SelfSelectionFragment.newInstance(getOTCSymbolInfo.getCurrencyList().get(i).getCurrency(),
                        count,customerIdentity);

                fragments.add(fragment);
            }
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
        for (GetOTCSymbolInfo.CurrencyListBean currencyBean : getOTCSymbolInfo.getCurrencyList()) {
            buyTabLayout.addTab(buyTabLayout.newTab().setText(currencyBean.getCurrency()));
        }
    }
}