package com.zxjk.duoduo.ui.socialspace;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CommunityInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.AddContactActivity;
import com.zxjk.duoduo.ui.msgpage.CreateGroupActivity;
import com.zxjk.duoduo.ui.msgpage.QrCodeActivity;
import com.zxjk.duoduo.ui.walletpage.RecipetQRActivity;
import com.zxjk.duoduo.ui.widget.ImagePagerIndicator;
import com.zxjk.duoduo.ui.widget.SlopScrollView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import de.hdodenhof.circleimageview.CircleImageView;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class SocialHomeActivity extends BaseActivity {

    private int[] detailTitles = {R.string.social_calture, R.string.social_act};
    //    private static final float SCROLL_SLOP = ;
    private int minimumHeightForVisibleOverlappingContent = 0;
    private int totalScrollRange = 0;
    private int toolbarHeight = 0;
    private int statusbarHeight = 0;

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

    private RecyclerView recyclerGroupMember;
    private BaseQuickAdapter<CommunityInfoResponse.MembersBean, BaseViewHolder> socialMemAdapter;
    private int colorOwner;
    private TextView tvSlogan;
    private TextView tvSocialName;
    private TextView tvSocialId;
    private ImageView ivHead;

    private QuickPopup menuPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);
        BarUtils.setStatusBarLightMode(this, true);
        setContentView(R.layout.activity_social_home);

        initView();

        initData();

        setSocialBackgroundHeight();

        setToolBarMarginTop();

        tvTitle.setText(R.string.de_actionbar_sub_group);

        setSupportActionBar(toolbar);

        onAppBarScroll();

        initSlopScrollView();

        setPagerHeight();

        initIndicator();

        initPager();

        ViewPagerHelper.bind(indicatorOut, pagerOut);
        ViewPagerHelper.bind(indicatorTop, pagerOut);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .communityInfo(getIntent().getStringExtra("id"))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    socialMemAdapter.setNewData(r.getMembers());
                    tvSlogan.setText("社群简介:" + r.getIntroduction());
                    tvSocialId.setText("社群号:" + r.getCode());
                    tvSocialName.setText(r.getName());
                    GlideUtil.loadNormalImg(ivBg, r.getBgi());
                    GlideUtil.loadNormalImg(ivHead, r.getLogo());
                    tvTitle.setText(r.getName());
                }, this::handleApiError);
    }

    private void initSlopScrollView() {
        int slop = CommonUtils.dip2px(this, 240);
        slopScroll.setScrollSlop(slop);

        slopScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY >= slop && indicatorTop.getVisibility() == View.INVISIBLE) {
                indicatorTop.setVisibility(View.VISIBLE);
                indicatorOut.setVisibility(View.INVISIBLE);
            } else if (scrollY < slop && indicatorTop.getVisibility() == View.VISIBLE) {
                indicatorTop.setVisibility(View.INVISIBLE);
                indicatorOut.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onAppBarScroll() {
        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int absOffset = Math.abs(verticalOffset);
            if (minimumHeightForVisibleOverlappingContent <= 0) {
                minimumHeightForVisibleOverlappingContent = app_bar.getMinimumHeightForVisibleOverlappingContent();
            }
            if (totalScrollRange <= 0) {
                totalScrollRange = app_bar.getTotalScrollRange();
            }

            if (absOffset <= minimumHeightForVisibleOverlappingContent) {
                if (ivToolBarEnd.getVisibility() == View.GONE) {
                    ivToolBarEnd.setVisibility(View.VISIBLE);
                    ivToolBarStart.setImageResource(R.drawable.ic_social_back);
                }
                if (ivToolBarStart.getVisibility() == View.GONE) {
                    ivToolBarStart.setVisibility(View.VISIBLE);
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
                if (ivToolBarEnd.getVisibility() == View.VISIBLE) {
                    ivToolBarEnd.setVisibility(View.GONE);
                }
                ivToolBarStart.setVisibility(View.VISIBLE);
                ivToolBarStart.setImageResource(R.drawable.ico_back);
            }
        });
    }

    private void setToolBarMarginTop() {
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarHeight = layoutParams1.height;
        statusbarHeight = BarUtils.getStatusBarHeight();
        layoutParams1.topMargin = statusbarHeight;
        toolbar.setLayoutParams(layoutParams1);
    }

    private void setSocialBackgroundHeight() {
        ViewGroup.LayoutParams layoutParams = app_bar.getLayoutParams();
        layoutParams.height = (int) (ScreenUtils.getScreenWidth() * 0.75);
        app_bar.setLayoutParams(layoutParams);
        ivBg.setImageResource(R.drawable.bg_default_social);
    }

    private void initView() {
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
        recyclerGroupMember = findViewById(R.id.recyclerGroupMember);
        tvSlogan = findViewById(R.id.tvSlogan);
        tvSocialName = findViewById(R.id.tvSocialName);
        tvSocialId = findViewById(R.id.tvSocialId);
        ivHead = findViewById(R.id.ivHead);

        initAdapterForSocialMem();
    }

    private void initAdapterForSocialMem() {
        int maxMemVisiableItem = (ScreenUtils.getScreenWidth() - CommonUtils.dip2px(this, 64)) % CommonUtils.dip2px(this, 48);
        recyclerGroupMember.setLayoutManager(new GridLayoutManager(this, maxMemVisiableItem));
        colorOwner = Color.parseColor("#ffc000");
        socialMemAdapter = new BaseQuickAdapter<CommunityInfoResponse.MembersBean, BaseViewHolder>(R.layout.item_social_membs) {
            @Override
            protected void convert(BaseViewHolder helper, CommunityInfoResponse.MembersBean item) {
                CircleImageView ivMemberHead = helper.getView(R.id.ivMemberHead);
                ImageView ivOwner = helper.getView(R.id.ivOwner);
                TextView tvMore = helper.getView(R.id.tvMore);

                if (helper.getAdapterPosition() == maxMemVisiableItem - 1) {
                    ivMemberHead.setVisibility(View.GONE);
                    tvMore.setVisibility(View.VISIBLE);

                    int moreNumb = getData().size() - maxMemVisiableItem;
                    if (moreNumb >= 1000) {
                        moreNumb = 999;
                    }
                    tvMore.setText("+" + moreNumb);
                    return;
                } else {
                    ivMemberHead.setVisibility(View.VISIBLE);
                    tvMore.setVisibility(View.GONE);
                }

                Glide.with(SocialHomeActivity.this).load(item.getHeadPortrait()).into(ivMemberHead);

                if (helper.getAdapterPosition() == 0) {
                    ivOwner.setVisibility(View.VISIBLE);
                    ivMemberHead.setBorderColor(colorOwner);
                } else {
                    ivOwner.setVisibility(View.GONE);
                    ivMemberHead.setBorderColor(Color.WHITE);
                }
            }
        };
        recyclerGroupMember.setAdapter(socialMemAdapter);
    }

    private void initPager() {
        pagerOut.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), R.string.appbar_scrolling_view_behavior) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                CalturePage calturePage = new CalturePage();
                if (position == 1) calturePage.social2();
                return calturePage;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }

    private void setPagerHeight() {
        ViewGroup.LayoutParams layoutParams = pagerOut.getLayoutParams();
        layoutParams.height = ScreenUtils.getScreenHeight() - (toolbarHeight + statusbarHeight + CommonUtils.dip2px(SocialHomeActivity.this, 48) + BarUtils.getNavBarHeight());
        pagerOut.setLayoutParams(layoutParams);
    }

    private void initIndicator() {
        CommonNavigator navigator1 = new CommonNavigator(this);
        navigator1.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return detailTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView pagerTitleView = new SimplePagerTitleView(context);
                pagerTitleView.setTextSize(15.5f);
                pagerTitleView.setNormalColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.textColor9));
                pagerTitleView.setSelectedColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.black));
                pagerTitleView.setText(detailTitles[index]);
                pagerTitleView.setOnClickListener(v -> pagerOut.setCurrentItem(index));
                return pagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return new ImagePagerIndicator(context, -3);
            }
        });

        CommonNavigator navigator2 = new CommonNavigator(SocialHomeActivity.this);
        navigator2.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return detailTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView pagerTitleView = new SimplePagerTitleView(context);
                pagerTitleView.setTextSize(15.5f);
                pagerTitleView.setNormalColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.textColor9));
                pagerTitleView.setSelectedColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.black));
                pagerTitleView.setText(detailTitles[index]);
                pagerTitleView.setOnClickListener(v -> pagerOut.setCurrentItem(index));
                return pagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return new ImagePagerIndicator(context, -3);
            }
        });

        indicatorTop.setNavigator(navigator1);
        indicatorOut.setNavigator(navigator2);
    }

    public void socialSlogan(View view) {

    }

    public void socialNotice(View view) {

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
