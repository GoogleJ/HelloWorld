package com.zxjk.duoduo.ui.socialspace;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.AddContactActivity;
import com.zxjk.duoduo.ui.msgpage.CreateGroupActivity;
import com.zxjk.duoduo.ui.msgpage.QrCodeActivity;
import com.zxjk.duoduo.ui.walletpage.RecipetQRActivity;
import com.zxjk.duoduo.ui.widget.SlopScrollView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class SocialHomeActivity extends BaseActivity {

    private int[] detailTitles = {R.string.social_calture, R.string.social_act};

    private AppBarLayout app_bar;
    private CollapsingToolbarLayout collapsingLayout;
    private ImageView ivBg;
    private Toolbar toolbar;
    private ImageView ivToolBarStart;
    private TextView tvTitle;
    private ImageView ivToolBarEnd;
    private SlopScrollView slopScroll;
    private ViewPager pagerOut;
    private MagicIndicator indicatorOut;
    private MagicIndicator indicatorTop;

    private QuickPopup menuPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);
        BarUtils.setStatusBarLightMode(this, true);
        setContentView(R.layout.activity_social_home);

        app_bar = findViewById(R.id.app_bar);
        collapsingLayout = findViewById(R.id.collapsingLayout);
        ivBg = findViewById(R.id.ivBg);
        toolbar = findViewById(R.id.toolbar);
        slopScroll = findViewById(R.id.slopScroll);
        pagerOut = findViewById(R.id.pagerOut);
        indicatorOut = findViewById(R.id.indicatorOut);
        indicatorTop = findViewById(R.id.indicatorTop);
        ivToolBarStart = findViewById(R.id.ivToolBarStart);
        tvTitle = findViewById(R.id.tvTitle);
        ivToolBarEnd = findViewById(R.id.ivToolBarEnd);

        ViewGroup.LayoutParams layoutParams = app_bar.getLayoutParams();
        layoutParams.height = (int) (ScreenUtils.getScreenWidth() * 0.75);
        app_bar.setLayoutParams(layoutParams);
        ivBg.setImageResource(R.drawable.bg_default_social);

        tvTitle.setText(R.string.de_actionbar_sub_group);

        setSupportActionBar(toolbar);

        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int absOffset = Math.abs(verticalOffset);
            int minimumHeightForVisibleOverlappingContent = app_bar.getMinimumHeightForVisibleOverlappingContent();
            int totalScrollRange = app_bar.getTotalScrollRange();

            if (absOffset <= minimumHeightForVisibleOverlappingContent) {
                if (ivToolBarEnd.getVisibility() == View.GONE) {
                    ivToolBarEnd.setVisibility(View.VISIBLE);
                    ivToolBarStart.setImageResource(R.drawable.ic_social_back);
                }
            } else if (absOffset < totalScrollRange) {
                if (ivToolBarEnd.getVisibility() == View.VISIBLE) {
                    ivToolBarEnd.setVisibility(View.GONE);
                    ivToolBarStart.setVisibility(View.GONE);
                }
                if (tvTitle.getVisibility() == View.VISIBLE) {
                    tvTitle.setVisibility(View.INVISIBLE);
                }
            } else if (absOffset == totalScrollRange) {
                if (tvTitle.getVisibility() == View.INVISIBLE) {
                    tvTitle.setVisibility(View.VISIBLE);
                }
                ivToolBarStart.setVisibility(View.VISIBLE);
                ivToolBarStart.setImageResource(R.drawable.ico_back);
            }
        });

//        setPagerHeight();

        initIndicator();

        initPager();

        ViewPagerHelper.bind(indicatorOut, pagerOut);
        ViewPagerHelper.bind(indicatorTop, pagerOut);
    }

    private void initPager() {
        pagerOut.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return new CalturePage();
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }

//    private void setPagerHeight() {
//        pagerOut.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            if (hasInitHeight) {
//                return;
//            }
//            ViewGroup.LayoutParams layoutParams = pagerOut.getLayoutParams();
//            layoutParams.height = slop + pagerOut.getHeight();
//            pagerOut.setLayoutParams(layoutParams);
//            hasInitHeight = true;
//        });
//    }

    private void initIndicator() {
        CommonNavigator navigator1 = new CommonNavigator(this);
//        navigator1.setAdjustMode(true);
        navigator1.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return detailTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView pagerTitleView = new SimplePagerTitleView(context);
                pagerTitleView.setTextSize(15.5f);
                pagerTitleView.setNormalColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.msgTitle));
                pagerTitleView.setSelectedColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.colorTheme));
                pagerTitleView.setText(detailTitles[index]);
                pagerTitleView.setOnClickListener(v -> pagerOut.setCurrentItem(index));
                return pagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setColors(ContextCompat.getColor(SocialHomeActivity.this, R.color.colorTheme));
                return linePagerIndicator;
            }
        });

        CommonNavigator navigator2 = new CommonNavigator(SocialHomeActivity.this);
//        navigator2.setAdjustMode(true);
        navigator2.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return detailTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView pagerTitleView = new SimplePagerTitleView(context);
                pagerTitleView.setTextSize(15.5f);
                pagerTitleView.setNormalColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.msgTitle));
                pagerTitleView.setSelectedColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.colorTheme));
                pagerTitleView.setText(detailTitles[index]);
                pagerTitleView.setOnClickListener(v -> pagerOut.setCurrentItem(index));
                return pagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setColors(ContextCompat.getColor(SocialHomeActivity.this, R.color.colorTheme));
                return linePagerIndicator;
            }
        });

        indicatorTop.setNavigator(navigator1);
        indicatorOut.setNavigator(navigator2);
    }

    public void back(View view) {
        finish();
    }

    public void menu(View view) {
        if (menuPop == null) {
            menuPop = QuickPopupBuilder.with(this)
                    .contentView(R.layout.pop_msg_top)
                    .config(new QuickPopupConfig()
                            .backgroundColor(android.R.color.transparent)
                            .gravity(Gravity.BOTTOM | Gravity.END)
                            .withShowAnimation(AnimationUtils.loadAnimation(this, R.anim.push_scale_in))
                            .withDismissAnimation(AnimationUtils.loadAnimation(this, R.anim.push_scale_out))
                            .withClick(R.id.send_group_chat, child -> {
                                Intent intent = new Intent(this, CreateGroupActivity.class);
                                intent.putExtra("eventType", 1);
                                startActivity(intent);
                            }, true)
                            .withClick(R.id.invite_friends, child -> startActivity(new Intent(this, AddContactActivity.class)), true)
                            .withClick(R.id.collection_and_payment, child -> startActivity(new Intent(this, RecipetQRActivity.class)), true))
                    .build();

            getPermisson(menuPop.findViewById(R.id.scan), granted -> {
                menuPop.dismiss();
                if (granted) startActivity(new Intent(this, QrCodeActivity.class));
            }, Manifest.permission.CAMERA);
        }

        menuPop.showPopupWindow(view);
    }

}
