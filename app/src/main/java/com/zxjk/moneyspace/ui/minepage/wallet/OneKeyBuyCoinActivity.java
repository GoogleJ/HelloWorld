package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.MsgTitleView;
import com.zxjk.moneyspace.utils.ClickUtils;
import com.zxjk.moneyspace.utils.CommonUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import static net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator.MODE_WRAP_CONTENT;

public class OneKeyBuyCoinActivity extends BaseActivity implements View.OnClickListener {

    private String customerIdentity;
    private ViewPager viewPager;
    private MagicIndicator indicator;
    private TextView tvSelfSelection;
    private TextView tvSpeedy;
    private LinearLayout llOneKey;
    private ImageView imgOrderList;
    private static int ONCLICKID = 1;
    private int mTitles[] = {
            R.string.to_buy, R.string.to_sell};

    public void setCustomerIdentity(String customerIdentity) {
        this.customerIdentity = customerIdentity;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key_buy_coin);
        setTrasnferStatusBar(true);

        getCustomerIdentity(0);

        initView();

        initData();
    }

    private void initView() {
        indicator = findViewById(R.id.indicator);
        viewPager = findViewById(R.id.viewpager);
        tvSelfSelection = findViewById(R.id.tv_self_selection);
        tvSpeedy = findViewById(R.id.tv_speedy);
        llOneKey = findViewById(R.id.ll_one_key);
        imgOrderList = findViewById(R.id.img_order_list);
        findViewById(R.id.tv_speedy).setOnClickListener(this::onClick);
        findViewById(R.id.tv_self_selection).setOnClickListener(this::onClick);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        findViewById(R.id.img_order_list).setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderInfoByTypeActivity.class);
            intent.putExtra("customerIdentity", customerIdentity);
            startActivity(intent);
        });
    }

    private void initData() {
        initIndicator(1);

        ViewPagerHelper.bind(indicator, viewPager);
    }

    private void initIndicator(int type) {
        final int finalType = type;
        CommonNavigator navigator = new CommonNavigator(this);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                MsgTitleView titleView = new MsgTitleView(context);
                titleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
                titleView.getTitleView().setText(mTitles[index]);
                titleView.getBadgeView().setVisibility(View.INVISIBLE);

                if (finalType == 1) {
                    titleView.setupNormalColor(Color.parseColor("#6D7278"));
                    titleView.setupSelectColor(Color.parseColor("#ffffff"));
                } else {
                    titleView.setupSelectColor(Color.parseColor("#272E3F"));
                    titleView.setupNormalColor(Color.parseColor("#6D7278"));
                }

                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(MODE_WRAP_CONTENT);
                indicator.setYOffset(-CommonUtils.dip2px(context, 4));
                return indicator;
            }
        });
        indicator.setNavigator(navigator);
        indicator.getNavigator().notifyDataSetChanged();
    }

    @SuppressLint("CheckResult")
    private void getCustomerIdentity(int type) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCustomerIdentity()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(d -> {
                    if (d.getIdentity().equals("0")) {
                        findViewById(R.id.rl_end).setVisibility(View.VISIBLE);
                    }

                    viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                        @NonNull
                        @Override
                        public Fragment getItem(int position) {
                            BuyCoinFragment buyCoinFragment = BuyCoinFragment.newInstance(d, position, type);

                            return position == 0 ? buyCoinFragment : buyCoinFragment;
                        }

                        @Override
                        public int getCount() {
                            return 2;
                        }

                        @Override
                        public CharSequence getPageTitle(int position) {
                            return getString(mTitles[position]);
                        }

                        @Override
                        public int getItemPosition(Object object) {
                            return POSITION_NONE;
                        }
                    });
                    customerIdentity = null;
                    setCustomerIdentity(d.getIdentity());
                }, this::handleApiError);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_speedy:
                ImageView img = findViewById(R.id.img_back);
                if (!ClickUtils.isFastDoubleClick(ONCLICKID)) {
                    if (customerIdentity.equals("1")) {
                        ToastUtils.showShort(R.string.developing);
                        return;
                    }
                    if (tvSpeedy.getBackground() != null) {
                        return;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    }
                    initIndicator(1);


                    img.setColorFilter(getResources().getColor(R.color.main_list_divider, null));
                    imgOrderList.setImageResource(R.drawable.ic_end_order2);
                    llOneKey.setBackgroundColor(Color.parseColor("#272E3F"));
                    tvSelfSelection.setBackground(null);
                    tvSpeedy.setBackground(getResources().getDrawable(R.drawable.shape_self_select_backgroud, null));
                    tvSpeedy.setTextColor(Color.parseColor("#FFFFFF"));
                    tvSelfSelection.setTextColor(Color.parseColor("#6D7278"));
                    getCustomerIdentity(0);
                    break;
                }
            case R.id.tv_self_selection:
                if (!ClickUtils.isFastDoubleClick(ONCLICKID)) {
                    if (customerIdentity.equals("1")) {
                        ToastUtils.showShort(R.string.developing);
                        return;
                    }
                    if (tvSelfSelection.getBackground() != null) {
                        return;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }

                    initIndicator(2);

                    img = findViewById(R.id.img_back);
                    img.setColorFilter(Color.parseColor("#272E3F"));
                    imgOrderList.setImageResource(R.drawable.ic_end_order);
                    llOneKey.setBackgroundColor(Color.parseColor("#F9F9F9"));
                    tvSelfSelection.setBackground(getResources().getDrawable(R.drawable.shape_self_select2, null));
                    tvSpeedy.setBackground(null);
                    tvSpeedy.setTextColor(Color.parseColor("#6D7278"));
                    tvSelfSelection.setTextColor(Color.parseColor("#272E3F"));
                    getCustomerIdentity(1);
                    break;
                }

        }
    }
}