package com.zxjk.duoduo.ui.minepage.wallet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.walletpage.OrdersFragment;
import com.zxjk.duoduo.ui.walletpage.ZhuanChuActivity;
import com.zxjk.duoduo.utils.GlideUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

public class WalletTradeActivity extends BaseActivity {
    private int[] mTitleDataList = new int[]{R.string.all, R.string.zhuanchu, R.string.collection_and_payment, R.string.huazhuan};

    private String walletAddress;
    private String money;
    private String symbol;
    private String sum;
    private String logo;
    private String coinType;
    private String parentSymbol;
    private String tokenDecimal;
    private String contractAddress;

    private ImageView ivLogo;
    private TextView tvBalance;
    private TextView tvBalanceToCny;
    private TextView tvHead1;
    private MagicIndicator indicator;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_trade);

        walletAddress = getIntent().getStringExtra("address");
        money = getIntent().getStringExtra("money");
        symbol = getIntent().getStringExtra("symbol");
        sum = getIntent().getStringExtra("sum");
        logo = getIntent().getStringExtra("logo");
        coinType = getIntent().getStringExtra("coinType");
        parentSymbol = getIntent().getStringExtra("parentSymbol");
        tokenDecimal = getIntent().getStringExtra("tokenDecimal");
        contractAddress = getIntent().getStringExtra("contractAddress");

        TextView title = findViewById(R.id.tv_title);
        title.setText(symbol);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ivLogo = findViewById(R.id.ivLogo);
        tvBalance = findViewById(R.id.tvBalance);
        tvBalanceToCny = findViewById(R.id.tvBalanceToCny);
        tvHead1 = findViewById(R.id.tvHead1);
        indicator = findViewById(R.id.indicator);
        pager = findViewById(R.id.pager);

        tvBalanceToCny.setText(money.equals("-") ? "-" : ("≈¥" + money));
        tvBalance.setText(sum);
        GlideUtil.loadNormalImg(ivLogo, logo);
        tvHead1.setText(tvHead1.getText() + "(" + symbol + ")");


        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleDataList == null ? 0 : mTitleDataList.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {

                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);
                commonPagerTitleView.setContentView(R.layout.pager_title_view);
                final ImageView titleImg = commonPagerTitleView.findViewById(R.id.title_img);
                titleImg.setImageResource(R.drawable.ic_hilamglogo2);
                final TextView titleText = commonPagerTitleView.findViewById(R.id.title_text);
                titleText.setText(mTitleDataList[index]);
                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {
                    @Override
                    public void onSelected(int index, int totalCount) {
                        titleText.setTextColor(ContextCompat.getColor(context,R.color.colorTheme));
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
                        titleText.setTextColor(Color.BLACK);
                    }

                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {

                    }

                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {

                    }
                });

                commonPagerTitleView.setOnClickListener(view -> pager.setCurrentItem(index));


                return commonPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {

                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(ContextCompat.getColor(context, R.color.colorTheme));
                indicator.setMode(LinePagerIndicator.MODE_MATCH_EDGE);
                return indicator;
            }
        });

        indicator.setNavigator(commonNavigator);

        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                OrdersFragment fragment = new OrdersFragment();
                fragment.address = walletAddress;
                fragment.symbol = symbol;

                if (position == 0) {
                    fragment.type = "1";
                } else if (position == 1) {
                    fragment.type = "2";
                } else if (position == 2) {
                    fragment.type = "3";
                } else if (position == 3) {
                    fragment.type = "4";
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 4;
            }
        });

        ViewPagerHelper.bind(indicator, pager);
    }

    public void tradeOut(View view) {
        Intent intent = new Intent(this, ZhuanChuActivity.class);
        intent.putExtra("symbol", symbol);
        intent.putExtra("address", walletAddress);
        intent.putExtra("balance", tvBalance.getText().toString().trim());
        intent.putExtra("coinType", coinType);
        intent.putExtra("parentSymbol", parentSymbol);
        intent.putExtra("tokenDecimal", tokenDecimal);
        intent.putExtra("contractAddress", contractAddress);
        intent.putExtra("sum",sum);
        startActivity(intent);
    }

    public void tradeIn(View view) {
        Intent intent = new Intent(this, BlockWalletPaymentQRActivity.class);
        intent.putExtra("symbol", symbol);
        intent.putExtra("address", walletAddress);
        intent.putExtra("logo", logo);
        startActivity(intent);
    }
}