package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.ReleaseRecord;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.util.List;

@SuppressLint("CheckResult")
public class DropRedRecordActivity extends BaseActivity {

    private MagicIndicator mDropRedMagicIndicator;
    private ViewPager mDropRedViewPager;
    private TextView mTvDropRecordLaveCount;
    private TextView mTvDropRecordCurrency;
    private TextView mTvDropRecordAirdrops;
    private TextView mTvDropRecordStatus;
    private TextView mTvTitle;
    private LinearLayout mLlDropRecord;
    private FrameLayout mFlDropNoRecord;

    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_red_record);

        initView();

        initData();
    }

    private void initView() {
        mDropRedMagicIndicator = findViewById(R.id.drop_red_indicator);
        mDropRedViewPager = findViewById(R.id.drop_red_pager);
        mTvDropRecordLaveCount = findViewById(R.id.tv_drop_record_laveCount);
        mTvDropRecordCurrency = findViewById(R.id.tv_drop_record_currency);
        mTvDropRecordAirdrops = findViewById(R.id.tv_drop_record_airdrops);
        mTvDropRecordStatus = findViewById(R.id.tv_drop_record_status);
        mTvTitle = findViewById(R.id.tv_title);
        mLlDropRecord = findViewById(R.id.ll_drop_record);
        mFlDropNoRecord = findViewById(R.id.fl_drop_no_record);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        mTvTitle.setText(R.string.drop_record);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        groupId = getIntent().getStringExtra("groupId");
        ServiceFactory.getInstance().getBaseService(Api.class)
                .releaseRecord(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    if (!s.isEmpty()) {
                        mLlDropRecord.setVisibility(View.VISIBLE);
                        mFlDropNoRecord.setVisibility(View.GONE);
                        onMagicIndicator(s);
                    } else {
                        mLlDropRecord.setVisibility(View.GONE);
                        mFlDropNoRecord.setVisibility(View.VISIBLE);
                    }
                }, this::handleApiError);
    }


    private void onMagicIndicator(List<ReleaseRecord> releaseRecords) {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(false);
        commonNavigator.setFollowTouch(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return releaseRecords == null ? 0 : releaseRecords.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);
                commonPagerTitleView.setContentView(R.layout.pager_title_view);
                final ImageView titleImg = commonPagerTitleView.findViewById(R.id.title_img);
                GlideUtil.loadNormalImg(titleImg, releaseRecords.get(index).getLogo());
                final TextView titleText = commonPagerTitleView.findViewById(R.id.title_text);
                titleText.setText(releaseRecords.get(index).getSymbol());
                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {
                    @Override
                    public void onSelected(int index, int totalCount) {
                        titleText.setTextColor(ContextCompat.getColor(context, R.color.colorTheme));
                        mTvDropRecordLaveCount.setText(releaseRecords.get(index).getLaveCount());
                        mTvDropRecordCurrency.setText(releaseRecords.get(index).getSymbol());
                        mTvDropRecordAirdrops.setText(releaseRecords.get(index).getAirdrops());
                        if (releaseRecords.get(index).getStatus().equals("0")) {
                            mTvDropRecordStatus.setText(R.string.notStart);
                            mTvDropRecordStatus.setTextColor(getResources().getColor(R.color.count_down));
                        } else if (releaseRecords.get(index).getStatus().equals("1")) {
                            mTvDropRecordStatus.setText(R.string.procssing);
                            mTvDropRecordStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                        } else if (releaseRecords.get(index).getStatus().equals("2")) {
                            mTvDropRecordStatus.setText(R.string.done2);
                            mTvDropRecordStatus.setTextColor(getResources().getColor(R.color.text_select_color));
                        } else if (releaseRecords.get(index).getStatus().equals("3")) {
                            mTvDropRecordStatus.setText(R.string.done3);
                            mTvDropRecordStatus.setTextColor(getResources().getColor(R.color.text_select_color));
                        }

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

                commonPagerTitleView.setOnClickListener(view -> mDropRedViewPager.setCurrentItem(index));

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
        mDropRedMagicIndicator.setNavigator(commonNavigator);
        mDropRedViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @NonNull
            @Override
            public Fragment getItem(int position) {
                DropRedRecordFragment dropRedRecordFragment = new DropRedRecordFragment();
                dropRedRecordFragment.groupId = groupId;
                dropRedRecordFragment.symbol = releaseRecords.get(position).getSymbol();
                dropRedRecordFragment.airdropId = releaseRecords.get(position).getAirdropId();

                return dropRedRecordFragment;
            }

            @Override
            public int getCount() {
                return releaseRecords.size();
            }
        });

        ViewPagerHelper.bind(mDropRedMagicIndicator, mDropRedViewPager);
    }
}
