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
import com.zxjk.moneyspace.bean.response.GetCustomerIdentity;
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
    private GetOTCSymbolInfo getOTCSymbolInfo;
    private GetCustomerIdentity getCustomerIdentity;
    private BuyCoinViewPagerFragment buyCoinViewPagerFragment;
    private SelfSelectionFragment selfSelectionFragment;

    public static BuyCoinFragment newInstance(GetCustomerIdentity getCustomerIdentity, int count, int type) {

        BuyCoinFragment fragment = new BuyCoinFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("GetCustomerIdentity", getCustomerIdentity);
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
        getCustomerIdentity = (GetCustomerIdentity)getArguments().getSerializable("GetCustomerIdentity");
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
                            this.getOTCSymbolInfo = getOTCSymbolInfo;
                            Constant.defaultRenegeNumber = getOTCSymbolInfo.getDefaultRenegeNumber();
                        },
                        this::handleApiError);
    }

    private void setPagerTitle(GetOTCSymbolInfo getOTCSymbolInfo) {

        buyViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (type == 0) {
                    buyCoinViewPagerFragment = BuyCoinViewPagerFragment.newInstance(getOTCSymbolInfo.getCurrencyList().get(position).getCurrency(),
                            getOTCSymbolInfo.getCurrencyList().get(position).getPrice(),
                            getOTCSymbolInfo.getCurrencyList().get(position).getRate(),
                            getOTCSymbolInfo.getCurrencyList().get(position).getBalance(),
                            getCustomerIdentity,
                            count,
                            getOTCSymbolInfo.getPayInfoList());
                } else {
                    selfSelectionFragment = SelfSelectionFragment.newInstance(getOTCSymbolInfo.getCurrencyList().get(position).getCurrency(),
                            count, getCustomerIdentity.getIdentity());
                }
                return type == 0 ? buyCoinViewPagerFragment : selfSelectionFragment;
            }

            @Override
            public int getCount() {
                return getOTCSymbolInfo.getCurrencyList().size();
            }
        });

        buyTabLayout.setupWithViewPager(buyViewPager);
        buyTabLayout.removeAllTabs();
        for (GetOTCSymbolInfo.CurrencyListBean currencyBean : getOTCSymbolInfo.getCurrencyList()) {
            buyTabLayout.addTab(buyTabLayout.newTab().setText(currencyBean.getCurrency()));
        }
    }
}