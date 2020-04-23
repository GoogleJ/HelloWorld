package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.BarUtils;
import com.google.android.material.tabs.TabLayout;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetBalanceInfoResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.DetailListActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MMKVUtils;

@SuppressLint("CheckResult")
public class BalanceLeftActivity extends BaseActivity {
    private TextView tvBalance;
    private TextView tvBalance2CNY;
    private ImageView ivShowOrHide;
    private LinearLayout llTop;
    private ViewPager pager;
    private TabLayout tab;

    private boolean isShow;

    private String hideStr1 = "***********";
    private String hideStr2 = "********";

    private GetBalanceInfoResponse response;

    private BalancePage2 page2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_left);
        BarUtils.setStatusBarColor(this, Color.parseColor("#272E3F"));

        tvBalance = findViewById(R.id.tvBalance);
        tvBalance2CNY = findViewById(R.id.tvBalance2CNY);
        ivShowOrHide = findViewById(R.id.ivShowOrHide);
        llTop = findViewById(R.id.llTop);
        pager = findViewById(R.id.pager);
        tab = findViewById(R.id.tab);

        tab.setupWithViewPager(pager);

        BarUtils.addMarginTopEqualStatusBarHeight(llTop);

        isShow = MMKVUtils.getInstance().decodeBool("bahaviour2_showWalletBalance", true);

        ivShowOrHide.setOnClickListener(v -> {
            isShow = !isShow;
            MMKVUtils.getInstance().enCode("bahaviour2_showWalletBalance", isShow);
            showOrHide();
        });

        page2 = new BalancePage2();
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 1:
                        return page2;
                    default:
                        return new BalancePage1();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 1:
                        return getString(R.string.wallet);
                    default:
                        return getString(R.string.lines);
                }
            }
        });

        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
    }

    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getBalanceInfo()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(response -> {
                    this.response = response;
                    tvBalance.setText(response.getTotalToBtc());
                    tvBalance2CNY.setText("≈" + response.getTotalToCny() + "CNY");

                    page2.showData(response.getBalanceList());
                    showOrHide();
                }, throwable -> {
                    handleApiError(throwable);
                    finish();
                });
    }

    private void showOrHide() {
        if (isShow) {
            ivShowOrHide.setImageResource(R.drawable.ic_blockwallet_hide);

            SpannableString string = new SpannableString(response.getTotalToBtc() + " BTC");
            string.setSpan(new RelativeSizeSpan(0.56f), string.length() - 3, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvBalance.setText(string);
            tvBalance2CNY.setText("≈" + response.getTotalToCny() + "CNY");
            page2.showOrHide(isShow);
        } else {
            ivShowOrHide.setImageResource(R.drawable.ic_blockwallet_show);
            tvBalance.setText(hideStr1);
            tvBalance2CNY.setText(hideStr2);
            page2.showOrHide(isShow);
        }
    }

    public void back(View view) {
        finish();
    }

    public void orderList(View view) {
        startActivity(new Intent(this, DetailListActivity.class));
    }

}
